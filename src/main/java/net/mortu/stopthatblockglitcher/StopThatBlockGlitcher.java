package net.mortu.stopthatblockglitcher;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.mortu.stopthatblockglitcher.commands.*;
import net.mortu.stopthatblockglitcher.tasks.*;
import net.mortu.stopthatblockglitcher.listeners.*;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class StopThatBlockGlitcher extends JavaPlugin implements Listener {
	
	private BlockGlitchAgingTask blockGlitchAgingTask;
	private BlockGlitchListener blockGlitchListener;
	private CraftListener craftListener;
	private Map<String, List<Date>> glitchTimes;
	
	@Override
	public void onEnable() {
		if (!new File(this.getDataFolder().getPath() + File.separatorChar + "config.yml").exists())
			saveDefaultConfig();
		
		getCommand("damage").setExecutor(new DamageCommandExecutor(this));
		getCommand("stbg").setExecutor(new ReloadCommandExecutor(this));

		glitchTimes = new HashMap<String, List<Date>>();
		getServer().getPluginManager().registerEvents(blockGlitchListener = new BlockGlitchListener(this), this);
		getServer().getPluginManager().registerEvents(craftListener = new CraftListener(this), this);
		startTasks();
	}
	
	@Override
	public void onDisable() {
		stopTasks();
		HandlerList.unregisterAll(craftListener);
		HandlerList.unregisterAll(blockGlitchListener);
		craftListener = null;
		blockGlitchListener = null;
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
	
	public Map<String, List<Date>> getGlitchTimes() {
		return glitchTimes;
	}
	
	public void runCommands(String node, Player player) {
		Map<String, String> substitutions = new HashMap<String, String>();
		substitutions.put("PLAYER", player.getName());
		substitutions.put("LIMIT", getConfig().getString("glitch-threshold", "3"));
		
		try {
			substitutions.put("GLITCHES", (new Integer(glitchTimes.get(player.getName()).size())).toString());
		} catch (NullPointerException e) {
			substitutions.put("GLITCHES", "0");
		}
		
		for (String command : getConfig().getStringList(node))
			CommandRunner.run(command, player, substitutions);
	}
	
}
