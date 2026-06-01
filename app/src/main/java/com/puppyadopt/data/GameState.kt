package com.puppyadopt.data

/**
 * 游戏核心数据模型 - 对应原HTML的G对象
 */
data class GameState(
    // 核心资源
    val prestige: Long = 0L,
    val diamond: Int = 10,
    val dogs: Int = 0,
    val dogMax: Int = 5,

    // 时间
    val startTime: Long = 0L,
    val finishTime: Long = 0L,
    val finished: Boolean = false,
    val totalPrestige: Long = 0L,       // 累计声望（用于徽章）

    // 等级
    val rescueLv: Int = 1,              // 救助等级 1-6
    val promoteLv: Int = 1,             // 宣传等级 1-6
    val prestigeLv: Int = 1,            // 声望等级 1-5

    // 员工
    val lovers: Int = 0,                // 爱护者
    val volunteers: Int = 0,            // 志愿者
    val keepers: Int = 0,               // 看守者

    // Buff/计时
    val loverSec: Int = 0,
    val volunSec: Int = 0,
    val luckyBracelet: Boolean = false,
    val specialFood: Boolean = false,
    val badge: Boolean = false,
    val diamondProb: Boolean = false,
    val diaryFreeUsed: Boolean = false,
    val lastBadgeThousand: Int = 0,

    // 小紫剧情事件
    val eMeet: Boolean = false,         // 初遇
    val eVisit: Boolean = false,        // 温暖探访
    val eClinic: Boolean = false,       // 宠物义诊
    val eTalk: Boolean = false,         // 深夜长谈
    val eChoice: Boolean = false,       // 小紫的抉择
    val eSurprise: Boolean = false,     // 意外惊喜
    val eSoul: Boolean = false,         // 心灵相通
    val eLife: Boolean = false,         // 一生之约

    // 剧情激活状态
    val xiaoziActive: Boolean = false,
    val communityActive: Boolean = false,
    val fulltimeActive: Boolean = false,

    // 临时Buff秒数
    val firmHeart: Boolean = false,
    val firmHeartSec: Int = 0,
    val goldSec: Int = 0,               // 黄金体验
    val foodSec: Int = 0,               // 特制狗粮
    val warmSec: Int = 0,               // 温暖效果
    val soulSec: Int = 0,               // 祝福
    val chatRedSec: Int = 0,            // 减点击

    // CD
    val chatCd: Int = 0,
    val goldCd: Int = 0,
    val lotteryCd: Int = 0,
    val lotteryFree: Boolean = false,
    val lotteryWinRate: Int = 15,

    // 进度
    val rescClk: Int = 0,               // 救助点击计数器
    val proClk: Int = 0,                // 宣传点击计数器
    val xzTimer: Int = 0,

    // UI 事件锁
    val eventLock: Boolean = false,
    val currentEvent: PuppyEvent? = null
)

/**
 * 小紫剧情事件类型
 */
enum class PuppyEvent(
    val keyName: String,
    val title: String,
    val message: String,
    val prestigeThreshold: Long,
    val buttons: List<EventButton>
) {
    MEET(
        "eMeet", "🌸 初遇",
        "声望+50，钻石+1\n小紫关怀已激活！商店新增「小紫的幸运手链」",
        200L,
        listOf(EventButton("好，一起努力！", isDisabled = false))
    ),
    VISIT(
        "eVisit", "🏠 温暖的探访",
        "流浪狗+1（无视上限）\n温暖效果持续60秒\n商店新增「小紫的特制狗粮」",
        800L,
        listOf(EventButton("真温暖！", isDisabled = false))
    ),
    CLINIC(
        "eClinic", "💉 宠物义诊",
        "声望+300\n社区声望已激活（每次宣传额外+5声望）",
        2000L,
        listOf(EventButton("太棒了！", isDisabled = false))
    ),
    TALK(
        "eTalk", "🌙 深夜长谈",
        "彩票免费概率已激活！（30%免费机会）\n界面新增「小紫的日记」赠礼按钮",
        8000L,
        listOf(EventButton("倾听她的故事", isDisabled = false))
    ),
    CHOICE(
        "eChoice", "💝 小紫的抉择",
        "小紫收到了重要的机会——去大城市深造动物医学。\n你支持她吗？",
        15000L,
        listOf(
            EventButton("全力支持她（消耗3💎）", isDisabled = false, costDiamond = 3),
            EventButton("希望她慎重考虑（+500声望+1💎）", isDisabled = false)
        )
    ),
    SURPRISE(
        "eSurprise", "🎉 意外的惊喜",
        "按当前流浪狗数量获得声望\n流浪狗+2\n商店新增「小紫的纪念徽章」",
        25000L,
        listOf(EventButton("太开心了！", isDisabled = false))
    ),
    SOUL(
        "eSoul", "💫 心灵相通",
        "声望+2000，钻石+3\n获得「小紫的祝福」按钮",
        40000L,
        listOf(EventButton("💕 我也是", isDisabled = false))
    ),
    LIFE(
        "eLife", "💕 一生之约",
        "声望达到50000！救助站获得圆满结局！",
        50000L,
        listOf(EventButton("🎉 感谢一路相伴", isDisabled = false))
    )
}

data class EventButton(
    val text: String,
    val isDisabled: Boolean = false,
    val costDiamond: Int = 0
)

/**
 * 员工类型
 */
enum class EmployeeType(val label: String, val icon: String, val baseCost: Int) {
    LOVER("爱护者", "❤️", 50),
    VOLUNTEER("志愿者", "📣", 70),
    KEEPER("看守者", "🏠", 100)
}

/**
 * 商店道具类型
 */
enum class ShopItem(val label: String, val icon: String, val cost: Long) {
    LUCKY_BRACELET("小紫的幸运手链", "🌸", 200L),
    SPECIAL_FOOD("小紫的特制狗粮", "🍖", 50L),
    BADGE("小紫的纪念徽章", "🏅", 300L)
}

/**
 * 升级类型
 */
enum class UpgradeType(val label: String, val icon: String, val maxLevel: Int) {
    RESCUE("救助", "🐾", 6),
    PROMOTE("宣传", "📢", 6),
    PRESTIGE("声望", "⭐", 5)
}

/** 救助所需点击次数表（索引=等级） */
val BASE_CLICKS = intArrayOf(0, 15, 12, 9, 6, 3, 1)

/** 救助升级费用表 */
val RESCUE_COSTS = longArrayOf(0L, 50L, 150L, 400L, 1000L, 2000L)

/** 宣传升级费用表 */
val PROMOTE_COSTS = longArrayOf(0L, 50L, 150L, 400L, 1000L, 2000L)

/** 声望升级费用表 */
val PRESTIGE_COSTS = longArrayOf(0L, 100L, 200L, 500L, 1500L)

/** 员工费用倍率 */
fun getHireMultiplier(count: Int): Int = when {
    count >= 12 -> 28
    count >= 8 -> 4
    count >= 4 -> 2
    else -> 1
}

/** 格式秒数为时/分/秒 */
fun formatTime(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return when {
        h > 0 -> "${h}时${m}分${s}秒"
        m > 0 -> "${m}分${s}秒"
        else -> "${s}秒"
    }
}
