package net.mortu.stopthatblockglitcher.listeners;

import net.mortu.stopthatblockglitcher.StopThatBlockGlitcher;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class CraftListener implements Listener {

	private final StopThatBlockGlitcher plugin;

	public CraftListener(StopThatBlockGlitcher plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onCraftItem(CraftItemEvent event) {
		if (!plugin.getConfig().getBoolean("prevent-crafting", true))
			return;
		
		Player player = (Player) event.getWhoClicked();
		
		if (player.isOp() || player.hasPermission("stbg.bypass"))
			return;

		event.setCancelled(true);
	}

}
