/**
 * SmartRemote Pro - Air Conditioner (AC) Controller
 */
class AcRemoteController {
  constructor() {
    this.temp = 24;
    this.isPowerOn = true;
    this.mode = 'cool'; // cool, heat, fan, auto, dry
    this.fanSpeed = 'auto'; // 1, 2, 3, auto, turbo
    this.isSwing = false;
    this.isEco = false;
  }

  init() {
    this.updateDisplay();
  }

  power() {
    this.isPowerOn = !this.isPowerOn;
    window.remoteBridge.playFeedbackSound('power');
    window.remoteBridge.send(`AC_POWER ${this.isPowerOn ? 'ON' : 'OFF'}`);
    window.showToast?.(this.isPowerOn ? 'AC Turned ON' : 'AC Turned OFF');
    this.updateDisplay();
  }

  tempUp() {
    if (this.temp < 30) {
      this.temp++;
      window.remoteBridge.send(`AC_TEMP ${this.temp}`);
      window.showToast?.(`AC: ${this.temp}°C`);
      this.updateDisplay();
    }
  }

  tempDown() {
    if (this.temp > 16) {
      this.temp--;
      window.remoteBridge.send(`AC_TEMP ${this.temp}`);
      window.showToast?.(`AC: ${this.temp}°C`);
      this.updateDisplay();
    }
  }

  setMode(mode) {
    this.mode = mode;
    window.remoteBridge.send(`AC_MODE ${mode.toUpperCase()}`);
    window.showToast?.(`AC Mode: ${mode.toUpperCase()}`);
    this.updateDisplay();
  }

  setFan(speed) {
    this.fanSpeed = speed;
    window.remoteBridge.send(`AC_FAN ${speed.toUpperCase()}`);
    window.showToast?.(`AC Fan: ${speed.toUpperCase()}`);
    this.updateDisplay();
  }

  toggleSwing() {
    this.isSwing = !this.isSwing;
    window.remoteBridge.send(`AC_SWING ${this.isSwing ? 'ON' : 'OFF'}`);
    window.showToast?.(this.isSwing ? 'AC Swing ON' : 'AC Swing OFF');
    this.updateDisplay();
  }

  toggleEco() {
    this.isEco = !this.isEco;
    window.remoteBridge.send(`AC_ECO ${this.isEco ? 'ON' : 'OFF'}`);
    window.showToast?.(this.isEco ? 'Eco Mode Enabled' : 'Eco Mode Disabled');
    this.updateDisplay();
  }

  updateDisplay() {
    const tempValEl = document.getElementById('acTempVal');
    const modeBadgeEl = document.getElementById('acModeBadge');
    if (tempValEl) tempValEl.textContent = this.temp;
    if (modeBadgeEl) modeBadgeEl.textContent = `${this.mode} • Fan: ${this.fanSpeed}`;

    // Update active mode buttons
    const modeBtns = document.querySelectorAll('.ac-mode-btn');
    modeBtns.forEach((btn) => {
      if (btn.dataset.mode === this.mode) {
        btn.classList.add('primary');
      } else {
        btn.classList.remove('primary');
      }
    });
  }
}

window.acRemote = new AcRemoteController();
