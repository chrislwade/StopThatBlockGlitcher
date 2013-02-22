package net.mortu.stopthatblockglitcher.tasks;

import net.mortu.stopthatblockglitcher.StopThatBlockGlitcher;

public class BlockGlitchAgingTask implements Runnable {

	private final StopThatBlockGlitcher plugin;
	private int id = -1;

	public BlockGlitchAgingTask(StopThatBlockGlitcher plugin) {
		this.plugin = plugin;

		Long interval = 20L * plugin.getConfig().getLong("aging-interval", 600L);
		id = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, interval, interval);
		if (id == -1)
			plugin.getLogger().severe("Failed to start StopThatBlockGlitcher.BlockGlitchAgingTask -- glitches won't age (never wear off).");
	}

	@Override
	public void run() {
		plugin.ageBlockGlitches();
	}

	public boolean stopTask() {
		if (id == -1)
			return false;
		
		plugin.getServer().getScheduler().cancelTask(id);
		return true;
	}

}
