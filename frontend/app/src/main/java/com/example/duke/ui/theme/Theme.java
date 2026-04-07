package com.example.yourapp.ui.theme;

public class Theme {
    // Light theme configuration
    public static class LightTheme {
        public static final int PRIMARY_COLOR = 0xFF6200EE;
        public static final int PRIMARY_DARK_COLOR = 0xFF3700B3;
        public static final int ACCENT_COLOR = 0xFF03DAC6;
        public static final int BACKGROUND_COLOR = 0xFFFAFAFA;
        public static final int SURFACE_COLOR = 0xFFFFFFFF;
        public static final int ERROR_COLOR = 0xFFB00020;
    }

    // Dark theme configuration
    public static class DarkTheme {
        public static final int PRIMARY_COLOR = 0xFFBB86FC;
        public static final int PRIMARY_DARK_COLOR = 0xFF7C3DED;
        public static final int ACCENT_COLOR = 0xFF03DAC6;
        public static final int BACKGROUND_COLOR = 0xFF121212;
        public static final int SURFACE_COLOR = 0xFF1F1F1F;
        public static final int ERROR_COLOR = 0xFFCF6679;
    }

    // Get theme based on mode
    public static int getColorForTheme(boolean isDarkMode, String colorName) {
        if (isDarkMode) {
            switch (colorName) {
                case "primary": return DarkTheme.PRIMARY_COLOR;
                case "primary_dark": return DarkTheme.PRIMARY_DARK_COLOR;
                case "background": return DarkTheme.BACKGROUND_COLOR;
                default: return DarkTheme.SURFACE_COLOR;
            }
        } else {
            switch (colorName) {
                case "primary": return LightTheme.PRIMARY_COLOR;
                case "primary_dark": return LightTheme.PRIMARY_DARK_COLOR;
                case "background": return LightTheme.BACKGROUND_COLOR;
                default: return LightTheme.SURFACE_COLOR;
            }
        }
    }
}