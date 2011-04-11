package com.roboticoverlord.micro.bukkit;

import org.bukkit.entity.Minecart;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleListener;

public class MinecartStarterVehicleListener extends VehicleListener {

	private MinecartStarter parent;

	public MinecartStarterVehicleListener(MinecartStarter parent) {
		this.parent = parent;
	}

//	public void onVehicleDestroy(VehicleDestroyEvent e) {
//
//	}

	@Override
	public void onVehicleExit(VehicleExitEvent e) {
		if (e.getVehicle() instanceof Minecart) {
			if (parent.mcarts.contains(e.getVehicle())) {
				e.getVehicle().remove();
				parent.mcarts.remove(e.getVehicle());
			}
		}
	}
}
