package com.salx.playsound.manager

import com.salx.playsound.SalxPlaySounds
import org.bukkit.ChatColor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class LanguageManager(private val plugin: SalxPlaySounds) {
    
    private var currentLanguage: String = "zh_CN"
    private var messages: FileConfiguration = YamlConfiguration()
    private val languagesFolder = File(plugin.dataFolder, "languages")
    
    fun loadLanguage(language: String? = null) {
        val lang = language ?: currentLanguage
        
        if (!languagesFolder.exists()) {
            languagesFolder.mkdirs()
        }
        
        val languageFile = File(languagesFolder, "$lang.yml")
        
        if (!languageFile.exists()) {
            plugin.saveResource("languages/$lang.yml", false)
        }
        
        if (!languageFile.exists()) {
            plugin.logger.warning("Language file $lang.yml not found, using default language")
            loadDefaultLanguage()
            return
        }
        
        messages = YamlConfiguration.loadConfiguration(languageFile)
        currentLanguage = lang
    }
    
    private fun loadDefaultLanguage() {
        if (!languagesFolder.exists()) {
            languagesFolder.mkdirs()
        }
        
        val defaultLanguageFile = File(languagesFolder, "zh_CN.yml")
        
        if (!defaultLanguageFile.exists()) {
            plugin.saveResource("languages/zh_CN.yml", false)
        }
        
        if (defaultLanguageFile.exists()) {
            messages = YamlConfiguration.loadConfiguration(defaultLanguageFile)
            currentLanguage = "zh_CN"
        }
    }
    
    fun reload() {
        loadLanguage()
    }
    
    fun getMessage(path: String, vararg placeholders: Pair<String, Any>): String {
        var message = messages.getString(path, "") ?: ""
        
        if (message.isEmpty()) {
            message = ""
        }
        
        for ((key, value) in placeholders) {
            message = message.replace("{$key}", value.toString())
        }
        
        return ChatColor.translateAlternateColorCodes('&', message)
    }
    
    fun getPrefix(): String {
        return getMessage("prefix")
    }
    
    fun getFormattedMessage(path: String, vararg placeholders: Pair<String, Any>): String {
        return getPrefix() + getMessage(path, *placeholders)
    }
    
    fun getCurrentLanguage(): String {
        return currentLanguage
    }
}