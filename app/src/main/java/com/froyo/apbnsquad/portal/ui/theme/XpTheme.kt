/*
 * Project Froyo — APBn Squad Message Portal
 * Created & Maintained by Fahad Al-Belal
 */

package com.froyo.apbnsquad.portal.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object XpColors {
    // Windows XP Signature Glossy Blue Gradient
    val TitleBarStart = Color(0xFF65ADFF)
    val TitleBarMid = Color(0xFF2679DD)
    val TitleBarEnd = Color(0xFF0B4BA7)
    val TitleBarBorder = Color(0xFF0A3678)
    val TitleBarTextShadow = Color(0xFF16457C)

    // Window Shell & Borders
    val WindowBorder = Color(0xFF526B88)
    val WindowBorderOuter = Color(0xFFDCE8F4)
    val WindowBackgroundStart = Color(0xFFF9FCFF)
    val WindowBackgroundEnd = Color(0xFFDBE9F8)

    // 3D Beveled Buttons (XP Luna style)
    val ButtonGradientStart = Color(0xFFFFFFFF)
    val ButtonGradientMid = Color(0xFFD9E7F5)
    val ButtonGradientEnd = Color(0xFFB7CDE4)
    val ButtonBorder = Color(0xFF315F91)
    val ButtonText = Color(0xFF102D4B)
    val ButtonInnerHighlight = Color(0xFFFFFFFF)

    // Secondary / Toolbar Buttons
    val ButtonSecondaryStart = Color(0xFFFFFFFF)
    val ButtonSecondaryMid = Color(0xFFE7EEF5)
    val ButtonSecondaryEnd = Color(0xFFCBD9E7)
    val ButtonSecondaryText = Color(0xFF21384F)

    // Close Button (XP Red Gradient)
    val CloseButtonStart = Color(0xFFFF7B70)
    val CloseButtonEnd = Color(0xFFB9150D)
    val CloseButtonBorder = Color(0xFFFFD0CC)

    // Danger Button
    val DangerButtonStart = Color(0xFFFFFFFF)
    val DangerButtonMid = Color(0xFFFFDEDE)
    val DangerButtonEnd = Color(0xFFF0A9A9)
    val DangerButtonText = Color(0xFF7C0000)
    val DangerButtonBorder = Color(0xFFAA5555)

    // Sunken Text Fields
    val InputBackground = Color(0xFFFFFFFF)
    val InputBorder = Color(0xFF8197AD)
    val InputFocusBorder = Color(0xFF347DCC)
    val InputText = Color(0xFF111111)
    val InputPlaceholder = Color(0xFF8899AA)

    // MSN Messenger Colors
    val MsnWindowBg = Color(0xFFEDF4FC)
    val MsnSidebarBg = Color(0xFFDBE8F5)
    val MsnSidebarBorder = Color(0xFFB5C4D4)
    val MsnChatHeaderBg = Color(0xFFCFE3F8)
    val MsnChatHeaderBorder = Color(0xFFA2C2E5)
    val MsnContactActive = Color(0xFFCDE1F7)
    val MsnRulesBg = Color(0xFFFFF7CF)
    val MsnRulesBorder = Color(0xFFDFCA79)
    val MsnRulesText = Color(0xFF655522)

    // Messages
    val MessageMineBg = Color(0xFFEAF3FC)
    val MessageOtherBg = Color(0xFFFFFFFF)
    val MessageBorder = Color(0xFFBAD3ED)
    val MessageReplyQuoteBg = Color(0xFFE2EDF8)
    val MessageReplyQuoteBorder = Color(0xFF347DCC)

    // Status Colors
    val StatusOnline = Color(0xFF2AA84A)
    val StatusDnd = Color(0xFFC60000)
    val StatusIdle = Color(0xFFF57C00)

    // Windows Taskbar
    val TaskbarTopBorder = Color(0x66FFFFFF)
    val TaskbarGlassStart = Color(0xE61E3C5F)
    val TaskbarGlassMid = Color(0xF00A192D)
    val TaskbarGlassEnd = Color(0xFA050C18)
    val TaskbarItemActiveBg = Color(0x4DFFFFFF)
    val TaskbarItemBorder = Color(0x55FFFFFF)

    // Start Orb Glow
    val StartOrbGlow = Color(0xFF52A0FD)
    val StartOrbBorder = Color(0xCCFFFFFF)

    // Gradients
    val TitleBarBrush = Brush.verticalGradient(
        listOf(TitleBarStart, TitleBarMid, TitleBarEnd)
    )

    val ButtonBrush = Brush.verticalGradient(
        listOf(ButtonGradientStart, ButtonGradientMid, ButtonGradientEnd)
    )

    val ButtonSecondaryBrush = Brush.verticalGradient(
        listOf(ButtonSecondaryStart, ButtonSecondaryMid, ButtonSecondaryEnd)
    )

    val DangerButtonBrush = Brush.verticalGradient(
        listOf(DangerButtonStart, DangerButtonMid, DangerButtonEnd)
    )

    val CloseButtonBrush = Brush.verticalGradient(
        listOf(CloseButtonStart, CloseButtonEnd)
    )

    val CardBrush = Brush.verticalGradient(
        listOf(WindowBackgroundStart, WindowBackgroundEnd)
    )

    val TaskbarBrush = Brush.verticalGradient(
        listOf(TaskbarGlassStart, TaskbarGlassMid, TaskbarGlassEnd)
    )
}
