package com.addongaming.prison.scenes;

import org.bukkit.plugin.java.JavaPlugin;

public class SceneManager {
	private JavaPlugin jp;

	public SceneManager(JavaPlugin jp) {
		this.jp = jp;
		new Introduction(jp);
	}
}
