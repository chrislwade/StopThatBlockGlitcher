package net.mortu.stopthatblockglitcher.listeners;

import java.util.ArrayList;
import java.util.Date;

import net.mortu.stopthatblockglitcher.StopThatBlockGlitcher;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockGlitchListener implements Listener {
	
	private final StopThatBlockGlitcher plugin;
	
	public BlockGlitchListener(StopThatBlockGlitcher plugin) {
		this.plugin = plugin;
	}
	
	private void recordBlockGlitch(Player player) {
		if (!plugin.getGlitchTimes().containsKey(player.getName()))
			plugin.getGlitchTimes().put(player.getName(), new ArrayList<Date>());
		plugin.getGlitchTimes().get(player.getName()).add(new Date());
		
		plugin.runCommands("glitch-commands", player);
		
		if (plugin.getGlitchTimes().get(player.getName()).size() >= plugin.getConfig().getInt("glitch-threshold", 3))
			plugin.runCommands("exceeded-commands", player);
	}
	
	@EventHandler
	public void onBlockDamage(BlockDamageEvent event) {
		Player player = event.getPlayer();
		
		if (player.isOp() || player.hasPermission("stbg.bypass"))
			return;
		if (plugin.getConfig().getIntegerList("allow-break-block-ids").contains(event.getBlock().getTypeId()))
			return;
		
		plugin.runCommands("damage-commands", player);
		event.setCancelled(false);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		
		if (player.isOp() || player.hasPermission("stbg.bypass"))
			return;
		if (plugin.getConfig().getIntegerList("allow-break-block-ids").contains(event.getBlock().getTypeId()))
			return;
		
		recordBlockGlitch(player);
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		
		if (player.isOp() || player.hasPermission("stbg.bypass"))
			return;
		if (plugin.getConfig().getIntegerList("allow-place-block-ids").contains(event.getBlock().getTypeId()))
			return;
		
		recordBlockGlitch(player);
		event.setCancelled(true);
	}
	
}
