/* ==========================================================================
   SMARTREMOTE PRO - INTERACTIVE PROTOTYPE JAVASCRIPT LOGIC
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
  initClock();
  initNavigation();
  initThemeToggle();
  initDraggableFloatingWidget();
  initPcTouchpad();
});

// Update status bar clock
function initClock() {
  const clockEl = document.getElementById('status-clock');
  function updateTime() {
    const now = new Date();
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    if (clockEl) clockEl.textContent = `${hours}:${minutes}`;
  }
  updateTime();
  setInterval(updateTime, 10000);
}

// Navigation & Screen switching
function initNavigation() {
  const screenSelect = document.getElementById('screen-select');
  const ribbonButtons = document.querySelectorAll('.ribbon-btn');
  const viewTabs = document.querySelectorAll('.tab-btn');
  const prototypeView = document.getElementById('prototype-view');
  const tokensView = document.getElementById('tokens-view');

  // Screen dropdown change
  screenSelect.addEventListener('change', (e) => {
    navigateTo(e.target.value);
  });

  // Ribbon buttons
  ribbonButtons.forEach(btn => {
    btn.addEventListener('click', () => {
      const screenId = btn.getAttribute('data-screen');
      navigateTo(screenId);
    });
  });

  // View switch tabs: Prototype vs Design Tokens
  viewTabs.forEach(tab => {
    tab.addEventListener('click', () => {
      viewTabs.forEach(t => t.classList.remove('active'));
      tab.classList.add('active');

      if (tab.id === 'tab-prototype') {
        prototypeView.classList.add('active');
        tokensView.classList.remove('active');
      } else {
        prototypeView.classList.remove('active');
        tokensView.classList.add('active');
      }
    });
  });
}

// Core navigation function
window.navigateTo = function(screenId) {
  // Hide all screens
  const screens = document.querySelectorAll('.app-screen');
  screens.forEach(s => s.classList.remove('active'));

  // Show target screen
  const targetScreen = document.getElementById(`screen-${screenId}`);
  if (targetScreen) {
    targetScreen.classList.add('active');
  }

  // Update Ribbon buttons
  document.querySelectorAll('.ribbon-btn').forEach(btn => {
    btn.classList.toggle('active', btn.getAttribute('data-screen') === screenId);
  });

  // Update Select dropdown
  const select = document.getElementById('screen-select');
  if (select) {
    select.value = screenId;
  }
};

// Onboarding slider state
let currentSlideIndex = 0;

window.goToSlide = function(index) {
  const slides = document.querySelectorAll('.onboarding-slide');
  const dots = document.querySelectorAll('.dot');
  const backBtn = document.getElementById('onboard-back');
  const nextBtn = document.getElementById('onboard-next');

  slides.forEach((s, idx) => s.classList.toggle('active', idx === index));
  dots.forEach((d, idx) => d.classList.toggle('active', idx === index));
  currentSlideIndex = index;

  if (backBtn && nextBtn) {
    if (index === 0) {
      backBtn.style.display = 'none';
      nextBtn.textContent = 'Next';
      nextBtn.style.background = 'var(--color-electric-blue)';
    } else if (index === 1) {
      backBtn.style.display = 'block';
      nextBtn.textContent = 'Next';
      nextBtn.style.background = 'var(--color-electric-blue)';
    } else {
      backBtn.style.display = 'block';
      nextBtn.textContent = 'Get Started';
      nextBtn.style.background = 'var(--color-accent-orange)';
    }
  }
};

window.nextSlide = function() {
  if (currentSlideIndex < 2) {
    goToSlide(currentSlideIndex + 1);
  } else {
    navigateTo('home');
  }
};

window.prevSlide = function() {
  if (currentSlideIndex > 0) {
    goToSlide(currentSlideIndex - 1);
  }
};

// Remote interactions
window.updateVolume = function(val) {
  const disp = document.getElementById('volume-display');
  if (disp) {
    disp.textContent = `🔊 ${val}%`;
  }
};

window.dpadPress = function(dir) {
  haptic(`D-Pad ${dir} Pressed`);
};

window.pressNum = function(num) {
  haptic(`Key ${num}`);
};

window.triggerPowerToggle = function(btn) {
  const isOff = btn.style.background === 'rgb(51, 65, 85)';
  if (isOff) {
    btn.style.background = 'var(--color-error)';
    haptic('Device Turned ON 🟢');
  } else {
    btn.style.background = '#334155';
    haptic('Device Turned OFF 🔴');
  }
};

window.toggleMute = function(btn) {
  const isMuted = btn.classList.toggle('muted');
  if (isMuted) {
    btn.style.borderColor = 'var(--color-accent-orange)';
    btn.style.color = 'var(--color-accent-orange)';
    haptic('Muted 🔇');
  } else {
    btn.style.borderColor = 'var(--border)';
    btn.style.color = 'var(--text-primary)';
    haptic('Unmuted 🔊');
  }
};

window.toggleAcState = function(e, btn) {
  e.stopPropagation();
  const card = btn.closest('.device-card');
  const dot = card.querySelector('.status-dot');
  const desc = card.querySelector('.device-desc');

  if (dot.classList.contains('offline')) {
    dot.classList.remove('offline');
    dot.classList.add('online');
    desc.textContent = 'Cooling at 24°C';
    btn.textContent = 'Control Now →';
    btn.classList.remove('btn-turn-on');
    haptic('AC Turned ON at 24°C ❄️');
  } else {
    dot.classList.remove('online');
    dot.classList.add('offline');
    desc.textContent = 'Currently OFF';
    btn.textContent = 'Turn On →';
    btn.classList.add('btn-turn-on');
    haptic('AC Turned OFF');
  }
};

// Voice Simulation
let isListening = true;

window.toggleVoiceListening = function() {
  const wave = document.getElementById('mic-wave');
  const stateLabel = document.getElementById('mic-state-text');
  isListening = !isListening;

  if (isListening) {
    stateLabel.textContent = 'Listening';
    stateLabel.style.color = 'var(--color-electric-blue)';
    if (wave) wave.style.display = 'block';
    haptic('Listening for Hindi / English speech...');
  } else {
    stateLabel.textContent = 'Paused';
    stateLabel.style.color = 'var(--text-secondary)';
    if (wave) wave.style.display = 'none';
    haptic('Microphone paused');
  }
};

window.simulateVoice = function(commandText, feedbackText) {
  const detected = document.getElementById('detected-speech');
  const feedback = document.getElementById('speech-feedback');

  if (detected) detected.textContent = `"${commandText}"`;
  if (feedback) {
    feedback.textContent = `✅ ${feedbackText}`;
    feedback.style.display = 'inline-block';
  }
  haptic(`Voice Command: "${commandText}"`);
};

// Macro actions
window.removeAction = function(btn) {
  const card = btn.closest('.action-card');
  if (card) {
    card.style.opacity = '0';
    setTimeout(() => card.remove(), 200);
    haptic('Action removed');
  }
};

window.addNewAction = function() {
  const input = prompt('Enter Action Name (e.g. Set AC to 22°C):', 'Turn on Soundbar');
  if (input) {
    const list = document.querySelector('#screen-macro .screen-scrollable');
    const newCard = document.createElement('div');
    newCard.className = 'action-card';
    newCard.innerHTML = `
      <div class="action-card-header">
        <span class="action-num">+</span>
        <span class="action-name">⚡ ${input}</span>
      </div>
      <div class="action-device">Device: Smart Device</div>
      <div class="action-btn-row">
        <button class="text-btn">Edit</button>
        <button class="text-btn danger" onclick="removeAction(this)">Remove</button>
      </div>
    `;
    const addBtn = document.querySelector('.btn-add-action');
    addBtn.parentNode.insertBefore(newCard, addBtn);
    haptic('Action added to macro sequence');
  }
};

window.selectEmoji = function(btn) {
  document.querySelectorAll('.emoji-chip').forEach(c => c.classList.remove('selected'));
  btn.classList.add('selected');
  haptic(`Icon selected: ${btn.textContent}`);
};

window.saveMacroNotice = function() {
  const name = document.getElementById('macro-name-input').value;
  alert(`Macro "${name}" saved successfully with 3 action steps and voice shortcut!`);
  navigateTo('home');
};

// Floating Widget
window.toggleFloatingExpanded = function(e) {
  if (e) e.stopPropagation();
  const card = document.getElementById('floating-expanded-card');
  if (card) {
    const isVisible = card.style.display === 'flex';
    card.style.display = isVisible ? 'none' : 'flex';
  }
};

function initDraggableFloatingWidget() {
  const bubble = document.getElementById('floating-bubble');
  const container = document.getElementById('floating-canvas');
  if (!bubble || !container) return;

  let isDragging = false;
  let startX, startY, initialLeft, initialTop;

  bubble.addEventListener('mousedown', (e) => {
    isDragging = true;
    startX = e.clientX;
    startY = e.clientY;
    initialLeft = bubble.offsetLeft;
    initialTop = bubble.offsetTop;
    bubble.style.transition = 'none';
  });

  window.addEventListener('mousemove', (e) => {
    if (!isDragging) return;
    const dx = e.clientX - startX;
    const dy = e.clientY - startY;
    bubble.style.left = `${initialLeft + dx}px`;
    bubble.style.top = `${initialTop + dy}px`;
    bubble.style.right = 'auto';
  });

  window.addEventListener('mouseup', () => {
    isDragging = false;
    bubble.style.transition = 'transform 0.15s ease';
  });
}

// Theme Switcher
function initThemeToggle() {
  const toggleBtn = document.getElementById('theme-toggle-btn');
  if (toggleBtn) {
    toggleBtn.addEventListener('click', toggleAppTheme);
  }
}

window.toggleAppTheme = function() {
  const body = document.body;
  const isDark = body.classList.contains('theme-dark');
  const label = document.getElementById('settings-theme-label');

  if (isDark) {
    body.classList.remove('theme-dark');
    body.classList.add('theme-light');
    if (label) label.textContent = 'Light';
    haptic('Switched to Light Theme');
  } else {
    body.classList.remove('theme-light');
    body.classList.add('theme-dark');
    if (label) label.textContent = 'Dark';
    haptic('Switched to Dark Theme');
  }
};

// Accessibility: High Contrast Toggle
window.toggleHighContrast = function() {
  const body = document.body;
  const isContrast = body.classList.toggle('theme-contrast');
  const btn = document.getElementById('contrast-toggle-btn');
  if (btn) btn.classList.toggle('active', isContrast);
  haptic(isContrast ? 'High Contrast Mode ON (WCAG AAA)' : 'High Contrast Mode OFF');
};

// Accessibility: Large Text Toggle
window.toggleLargeText = function() {
  const body = document.body;
  const isLarge = body.classList.toggle('large-text');
  const btn = document.getElementById('large-text-btn');
  if (btn) btn.classList.toggle('active', isLarge);
  haptic(isLarge ? 'Large Text Mode ON (18sp+)' : 'Large Text Mode OFF');
};

// Indian Localization: Bilingual Toggle (Hindi / English)
let currentLanguage = 'en';

const BILINGUAL_DICT = {
  en: {
    appTitle: 'SmartRemote Pro',
    langBadge: 'EN / हिं',
    searchPlaceholder: '"Say a command..."',
    quickActions: 'Quick Actions:',
    myDevices: 'My Devices',
    livingRoomTv: 'Living Room TV',
    samsungTvDesc: 'Samsung Smart TV',
    lastUsed5m: 'Last used: 5 min ago',
    controlNow: 'Control Now →',
    airtelTitle: 'Airtel Xstream',
    airtelDesc: 'Channel: Star Plus (100)',
    bedroomAc: 'Bedroom AC',
    currentlyOff: 'Currently OFF',
    turnOn: 'Turn On →',
    addNewDevice: '➕ Add New Device',
    volLabel: 'Volume:',
    muteBtn: '🔇 Mute',
    sourceBtn: '⚙️ Source',
    appsLabel: 'Apps:',
    macrosLabel: '💬 Macros',
    timerLabel: '⏰ Timer',
    voiceTitle: '🎤 Voice Control',
    micListening: 'Listening',
    micPaused: 'Paused',
    detectedVoice: '"TV chalu karo"',
    feedbackVoice: '✅ TV Turned On',
    quickVoiceTitle: 'Quick Voice Commands:',
    myShortcutsTitle: 'My Shortcuts:',
    createShortcut: '+ Create New Shortcut',
    createMacroTitle: 'Create Macro',
    macroNameLabel: 'Macro Name:',
    actionsSequenceLabel: 'Actions Sequence:',
    voiceTriggerLabel: 'Voice Trigger (Optional):',
    iconLabel: 'Icon:',
    saveMacro: 'Save Macro',
    cancel: 'Cancel',
    starPlusCurrent: '📺 Star Plus (100)',
    yehRishta: 'Yeh Rishta Kya Kehlata Hai',
    favLabel: 'Favorites ⭐',
    rechargeInfo: 'Recharge Info:',
    rechargeBal: 'Balance: ₹250.00',
    rechargeDate: 'Valid till: 15 Jan 2025',
    rechargeNow: 'Recharge Now',
    settingsTitle: 'Settings'
  },
  hi: {
    appTitle: 'स्मार्ट रिमोट प्रो',
    langBadge: 'हिन्दी / EN',
    searchPlaceholder: '"कमांड बोलें... (जैसे टीवी चालू करो)"',
    quickActions: 'त्वरित क्रियाएं:',
    myDevices: 'मेरे उपकरण',
    livingRoomTv: 'लिविंग रूम टीवी',
    samsungTvDesc: 'सैमसंग स्मार्ट टीवी',
    lastUsed5m: '5 मिनट पहले उपयोग किया गया',
    controlNow: 'कंट्रोल करें →',
    airtelTitle: 'एयरटेल एक्सट्रीम',
    airtelDesc: 'चैनल: स्टार प्लस (100)',
    bedroomAc: 'बेडरूम एसी',
    currentlyOff: 'वर्तमान में बंद',
    turnOn: 'चालू करें →',
    addNewDevice: '➕ नया उपकरण जोड़ें',
    volLabel: 'आवाज़ (वॉल्यूम):',
    muteBtn: '🔇 म्यूट',
    sourceBtn: '⚙️ सोर्स',
    appsLabel: 'ऐप्स:',
    macrosLabel: '💬 मैक्रोज़',
    timerLabel: '⏰ टाइमर',
    voiceTitle: '🎤 वॉयस कंट्रोल',
    micListening: 'सुन रहे हैं...',
    micPaused: 'रोका गया',
    detectedVoice: '"टीवी चालू करो"',
    feedbackVoice: '✅ टीवी चालू हो गया',
    quickVoiceTitle: 'त्वरित वॉयस कमांड:',
    myShortcutsTitle: 'मेरे शॉर्टकट:',
    createShortcut: '+ नया शॉर्टकट बनाएं',
    createMacroTitle: 'मैक्रो बनाएं',
    macroNameLabel: 'मैक्रो का नाम:',
    actionsSequenceLabel: 'कार्रवाई क्रम:',
    voiceTriggerLabel: 'वॉयस ट्रिगर (वैकल्पिक):',
    iconLabel: 'आइकन:',
    saveMacro: 'मैक्रो सहेजें',
    cancel: 'रद्द करें',
    starPlusCurrent: '📺 स्टार प्लस (100)',
    yehRishta: 'ये रिश्ता क्या कहलाता है',
    favLabel: 'पसंदीदा चैनल ⭐',
    rechargeInfo: 'रिचार्ज जानकारी:',
    rechargeBal: 'बैलेंस: ₹250.00',
    rechargeDate: 'वैधता: 15 जनवरी 2025',
    rechargeNow: 'अभी रिचार्ज करें',
    settingsTitle: 'सेटिंग्स'
  }
};

window.toggleLanguage = function() {
  currentLanguage = (currentLanguage === 'en') ? 'hi' : 'en';
  const dict = BILINGUAL_DICT[currentLanguage];

  // Update Status bar badge & top bar
  const langBadges = document.querySelectorAll('.status-badge-lang, #lang-toggle-btn');
  langBadges.forEach(b => {
    if (b.id === 'lang-toggle-btn') {
      b.textContent = currentLanguage === 'en' ? '🇮🇳 EN / हिं' : '🇮🇳 हिन्दी / EN';
    } else {
      b.textContent = dict.langBadge;
    }
  });

  // Home screen translations
  const searchPlaceholder = document.querySelector('.search-placeholder');
  if (searchPlaceholder) searchPlaceholder.textContent = dict.searchPlaceholder;

  const sectionTitles = document.querySelectorAll('.section-title');
  if (sectionTitles[0]) sectionTitles[0].textContent = dict.quickActions;
  if (sectionTitles[1]) sectionTitles[1].textContent = dict.myDevices;

  const livingTvName = document.querySelector('.device-card .device-name');
  if (livingTvName) livingTvName.textContent = dict.livingRoomTv;

  const fabAdd = document.querySelector('.fab-add span:last-child');
  if (fabAdd) fabAdd.textContent = dict.addNewDevice;

  // Voice screen translations
  const detected = document.getElementById('detected-speech');
  if (detected) detected.textContent = dict.detectedVoice;
  const feedback = document.getElementById('speech-feedback');
  if (feedback) feedback.textContent = dict.feedbackVoice;

  haptic(currentLanguage === 'hi' ? 'भाषा बदली: हिन्दी' : 'Language switched: English');
};

// Web Audio API Synthesizer for Tactile Click
let audioCtx = null;
function playTactileClick() {
  try {
    if (!audioCtx) {
      audioCtx = new (window.AudioContext || window.webkitAudioContext)();
    }
    if (audioCtx.state === 'suspended') {
      audioCtx.resume();
    }
    const osc = audioCtx.createOscillator();
    const gain = audioCtx.createGain();
    osc.type = 'sine';
    osc.frequency.setValueAtTime(380, audioCtx.currentTime);
    osc.frequency.exponentialRampToValueAtTime(120, audioCtx.currentTime + 0.02);
    gain.gain.setValueAtTime(0.08, audioCtx.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.001, audioCtx.currentTime + 0.02);
    osc.connect(gain);
    gain.connect(audioCtx.destination);
    osc.start();
    osc.stop(audioCtx.currentTime + 0.02);
  } catch (e) {}

  if (navigator.vibrate) {
    try { navigator.vibrate(12); } catch (e) {}
  }
}

// Haptic & Toast Feedback with Sound
window.haptic = function(actionName) {
  playTactileClick();
  const toast = document.getElementById('toast-banner');
  if (!toast) return;

  toast.textContent = actionName;
  toast.classList.add('show');

  clearTimeout(window.toastTimer);
  window.toastTimer = setTimeout(() => {
    toast.classList.remove('show');
  }, 1600);
};

// Airtel DTH Controls
window.tuneAirtelChannel = function(num, name, show, time) {
  const curCh = document.querySelector('.cur-channel-title');
  const curProg = document.querySelector('.cur-program-title');
  const curTime = document.querySelector('.cur-program-time');
  if (curCh) curCh.textContent = `📺 ${name} (${num})`;
  if (curProg) curProg.textContent = show || 'Popular Show';
  if (curTime) curTime.textContent = time || '8:00 PM - 9:00 PM';
  haptic(`Tuned to ${num} ${name}`);
};

window.pressColorKey = function(color) {
  const actions = {
    red: '🔴 Red Key: Interactive Services & EPG',
    green: '🟢 Green Key: Search & Voice Guide',
    yellow: '🟡 Yellow Key: Language & Subtitles',
    blue: '🔵 Blue Key: Favorites List'
  };
  haptic(actions[color] || `${color} key pressed`);
};

// Animation Playground Tester
window.testAnimation = function(type) {
  if (type === 'screen') {
    const activeScreen = document.querySelector('.app-screen.active');
    if (activeScreen) {
      activeScreen.style.animation = 'none';
      void activeScreen.offsetWidth;
      activeScreen.style.animation = 'screenSlideFade 300ms cubic-bezier(0.16, 1, 0.3, 1)';
      haptic('Screen Transition: 300ms slide + fade');
    }
  } else if (type === 'button') {
    const demoBtn = document.getElementById('demo-anim-btn');
    if (demoBtn) {
      demoBtn.style.transform = 'scale(0.95)';
      setTimeout(() => { demoBtn.style.transform = 'scale(1)'; }, 100);
      haptic('Button Press: Scale 0.95 with 100ms');
    }
  } else if (type === 'voice') {
    const wave = document.getElementById('demo-voice-wave');
    if (wave) {
      wave.classList.add('mic-wave-pulse');
      setTimeout(() => wave.classList.remove('mic-wave-pulse'), 3600);
      haptic('Voice Animation: Pulsing wave effect');
    }
  } else if (type === 'shimmer') {
    const card = document.getElementById('demo-shimmer-card');
    if (card) {
      card.classList.add('loading-shimmer');
      setTimeout(() => card.classList.remove('loading-shimmer'), 2500);
      haptic('Loading: Shimmer effect on cards');
    }
  } else if (type === 'success') {
    const check = document.getElementById('demo-success-badge');
    if (check) {
      check.classList.remove('success-scale-active');
      void check.offsetWidth;
      check.classList.add('success-scale-active');
      haptic('Success Feedback: Checkmark with scale up');
    }
  } else if (type === 'error') {
    const box = document.getElementById('demo-error-box');
    if (box) {
      box.classList.remove('error-shake-active');
      void box.offsetWidth;
      box.classList.add('error-shake-active');
      haptic('Error Shake: Horizontal shake 3 times');
    }
  }
};

// Copy Icon SVG Helper
window.copyIconSvg = function(iconName) {
  const iconMap = {
    power: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M12 2v10M18.4 6.6a9 9 0 11-12.8 0"/></svg>',
    dpad: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="9"/><path d="M12 8v8M8 12h8"/></svg>',
    mic: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="9" y="2" width="6" height="12" rx="3"/><path d="M5 10a7 7 0 0014 0M12 19v3M8 22h8"/></svg>',
    tv: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M8 3l4 4 4-4"/></svg>',
    volume: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"/><path d="M15.54 8.46a5 5 0 010 7.07M19.07 4.93a10 10 0 010 14.14"/></svg>',
    mute: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"/><line x1="23" y1="9" x2="17" y2="15"/><line x1="17" y1="9" x2="23" y2="15"/></svg>'
  };
  const svg = iconMap[iconName] || '<svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"/></svg>';
  navigator.clipboard.writeText(svg).then(() => {
    haptic(`Copied SVG for ${iconName} icon!`);
  });
};

// Export Figma Tokens JSON
window.exportFigmaTokens = function() {
  const tokens = {
    version: '1.0.0',
    name: 'SmartRemote Pro Design Tokens',
    target: 'Android MD3 (Indian Users)',
    colors: {
      primary: {
        deepBlue: { value: '#1E3A8A', type: 'color', description: 'Trust, technology' },
        electricBlue: { value: '#3B82F6', type: 'color', description: 'Interactive elements' },
        accentOrange: { value: '#F97316', type: 'color', description: 'CTAs, Pro badges' }
      },
      neutral: {
        bgDark: { value: '#0F172A', type: 'color' },
        bgLight: { value: '#F8FAFC', type: 'color' },
        surfaceDark: { value: '#1E293B', type: 'color' },
        surfaceLight: { value: '#FFFFFF', type: 'color' },
        textPrimaryDark: { value: '#F1F5F9', type: 'color' },
        textPrimaryLight: { value: '#0F172A', type: 'color' },
        textSecondary: { value: '#94A3B8', type: 'color' }
      },
      semantic: {
        success: { value: '#10B981', type: 'color' },
        error: { value: '#EF4444', type: 'color' },
        warning: { value: '#F59E0B', type: 'color' },
        info: { value: '#06B6D4', type: 'color' }
      }
    },
    typography: {
      fontFamily: 'Inter',
      h1: { fontSize: '28sp', fontWeight: 600, lineHeight: '36sp' },
      h2: { fontSize: '24sp', fontWeight: 600, lineHeight: '32sp' },
      h3: { fontSize: '20sp', fontWeight: 600, lineHeight: '28sp' },
      body: { fontSize: '16sp', fontWeight: 400, lineHeight: '24sp' },
      caption: { fontSize: '14sp', fontWeight: 400, lineHeight: '20sp' },
      small: { fontSize: '12sp', fontWeight: 400, lineHeight: '16sp' }
    },
    spacing: {
      grid: '4dp',
      xs: '4dp',
      sm: '8dp',
      md: '12dp',
      lg: '16dp',
      xl: '24dp',
      xxl: '32dp'
    },
    elevation: {
      level1: '0 2px 8px rgba(0,0,0,0.3)',
      level2: '0 6px 16px rgba(0,0,0,0.4)',
      level3: '0 12px 32px rgba(0,0,0,0.6)'
    },
    motion: {
      screenTransition: { duration: '300ms', easing: 'cubic-bezier(0.16, 1, 0.3, 1)' },
      buttonPress: { scale: 0.95, duration: '100ms' },
      errorShake: { duration: '320ms', count: 3 }
    }
  };

  const jsonStr = JSON.stringify(tokens, null, 2);
  const blob = new Blob([jsonStr], { type: 'application/json' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = 'smartremote_figma_tokens.json';
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
  haptic('Figma Tokens JSON downloaded!');
};

// Copy Design Token
window.copyToken = function(hex) {
  navigator.clipboard.writeText(hex).then(() => {
    haptic(`Copied ${hex} to clipboard!`);
  }).catch(() => {
    haptic(`Color: ${hex}`);
  });
};

/* ==========================================================================
   11. PC / LAPTOP WIRELESS MOUSE & TOUCHPAD CONTROLLER LOGIC
   ========================================================================== */

let pcConnectionMode = 'wifi'; // 'wifi' or 'bt'
let pcCursorX = 960;
let pcCursorY = 540;

// Mode Switching: Touchpad, Air Mouse, PPT Clicker
window.switchPcMode = function(mode) {
  const tabs = document.querySelectorAll('.pcmouse-tab');
  tabs.forEach(t => t.classList.remove('active'));
  
  const targetTab = document.getElementById(`tab-mode-${mode}`);
  if (targetTab) targetTab.classList.add('active');

  const views = document.querySelectorAll('.pcmouse-view-content');
  views.forEach(v => v.classList.remove('active'));

  const targetView = document.getElementById(`view-${mode}-content`);
  if (targetView) targetView.classList.add('active');

  haptic(mode === 'touchpad' ? 'Touchpad Mouse Mode' : mode === 'air' ? 'Gyro Air Mouse Mode' : 'PPT Presentation Mode');
};

// Toggle Connection (WiFi / Bluetooth HID)
window.togglePcConnection = function() {
  const btn = document.getElementById('btn-pc-conn');
  const subtitle = document.querySelector('#screen-pcmouse .remote-subtitle');
  
  if (pcConnectionMode === 'wifi') {
    pcConnectionMode = 'bt';
    if (btn) btn.textContent = '🦷';
    if (subtitle) subtitle.textContent = 'Bluetooth HID: Paired (Direct Mouse)';
    haptic('Switched to Bluetooth HID Direct Mode (No PC software needed)');
  } else {
    pcConnectionMode = 'wifi';
    if (btn) btn.textContent = '📶';
    if (subtitle) subtitle.textContent = 'WiFi: 192.168.1.10 (3ms latency)';
    haptic('Switched to High-Speed WiFi UDP Mode (< 3ms)');
  }
};

// Touchpad & Scroll Strip Event Handlers
function initPcTouchpad() {
  const pad = document.getElementById('touchpad-surface');
  const scrollStrip = document.getElementById('touchpad-scroll-strip');
  const cursorDot = document.getElementById('virtual-cursor');
  const coordsLabel = document.getElementById('cursor-coords');
  const ripple = document.getElementById('touch-ripple');

  if (!pad) return;

  let isTouching = false;
  let lastX = 0;
  let lastY = 0;
  let touchStartTime = 0;
  let totalMoveDistance = 0;

  // Pointer Down on Touchpad
  pad.addEventListener('pointerdown', (e) => {
    isTouching = true;
    lastX = e.clientX;
    lastY = e.clientY;
    touchStartTime = Date.now();
    totalMoveDistance = 0;
    pad.setPointerCapture(e.pointerId);

    // Show ripple
    if (ripple) {
      const rect = pad.getBoundingClientRect();
      ripple.style.left = `${e.clientX - rect.left}px`;
      ripple.style.top = `${e.clientY - rect.top}px`;
      ripple.style.transform = 'translate(-50%, -50%) scale(1.5)';
      ripple.style.opacity = '1';
    }
  });

  // Pointer Move on Touchpad
  pad.addEventListener('pointermove', (e) => {
    if (!isTouching) return;

    const dx = (e.clientX - lastX) * 2.2;
    const dy = (e.clientY - lastY) * 2.2;
    lastX = e.clientX;
    lastY = e.clientY;
    totalMoveDistance += Math.abs(dx) + Math.abs(dy);

    // Update virtual coordinates (1920x1080 bounds)
    pcCursorX = Math.max(0, Math.min(1920, pcCursorX + Math.round(dx)));
    pcCursorY = Math.max(0, Math.min(1080, pcCursorY + Math.round(dy)));

    // Update coords label
    if (coordsLabel) {
      coordsLabel.textContent = `X: ${pcCursorX}, Y: ${pcCursorY}`;
    }

    // Update mini monitor dot position
    if (cursorDot) {
      const pctX = (pcCursorX / 1920) * 100;
      const pctY = (pcCursorY / 1080) * 100;
      cursorDot.style.left = `${pctX}%`;
      cursorDot.style.top = `${pctY}%`;
    }
  });

  // Pointer Up on Touchpad
  pad.addEventListener('pointerup', (e) => {
    if (!isTouching) return;
    isTouching = false;
    pad.releasePointerCapture(e.pointerId);

    if (ripple) {
      ripple.style.transform = 'translate(-50%, -50%) scale(0)';
      ripple.style.opacity = '0';
    }

    // Tap detection (< 220ms and moved < 10px = Left Click)
    const duration = Date.now() - touchStartTime;
    if (duration < 220 && totalMoveDistance < 10) {
      simulatePcClick('left');
    }
  });

  // Scroll Strip Events
  if (scrollStrip) {
    let isScrolling = false;
    let scrollLastY = 0;

    scrollStrip.addEventListener('pointerdown', (e) => {
      isScrolling = true;
      scrollLastY = e.clientY;
      scrollStrip.setPointerCapture(e.pointerId);
      haptic('Scrolling');
    });

    scrollStrip.addEventListener('pointermove', (e) => {
      if (!isScrolling) return;
      const dy = e.clientY - scrollLastY;
      if (Math.abs(dy) > 12) {
        scrollLastY = e.clientY;
        if (dy < 0) {
          haptic('Scroll Up ⇡');
        } else {
          haptic('Scroll Down ⇣');
        }
      }
    });

    scrollStrip.addEventListener('pointerup', (e) => {
      isScrolling = false;
      scrollStrip.releasePointerCapture(e.pointerId);
    });
  }
}

// Mouse Clicks Simulation
window.simulatePcClick = function(type) {
  if (type === 'left') {
    haptic('🖱️ Left Click [TAP]');
  } else if (type === 'right') {
    haptic('🖱️ Right Click [MENU]');
  } else if (type === 'middle') {
    haptic('⚙️ Middle Click');
  }
};

// PC Shortcut Keys Simulation
window.simulatePcKey = function(key) {
  const keyNames = {
    win: '⊞ Windows Key Pressed',
    alt_tab: 'Alt + Tab App Switcher',
    ctrl_c: 'Ctrl + C (Copied)',
    ctrl_v: 'Ctrl + V (Pasted)',
    esc: 'Escape Key',
    enter: 'Enter ↵'
  };
  haptic(keyNames[key] || `Key: ${key}`);
};

// Direct Keyboard Input Simulation
window.sendTypedKeys = function() {
  const input = document.getElementById('pc-kb-input');
  if (input && input.value.trim() !== '') {
    haptic(`Typed to PC: "${input.value}"`);
    input.value = '';
  }
};

window.handlePcKeyInput = function(e) {
  if (e.key === 'Enter') {
    sendTypedKeys();
  }
};

// PPT Presentation Clicker Simulation
window.simulatePptNav = function(action) {
  const actions = {
    prev: '◀ Previous Slide (Page Up)',
    next: 'Next Slide ▶ (Page Down)',
    f5: '▶️ Fullscreen Presentation Started (F5)',
    black: '⬛ Screen Blacked Out (B key)',
    laser: '🔴 Laser Pointer Toggled',
    esc: '⏹️ Presentation Exited (Esc)'
  };
  haptic(actions[action] || `PPT Action: ${action}`);
};

// Register Service Worker for PWA (Free Mobile App Installation)
if ('serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    navigator.serviceWorker.register('./sw.js')
      .then(reg => console.log('SmartRemote Pro PWA Service Worker Registered:', reg.scope))
      .catch(err => console.log('Service Worker registration failed:', err));
  });
}
