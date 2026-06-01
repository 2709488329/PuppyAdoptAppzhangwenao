package com.puppyadopt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.puppyadopt.data.GameState
import com.puppyadopt.ui.components.*
import com.puppyadopt.ui.theme.*

/**
 * 小紫故事线页面
 * 显示所有剧情的解锁状态
 */
@Composable
fun StoryScreen(
    state: GameState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // 小紫角色卡片
        GameCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 头像区域
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "🌸", fontSize = 32.sp)
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "小紫",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkPink
                    )
                    Text(
                        text = when {
                            state.finished -> "💕 一生的承诺"
                            state.eLife -> "💕 一生之约"
                            state.eSoul -> "💫 心灵相通"
                            state.eSurprise -> "🎉 温馨日常"
                            state.eChoice -> "💝 重要抉择"
                            state.eTalk -> "🌙 信任加深"
                            state.eClinic -> "💉 并肩作战"
                            state.eVisit -> "🏠 逐渐熟络"
                            state.eMeet -> "🌸 初识"
                            else -> "❓ 尚未相遇"
                        },
                        fontSize = 13.sp,
                        color = LightText,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            if (state.xiaoziActive) {
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = DividerColor)
                Spacer(Modifier.height(8.dp))

                Text(
                    text = when {
                        state.fulltimeActive -> "💼 小紫在深造后选择全职回到救助站工作，有了她的专业帮助，一切更加顺利了。"
                        state.firmHeart -> "💪 小紫选择了留下，但那个深造的机会始终是一个重要的念想。"
                        state.eSoul -> "💫 你们之间有了一种无需言语的默契，一个眼神就能明白对方的心意。"
                        state.eSurprise -> "🎉 小紫总是能带来意想不到的快乐，她的笑容是救助站里最温暖的阳光。"
                        state.eChoice -> "💝 小紫站在了人生的十字路口，她的选择将改变一切。"
                        state.eTalk -> "🌙 夜深人静的时候，小紫会讲起她从前的故事，那些关于流浪猫狗的回忆。"
                        state.eClinic -> "💉 小紫的兽医知识在义诊中大放异彩，她认真工作的样子真美。"
                        state.eVisit -> "🏠 小紫第一次来救助站做客，她带来的自制狗粮很受欢迎。"
                        state.eMeet -> "🌸 小紫第一次来到救助站，她温柔的眼神让所有狗狗都安静了下来。"
                        else -> ""
                    },
                    fontSize = 12.sp,
                    color = DarkText,
                    lineHeight = 20.sp
                )
            }
        }

        // 剧情线历程
        GameCard {
            CardTitle("📖 剧情历程")

            StoryChapter(
                icon = "🌸",
                title = "初遇",
                desc = "声望达到200解锁",
                unlocked = state.eMeet,
                progress = (state.prestige.toFloat() / 200f).coerceIn(0f, 1f)
            )
            StoryChapter(
                icon = "🏠",
                title = "温暖的探访",
                desc = "声望达到800解锁",
                unlocked = state.eVisit,
                progress = (state.prestige.toFloat() / 800f).coerceIn(0f, 1f)
            )
            StoryChapter(
                icon = "💉",
                title = "宠物义诊",
                desc = "声望达到2000解锁",
                unlocked = state.eClinic,
                progress = (state.prestige.toFloat() / 2000f).coerceIn(0f, 1f)
            )
            StoryChapter(
                icon = "🌙",
                title = "深夜长谈",
                desc = "声望达到8000解锁",
                unlocked = state.eTalk,
                progress = (state.prestige.toFloat() / 8000f).coerceIn(0f, 1f)
            )
            StoryChapter(
                icon = "💝",
                title = "小紫的抉择",
                desc = "声望达到15000解锁",
                unlocked = state.eChoice,
                progress = (state.prestige.toFloat() / 15000f).coerceIn(0f, 1f)
            )
            StoryChapter(
                icon = "🎉",
                title = "意外惊喜",
                desc = "声望达到25000解锁",
                unlocked = state.eSurprise,
                progress = (state.prestige.toFloat() / 25000f).coerceIn(0f, 1f)
            )
            StoryChapter(
                icon = "💫",
                title = "心灵相通",
                desc = "声望达到40000解锁",
                unlocked = state.eSoul,
                progress = (state.prestige.toFloat() / 40000f).coerceIn(0f, 1f)
            )
            StoryChapter(
                icon = "💕",
                title = "一生之约",
                desc = "声望达到50000解锁（结局）",
                unlocked = state.finished || state.eLife,
                progress = (state.prestige.toFloat() / 50000f).coerceIn(0f, 1f)
            )
        }

        // 统计信息
        GameCard {
            CardTitle("📊 救助站统计")

            StatRow("累计声望", "${state.totalPrestige}")
            StatRow("员工总数", "${state.lovers + state.volunteers + state.keepers}")
            StatRow("  爱护者", "${state.lovers}")
            StatRow("  志愿者", "${state.volunteers}")
            StatRow("  看守者", "${state.keepers}")
            StatRow("狗数量/上限", "${state.dogs}/${state.dogMax}")
            StatRow("救助等级", "Lv.${state.rescueLv}")
            StatRow("宣传等级", "Lv.${state.promoteLv}")
            StatRow("声望等级", "Lv.${state.prestigeLv}")
            StatRow("钻石抽奖概率", "${state.lotteryWinRate}%")
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun StoryChapter(
    icon: String,
    title: String,
    desc: String,
    unlocked: Boolean,
    progress: Float
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(36.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (unlocked) "✅" else icon,
                fontSize = 18.sp
            )
        }

        Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = if (unlocked) FontWeight.Bold else FontWeight.Normal,
                color = if (unlocked) DarkPink else LightText
            )
            Text(
                text = if (unlocked) "✅ 已解锁" else desc,
                fontSize = 11.sp,
                color = LightText
            )
            if (!unlocked) {
                Spacer(Modifier.height(4.dp))
                ProgressBar(progress = progress)
            }
        }
    }
    HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = LightText)
        Text(text = value, fontSize = 12.sp, color = DarkText, fontWeight = FontWeight.Medium)
    }
}
