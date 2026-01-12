package com.salx.playsound.command

import com.salx.playsound.SalxPlaySounds
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.lang.reflect.Method

class PlaySoundCommand : CommandExecutor, TabCompleter {

    private var papiSetPlaceholdersMethod: Method? = null
    private var papiClass: Class<*>? = null
    private var cachedVanillaSounds: List<String>? = null
    private var cachedNexoSounds: List<String>? = null
    private var cachedItemsAdderSounds: List<String>? = null
    private var lastNexoFileModified: Long = 0
    private var lastItemsAdderFolderModified: Long = 0

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (!sender.hasPermission("salxplaysound.use")) {
            sender.sendMessage(SalxPlaySounds.languageManager.getFormattedMessage("play_sound.no_permission"))
            return true
        }

        if (args.size < 2) {
            val usageMessage = if (sender is Player) {
                SalxPlaySounds.languageManager.getMessage("play_sound.player_usage")
            } else {
                SalxPlaySounds.languageManager.getMessage("play_sound.console_usage")
            }
            sender.sendMessage(SalxPlaySounds.languageManager.getPrefix() + usageMessage)
            return true
        }

        val soundName = args[0]
        var playerName = args[1]

        if (SalxPlaySounds.papiEnabled && sender is Player) {
            playerName = parsePlaceholders(sender, playerName)
        }

        val targetPlayer = Bukkit.getPlayerExact(playerName)
        if (targetPlayer == null) {
            sender.sendMessage(SalxPlaySounds.languageManager.getFormattedMessage("play_sound.player_not_found", "player" to playerName))
            return true
        }

        val isCustomSound = playSound(targetPlayer, soundName)
        if (!isCustomSound) {
            sender.sendMessage(SalxPlaySounds.languageManager.getFormattedMessage("play_sound.sound_not_found", "sound" to soundName))
            return true
        }

        sender.sendMessage(SalxPlaySounds.languageManager.getFormattedMessage("play_sound.success", "sound" to soundName, "player" to targetPlayer.name))

        return true
    }

    private fun playSound(player: Player, soundName: String): Boolean {
        return try {
            val sound = Sound.valueOf(soundName.uppercase())
            player.playSound(player.location, sound, SalxPlaySounds.soundVolume, SalxPlaySounds.soundPitch)
            true
        } catch (e: IllegalArgumentException) {
            try {
                player.playSound(player.location, soundName, SalxPlaySounds.soundVolume, SalxPlaySounds.soundPitch)
                true
            } catch (ex: Exception) {
                false
            }
        }
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
            1 -> getSoundSuggestions(args[0])
            2 -> Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[0]) }
            else -> emptyList()
        }
    }

    private fun getSoundSuggestions(input: String): List<String> {
        val vanillaSounds = getVanillaSounds()
        val customSounds = mutableListOf<String>()

        if (SalxPlaySounds.nexoEnabled) {
            customSounds.addAll(getNexoSounds())
        }

        if (SalxPlaySounds.itemsAdderEnabled) {
            customSounds.addAll(getItemsAdderSounds())
        }

        val allSounds = vanillaSounds + customSounds

        return if (input.isEmpty()) {
            allSounds.take(100)
        } else {
            allSounds.filter { it.contains(input.lowercase()) }.take(100)
        }
    }

    private fun getVanillaSounds(): List<String> {
        if (cachedVanillaSounds == null) {
            cachedVanillaSounds = Sound.values().map { it.name.lowercase() }
        }
        return cachedVanillaSounds!!
    }

    fun getNexoSoundCount(): Int {
        return getNexoSounds().size
    }

    fun getItemsAdderSoundCount(): Int {
        return getItemsAdderSounds().size
    }

    private fun getNexoSounds(): List<String> {
        try {
            val nexoPlugin = Bukkit.getPluginManager().getPlugin("Nexo") ?: return emptyList()
            
            val nexoFolder = nexoPlugin.dataFolder
            val soundsFile = java.io.File(nexoFolder, "sounds.yml")
            
            if (!soundsFile.exists()) {
                return emptyList()
            }
            
            val currentModified = soundsFile.lastModified()
            if (cachedNexoSounds != null && currentModified == lastNexoFileModified) {
                return cachedNexoSounds!!
            }
            
            val config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(soundsFile)
            
            val soundsList = config.getList("sounds") ?: return emptyList()
            val sounds = mutableListOf<String>()
            
            for (soundEntry in soundsList) {
                try {
                    if (soundEntry is Map<*, *>) {
                        val soundId = soundEntry["id"]?.toString()
                        if (!soundId.isNullOrEmpty()) {
                            sounds.add(soundId.lowercase())
                        }
                    }
                } catch (e: Exception) {
                    continue
                }
            }
            
            cachedNexoSounds = sounds.toList()
            lastNexoFileModified = currentModified
            return cachedNexoSounds!!
        } catch (e: Exception) {
            return emptyList()
        }
    }

    private fun getItemsAdderSounds(): List<String> {
        try {
            val itemsAdderPlugin = Bukkit.getPluginManager().getPlugin("ItemsAdder") ?: return emptyList()
            
            val itemsAdderFolder = itemsAdderPlugin.dataFolder
            val configFolder = java.io.File(itemsAdderFolder, "config")
            
            if (!configFolder.exists()) {
                return emptyList()
            }
            
            val soundsFolder = java.io.File(configFolder, "sounds")
            if (!soundsFolder.exists()) {
                return emptyList()
            }
            
            val currentModified = soundsFolder.lastModified()
            if (cachedItemsAdderSounds != null && currentModified == lastItemsAdderFolderModified) {
                return cachedItemsAdderSounds!!
            }
            
            val soundFiles = soundsFolder.listFiles { file -> file.extension == "yml" } ?: emptyArray()
            val sounds = mutableListOf<String>()
            
            for (soundFile in soundFiles) {
                try {
                    val config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(soundFile)
                    val soundKeys = config.getKeys(false)
                    
                    for (key in soundKeys) {
                        try {
                            val soundName = key.toString()
                            if (soundName.isNotEmpty()) {
                                sounds.add(soundName.lowercase())
                            }
                        } catch (e: Exception) {
                            continue
                        }
                    }
                } catch (e: Exception) {
                    continue
                }
            }
            
            cachedItemsAdderSounds = sounds.toList()
            lastItemsAdderFolderModified = currentModified
            return cachedItemsAdderSounds!!
        } catch (e: Exception) {
            return emptyList()
        }
    }
}
