package dev.wersize;

import java.awt.Color;

/** Colour styles from the editor. Each one knows how to recolour a single ARGB pixel. */
public enum Style {
    VANILLA("Vanilla", 0xFFB0B0B0, Mode.NONE, 0f, 1f, 0f, 1f),
    CRIMSON("Crimson", 0xFFD83A3A, Mode.HUE, 0.000f, 1.0f, 0.65f, 1.00f),
    FROST("Frost", 0xFF6CC8F0, Mode.HUE, 0.550f, 0.8f, 0.45f, 1.05f),
    TOXIC("Toxic", 0xFF4CD84C, Mode.HUE, 0.330f, 1.0f, 0.65f, 1.00f),
    ROYAL("Royal", 0xFF9A55E0, Mode.HUE, 0.760f, 1.0f, 0.55f, 1.00f),
    GILDED("Gilded", 0xFFE8B820, Mode.HUE, 0.125f, 1.0f, 0.70f, 1.00f),
    SAKURA("Sakura", 0xFFF08CB4, Mode.HUE, 0.930f, 0.7f, 0.40f, 1.10f),
    OCEAN("Ocean", 0xFF2E6CE0, Mode.HUE, 0.615f, 1.0f, 0.65f, 1.00f),
    MIDNIGHT("Midnight", 0xFF2A2F5A, Mode.HUE, 0.650f, 0.9f, 0.50f, 0.55f),
    NEON("Neon", 0xFFFF3CC8, Mode.HUE, 0.860f, 1.3f, 0.85f, 1.20f),
    PASTEL("Pastel", 0xFFC8D8F0, Mode.PASTEL, 0f, 0.4f, 0f, 1f),
    MONOCHROME("Monochrome", 0xFFE0E0E0, Mode.GRAY, 0f, 0f, 0f, 1f),
    INVERTED("Inverted", 0xFF202020, Mode.INVERT, 0f, 1f, 0f, 1f);

    public enum Mode { NONE, HUE, PASTEL, GRAY, INVERT }

    public final String label;
    public final int preview;
    private final Mode mode;
    private final float hue, satMul, minSat, briMul;

    Style(String label, int preview, Mode mode, float hue, float satMul, float minSat, float briMul) {
        this.label = label;
        this.preview = preview;
        this.mode = mode;
        this.hue = hue;
        this.satMul = satMul;
        this.minSat = minSat;
        this.briMul = briMul;
    }

    public static Style byName(String name) {
        for (Style s : values()) if (s.name().equalsIgnoreCase(name)) return s;
        return VANILLA;
    }

    public int apply(int argb) {
        int a = argb >>> 24;
        if (a == 0 || mode == Mode.NONE) return argb;
        int r = (argb >> 16) & 0xFF, g = (argb >> 8) & 0xFF, b = argb & 0xFF;
        int rgb;
        switch (mode) {
            case INVERT -> rgb = ((255 - r) << 16) | ((255 - g) << 8) | (255 - b);
            case GRAY -> {
                int l = Math.round(0.299f * r + 0.587f * g + 0.114f * b);
                rgb = (l << 16) | (l << 8) | l;
            }
            case PASTEL -> {
                float[] hsb = Color.RGBtoHSB(r, g, b, null);
                rgb = Color.HSBtoRGB(hsb[0], hsb[1] * satMul, hsb[2] * 0.5f + 0.5f) & 0xFFFFFF;
            }
            default -> { // HUE
                float[] hsb = Color.RGBtoHSB(r, g, b, null);
                float s = Math.min(1f, Math.max(hsb[1] * satMul, minSat));
                float v = Math.min(1f, hsb[2] * briMul);
                rgb = Color.HSBtoRGB(hue, s, v) & 0xFFFFFF;
            }
        }
        return (a << 24) | rgb;
    }
}
