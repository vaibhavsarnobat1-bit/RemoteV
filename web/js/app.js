/**
 * SmartRemote Pro - Main Application Coordinator
 */
document.addEventListener('DOMContentLoaded', () => {
  // Initialize Connection Bridge
  window.remoteBridge.init();

  // Initialize Sub-Controllers
  window.pcRemote.init();
  window.airtelRemote.init();
  window.acRemote.init();
  window.voiceRemote.init();

  // Tab Navigation
  const tabPills = document.querySelectorAll('.tab-pill');
  const panels = document.querySelectorAll('.remote-panel');

  tabPills.forEach((pill) => {
    pill.addEventListener('click', () => {
      const target = pill.dataset.target;

      tabPills.forEach((p) => p.classList.remove('active'));
      panels.forEach((pan) => pan.classList.remove('active'));

      pill.classList.add('active');
      const activePanel = document.getElementById(target);
      if (activePanel) {
        activePanel.classList.add('active');
      }

      window.remoteBridge.playFeedbackSound('click');
      window.remoteBridge.triggerHaptic(15);
    });
  });

  // Elder Mode Toggle
  const elderModeToggleBtn = document.getElementById('elderModeToggleBtn');
  if (elderModeToggleBtn) {
    elderModeToggleBtn.addEventListener('click', () => {
      document.body.classList.toggle('elder-mode');
      const isElder = document.body.classList.contains('elder-mode');
      elderModeToggleBtn.classList.toggle('primary', isElder);
      window.showToast?.(isElder ? '👴 Elder Mode Enabled (Big Buttons)' : 'Standard Mode Enabled');
      window.remoteBridge.playFeedbackSound('success');
    });
  }

  // Voice Assistant Modal Triggers
  const voiceTriggerBtn = document.getElementById('voiceTriggerBtn');
  const voiceCloseBtn = document.getElementById('voiceCloseBtn');
  const voiceMicCircle = document.getElementById('voiceMicCircle');

  if (voiceTriggerBtn) {
    voiceTriggerBtn.addEventListener('click', () => window.voiceRemote.startListening());
  }
  if (voiceCloseBtn) {
    voiceCloseBtn.addEventListener('click', () => window.voiceRemote.closeModal());
  }
  if (voiceMicCircle) {
    voiceMicCircle.addEventListener('click', () => window.voiceRemote.toggleListening());
  }

  // Info / QR Modal Trigger
  const infoModalTrigger = document.getElementById('infoModalTrigger');
  const infoModal = document.getElementById('infoModal');
  const infoCloseBtn = document.getElementById('infoCloseBtn');

  if (infoModalTrigger && infoModal) {
    infoModalTrigger.addEventListener('click', () => {
      const localIpSpan = document.getElementById('localIpDisplay');
      if (localIpSpan) {
        const port = window.location.port ? `:${window.location.port}` : '';
        localIpSpan.textContent = `http://${window.location.hostname || 'localhost'}${port}`;
      }
      infoModal.classList.add('open');
    });
  }
  if (infoCloseBtn && infoModal) {
    infoCloseBtn.addEventListener('click', () => {
      infoModal.classList.remove('open');
    });
  }
});

// Global Toast System
window.showToast = function (message) {
  let toastContainer = document.getElementById('toastContainer');
  if (!toastContainer) {
    toastContainer = document.createElement('div');
    toastContainer.id = 'toastContainer';
    toastContainer.className = 'toast-container';
    document.body.appendChild(toastContainer);
  }

  const toast = document.createElement('div');
  toast.className = 'toast-msg';
  toast.textContent = message;
  toastContainer.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateY(-10px)';
    toast.style.transition = '0.3s ease';
    setTimeout(() => toast.remove(), 300);
  }, 2200);
};
