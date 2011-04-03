/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.roboticoverlord.micro.bukkit;

import org.bukkit.entity.Minecart;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleListener;

/**
 *
 * @author Owner
 */
public class MineCartVehicleListener extends VehicleListener {

	private MineCartStarterPlugin p;

	public MineCartVehicleListener(MineCartStarterPlugin plugin) {
		this.p = plugin;
	}

	@Override
	public void onVehicleExit(VehicleExitEvent e) {
		if(e.getVehicle() instanceof Minecart)
		if(p.mcarts.contains(e.getVehicle().getEntityId())) {
			e.getVehicle().remove();
		}
	}



}
