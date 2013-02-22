package net.mortu.stopthatblockglitcher.commands;

import net.mortu.stopthatblockglitcher.StopThatBlockGlitcher;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DamageCommandExecutor implements CommandExecutor {
	
	private final StopThatBlockGlitcher plugin;
	
	public DamageCommandExecutor(StopThatBlockGlitcher plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player && !((Player) sender).hasPermission("stbg.damage")) {
			sender.sendMessage(command.getPermissionMessage());
			return true;
		}
		
		if (args.length < 2)
			return false;
		
		Player target = plugin.getServer().getPlayer(args[0]);
		
		if (target == null) {
			sender.sendMessage("Could not find target player.");
			return true;
		}
		
		try {
			target.damage(Integer.parseInt(args[1]));
		} catch (NumberFormatException e) {
			sender.sendMessage(args[1] + " does not appear to be a number.");
		}
		
		return true;
	}

}
