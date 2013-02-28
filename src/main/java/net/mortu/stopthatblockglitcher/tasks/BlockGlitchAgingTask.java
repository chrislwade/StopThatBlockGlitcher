package net.mortu.stopthatblockglitcher.tasks;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

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
		Long now = (new Date()).getTime();
		Long interval = plugin.getConfig().getLong("aging-interval", 600L);
		
		Iterator<Entry<String, List<Date>>> glitchTimesIterator = plugin.getGlitchTimes().entrySet().iterator();
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

	public boolean stopTask() {
		if (id == -1)
			return false;
		
		plugin.getServer().getScheduler().cancelTask(id);
		return true;
	}
	
}
