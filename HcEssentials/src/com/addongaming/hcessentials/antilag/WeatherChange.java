package com.addongaming.hcessentials.antilag;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WeatherChange implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void weatherChange(WeatherChangeEvent event) {
		if (event.toWeatherState())
			event.setCancelled(true);
	}
}
