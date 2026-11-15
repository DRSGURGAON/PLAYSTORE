// Renders Play Store graphics (512x512 icon, 1024x500 feature graphic) with Playwright.
// Usage: node tools/render-play-graphics.js <play/app-dir> <icon module> "App Name" "Line 1" "Line 2"
//   e.g. node tools/render-play-graphics.js play/direct-chat ./icons/direct-chat.js "What'z Direct Chat" "बिना नंबर सेव किए चैट करें" "Chat without saving the number"
const { chromium } = require('playwright');
const path = require('path');
const [dir, iconModule, name, line1, line2] = process.argv.slice(2);
const art = require(path.resolve(__dirname, iconModule));
const esc = (s) => String(s || '').replace(/[&<>"]/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;' }[c]));
const font = `font-family: 'Noto Sans', 'Noto Sans Devanagari', system-ui, sans-serif;`;
(async () => {
  const browser = await chromium.launch();
  const page = await browser.newPage({ deviceScaleFactor: 1 });
  // Icon: full-bleed square; Google Play applies the rounded mask and shadow itself.
  await page.setViewportSize({ width: 512, height: 512 });
  await page.setContent(`<body style="margin:0">${art.icon(512)}</body>`);
  await page.screenshot({ path: path.join(dir, 'graphics/icon-512.png') });
  const [c1, c2] = art.background;
  await page.setViewportSize({ width: 1024, height: 500 });
  await page.setContent(`<body style="margin:0;width:1024px;height:500px;overflow:hidden;position:relative;background:linear-gradient(120deg,${c1},${c2});display:flex;align-items:center;${font}color:#fff">
    <div style="position:absolute;right:-120px;top:-160px;width:520px;height:520px;border-radius:50%;background:rgba(255,255,255,.08)"></div>
    <div style="position:absolute;left:-90px;bottom:-200px;width:420px;height:420px;border-radius:50%;background:rgba(255,255,255,.07)"></div>
    <div style="margin-left:80px;flex:none;filter:drop-shadow(0 18px 30px rgba(120,0,40,.35))">${art.glyphSvg(280)}</div>
    <div style="margin-left:48px;margin-right:56px;position:relative">
      <div style="font-size:62px;font-weight:800;line-height:1.05;letter-spacing:-.5px">${esc(name)}</div>
      <div style="font-size:36px;font-weight:600;margin-top:22px">${esc(line1)}</div>
      <div style="font-size:27px;margin-top:10px;opacity:.92">${esc(line2)}</div>
    </div></body>`);
  await page.screenshot({ path: path.join(dir, 'graphics/feature-graphic-1024x500.png') });
  await browser.close();
})();
