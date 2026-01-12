package com.salx.playsound.command

import com.salx.playsound.SalxPlaySounds
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class ReloadCommand : CommandExecutor, TabCompleter {
    
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (!sender.hasPermission("salxplaysound.admin")) {
            sender.sendMessage(SalxPlaySounds.languageManager.getFormattedMessage("reload.no_permission"))
            return true
        }
        
        SalxPlaySounds.instance.reloadConfig()
        SalxPlaySounds.instance.reloadConfigValues()
        
        sender.sendMessage(SalxPlaySounds.languageManager.getFormattedMessage("reload.success"))
        
        return true
    }
    
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> {
        return emptyList()
    }
}