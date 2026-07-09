# 久坐助手 (Sitting Reminder)

一款轻量级 Android 提醒应用，定时提醒您起身活动，预防久坐带来的健康问题。

## 功能特性

- **定时站立提醒** — 可配置提醒间隔（默认 55 分钟），到时间自动响铃提醒您站起来活动
- **入座延迟提醒** — 站立提醒后延迟一段时间（默认 5 分钟），提醒您可以坐下继续工作
- **活跃时间窗口** — 可设置每天提醒的有效时段（如 9:30-23:00），窗口外自动跳过不打扰
- **静默提醒模式** — 开启后提醒时不响铃，仅通过通知栏告知（适合会议、图书馆等场景）
- **振动加强提醒** — 振动强度 1-5 级可调，加强提醒效果
- **自定义铃声** — 支持通过系统铃声选择器更换提醒铃声
- **实时倒计时** — 通知栏和应用内均显示距下次提醒的精确倒计时
- **后台常驻服务** — 基于 AlarmManager 精确闹钟 + 前台服务，确保提醒准时送达
- **开机自启** — 设备重启后自动恢复提醒服务

## 工作流程

```
启动 → 等待间隔 → 站立提醒（响铃+振动）→ 入座延迟 → 坐回提醒（响铃+振动）→ 循环
```

1. 到达提醒时间 → 响铃/振动，通知栏弹出提醒
2. 经过入座延迟 → 再次提醒，通知您可以坐下
3. 自动进入下一轮循环

## 屏幕截图

（待添加）

## 技术栈

| 层级 | 技术 |
|------|------|
| 语言 | Kotlin |
| UI 框架 | Jetpack Compose + Material 3 |
| 架构 | MVVM (ViewModel + StateFlow) |
| 数据库 | Room |
| 后台服务 | ForegroundService + AlarmManager |
| 音频 | MediaPlayer |
| 最低支持 | Android 8.0 (API 26) |
| 目标版本 | Android 15 (API 35) |

## 下载

从 [Releases](https://github.com/qysnb/Sitting-Remider/releases) 页面下载最新 APK。

## 构建

```bash
./gradlew assembleDebug    # Debug 版本
./gradlew assembleRelease  # Release 版本
```

## 开源许可

Apache License 2.0

## 开发者

Qysnb with DeepSeek V4 Flash
