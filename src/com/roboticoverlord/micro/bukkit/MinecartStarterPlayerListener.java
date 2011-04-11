/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.roboticoverlord.micro.bukkit;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 *
 * @author Owner
 */
public class MinecartStarterPlayerListener extends PlayerListener {

	MinecartStarter parent;

	public MinecartStarterPlayerListener(MinecartStarter p) {
		this.parent = p;
	}

	@Override
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		if(e.getPlayer().isInsideVehicle()) {
			if(this.parent.mcarts.contains(e.getPlayer().getVehicle())) {
				e.getPlayer().leaveVehicle();
			}
		}
	}

}
