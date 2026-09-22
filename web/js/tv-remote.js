/**
 * SmartRemote Pro - Smart TV Remote Controller
 */
class TvRemoteController {
  constructor() {
    this.currentBrand = 'Samsung Tizen';
    this.volume = 20;
    this.channel = 101;
    this.isMuted = false;
    this.isPowered = true;
  }

  setBrand(brand) {
    this.currentBrand = brand;
    window.showToast?.(`TV set to ${brand}`);
  }

  power() {
    this.isPowered = !this.isPowered;
    window.remoteBridge.playFeedbackSound('power');
    window.remoteBridge.send(`TV_POWER ${this.currentBrand}`);
    window.showToast?.(this.isPowered ? 'TV Powered ON' : 'TV Powered OFF');
  }

  dpad(direction) {
    window.remoteBridge.send(`TV_DPAD ${direction.toUpperCase()}`);
    window.showToast?.(`TV: ${direction.toUpperCase()}`);
  }

  volumeUp() {
    if (this.volume < 100) this.volume++;
    this.isMuted = false;
    window.remoteBridge.send('TV_VOL_UP');
    window.showToast?.(`Volume: ${this.volume}`);
  }

  volumeDown() {
    if (this.volume > 0) this.volume--;
    this.isMuted = false;
    window.remoteBridge.send('TV_VOL_DOWN');
    window.showToast?.(`Volume: ${this.volume}`);
  }

  channelUp() {
    this.channel++;
    window.remoteBridge.send('TV_CH_UP');
    window.showToast?.(`Channel: ${this.channel}`);
  }

  channelDown() {
    if (this.channel > 1) this.channel--;
    window.remoteBridge.send('TV_CH_DOWN');
    window.showToast?.(`Channel: ${this.channel}`);
  }

  mute() {
    this.isMuted = !this.isMuted;
    window.remoteBridge.send('TV_MUTE');
    window.showToast?.(this.isMuted ? 'TV Muted' : 'TV Unmuted');
  }

  action(act) {
    window.remoteBridge.send(`TV_ACTION ${act.toUpperCase()}`);
    window.showToast?.(`TV: ${act.toUpperCase()}`);
  }

  launchApp(appName) {
    window.remoteBridge.send(`TV_LAUNCH ${appName.toUpperCase()}`);
    window.showToast?.(`Launching ${appName}...`);
  }
}

window.tvRemote = new TvRemoteController();
