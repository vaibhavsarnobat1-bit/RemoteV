/**
 * SmartRemote Pro - Bilingual Voice Assistant (English & Hindi)
 */
class VoiceRemoteController {
  constructor() {
    this.recognition = null;
    this.isListening = false;
    this.canvas = null;
    this.ctx = null;
    this.animationId = null;
    this.phase = 0;
    this.transcriptEl = null;
    this.lang = 'hi-IN'; // Default bilingual
  }

  init() {
    this.canvas = document.getElementById('voiceWaveform');
    if (this.canvas) {
      this.ctx = this.canvas.getContext('2d');
    }
    this.transcriptEl = document.getElementById('voiceTranscript');

    const SpeechRec = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (SpeechRec) {
      this.recognition = new SpeechRec();
      this.recognition.continuous = false;
      this.recognition.interimResults = true;
      this.recognition.lang = this.lang;

      this.recognition.onstart = () => {
        this.isListening = true;
        this.startWaveformAnimation();
        this.updateTranscript('Listening... / Sun raha hoon...');
      };

      this.recognition.onresult = (event) => {
        let interim = '';
        for (let i = event.resultIndex; i < event.results.length; ++i) {
          if (event.results[i].isFinal) {
            const finalTranscript = event.results[i][0].transcript.trim();
            this.handleFinalCommand(finalTranscript);
          } else {
            interim += event.results[i][0].transcript;
            this.updateTranscript(interim);
          }
        }
      };

      this.recognition.onerror = (event) => {
        console.warn('Speech Recognition Error:', event.error);
        this.stopListening();
        this.updateTranscript(`Error: ${event.error}. Try again.`);
      };

      this.recognition.onend = () => {
        this.stopListening();
      };
    }
  }

  toggleListening() {
    if (this.isListening) {
      this.stopListening();
    } else {
      this.startListening();
    }
  }

  startListening() {
    const modal = document.getElementById('voiceModal');
    if (modal) modal.classList.add('open');

    if (this.recognition) {
      try {
        this.recognition.start();
      } catch (e) {
        this.startWaveformAnimation();
      }
    } else {
      // Fallback for browsers without Web Speech API
      this.startWaveformAnimation();
      this.updateTranscript('Web Speech API unavailable in this browser. Type command:');
      const sampleCmd = prompt("Enter voice command (e.g. 'TV chalu karo', 'Volume up', 'Open YouTube', 'Movie Mode'):");
      if (sampleCmd) {
        this.handleFinalCommand(sampleCmd);
      }
    }
  }

  stopListening() {
    this.isListening = false;
    if (this.recognition) {
      try { this.recognition.stop(); } catch (e) {}
    }
    this.stopWaveformAnimation();
  }

  closeModal() {
    this.stopListening();
    const modal = document.getElementById('voiceModal');
    if (modal) modal.classList.remove('open');
  }

  updateTranscript(text) {
    if (this.transcriptEl) {
      this.transcriptEl.textContent = text;
    }
  }

  handleFinalCommand(rawCmd) {
    const cmd = rawCmd.toLowerCase();
    this.updateTranscript(`"${rawCmd}"`);
    let executed = false;
    let spokenReply = '';

    // Bilingual Rule-Based Matcher
    if (cmd.includes('tv chalu') || cmd.includes('turn on tv') || cmd.includes('power on tv')) {
      window.tvRemote.power();
      spokenReply = 'TV turned on';
      executed = true;
    } else if (cmd.includes('tv band') || cmd.includes('turn off tv') || cmd.includes('power off')) {
      window.tvRemote.power();
      spokenReply = 'TV turned off';
      executed = true;
    } else if (cmd.includes('awaaz badao') || cmd.includes('volume up') || cmd.includes('volume badhao')) {
      window.tvRemote.volumeUp();
      spokenReply = 'Volume increased';
      executed = true;
    } else if (cmd.includes('awaaz kam') || cmd.includes('volume down') || cmd.includes('volume kam')) {
      window.tvRemote.volumeDown();
      spokenReply = 'Volume decreased';
      executed = true;
    } else if (cmd.includes('mute') || cmd.includes('awaaz band')) {
      window.tvRemote.mute();
      spokenReply = 'Muted';
      executed = true;
    } else if (cmd.includes('youtube')) {
      window.tvRemote.launchApp('YouTube');
      spokenReply = 'Opening YouTube';
      executed = true;
    } else if (cmd.includes('netflix')) {
      window.tvRemote.launchApp('Netflix');
      spokenReply = 'Opening Netflix';
      executed = true;
    } else if (cmd.includes('movie mode') || cmd.includes('cinema mode')) {
      window.macroController.runMovieMode();
      spokenReply = 'Starting Movie Mode';
      executed = true;
    } else if (cmd.includes('ac 24') || cmd.includes('set ac to 24')) {
      window.acRemote.temp = 24;
      window.acRemote.updateDisplay();
      window.remoteBridge.send('AC_TEMP 24');
      spokenReply = 'AC set to 24 degrees';
      executed = true;
    } else if (cmd.includes('night mode') || cmd.includes('sab band')) {
      window.macroController.runNightMode();
      spokenReply = 'Activating Night Mode';
      executed = true;
    } else {
      spokenReply = `Command recognized: ${rawCmd}`;
      window.remoteBridge.send(`VOICE_CMD ${rawCmd}`);
      executed = true;
    }

    if (executed) {
      window.remoteBridge.playFeedbackSound('success');
      this.speakFeedback(spokenReply);
      setTimeout(() => this.closeModal(), 1600);
    }
  }

  speakFeedback(text) {
    if ('speechSynthesis' in window) {
      const utterance = new SpeechSynthesisUtterance(text);
      utterance.rate = 1.0;
      utterance.pitch = 1.0;
      window.speechSynthesis.speak(utterance);
    }
  }

  startWaveformAnimation() {
    if (!this.canvas || !this.ctx) return;
    const render = () => {
      const w = this.canvas.width;
      const h = this.canvas.height;
      this.ctx.clearRect(0, 0, w, h);

      this.ctx.lineWidth = 3;
      this.ctx.strokeStyle = '#00f0ff';
      this.ctx.beginPath();

      const centerY = h / 2;
      for (let x = 0; x < w; x++) {
        const y = centerY + Math.sin(x * 0.05 + this.phase) * 18 * Math.sin(x / w * Math.PI);
        if (x === 0) this.ctx.moveTo(x, y);
        else this.ctx.lineTo(x, y);
      }
      this.ctx.stroke();

      // Secondary purple wave
      this.ctx.lineWidth = 2;
      this.ctx.strokeStyle = '#8b5cf6';
      this.ctx.beginPath();
      for (let x = 0; x < w; x++) {
        const y = centerY + Math.cos(x * 0.04 - this.phase * 0.8) * 12 * Math.sin(x / w * Math.PI);
        if (x === 0) this.ctx.moveTo(x, y);
        else this.ctx.lineTo(x, y);
      }
      this.ctx.stroke();

      this.phase += 0.12;
      this.animationId = requestAnimationFrame(render);
    };

    render();
  }

  stopWaveformAnimation() {
    if (this.animationId) {
      cancelAnimationFrame(this.animationId);
      this.animationId = null;
    }
    if (this.canvas && this.ctx) {
      this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
    }
  }
}

window.voiceRemote = new VoiceRemoteController();
