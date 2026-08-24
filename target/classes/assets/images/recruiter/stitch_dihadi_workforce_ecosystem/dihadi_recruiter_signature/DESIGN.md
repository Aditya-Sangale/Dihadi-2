---
name: Dihadi Recruiter Signature
colors:
  surface: '#fff8f5'
  surface-dim: '#e8d7ca'
  surface-bright: '#fff8f5'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#fff1e7'
  surface-container: '#fcebdd'
  surface-container-high: '#f7e5d8'
  surface-container-highest: '#f1dfd2'
  on-surface: '#221a12'
  on-surface-variant: '#4d4635'
  inverse-surface: '#382f26'
  inverse-on-surface: '#ffeee0'
  outline: '#7f7663'
  outline-variant: '#d0c5af'
  surface-tint: '#735c00'
  primary: '#735c00'
  on-primary: '#ffffff'
  primary-container: '#d4af37'
  on-primary-container: '#554300'
  inverse-primary: '#e9c349'
  secondary: '#665e4a'
  on-secondary: '#ffffff'
  secondary-container: '#eadfc6'
  on-secondary-container: '#6a624e'
  tertiary: '#5a5f63'
  on-tertiary: '#ffffff'
  tertiary-container: '#afb3b8'
  on-tertiary-container: '#41454a'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#ffe088'
  primary-fixed-dim: '#e9c349'
  on-primary-fixed: '#241a00'
  on-primary-fixed-variant: '#574500'
  secondary-fixed: '#ede1c9'
  secondary-fixed-dim: '#d1c5ae'
  on-secondary-fixed: '#201b0c'
  on-secondary-fixed-variant: '#4d4634'
  tertiary-fixed: '#dfe3e8'
  tertiary-fixed-dim: '#c3c7cc'
  on-tertiary-fixed: '#171c20'
  on-tertiary-fixed-variant: '#43474b'
  background: '#fff8f5'
  on-background: '#221a12'
  surface-variant: '#f1dfd2'
typography:
  display-lg:
    fontFamily: Playfair Display
    fontSize: 64px
    fontWeight: '700'
    lineHeight: '1.1'
    letterSpacing: -0.02em
  display-lg-mobile:
    fontFamily: Playfair Display
    fontSize: 40px
    fontWeight: '700'
    lineHeight: '1.2'
  headline-md:
    fontFamily: Playfair Display
    fontSize: 32px
    fontWeight: '600'
    lineHeight: '1.3'
  quote-text:
    fontFamily: Playfair Display
    fontSize: 24px
    fontWeight: '400'
    lineHeight: '1.5'
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '400'
    lineHeight: '1.6'
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: '1.6'
  button-text:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '600'
    lineHeight: '1'
    letterSpacing: 0.05em
  label-caps:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '700'
    lineHeight: '1'
    letterSpacing: 0.1em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 8px
  section-gap-desktop: 120px
  section-gap-mobile: 64px
  container-padding-desktop: 80px
  container-padding-mobile: 24px
  gutter: 24px
---

## Brand & Style

The design system is anchored in a **Premium Human-Centric** aesthetic. It balances the warmth of a boutique agency with the precision of high-end enterprise recruitment. The visual narrative avoids cold, corporate sterility, opting instead for a "Quiet Luxury" approach that fosters trust and calmness.

The style leverages **Soft Minimalism** with tactile depth. Layouts are spacious and editorial, prioritizing high-quality photography and intentional white space to allow the recruiter to focus on the human element of talent acquisition. Transitions should be fluid and eased, mimicking the grace of a high-end physical portfolio.

- **Emotional Response:** Respected, calm, empowered, and sophisticated.
- **Visual Influence:** Editorial luxury magazines and premium hospitality interfaces.

## Colors

The palette is a sophisticated blend of metallic warmth and organic tones. 

- **Primary (Champagne Gold):** Reserved for high-action touchpoints and critical branding elements. It symbolizes excellence and value.
- **Secondary (Cream):** The primary surface color, providing a softer, more inviting alternative to pure white.
- **Tertiary (Light Silver):** Used for structural grounding, secondary background sections, and subtle dividers.
- **Neutral (Dark Espresso):** Used exclusively for typography and deep interactive states to maintain high legibility and a sense of "ink on paper."
- **Warm Beige:** Applied to page-level backgrounds and container transitions to add depth and "hearth" to the digital environment.

## Typography

This design system utilizes a high-contrast typographic pairing to establish hierarchy and tone. 

- **Playfair Display:** Used for displays, headlines, and pull quotes. It provides the "editorial" feel that differentiates the recruiter page from a standard SaaS dashboard.
- **Plus Jakarta Sans:** (Substituting for professional body text) Used for all functional copy, UI labels, and buttons. It offers superior legibility at smaller sizes compared to more decorative faces.

**Implementation Notes:**
- Ensure ample line height (1.6) for body text to maintain the "calm" brand promise.
- Use the `label-caps` style for section headers above headlines to provide a structured, organized feel.

## Layout & Spacing

The layout follows a **Fixed Grid** philosophy for the central content (max-width 1280px) to ensure a controlled, premium reading experience. 

- **Grid:** 12-column layout for desktop; 4-column for mobile.
- **Rhythm:** An 8px base unit drives all padding and margin decisions. 
- **Borders & Margins:** Generous outer margins are essential to the "Minimalist" aesthetic. Content should never feel cramped against the screen edges.
- **Breakpoints:** 
    - Mobile: < 768px
    - Tablet: 768px - 1024px
    - Desktop: > 1024px

## Elevation & Depth

In this design system, depth is achieved through **Tonal Layers** and **Ambient Shadows** rather than aggressive elevation.

- **Recruiter Cards:** Use a soft, ultra-diffused shadow (`0px 12px 32px rgba(58, 48, 39, 0.05)`) to lift them slightly off the Warm Beige background.
- **Surfaces:** Tier 1 is Warm Beige (#E8DCC6). Tier 2 (Containers) is Cream (#F3E7CE). Tier 3 (Inner Details) is Light Silver (#D9DDE2).
- **Glassmorphism:** Reserved specifically for the Talent Pool Cinematic Slideshow. Use a `backdrop-filter: blur(12px)` on text overlays to ensure legibility over diverse imagery without losing the visual connection to the media.

## Shapes

The shape language is organic and approachable, utilizing varied corner radii to distinguish between layout levels.

- **Large Containers:** 24px radius creates a soft, frame-like appearance for major sections.
- **Cards:** 15px radius for recruiter personas and talent highlights, providing a distinct "object" feel.
- **Interactive Elements:** 8px radius for buttons and input fields to maintain a professional, slightly sharper edge for functional items.
- **Media:** Images should generally follow the radius of their parent container.

## Components

### Primary Buttons
- **Style:** Solid Gold (#D4AF37) background.
- **Typography:** Dark Espresso (#3A3027) text, `button-text` style.
- **Interaction:** On hover, darken slightly; on click, scale 0.98.

### Secondary Buttons
- **Style:** 1.5px Outlined Gold (#D4AF37), no fill.
- **Typography:** Gold (#D4AF37) text, `button-text` style.
- **Interaction:** Subtle gold tint fill (10% opacity) on hover.

### Recruiter Persona Cards
- **Structure:** Cream (#F3E7CE) base with 15px radius.
- **Sections:** Internal details (e.g., stats or bio) should be housed in Light Silver (#D9DDE2) sub-sections with 8px radius.
- **Imagery:** Circular headshots with a 2px Gold border.

### Cinematic Slideshow
- **Layout:** Full-width (edge-to-edge).
- **Overlays:** Dark Espresso text on a Glassmorphic (blurred Cream) bottom-third overlay.
- **Navigation:** Minimalist "Next/Prev" arrows in Gold; progress bar at the bottom using a thin Gold line.

### Input Fields
- **Style:** Cream background with a Light Silver 1px border. 
- **Focus State:** Border transitions to Gold with a 2px outer glow of the same color.