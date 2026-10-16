/*
 * Project Froyo — APBn Squad Message Portal
 * Created & Maintained by Fahad Al-Belal
 */

package com.froyo.apbnsquad.portal.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.froyo.apbnsquad.portal.ui.theme.XpColors

enum class XpButtonStyle {
    PRIMARY,
    SECONDARY,
    DANGER
}

@Composable
fun XpButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: XpButtonStyle = XpButtonStyle.PRIMARY,
    contentPadding: PaddingValues = PaddingValues(horizontal = 10.dp, vertical = 3.dp),
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val (brush, borderColor, textColor) = when (style) {
        XpButtonStyle.PRIMARY -> Triple(
            XpColors.ButtonBrush,
            XpColors.ButtonBorder,
            XpColors.ButtonText
        )
        XpButtonStyle.SECONDARY -> Triple(
            XpColors.ButtonSecondaryBrush,
            Color(0xFF768CA2),
            XpColors.ButtonSecondaryText
        )
        XpButtonStyle.DANGER -> Triple(
            XpColors.DangerButtonBrush,
            XpColors.DangerButtonBorder,
            XpColors.DangerButtonText
        )
    }

    val shape = RoundedCornerShape(3.dp)

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 22.dp)
            .shadow(if (isPressed) 0.dp else 1.dp, shape)
            .clip(shape)
            .background(if (enabled) brush else XpColors.ButtonSecondaryBrush)
            .border(BorderStroke(1.dp, if (enabled) borderColor else Color(0xFFAAAAAA)), shape)
            .drawBehind {
                if (enabled && !isPressed) {
                    // Crisp 1px inner top highlight for classic 3D beveled appearance
                    drawLine(
                        color = Color.White.copy(alpha = 0.9f),
                        start = Offset(2f, 2f),
                        end = Offset(size.width - 2f, 2f),
                        strokeWidth = 1.5f
                    )
                }
            }
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.runtime.CompositionLocalProvider(
            androidx.compose.material3.LocalContentColor provides textColor
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}

@Composable
fun XpTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fullWidth: Boolean = false,
    style: XpButtonStyle = XpButtonStyle.PRIMARY
) {
    XpButton(
        onClick = onClick,
        modifier = if (fullWidth) modifier.fillMaxWidth() else modifier,
        enabled = enabled,
        style = style
    ) {
        val textColor = when (style) {
            XpButtonStyle.PRIMARY -> XpColors.ButtonText
            XpButtonStyle.SECONDARY -> XpColors.ButtonSecondaryText
            XpButtonStyle.DANGER -> XpColors.DangerButtonText
        }
        Text(
            text = text,
            color = if (enabled) textColor else Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
