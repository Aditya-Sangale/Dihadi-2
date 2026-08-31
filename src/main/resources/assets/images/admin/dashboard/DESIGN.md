---
name: DIHADI Artisan Flow
colors:
  surface: '#fff8f0'
  surface-dim: '#e0d9ce'
  surface-bright: '#fff8f0'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#faf3e8'
  surface-container: '#f4ede2'
  surface-container-high: '#eee7dc'
  surface-container-highest: '#e9e2d7'
  on-surface: '#1e1b15'
  on-surface-variant: '#4c4637'
  inverse-surface: '#333029'
  inverse-on-surface: '#f7f0e5'
  outline: '#7e7665'
  outline-variant: '#cfc6b2'
  surface-tint: '#735c00'
  primary: '#574500'
  on-primary: '#ffffff'
  primary-container: '#735c00'
  on-primary-container: '#f6d676'
  inverse-primary: '#e3c466'
  secondary: '#685c52'
  on-secondary: '#ffffff'
  secondary-container: '#eeddd0'
  on-secondary-container: '#6d6056'
  tertiary: '#27438a'
  on-tertiary: '#ffffff'
  tertiary-container: '#415ba4'
  on-tertiary-container: '#cdd7ff'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#ffe085'
  primary-fixed-dim: '#e3c466'
  on-primary-fixed: '#231b00'
  on-primary-fixed-variant: '#574500'
  secondary-fixed: '#f0dfd3'
  secondary-fixed-dim: '#d4c4b7'
  on-secondary-fixed: '#221a12'
  on-secondary-fixed-variant: '#50453b'
  tertiary-fixed: '#dbe1ff'
  tertiary-fixed-dim: '#b4c5ff'
  on-tertiary-fixed: '#00174b'
  on-tertiary-fixed-variant: '#27438a'
  background: '#fff8f0'
  on-background: '#1e1b15'
  surface-variant: '#e9e2d7'
  gold-accent: '#D4AF37'
  surface-warm: '#FFF8F0'
  on-surface-muted: '#4D4635'
  border-subtle: '#D0C5AF'
typography:
  display-lg:
    fontFamily: Playfair Display
    fontSize: 48px
    fontWeight: '700'
    lineHeight: 56px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Playfair Display
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
  headline-lg-mobile:
    fontFamily: Playfair Display
    fontSize: 28px
    fontWeight: '600'
    lineHeight: 36px
  quote-it:
    fontFamily: Playfair Display
    fontSize: 22px
    fontWeight: '400'
    lineHeight: 32px
  title-md:
    fontFamily: Be Vietnam Pro
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Be Vietnam Pro
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Be Vietnam Pro
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-md:
    fontFamily: Be Vietnam Pro
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.05em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  xs: 4px
  base: 8px
  sm: 12px
  gutter: 24px
  md: 24px
  lg: 48px
  xl: 80px
  container-max: 1280px
---

## Brand & Style

DIHADI embodies a "Premium Essentialism" brand personality. It targets skilled artisans and service professionals, elevating blue-collar expertise through a sophisticated, editorial-inspired interface. The aesthetic is **Corporate Modern with a Tactile twist**, combining the reliability of a structured professional tool with the warmth of high-end craftsmanship. 

The emotional response should be one of "dignified professional pride." This is achieved through the use of serif display typography (suggesting tradition and authority) paired with a clean, functional sans-serif for task-oriented data. The style utilizes a "warm-neutral" foundation to differentiate itself from cold, tech-heavy competitors.

## Colors

The palette is anchored by **Aureate Gold (#735C00)**, symbolizing value and excellence in trade. This is complemented by a **Timber Brown (#685C52)** secondary color that grounds the brand in the physical world of materials. 

The background system uses a highly nuanced "Warm White" scale (ranging from `#FFF8F0` to `#EAE1D4`) to create a soft, paper-like feel that reduces eye strain for users who may be working outdoors. High-contrast elements are avoided in favor of tonal harmony, using deep charcoals rather than pure blacks for text to maintain the organic, high-end feel.

## Typography

The system employs a dual-font strategy. **Playfair Display** is reserved for high-impact brand moments: headlines, quotes, and primary titles. It provides an editorial, premium feel. **Be Vietnam Pro** handles all functional, multi-lingual, and data-heavy content. It was chosen for its excellent legibility in both English and Devanagari scripts, ensuring a consistent height and weight when switching languages. 

Hierarchy is established through significant weight shifts. Labels use increased letter-spacing and semi-bold weights to remain distinct at smaller sizes.

## Layout & Spacing

The system follows a **Fixed Grid with Fluid behavior** within a maximum container width of 1280px. 

- **Mobile:** Single column or 2-column small grid cards. Side margins are 24px (`md`).
- **Desktop:** Multi-column grids (3-4 columns for cards). Side margins scale up to 80px (`xl`) for a generous, airy feel.
- **Rhythm:** An 8px base unit drives all spacing. Component internal padding should favor `sm` (12px) or `md` (24px) to ensure touch targets are accessible for professionals who may be using the app in work environments.

## Elevation & Depth

Depth is primarily communicated through **Ambient Shadows** and **Tonal Layering**. 

1.  **The Base:** The background uses `surface-warm`.
2.  **The Container:** Interactive elements (cards) sit on a `surface-container-lowest` (pure white) to pop against the warm background.
3.  **The Shadow:** Shadows are extremely soft and tinted with the secondary brown (`rgba(58, 48, 39, 0.1)`), creating a more natural "organic" lift than standard gray shadows.
4.  **The Active State:** Selection is indicated by a 2px `gold-accent` border and an increased shadow depth, simulating a physical lift of the card toward the user.

## Shapes

The shape language is consistently "Rounded." 

- **Standard Buttons & Inputs:** Use an 18px or fully rounded pill-shape to invite interaction.
- **Cards:** Use a custom `15px` radius (between `lg` and `xl`) to balance the organic feel of the imagery with the structural layout.
- **Image Assets:** Should always inherit the parent card's radius or use a soft 16px radius when standalone.

## Components

### Buttons
- **Primary:** Filled with `primary-container`, high-contrast text, pill-shaped.
- **Secondary:** Outlined with `outline` color, 1px stroke, no fill.
- **Navigation:** Header buttons are icon-only with circular hover states.

### Skill Cards
Interactive cards featuring a top-aligned image (`180px` height) and a bottom-aligned text area. They must support a "Selected" state: a `2px` gold border and a deeper shadow. Transitions should be `0.2s ease-in-out`.

### Bottom Action Bar
A sticky footer with `backdrop-blur` and a `surface-warm` background at 90% opacity. This ensures primary actions (Back/Next) are always reachable while allowing content to peek through, maintaining context.

### Inputs & Labels
Labels for multi-lingual support should have a clear hierarchy: English in `title-md`, local script in `body-md` with `on-surface-variant` (muted) coloring.