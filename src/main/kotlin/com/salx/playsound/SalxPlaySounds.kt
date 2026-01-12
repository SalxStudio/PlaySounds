package com.salx.playsound

import com.salx.playsound.command.PlaySoundCommand
import com.salx.playsound.command.ReloadCommand
import com.salx.playsound.command.StopSoundCommand
import com.salx.playsound.manager.LanguageManager
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
        lateinit var languageManager: LanguageManager
            private set
        var soundVolume = 1.0f
            private set
        var soundPitch = 1.0f
            private set
    }

    private var playSoundCommand: PlaySoundCommand? = null
    private var stopSoundCommand: StopSoundCommand? = null
    private var reloadCommand: ReloadCommand? = null

    override fun onEnable() {
        instance = this
        
        saveDefaultConfig()
        saveAllLanguageFiles()
        
        val configLanguage = config.getString("language", "zh_CN")
        languageManager = LanguageManager(this)
        languageManager.loadLanguage(configLanguage)
        
        val enablePapi = config.getBoolean("enable-papi", true)
        val enableNexo = config.getBoolean("enable-nexo", true)
        val enableItemsAdder = config.getBoolean("enable-itemsadder", true)
        
        papiEnabled = enablePapi && server.pluginManager.isPluginEnabled("PlaceholderAPI")
        nexoEnabled = enableNexo && server.pluginManager.isPluginEnabled("Nexo")
        itemsAdderEnabled = enableItemsAdder && server.pluginManager.isPluginEnabled("ItemsAdder")
        
        soundVolume = config.getDouble("volume", 1.0).toFloat()
        soundPitch = config.getDouble("pitch", 1.0).toFloat()

        playSoundCommand = PlaySoundCommand()
        stopSoundCommand = StopSoundCommand()
        reloadCommand = ReloadCommand()

        getCommand("salxplaysound")?.setExecutor(playSoundCommand)
        getCommand("salxplaysound")?.tabCompleter = playSoundCommand
        getCommand("salxstopsound")?.setExecutor(stopSoundCommand)
        getCommand("salxstopsound")?.tabCompleter = stopSoundCommand
        getCommand("salxplaysoundreload")?.setExecutor(reloadCommand)
        getCommand("salxplaysoundreload")?.tabCompleter = reloadCommand

        logEnableInfo()
    }

    private fun saveAllLanguageFiles() {
        val languagesFolder = java.io.File(dataFolder, "languages")
        if (!languagesFolder.exists()) {
            languagesFolder.mkdirs()
        }
        
        val languages = listOf("zh_CN", "en_US")
        for (lang in languages) {
            val languageFile = java.io.File(languagesFolder, "$lang.yml")
            if (!languageFile.exists()) {
                saveResource("languages/$lang.yml", false)
            }
        }
    }

    private fun logEnableInfo() {
        logger.info(languageManager.getMessage("plugin.enabled"))
        if (papiEnabled) {
            logger.info(languageManager.getMessage("plugin.papi_enabled"))
        }
        if (nexoEnabled) {
            val soundCount = playSoundCommand?.getNexoSoundCount() ?: 0
            if (soundCount > 0) {
                logger.info(languageManager.getMessage("plugin.nexo_enabled", "count" to soundCount))
            }
        }
        if (itemsAdderEnabled) {
            val soundCount = playSoundCommand?.getItemsAdderSoundCount() ?: 0
            if (soundCount > 0) {
                logger.info(languageManager.getMessage("plugin.itemsadder_enabled", "count" to soundCount))
            }
        }
        logger.info(languageManager.getMessage("plugin.language_loaded", "language" to languageManager.getCurrentLanguage()))
    }

    override fun onDisable() {
        playSoundCommand = null
        stopSoundCommand = null
        reloadCommand = null
        logger.info(languageManager.getMessage("plugin.disabled"))
    }

    fun reloadConfigValues() {
        val configLanguage = config.getString("language", "zh_CN")
        languageManager.loadLanguage(configLanguage)

        val enablePapi = config.getBoolean("enable-papi", true)
        val enableNexo = config.getBoolean("enable-nexo", true)
        val enableItemsAdder = config.getBoolean("enable-itemsadder", true)

        papiEnabled = enablePapi && server.pluginManager.isPluginEnabled("PlaceholderAPI")
        nexoEnabled = enableNexo && server.pluginManager.isPluginEnabled("Nexo")
        itemsAdderEnabled = enableItemsAdder && server.pluginManager.isPluginEnabled("ItemsAdder")

        soundVolume = config.getDouble("volume", 1.0).toFloat()
        soundPitch = config.getDouble("pitch", 1.0).toFloat()
    }
}
