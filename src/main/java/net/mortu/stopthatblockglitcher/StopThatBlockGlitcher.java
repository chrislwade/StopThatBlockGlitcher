package net.mortu.stopthatblockglitcher;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.mortu.stopthatblockglitcher.commands.*;
import net.mortu.stopthatblockglitcher.tasks.*;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class StopThatBlockGlitcher extends JavaPlugin implements Listener {
	
	private BlockGlitchAgingTask blockGlitchAgingTask;
	private CommandRunner commandRunner;
	
	private Map<String, List<Date>> glitchTimes;
	
	@Override
	public void onEnable() {
		if (!new File(this.getDataFolder().getPath() + File.separatorChar + "config.yml").exists())
			saveDefaultConfig();
		
		getCommand("damage").setExecutor(new DamageCommandExecutor(this));
		getCommand("stbg").setExecutor(new ReloadCommandExecutor(this));

		getServer().getPluginManager().registerEvents(this, this);
		
		glitchTimes = new HashMap<String, List<Date>>();
		commandRunner = new CommandRunner();
		startTasks();
	}
	
	@Override
	public void onDisable() {
		stopTasks();
		HandlerList.unregisterAll((Listener) this);
		commandRunner = null;
		glitchTimes = null;
	}
	
	public void reload() {
		stopTasks();
		reloadConfig();
		startTasks();
	}

	private void startTasks() {
		blockGlitchAgingTask = new BlockGlitchAgingTask(this);
	}

	private void stopTasks() {
		blockGlitchAgingTask.stopTask();
		blockGlitchAgingTask = null;
	}
	
	@EventHandler
	public void onBlockDamage(BlockDamageEvent event) {
		Player player = event.getPlayer();
		
		if (player.isOp() || player.hasPermission("stbg.bypass"))
			return;
		if (getConfig().getIntegerList("allow-block-ids").contains(event.getBlock().getTypeId()))
			return;
		
		runCommands("damage-commands", player);
		
		event.setCancelled(false);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		
		if (player.isOp() || player.hasPermission("stbg.bypass"))
			return;
		if (getConfig().getIntegerList("allow-block-ids").contains(event.getBlock().getTypeId()))
			return;
		
		if (!glitchTimes.containsKey(player.getName()))
			glitchTimes.put(player.getName(), new ArrayList<Date>());
		glitchTimes.get(player.getName()).add(new Date());
		
		runCommands("break-commands", player);
		
		if (glitchTimes.get(player.getName()).size() >= getConfig().getInt("break-threshold", 3))
			runCommands("exceeded-commands", player);
		
		event.setCancelled(true);
	}
	
	public void ageBlockGlitches() {
		Long now = (new Date()).getTime();
		Long interval = getConfig().getLong("aging-interval", 600L);
		
		Iterator<Entry<String, List<Date>>> glitchTimesIterator = glitchTimes.entrySet().iterator();
		while (glitchTimesIterator.hasNext()) {
			Entry<String, List<Date>> entry = glitchTimesIterator.next();
			
			Iterator<Date> datesIterator = entry.getValue().iterator();
			while (datesIterator.hasNext()) {
				Date date = datesIterator.next();
				if ((now - date.getTime()) / 1000.0 > interval)
					datesIterator.remove();
			}
			
			if (entry.getValue().isEmpty())
				glitchTimesIterator.remove();
		}
	}
	
	private void runCommands(String node, Player player) {
		Map<String, String> substitutions = new HashMap<String, String>();
		substitutions.put("PLAYER", player.getName());
		substitutions.put("LIMIT", getConfig().getString("break-threshold", "3"));
		
		try {
			substitutions.put("BREAKS", (new Integer(glitchTimes.get(player.getName()).size())).toString());
		} catch (NullPointerException e) {
			substitutions.put("BREAKS", "0");
		}
		
		for (String command : getConfig().getStringList(node))
			commandRunner.run(command, player, substitutions);
	}
	
}
