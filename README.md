# SalxPlaySounds

一个简化版的Minecraft声音播放插件，支持精准控制原版playsound命令，支持PlaceholderAPI占位符，以及Nexo和ItemsAdder自定义声音。

## 功能特性

- **简化命令**: 无需指定世界坐标，直接为指定玩家播放声音
- **PAPI支持**: 支持PlaceholderAPI占位符（如 `%player_name%`）
- **自定义声音**: 支持Nexo和ItemsAdder的自定义声音
- **Tab补全**: 智能补全原版声音和自定义声音
- **模糊搜索**: 支持声音名称模糊匹配
- **停止声音**: 支持停止指定玩家的所有声音
- **多版本支持**: 兼容Minecraft 1.20-1.21+版本

## 兼容性

- **Minecraft版本**: 1.20 - 1.21+
- **服务端**: Paper / Purpur / Pufferfish
- **Java版本**: 17+
- **依赖插件** (可选):
  - PlaceholderAPI (用于占位符支持)
  - Nexo (用于自定义声音)
  - ItemsAdder (用于自定义声音)

## 安装

1. 下载最新版本的 `SalxPlaySounds-x.x.jar`
2. 将文件放入服务器的 `plugins` 文件夹
3. 重启服务器或使用 `/reload` 命令
4. 安装可选依赖插件以获得完整功能

## 使用方法

### 基本命令

```
/salxplaysound <声音> <玩家>
/splay <声音> <玩家>
/sps <声音> <玩家>
/salxstopsound <玩家>
/sstop <玩家>
/sss <玩家>
```

### 参数说明

- `<声音>`: 要播放的声音名称（原版声音或自定义声音）
- `<玩家>`: 目标玩家名称或PAPI占位符

### 示例

```bash
# 为玩家Steve播放原版声音
/salxplaysound entity.experience_orb.pickup Steve

# 使用别名命令
/splay entity.player.levelup Steve

# 使用PAPI占位符
/salxplaysound entity.experience_orb.pickup %player_name%

# 播放Nexo自定义声音
/splay custom.chat Steve

# 播放ItemsAdder自定义声音
/splay custom_sound_name Steve

# 停止玩家Steve的所有声音
/salxstopsound Steve

# 使用别名停止声音
/sstop Steve
/sss Steve
```

### Tab补全

- 输入命令后按 `Tab` 键可以查看所有可用的声音
- 支持模糊搜索，输入部分声音名称即可过滤
- 最多显示100个匹配的声音

## 权限

| 权限节点 | 描述 | 默认值 |
|---------|------|--------|
| `salxplaysound.use` | 使用salxplaysound命令 | OP |

## 配置

插件无需配置文件，开箱即用。启动时会自动检测并加载以下内容：

- PlaceholderAPI支持
- Nexo自定义声音（从 `plugins/Nexo/sounds.yml` 读取）
- ItemsAdder自定义声音（从 `plugins/ItemsAdder/config/sounds/` 读取）

## 控制台输出

插件启动时会显示加载信息：

```
[SalxPlaySounds] SalxPlaySounds 已启用!
[SalxPlaySounds] 已启用 PlaceholderAPI 支持!
[SalxPlaySounds] 已启用 Nexo 支持! (已加载 3 个声音)
[SalxPlaySounds] 已启用 ItemsAdder 支持! (已加载 10 个声音)
```

## 常见问题

**Q: 为什么自定义声音无法播放？**

A: 请确保：
1. Nexo或ItemsAdder插件已正确安装并启用
2. 自定义声音配置文件格式正确
3. 玩家已安装相应的资源包

**Q: Tab补全中没有显示自定义声音？**

A: 请检查：
1. Nexo的 `sounds.yml` 文件是否存在
2. ItemsAdder的 `config/sounds/` 文件夹中是否有配置文件
3. 查看控制台是否有错误信息

**Q: 支持哪些原版声音？**

A: 支持所有Minecraft原版声音，可以使用Tab补全查看完整列表。

## 开源协议

本项目采用 MIT 协议开源。

## 作者

Salx

## 版本历史

### v1.4
- 添加多语言支持系统
- 支持中文和英文语言文件
- 添加语言文件热重载功能（/salxplaysoundreload命令）
- 改进消息系统，支持自定义消息
- 添加语言文件管理器

### v1.3
- 大幅优化性能和TPS占用
- 缓存反射方法，减少运行时开销
- 缓存声音列表，避免重复文件读取
- 优化PAPI占位符解析性能
- 改进内存管理，减少GC压力
- 优化启动流程，延迟初始化非必要组件

### v1.2
- 添加停止声音功能（salxstopsound命令）
- 支持停止指定玩家的所有声音
- 添加停止声音命令别名（sstop、sss）
- 支持PAPI占位符在停止声音命令中使用

### v1.1
- 添加Nexo和ItemsAdder自定义声音支持
- 优化Tab补全功能
- 添加声音数量显示
- 支持Minecraft 1.20-1.21+

### v1.0
- 初始版本
- 基础声音播放功能
- PAPI占位符支持
- Tab补全功能
