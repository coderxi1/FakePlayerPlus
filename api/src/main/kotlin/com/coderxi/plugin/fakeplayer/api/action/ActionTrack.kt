package com.coderxi.plugin.fakeplayer.api.action

// 同轨道的行为互斥
enum class ActionTrack {
    MOVEMENT, // 移动 (寻路、跟随)
    POSTURE, // 姿态 (潜行、跳跃、坐下、躺下)
    INTERACTION, // 交互 (攻击、挖掘、右键使用、钓鱼)
    GLOBAL // 独立, 不与任何轨道互斥
}