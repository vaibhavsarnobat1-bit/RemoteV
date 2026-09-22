/**
 * SmartRemote Pro - PC Wireless Mouse, Touchpad & Controller
 */
class PcRemoteController {
  constructor() {
    this.touchpad = null;
    this.startX = 0;
    this.startY = 0;
    this.lastX = 0;
    this.lastY = 0;
    this.startTime = 0;
    this.hasMoved = false;
    this.twoFinger = false;
    this.sensitivity = 1.6;
  }

  init() {
    this.touchpad = document.getElementById('pcTouchpad');
    if (!this.touchpad) return;

    // Touch Events for Mobile / Tablets
    this.touchpad.addEventListener('touchstart', (e) => this.handleTouchStart(e), { passive: false });
    this.touchpad.addEventListener('touchmove', (e) => this.handleTouchMove(e), { passive: false });
    this.touchpad.addEventListener('touchend', (e) => this.handleTouchEnd(e), { passive: false });

    // Pointer Events for Desktop Mouse Testing
    let isMouseDown = false;
    this.touchpad.addEventListener('pointerdown', (e) => {
      if (e.pointerType === 'mouse') {
        isMouseDown = true;
        this.startX = e.clientX;
        this.startY = e.clientY;
        this.lastX = e.clientX;
        this.lastY = e.clientY;
        this.startTime = Date.now();
        this.hasMoved = false;
        this.touchpad.classList.add('touching');
      }
    });

    window.addEventListener('pointermove', (e) => {
      if (isMouseDown && e.pointerType === 'mouse') {
        const dx = Math.round((e.clientX - this.lastX) * this.sensitivity);
        const dy = Math.round((e.clientY - this.lastY) * this.sensitivity);

        if (Math.abs(dx) > 0 || Math.abs(dy) > 0) {
          this.hasMoved = true;
          window.remoteBridge.send(`MOVE ${dx} ${dy}`);
          this.lastX = e.clientX;
          this.lastY = e.clientY;
        }
      }
    });

    window.addEventListener('pointerup', (e) => {
      if (isMouseDown && e.pointerType === 'mouse') {
        isMouseDown = false;
        this.touchpad.classList.remove('touching');
        const elapsed = Date.now() - this.startTime;
        if (!this.hasMoved && elapsed < 250) {
          window.remoteBridge.send('CLICK left');
          window.showToast?.('Left Click');
        }
      }
    });

    // Keyboard Text Sender
    const textInput = document.getElementById('pcTextInput');
    const sendTextBtn = document.getElementById('pcSendTextBtn');

    if (sendTextBtn && textInput) {
      sendTextBtn.addEventListener('click', () => {
        const text = textInput.value.trim();
        if (text) {
          window.remoteBridge.send(`TEXT ${text}`);
          window.showToast?.(`Sent: "${text}"`);
          textInput.value = '';
        }
      });

      textInput.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
          sendTextBtn.click();
        }
      });
    }
  }

  handleTouchStart(e) {
    e.preventDefault();
    this.touchpad.classList.add('touching');
    this.startTime = Date.now();
    this.hasMoved = false;

    if (e.touches.length === 1) {
      this.twoFinger = false;
      this.startX = e.touches[0].clientX;
      this.startY = e.touches[0].clientY;
      this.lastX = this.startX;
      this.lastY = this.startY;
    } else if (e.touches.length === 2) {
      this.twoFinger = true;
      this.startY = (e.touches[0].clientY + e.touches[1].clientY) / 2;
      this.lastY = this.startY;
    }
  }

  handleTouchMove(e) {
    e.preventDefault();

    if (e.touches.length === 1 && !this.twoFinger) {
      const curX = e.touches[0].clientX;
      const curY = e.touches[0].clientY;
      const dx = Math.round((curX - this.lastX) * this.sensitivity);
      const dy = Math.round((curY - this.lastY) * this.sensitivity);

      if (Math.abs(dx) > 0 || Math.abs(dy) > 0) {
        this.hasMoved = true;
        window.remoteBridge.send(`MOVE ${dx} ${dy}`);
        this.lastX = curX;
        this.lastY = curY;
      }
    } else if (e.touches.length === 2) {
      // 2-Finger Scrolling
      const curY = (e.touches[0].clientY + e.touches[1].clientY) / 2;
      const dy = curY - this.lastY;

      if (Math.abs(dy) > 10) {
        this.hasMoved = true;
        const delta = dy > 0 ? 1 : -1;
        window.remoteBridge.send(`SCROLL ${delta}`);
        this.lastY = curY;
      }
    }
  }

  handleTouchEnd(e) {
    e.preventDefault();
    this.touchpad.classList.remove('touching');
    const elapsed = Date.now() - this.startTime;

    // Single Tap -> Left Click
    if (!this.hasMoved && elapsed < 250 && !this.twoFinger) {
      window.remoteBridge.send('CLICK left');
      window.showToast?.('Left Click');
    }

    // Two Finger Tap -> Right Click
    if (!this.hasMoved && elapsed < 300 && this.twoFinger) {
      window.remoteBridge.send('CLICK right');
      window.showToast?.('Right Click');
    }
  }

  click(btn) {
    window.remoteBridge.send(`CLICK ${btn}`);
    window.showToast?.(`${btn.toUpperCase()} Click`);
  }

  sendKey(keyName) {
    window.remoteBridge.send(`KEY ${keyName}`);
    window.showToast?.(`Key: ${keyName.toUpperCase()}`);
  }

  sendPpt(action) {
    window.remoteBridge.send(`PPT ${action}`);
    window.showToast?.(`PPT: ${action.toUpperCase()}`);
  }
}

window.pcRemote = new PcRemoteController();
