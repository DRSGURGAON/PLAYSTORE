// Renders Play Store graphics (512x512 icon, 1024x500 feature graphic) from HTML with Playwright.
// Usage: node tools/render-play-graphics.js play/direct-chat "App Name" "Tagline line 1" "Tagline line 2"
const { chromium } = require('playwright');
const path = require('path');
const [dir, name, line1, line2] = process.argv.slice(2);
const esc = (s) => String(s || '').replace(/[&<>"]/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;' }[c]));
// Same bubble + arrow as the launcher icon (108-unit viewport).
const glyph = (size, bubble, arrow) => `<svg width="${size}" height="${size}" viewBox="18 18 72 72" xmlns="http://www.w3.org/2000/svg">
  <path fill="${bubble}" d="M34,34h40a6,6 0 0 1 6,6v24a6,6 0 0 1 -6,6h-24l-10,9v-9h-6a6,6 0 0 1 -6,-6v-24a6,6 0 0 1 6,-6z"/>
  <path fill="none" stroke="${arrow}" stroke-width="5" stroke-linecap="round" stroke-linejoin="round" d="M42,52h22M56,44l8,8l-8,8"/></svg>`;
const font = `font-family: 'Noto Sans', 'Noto Sans Devanagari', system-ui, sans-serif;`;
(async () => {
  const browser = await chromium.launch();
  const page = await browser.newPage({ deviceScaleFactor: 1 });
  await page.setViewportSize({ width: 512, height: 512 });
  await page.setContent(`<body style="margin:0;width:512px;height:512px;background:#00796B;display:flex;align-items:center;justify-content:center">${glyph(400, '#fff', '#00796B')}</body>`);
  await page.screenshot({ path: path.join(dir, 'graphics/icon-512.png') });
  await page.setViewportSize({ width: 1024, height: 500 });
  await page.setContent(`<body style="margin:0;width:1024px;height:500px;background:linear-gradient(135deg,#004D40,#00897B);display:flex;align-items:center;${font}color:#fff">
    <div style="margin-left:90px;flex:none">${glyph(260, '#fff', '#00796B')}</div>
    <div style="margin-left:56px;margin-right:60px">
      <div style="font-size:60px;font-weight:700;line-height:1.1">${esc(name)}</div>
      <div style="font-size:34px;margin-top:22px;opacity:.95">${esc(line1)}</div>
      <div style="font-size:26px;margin-top:10px;opacity:.8">${esc(line2)}</div>
    </div></body>`);
  await page.screenshot({ path: path.join(dir, 'graphics/feature-graphic-1024x500.png') });
  await browser.close();
})();
