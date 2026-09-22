/**
 * SmartRemote Pro - Airtel Xstream DTH Remote & EPG Channel Guide
 */
class AirtelRemoteController {
  constructor() {
    this.channels = [
      // Entertainment
      { num: 105, name: 'Star Plus HD', cat: 'entertainment' },
      { num: 110, name: 'Sony SET HD', cat: 'entertainment' },
      { num: 115, name: 'Zee TV HD', cat: 'entertainment' },
      { num: 120, name: 'Colors HD', cat: 'entertainment' },
      { num: 125, name: 'Sony SAB HD', cat: 'entertainment' },
      { num: 130, name: '&TV HD', cat: 'entertainment' },
      // Sports
      { num: 277, name: 'Star Sports 1 HD', cat: 'sports' },
      { num: 283, name: 'Star Sports Select 1', cat: 'sports' },
      { num: 290, name: 'Sony Sports Ten 1 HD', cat: 'sports' },
      { num: 295, name: 'Sports18 1 HD', cat: 'sports' },
      { num: 298, name: 'DD Sports', cat: 'sports' },
      // Movies
      { num: 310, name: 'Star Gold HD', cat: 'movies' },
      { num: 315, name: 'Sony MAX HD', cat: 'movies' },
      { num: 320, name: 'Zee Cinema HD', cat: 'movies' },
      { num: 325, name: '&pictures HD', cat: 'movies' },
      { num: 340, name: 'Star Movies HD', cat: 'movies' },
      // News
      { num: 380, name: 'Aaj Tak HD', cat: 'news' },
      { num: 385, name: 'India Today', cat: 'news' },
      { num: 390, name: 'NDTV 24x7', cat: 'news' },
      { num: 395, name: 'CNN-News18', cat: 'news' },
      { num: 400, name: 'ABP News', cat: 'news' },
      // Kids
      { num: 450, name: 'Cartoon Network HD', cat: 'kids' },
      { num: 455, name: 'Disney Channel', cat: 'kids' },
      { num: 460, name: 'Pogo', cat: 'kids' },
      { num: 465, name: 'Nickelodeon HD+', cat: 'kids' },
      // Regional
      { num: 601, name: 'Star Jalsha HD', cat: 'regional' },
      { num: 605, name: 'Zee Bangla HD', cat: 'regional' },
      { num: 701, name: 'Sun TV HD', cat: 'regional' },
      { num: 750, name: 'Asianet HD', cat: 'regional' }
    ];

    this.activeCategory = 'all';
    this.searchQuery = '';
  }

  init() {
    this.renderEpgList();

    const searchInput = document.getElementById('epgSearchInput');
    if (searchInput) {
      searchInput.addEventListener('input', (e) => {
        this.searchQuery = e.target.value.toLowerCase().trim();
        this.renderEpgList();
      });
    }

    const catChips = document.querySelectorAll('.epg-cat-chip');
    catChips.forEach((chip) => {
      chip.addEventListener('click', () => {
        catChips.forEach((c) => c.classList.remove('active'));
        chip.classList.add('active');
        this.activeCategory = chip.dataset.cat;
        this.renderEpgList();
      });
    });
  }

  renderEpgList() {
    const listEl = document.getElementById('epgList');
    if (!listEl) return;

    const filtered = this.channels.filter((ch) => {
      const matchCat = this.activeCategory === 'all' || ch.cat === this.activeCategory;
      const matchQuery =
        !this.searchQuery ||
        ch.name.toLowerCase().includes(this.searchQuery) ||
        ch.num.toString().includes(this.searchQuery);
      return matchCat && matchQuery;
    });

    listEl.innerHTML = filtered
      .map(
        (ch) => `
      <div class="epg-item" onclick="window.airtelRemote.tuneChannel(${ch.num}, '${ch.name}')">
        <span class="epg-ch-num">${ch.num}</span>
        <span class="epg-ch-name">${ch.name}</span>
        <span class="status-pill" style="font-size:0.7rem; padding: 2px 8px;">Tune</span>
      </div>
    `
      )
      .join('');
  }

  tuneChannel(num, name) {
    window.remoteBridge.send(`AIRTEL_TUNE ${num}`);
    window.showToast?.(`Tuned to ${num} - ${name}`);
  }

  sendDigit(digit) {
    window.remoteBridge.send(`AIRTEL_DIGIT ${digit}`);
    window.showToast?.(`Key: ${digit}`);
  }

  sendColorKey(color) {
    window.remoteBridge.send(`AIRTEL_COLOR ${color.toUpperCase()}`);
    window.showToast?.(`Airtel: ${color.toUpperCase()} Key`);
  }

  sendAction(action) {
    window.remoteBridge.send(`AIRTEL_ACTION ${action.toUpperCase()}`);
    window.showToast?.(`Airtel: ${action.toUpperCase()}`);
  }
}

window.airtelRemote = new AirtelRemoteController();
