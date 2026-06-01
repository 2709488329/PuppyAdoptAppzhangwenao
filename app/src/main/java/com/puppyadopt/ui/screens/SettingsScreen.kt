package com.puppyadopt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.puppyadopt.ui.components.*
import com.puppyadopt.ui.theme.*

@Composable
fun SettingsScreen(
    onResetGame: () -> Unit,
    gameSeconds: Long
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        GameCard {
            CardTitle("🎮 游戏信息")
            Text(
                text = "领养小奶狗 v1.0 (Android)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
            Text(
                text = "帮助流浪狗找到温暖的家。招募员工，经营救助站，与小紫一起努力！",
                fontSize = 12.sp,
                color = LightText,
                modifier = Modifier.padding(top = 8.dp),
                lineHeight = 18.sp
            )
        }

        GameCard {
            CardTitle("📋 操作说明")
            Instruction("🐾 救助", "点击按钮救助流浪狗，凑够一定次数可获得1只狗")
            Instruction("📢 宣传", "消耗1只狗进行宣传，获得声望值")
            Instruction("💬 聊天", "与小紫聊天，有机会获得额外声望")
            Instruction("👥 员工", "雇佣员工自动进行救助和宣传")
            Instruction("⬆️ 升级", "提升技能等级，减少所需点击次数")
            Instruction("🎰 彩票", "消耗100声望抽奖，有机会获得大量声望")
            Instruction("✨ 黄金体验", "激活后声望等级暂时变为最高级")
            Instruction("💝 小紫剧情", "声望达到一定值后触发小紫的剧情事件")
        }

        GameCard {
            CardTitle("⚙️ 游戏操作")

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onResetGame,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("🔄 重置游戏", color = MaterialTheme.colorScheme.onError)
            }

            Text(
                text = "警告：重置将清除所有游戏进度！",
                fontSize = 11.sp,
                color = LightText,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun Instruction(iconAndTitle: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .padding(start = 4.dp)
    ) {
        Text(
            text = iconAndTitle,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = desc,
            fontSize = 12.sp,
            color = LightText,
            modifier = Modifier.weight(1f)
        )
    }
}
