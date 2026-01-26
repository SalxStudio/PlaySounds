package com.salx.playsound

import com.salx.playsound.command.PlaySoundCommand
import com.salx.playsound.command.StopSoundCommand
import org.bukkit.plugin.java.JavaPlugin

class SalxPlaySounds : JavaPlugin() {

    companion object {
        lateinit var instance: SalxPlaySounds
            private set
        var papiEnabled = false
            private set
        var nexoEnabled = false
            private set
        var itemsAdderEnabled = false
            private set
    }

    private var playSoundCommand: PlaySoundCommand? = null
    private var stopSoundCommand: StopSoundCommand? = null

    override fun onEnable() {
        instance = this
        papiEnabled = server.pluginManager.isPluginEnabled("PlaceholderAPI")
        nexoEnabled = server.pluginManager.isPluginEnabled("Nexo")
        itemsAdderEnabled = server.pluginManager.isPluginEnabled("ItemsAdder")

        playSoundCommand = PlaySoundCommand()
        stopSoundCommand = StopSoundCommand()

        getCommand("salxplaysound")?.setExecutor(playSoundCommand)
        getCommand("salxplaysound")?.tabCompleter = playSoundCommand
        getCommand("salxstopsound")?.setExecutor(stopSoundCommand)
        getCommand("salxstopsound")?.tabCompleter = stopSoundCommand

        logEnableInfo()
    }

    private fun logEnableInfo() {
        logger.info("SalxPlaySounds 已启用!")
        if (papiEnabled) {
            logger.info("已启用 PlaceholderAPI 支持!")
        }
        if (nexoEnabled) {
            val soundCount = playSoundCommand?.getNexoSoundCount() ?: 0
            if (soundCount > 0) {
                logger.info("已启用 Nexo 支持! (已加载 $soundCount 个自定义声音)")
            }
        }
        if (itemsAdderEnabled) {
            val soundCount = playSoundCommand?.getItemsAdderSoundCount() ?: 0
            if (soundCount > 0) {
                logger.info("已启用 ItemsAdder 支持! (已加载 $soundCount 个自定义声音)")
            }
        }
    }

    override fun onDisable() {
        playSoundCommand = null
        stopSoundCommand = null
        logger.info("SalxPlaySounds 已卸载!")
    }
}
