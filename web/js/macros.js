/**
 * SmartRemote Pro - Automation & Multi-Step Macros
 */
class MacroController {
  constructor() {
    this.isRunning = false;
  }

  async runMovieMode() {
    if (this.isRunning) return;
    this.isRunning = true;
    window.showToast?.('🎬 Starting Movie Mode...');

    const steps = [
      { msg: '1/4 Turning on Samsung Smart TV...', fn: () => window.tvRemote.power(), delay: 700 },
      { msg: '2/4 Launching Netflix...', fn: () => window.tvRemote.launchApp('Netflix'), delay: 900 },
      { msg: '3/4 Soundbar: Cinema EQ & Volume 25...', fn: () => {
        window.soundbarRemote.setEq('movie');
        window.soundbarRemote.volume = 25;
      }, delay: 800 },
      { msg: '4/4 Setting AC to 24°C Cool Mode...', fn: () => {
        window.acRemote.temp = 24;
        window.acRemote.setMode('cool');
      }, delay: 600 }
    ];

    await this.executeSteps(steps, '🍿 Movie Mode Activated!');
  }

  async runGamingMode() {
    if (this.isRunning) return;
    this.isRunning = true;
    window.showToast?.('🎮 Starting Gaming Mode...');

    const steps = [
      { msg: '1/3 TV Input switched to HDMI 1 (Gaming Console)...', fn: () => window.tvRemote.action('HDMI 1'), delay: 800 },
      { msg: '2/3 Soundbar: Bass Boost +4 & Music Mode...', fn: () => {
        window.soundbarRemote.bass = 4;
        window.soundbarRemote.updateDisplay();
        window.soundbarRemote.setEq('music');
      }, delay: 800 },
      { msg: '3/3 AC set to 22°C Turbo Cooling...', fn: () => {
        window.acRemote.temp = 22;
        window.acRemote.setFan('turbo');
      }, delay: 600 }
    ];

    await this.executeSteps(steps, '⚡ Gaming Mode Ready!');
  }

  async runNightMode() {
    if (this.isRunning) return;
    this.isRunning = true;
    window.showToast?.('🌙 Activating Night Mode...');

    const steps = [
      { msg: '1/3 Turning off Smart TV...', fn: () => {
        window.tvRemote.isPowered = false;
        window.remoteBridge.send('TV_POWER_OFF');
      }, delay: 700 },
      { msg: '2/3 Turning off Soundbar...', fn: () => {
        window.soundbarRemote.power();
      }, delay: 700 },
      { msg: '3/3 Setting AC to 26°C Eco Sleep Mode...', fn: () => {
        window.acRemote.temp = 26;
        window.acRemote.toggleEco();
      }, delay: 600 }
    ];

    await this.executeSteps(steps, '💤 Good Night - Night Mode Active');
  }

  async executeSteps(steps, finalMsg) {
    for (const step of steps) {
      window.showToast?.(step.msg);
      window.remoteBridge.playFeedbackSound('click');
      step.fn();
      await new Promise((resolve) => setTimeout(resolve, step.delay));
    }
    window.remoteBridge.playFeedbackSound('success');
    window.showToast?.(finalMsg);
    this.isRunning = false;
  }
}

window.macroController = new MacroController();
