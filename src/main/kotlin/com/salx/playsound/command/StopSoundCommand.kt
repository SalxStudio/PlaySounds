package com.salx.playsound.command

import com.salx.playsound.SalxPlaySounds
import org.bukkit.Bukkit
import org.bukkit.SoundCategory
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.lang.reflect.Method

class StopSoundCommand : CommandExecutor, TabCompleter {

    private var papiSetPlaceholdersMethod: Method? = null
    private var papiClass: Class<*>? = null
    private var stopAllSoundsMethod: Method? = null
    private var stopSoundStringMethod: Method? = null
    private var stopSoundMethod: Method? = null
    private var stopSoundCategoryMethod: Method? = null
    private var methodsInitialized = false

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage("§c用法: /$label <玩家>")
            return true
        }

        var playerName = args[0]

        if (SalxPlaySounds.papiEnabled && sender is Player) {
            playerName = parsePlaceholders(sender, playerName)
        }

        val targetPlayer = Bukkit.getPlayerExact(playerName)
        if (targetPlayer == null) {
            sender.sendMessage("§c玩家 $playerName 不在线")
            return true
        }

        stopSound(targetPlayer)

        val senderName = if (sender is Player) sender.name else "控制台"
        sender.sendMessage("§a[$senderName] 已停止玩家 ${targetPlayer.name} 的声音")

        return true
    }

    private fun stopSound(player: Player) {
        if (!methodsInitialized) {
            initializeMethods()
        }

        try {
            stopAllSoundsMethod?.invoke(player)
        } catch (e: Exception) {
            try {
                stopSoundStringMethod?.invoke(player, null as String?)
            } catch (ex: Exception) {
                try {
                    for (sound in org.bukkit.Sound.values()) {
                        try {
                            stopSoundMethod?.invoke(player, sound)
                        } catch (e: Exception) {
                            continue
                        }
                    }
                } catch (e: Exception) {
                    try {
                        stopSoundCategoryMethod?.invoke(player, SoundCategory.MASTER)
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }

    private fun initializeMethods() {
        try {
            stopAllSoundsMethod = playerClass.getMethod("stopAllSounds")
        } catch (e: Exception) {
        }

        try {
            stopSoundStringMethod = playerClass.getMethod("stopSound", String::class.java)
        } catch (e: Exception) {
        }

        try {
            stopSoundMethod = playerClass.getMethod("stopSound", org.bukkit.Sound::class.java)
        } catch (e: Exception) {
        }

        try {
            stopSoundCategoryMethod = playerClass.getMethod("stopSound", SoundCategory::class.java)
        } catch (e: Exception) {
        }

        methodsInitialized = true
    }

    private fun parsePlaceholders(player: Player, text: String): String {
        if (papiSetPlaceholdersMethod == null) {
            try {
                papiClass = Class.forName("me.clip.placeholderapi.PlaceholderAPI")
                papiSetPlaceholdersMethod = papiClass?.getMethod("setPlaceholders", org.bukkit.entity.Player::class.java, String::class.java)
            } catch (e: Exception) {
                return text
            }
        }

        return try {
            papiSetPlaceholdersMethod?.invoke(null, player, text) as String
        } catch (e: Exception) {
            text
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> {
        return when (args.size) {
            1 -> Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[0]) }
            else -> emptyList()
        }
    }

    private companion object {
        private val playerClass = Player::class.java
    }
}
