/**
 * SmartRemote Pro - Soundbar & Home Audio Controller
 */
class SoundbarRemoteController {
  constructor() {
    this.volume = 30;
    this.bass = 3;
    this.treble = 2;
    this.eq = 'movie'; // movie, music, voice, night
    this.input = 'hdmi_arc'; // hdmi_arc, optical, bluetooth, aux
    this.isMuted = false;
  }

  power() {
    window.remoteBridge.playFeedbackSound('power');
    window.remoteBridge.send('SOUNDBAR_POWER');
    window.showToast?.('Soundbar Power Toggled');
  }

  volumeUp() {
    if (this.volume < 100) this.volume += 2;
    this.isMuted = false;
    window.remoteBridge.send('SOUNDBAR_VOL_UP');
    window.showToast?.(`Soundbar Vol: ${this.volume}`);
  }

  volumeDown() {
    if (this.volume > 0) this.volume -= 2;
    this.isMuted = false;
    window.remoteBridge.send('SOUNDBAR_VOL_DOWN');
    window.showToast?.(`Soundbar Vol: ${this.volume}`);
  }

  mute() {
    this.isMuted = !this.isMuted;
    window.remoteBridge.send('SOUNDBAR_MUTE');
    window.showToast?.(this.isMuted ? 'Soundbar Muted' : 'Soundbar Unmuted');
  }

  bassUp() {
    if (this.bass < 6) this.bass++;
    window.remoteBridge.send(`SOUNDBAR_BASS ${this.bass}`);
    window.showToast?.(`Bass: +${this.bass}`);
    this.updateDisplay();
  }

  bassDown() {
    if (this.bass > -6) this.bass--;
    window.remoteBridge.send(`SOUNDBAR_BASS ${this.bass}`);
    window.showToast?.(`Bass: ${this.bass}`);
    this.updateDisplay();
  }

  trebleUp() {
    if (this.treble < 6) this.treble++;
    window.remoteBridge.send(`SOUNDBAR_TREBLE ${this.treble}`);
    window.showToast?.(`Treble: +${this.treble}`);
    this.updateDisplay();
  }

  trebleDown() {
    if (this.treble > -6) this.treble--;
    window.remoteBridge.send(`SOUNDBAR_TREBLE ${this.treble}`);
    window.showToast?.(`Treble: ${this.treble}`);
    this.updateDisplay();
  }

  setEq(eq) {
    this.eq = eq;
    window.remoteBridge.send(`SOUNDBAR_EQ ${eq.toUpperCase()}`);
    window.showToast?.(`EQ: ${eq.toUpperCase()}`);

    const eqBtns = document.querySelectorAll('.eq-btn');
    eqBtns.forEach((btn) => {
      if (btn.dataset.eq === eq) {
        btn.classList.add('active');
      } else {
        btn.classList.remove('active');
      }
    });
  }

  setInput(input) {
    this.input = input;
    window.remoteBridge.send(`SOUNDBAR_INPUT ${input.toUpperCase()}`);
    window.showToast?.(`Audio Source: ${input.toUpperCase()}`);
  }

  updateDisplay() {
    const bassEl = document.getElementById('soundbarBassVal');
    const trebleEl = document.getElementById('soundbarTrebleVal');
    if (bassEl) bassEl.textContent = this.bass >= 0 ? `+${this.bass}` : `${this.bass}`;
    if (trebleEl) trebleEl.textContent = this.treble >= 0 ? `+${this.treble}` : `${this.treble}`;
  }
}

window.soundbarRemote = new SoundbarRemoteController();
