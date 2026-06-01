package com.puppyadopt.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.puppyadopt.ui.theme.*

// ===================== 粉色渐变按钮 =====================
@Composable
fun PinkButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    small: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(if (small) 36.dp else 48.dp),
        shape = RoundedCornerShape(11.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color(0xFFCCCCCC),
            disabledContentColor = Color(0xFF999999),
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = if (small) 12.dp else 16.dp, vertical = if (small) 4.dp else 8.dp)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.horizontalGradient(listOf(Color(0xFFF48FB1), Color(0xFFEC407A))),
                    shape = RoundedCornerShape(11.dp)
                )
        )
        Text(
            text = text,
            fontSize = if (small) 11.sp else 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (enabled) Color.White else Color(0xFF999999)
        )
    }
}

// ===================== 灰色描边按钮 =====================
@Composable
fun GrayOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    small: Boolean = false
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(if (small) 32.dp else 44.dp),
        shape = RoundedCornerShape(11.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = DarkPink,
            disabledContentColor = Color(0xFFBBBBBB)
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = Brush.horizontalGradient(listOf(Color(0xFFF48FB1), Color(0xFFF48FB1)))
        ),
        contentPadding = PaddingValues(horizontal = if (small) 8.dp else 12.dp, vertical = if (small) 2.dp else 6.dp)
    ) {
        Text(
            text = text,
            fontSize = if (small) 10.sp else 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ===================== 进度条 =====================
@Composable
fun ProgressBar(
    progress: Float,    // 0.0 - 1.0
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(6.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(3.dp))
            .background(Color(0xFFF0E0E4))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .clip(RoundedCornerShape(3.dp))
                .background(
                    Brush.horizontalGradient(listOf(Color(0xFFF48FB1), Color(0xFFEC407A)))
                )
        )
    }
}

// ===================== 卡片容器 =====================
@Composable
fun GameCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            content = content
        )
    }
}

// ===================== 卡片标题 =====================
@Composable
fun CardTitle(text: String) {
    Text(
        text = text,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = DarkPink,
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

// ===================== Buff标签行 =====================
@Composable
fun BuffRow(buffs: List<BuffInfo>) {
    if (buffs.isEmpty()) return
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        buffs.forEach { buff ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(GreenLight)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${buff.icon}${buff.label} ${buff.seconds}s",
                    fontSize = 10.sp,
                    color = Green,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

data class BuffInfo(val icon: String, val label: String, val seconds: Int)

// ===================== 员工信息行 =====================
@Composable
fun EmployeeRow(
    icon: String,
    name: String,
    count: Int,
    desc: String,
    costDesc: String,
    onHire: () -> Unit,
    hireEnabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "$icon $name", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(text = desc, fontSize = 11.sp, color = LightText, modifier = Modifier.padding(top = 2.dp))
            Text(text = "拥有: $count", fontSize = 10.sp, color = LightText)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = costDesc, fontSize = 10.sp, color = DarkPink)
            Spacer(Modifier.height(4.dp))
            GrayOutlinedButton(text = "雇佣", onClick = onHire, enabled = hireEnabled, small = true)
        }
    }
    HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
}

// ===================== 升级按钮 =====================
@Composable
fun UpgradeRow(
    icon: String,
    name: String,
    level: Int,
    maxLevel: Int,
    cost: Long,
    canAfford: Boolean,
    onUpgrade: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "$icon $name Lv.$level", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            val desc = if (level >= maxLevel) "已达最高等级" else "升级费用: ⭐$cost"
            Text(text = desc, fontSize = 11.sp, color = LightText)
        }
        if (level < maxLevel) {
            PinkButton(text = "升级", onClick = onUpgrade, enabled = canAfford, small = true)
        } else {
            Text(text = "已满级", fontSize = 11.sp, color = Green, fontWeight = FontWeight.Bold)
        }
    }
}

// ===================== 弹窗 =====================
@Composable
fun GameDialog(
    title: String,
    message: String,
    buttons: List<Pair<String, (() -> Unit)?>>,  // text, onClick (null=disabled)
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(18.dp),
        containerColor = CardBackground,
        title = {
            Text(
                text = title,
                color = DarkPink,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = message,
                color = DarkText,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                buttons.forEach { (text, onClick) ->
                    PinkButton(
                        text = text,
                        onClick = { onClick?.invoke() ?: onDismiss() },
                        enabled = onClick != null,
                        small = false,
                        modifier = Modifier.padding(vertical = 3.dp)
                    )
                }
            }
        }
    )
}

// ===================== Toast =====================
@Composable
fun GameToast(message: String?) {
    AnimatedVisibility(
        visible = message != null,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(11.dp),
                color = Color.Black.copy(alpha = 0.85f),
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(
                    text = message ?: "",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
