// What'z Direct Chat icon artwork (108-unit adaptive-icon grid). Keep in sync with
// apps/direct-chat/src/main/res/drawable/ic_launcher_*.xml.
const BUBBLE = 'M28,29h52a10,10 0 0 1 10,10v30a10,10 0 0 1 -10,10h-27l-14,12v-12h-11a10,10 0 0 1 -10,-10v-30a10,10 0 0 1 10,-10z';
const BOLT = 'M60,33 L40,58 H52.5 L47.5,75 L69,48.5 H56 Z';
module.exports = {
  background: ['#FF8A00', '#FF2E63'],
  // Full icon (background + glyph) as SVG markup, any pixel size.
  icon(size, id = 'i') {
    return `<svg width="${size}" height="${size}" viewBox="0 0 108 108" xmlns="http://www.w3.org/2000/svg"><defs>
<linearGradient id="bg${id}" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="#FF8A00"/><stop offset="1" stop-color="#FF2E63"/></linearGradient>
<linearGradient id="bo${id}" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#FF7A00"/><stop offset="1" stop-color="#E8175D"/></linearGradient></defs>
<rect width="108" height="108" fill="url(#bg${id})"/>${this.glyph(id)}</svg>`;
  },
  // Glyph only (bubble + bolt), for placing on a gradient (feature graphic).
  glyph(id = 'i') {
    return `<path fill="#fff" d="${BUBBLE}"/><path fill="url(#bo${id})" d="${BOLT}"/>`;
  },
  glyphSvg(size, id = 'g') {
    return `<svg width="${size}" height="${size}" viewBox="18 18 72 72" xmlns="http://www.w3.org/2000/svg"><defs>
<linearGradient id="bo${id}" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#FF7A00"/><stop offset="1" stop-color="#E8175D"/></linearGradient></defs>${this.glyph(id)}</svg>`;
  },
};
