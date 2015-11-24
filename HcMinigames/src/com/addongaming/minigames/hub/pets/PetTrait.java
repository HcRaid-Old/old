package com.addongaming.minigames.hub.pets;

import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;
import net.citizensnpcs.api.trait.Trait;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.addongaming.minigames.core.MinigameUser;
import com.addongaming.minigames.events.PlayerEnteredGameEvent;

public class PetTrait extends Trait {
	private MinigameUser user;
	private int counter = 0;

	public PetTrait() {
		super("Pet");
	}

	private void check() {
		if (!getNPC().isSpawned())
			respawn();
		else
			reevaluate();
	}

	@EventHandler
	public void playerQuit(PlayerQuitEvent event) {
		if (getNPC().isSpawned()) {
			if (event.getPlayer().getName().equalsIgnoreCase(user.getName()))
				user.destroyPet();
		}
	}

	@EventHandler
	public void playerEnteredGame(PlayerEnteredGameEvent event) {
		if (getNPC().isSpawned()) {
			if (event.getUser().getName().equalsIgnoreCase(user.getName()))
				user.destroyPet();
		}
	}

	@EventHandler
	public void navigationCompleted(NavigationCompleteEvent event) {
		if (getNPC().isSpawned()) {
			if (event.getNPC().getId() == getNPC().getId()) {
			}
		}
	}

	private void reevaluate() {
		if (getNPC().getEntity().getLocation().distance(user.getLocation()) > 5)
			getNPC().getNavigator().setTarget(user.getLocation());
		else if (getNPC().getNavigator().isNavigating())
			getNPC().getNavigator().cancelNavigation();
	}

	@Override
	public void onSpawn() {
		getNPC().setProtected(true);
		getNPC().setFlyable(false);
		getNPC().getNavigator().getLocalParameters().speedModifier(1.5f);
	}

	@EventHandler
	public void die(EntityDeathEvent event) {

	}

	private void respawn() {
		if (user.isValid())
			getNPC().spawn(user.getLocation());
	}

	public void setUser(MinigameUser user) {
		this.user = user;
	}

	@Override
	public void run() {
		if (counter == 5) {
			check();
			counter = 0;
		} else
			counter++;
	}

}
