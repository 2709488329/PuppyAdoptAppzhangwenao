package com.puppyadopt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.puppyadopt.data.GameState
import com.puppyadopt.data.PuppyEvent
import com.puppyadopt.data.formatTime
import com.puppyadopt.ui.components.*
import com.puppyadopt.ui.theme.*

@Composable
fun HomeScreen(
    state: GameState,
    gameSeconds: Long,
    onRescue: () -> Unit,
    onPromote: () -> Unit,
    showRescueClicks: Int,
    showPromoteClicks: Int,
    rescueNeeded: Int,
    promoteNeeded: Int
) {
    // 收集所有活跃的Buff
    val buffs = mutableListOf<BuffInfo>().apply {
        if (state.goldSec > 0) add(BuffInfo("✨", "黄金体验", state.goldSec))
        if (state.foodSec > 0) add(BuffInfo("🍖", "特制狗粮", state.foodSec))
        if (state.warmSec > 0) add(BuffInfo("🏡", "温暖", state.warmSec))
        if (state.firmHeartSec > 0) add(BuffInfo("💪", "坚定的心", state.firmHeartSec))
        if (state.soulSec > 0) add(BuffInfo("💫", "祝福", state.soulSec))
        if (state.chatRedSec > 0) add(BuffInfo("💬", "减点击", state.chatRedSec))
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // ===== 顶部状态栏 =====
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatChip("⭐", "声望", state.prestige.toString())
                StatChip("💎", "钻石", state.diamond.toString())
                StatChip("🐕", "狗", "${state.dogs}/${state.dogMax}")
                StatChip("⏱️", "时间", formatTime(gameSeconds))
            }
        }

        // ===== Buff区域 =====
        if (buffs.isNotEmpty()) {
            BuffRow(buffs)
        }

        // ===== 主要操作区 =====
        GameCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 救助按钮
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PinkButton(
                        text = "🐾 救助 (${showRescueClicks}/${rescueNeeded})",
                        onClick = onRescue,
                        enabled = !state.finished && !state.eventLock
                    )
                    Text(
                        text = "救助流浪狗",
                        fontSize = 10.sp,
                        color = LightText,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // 宣传按钮
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PinkButton(
                        text = "📢 宣传 (${showPromoteClicks}/${promoteNeeded})",
                        onClick = onPromote,
                        enabled = state.dogs > 0 && !state.finished && !state.eventLock
                    )
                    Text(
                        text = "需要至少1只狗",
                        fontSize = 10.sp,
                        color = LightText,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // ===== 与小紫聊天 =====
        if (state.xiaoziActive) {
            GameCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "💬 与小紫聊天",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        if (state.chatCd > 0) {
                            Text(
                                text = "冷却: ${state.chatCd}s",
                                fontSize = 11.sp,
                                color = LightText
                            )
                        }
                    }
                    PinkButton(
                        text = "聊天",
                        onClick = onRescue,  // placeholder, will use onChat
                        enabled = state.chatCd <= 0 && !state.finished,
                        small = true
                    )
                }
            }
        }

        // ===== 快速访问区域 =====
        GameCard {
            CardTitle("⚡ 快速操作")

            if (state.eSoul && !state.finished) {
                val canClaim = state.soulSec <= 0
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💫 小紫的祝福", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    PinkButton(
                        text = if (canClaim) "领取" else "${state.soulSec}s",
                        onClick = onRescue,
                        enabled = canClaim,
                        small = true
                    )
                }
                HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
            }

            if (state.eTalk && !state.diaryFreeUsed && !state.finished) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📕 小紫的日记", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    PinkButton(text = "领取 (+500声望)", onClick = onRescue, small = true)
                }
                HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
            }

            if (state.eTalk) {
                val lotteryDisabled = state.prestige < 100 && !state.lotteryFree
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "🎰 彩票 (${if (state.lotteryFree) "免费" else "⭐100"})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (state.lotteryCd > 0) {
                        Text("冷却: ${state.lotteryCd}s", fontSize = 10.sp, color = LightText)
                    } else {
                        PinkButton(
                            text = "抽奖",
                            onClick = onRescue,
                            enabled = !lotteryDisabled && state.lotteryCd <= 0,
                            small = true
                        )
                    }
                }
                HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
            }

            if (!state.finished) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "✨ 黄金体验 (⭐300)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (state.goldCd > 0) {
                        Text("冷却: ${state.goldCd}s", fontSize = 10.sp, color = LightText)
                    } else {
                        PinkButton(
                            text = "使用",
                            onClick = onRescue,
                            enabled = state.prestige >= 300 && state.goldCd <= 0,
                            small = true
                        )
                    }
                }
            }
        }

        // ===== 进度提示 =====
        if (state.dogs < state.dogMax) {
            GameCard {
                Text(
                    text = "🏠 救助站还有 ${state.dogMax - state.dogs} 个空位",
                    fontSize = 12.sp,
                    color = LightText
                )
                ProgressBar(
                    progress = state.dogs.toFloat() / state.dogMax.toFloat(),
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun StatChip(icon: String, label: String, value: String) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.padding(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = icon, fontSize = 12.sp)
            Column {
                Text(text = label, fontSize = 9.sp, color = LightText)
                Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkPink)
            }
        }
    }
}
