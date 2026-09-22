/**
 * SmartRemote Pro - Connection Bridge & Hardware Feedback
 */
class RemoteBridge {
  constructor() {
    this.ws = null;
    this.isConnected = false;
    this.reconnectAttempts = 0;
    this.audioCtx = null;
    this.statusDot = null;
    this.statusText = null;

    this.initAudio();
  }

  init() {
    this.statusDot = document.getElementById('statusDot');
    this.statusText = document.getElementById('statusText');
    this.connect();
  }

  initAudio() {
    try {
      const AudioContext = window.AudioContext || window.webkitAudioContext;
      if (AudioContext) {
        this.audioCtx = new AudioContext();
      }
    } catch (e) {
      console.warn("AudioContext not supported", e);
    }
  }

  playFeedbackSound(type = 'click') {
    if (!this.audioCtx) return;
    if (this.audioCtx.state === 'suspended') {
      this.audioCtx.resume();
    }

    try {
      const osc = this.audioCtx.createOscillator();
      const gain = this.audioCtx.createGain();
      osc.connect(gain);
      gain.connect(this.audioCtx.destination);

      const now = this.audioCtx.currentTime;

      if (type === 'click') {
        osc.type = 'sine';
        osc.frequency.setValueAtTime(800, now);
        osc.frequency.exponentialRampToValueAtTime(300, now + 0.04);
        gain.gain.setValueAtTime(0.2, now);
        gain.gain.linearRampToValueAtTime(0.01, now + 0.04);
        osc.start(now);
        osc.stop(now + 0.04);
      } else if (type === 'success') {
        osc.type = 'triangle';
        osc.frequency.setValueAtTime(440, now);
        osc.frequency.setValueAtTime(880, now + 0.08);
        gain.gain.setValueAtTime(0.2, now);
        gain.gain.linearRampToValueAtTime(0.01, now + 0.2);
        osc.start(now);
        osc.stop(now + 0.2);
      } else if (type === 'power') {
        osc.type = 'sawtooth';
        osc.frequency.setValueAtTime(300, now);
        osc.frequency.exponentialRampToValueAtTime(600, now + 0.15);
        gain.gain.setValueAtTime(0.25, now);
        gain.gain.linearRampToValueAtTime(0.01, now + 0.15);
        osc.start(now);
        osc.stop(now + 0.15);
      }
    } catch (err) {
      // Audio autoplay policy error ignore
    }
  }

  triggerHaptic(ms = 30) {
    if ('vibrate' in navigator) {
      try {
        navigator.vibrate(ms);
      } catch (e) {}
    }
  }

  connect() {
    const host = window.location.hostname || 'localhost';
    const wsUrl = `ws://${host}:8765`;

    try {
      this.ws = new WebSocket(wsUrl);

      this.ws.onopen = () => {
        this.isConnected = true;
        this.reconnectAttempts = 0;
        this.updateStatus(true, 'Connected (PC Live)');
        this.send('DISCOVER_PC_REMOTE');
      };

      this.ws.onclose = () => {
        this.isConnected = false;
        this.updateStatus(false, 'Simulation Mode');
        this.scheduleReconnect();
      };

      this.ws.onerror = () => {
        this.isConnected = false;
        this.updateStatus(false, 'Simulation Mode');
      };

      this.ws.onmessage = (event) => {
        this.handleMessage(event.data);
      };
    } catch (e) {
      this.isConnected = false;
      this.updateStatus(false, 'Simulation Mode');
      this.scheduleReconnect();
    }
  }

  scheduleReconnect() {
    if (this.reconnectAttempts < 5) {
      this.reconnectAttempts++;
      setTimeout(() => this.connect(), 3000);
    }
  }

  updateStatus(connected, text) {
    if (this.statusDot) {
      if (connected) {
        this.statusDot.classList.add('connected');
      } else {
        this.statusDot.classList.remove('connected');
      }
    }
    if (this.statusText) {
      this.statusText.textContent = text;
    }
  }

  send(command) {
    this.triggerHaptic(20);
    this.playFeedbackSound('click');

    if (this.isConnected && this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(command);
    } else {
      // Standalone simulation mode
      console.log(`[SIMULATED COMMAND] -> ${command}`);
    }
  }

  handleMessage(data) {
    if (typeof data === 'string' && data.startsWith('PC_REMOTE_ACK')) {
      const parts = data.split('|');
      const hostname = parts[1] || 'PC';
      const osName = parts[2] || 'Windows';
      window.showToast?.(`Connected to ${hostname} (${osName})`);
    }
  }
}

window.remoteBridge = new RemoteBridge();
