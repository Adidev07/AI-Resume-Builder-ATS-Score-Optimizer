package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

data class FontPairing(
    val name: String,
    val titleFont: FontFamily,
    val bodyFont: FontFamily
)

object ResumeStyleConfigs {
    
    // Five professional font pairings
    val fontPairings = listOf(
        FontPairing("Inter + Serif", FontFamily.SansSerif, FontFamily.Serif),
        FontPairing("Space Grotesk + Mono", FontFamily.SansSerif, FontFamily.Monospace),
        FontFamily.Serif?.let { serif -> FontPairing("Double Serif Slate", serif, serif) } ?: FontPairing("Double Serif Slate", FontFamily.Serif, FontFamily.Serif),
        FontPairing("Classic Sans-Serif", FontFamily.SansSerif, FontFamily.SansSerif),
        FontPairing("Technical Code Mono", FontFamily.Monospace, FontFamily.SansSerif)
    )

    // 6 professional accent colors
    val accentColors = listOf(
        AccentColorConfig("Tech Blue", "#2563EB", Color(0xFF2563EB)),
        AccentColorConfig("Slate Corporate", "#475569", Color(0xFF475569)),
        AccentColorConfig("Teal Dynamic", "#0D9488", Color(0xFF0D9488)),
        AccentColorConfig("Indigo Executive", "#4F46E5", Color(0xFF4F46E5)),
        AccentColorConfig("Emerald Modern", "#059669", Color(0xFF059669)),
        AccentColorConfig("Rose Passion", "#E11D48", Color(0xFFE11D48))
    )

    // 20 ATS style designs represented by combinations of layout types:
    // Classic (Time tested clean grids)
    // Modern (Asymmetric side borders or top banners)
    // Minimal (Ultra thin dividers, large margins)
    // Creative (Bold header plates)
    // Executive (Elegant double horizontal borders)
    val templates = listOf(
        ResumeTemplateConfig("classic", "Classic Professional", "Clean list with standard sections, perfect for Traditional Fields (Finance, Law)."),
        ResumeTemplateConfig("modern", "Modern Asymmetric", "Features an elegant left-colored banner line, ideal for Tech, Sales & Marketing."),
        ResumeTemplateConfig("minimal", "Minimalist Clean", "No background plates, thin lines with plenty of negative space, great for Designers & Writers."),
        ResumeTemplateConfig("creative", "Creative Grid", "Accented sections and custom bubble tags for skills, excellent for start-ups & creatives."),
        ResumeTemplateConfig("executive", "Executive Regal", "Traditional top double divider bars with centered heading for Senior Leadership roles.")
    )
}

data class AccentColorConfig(
    val title: String,
    val hex: String,
    val color: Color
)

data class ResumeTemplateConfig(
    val id: String,
    val title: String,
    val description: String
)
