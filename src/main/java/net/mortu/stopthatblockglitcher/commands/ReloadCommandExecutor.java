package net.mortu.stopthatblockglitcher.commands;

import net.mortu.stopthatblockglitcher.StopThatBlockGlitcher;
import net.mortu.stopthatblockglitcher.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommandExecutor implements CommandExecutor {
	
	private final StopThatBlockGlitcher plugin;
	
	public ReloadCommandExecutor(StopThatBlockGlitcher plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player && !((Player) sender).hasPermission("stbg.reload")) {
			sender.sendMessage(command.getPermissionMessage());
			return true;
		}
		
		plugin.reload();
		sender.sendMessage(Utils.formatMessage("&aStopThatBlockGlitcher configuration reloaded."));
		
		return true;
	}

}
