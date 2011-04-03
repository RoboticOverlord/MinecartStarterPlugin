/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.roboticoverlord.micro.bukkit;

import com.nijikokun.bukkit.Permissions.Permissions;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

/**
 *
 * @author Owner
 */
public class MineCartStarterPlugin extends JavaPlugin {

	public ArrayList mcarts;
	public boolean permsPluginAvailable;

	public MineCartStarterPlugin() {
		this.mcarts = new ArrayList();
	}

	public void onDisable() {
		Logger l = this.getServer().getLogger();
		this.mcarts.clear();
		l.info("[MineCartStarterPlugin] Plugin successfully disabled.");
	}

	public void onEnable() {
		this.permsPluginAvailable = (this.getServer().getPluginManager().getPlugin("Permissions") == null ? false : true);
		Logger l = this.getServer().getLogger();

		this.mcarts.clear();

		if (this.permsPluginAvailable) {
			l.info("[MineCartStarterPlugin] Permissions plugin found, using micro.minecartstarter.can permissions.");
		} else {
			l.info("[MineCartStarterPlugin] Permissions plugin not found, defaulting to allow.");
		}

		this.getServer().getPluginManager().registerEvent(Type.VEHICLE_EXIT, new MineCartVehicleListener(this), Priority.Low, this);

		l.info("[MineCartStarterPlugin] Plugin enabled.");
	}

	private boolean hasPermission(Player p) {
		if (this.getServer().getPluginManager().getPlugin("Permissions") != null) {
			return Permissions.Security.permission(p, "micro.minecartstarter.can") || p.isOp();
		} else {
			return true;
		}
	}

	private boolean isAnonymous(Object t) {
		return !(t instanceof Player);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("minecart")) {
			if (!this.isAnonymous(sender)) {
				if(!this.hasPermission((Player)sender)) {
					((Player)sender).sendMessage("You do not have permission to use this command.");
					return true;
				}
				
				World w = ((Player) sender).getWorld();
				int amion = w.getBlockTypeIdAt(((Player) sender).getLocation());
				if (amion == 66) {
					Minecart m = w.spawnMinecart(((Player) sender).getLocation());
					m.setPassenger(((Player) sender));

					this.mcarts.add(m.getEntityId());

					Vector v = m.getVelocity();
					double degreeRotation = (((Player) sender).getLocation().getYaw() - 90.0F) % 360.0F;
					if (degreeRotation < 0.0D) {
						degreeRotation += 360.0D;
					}

					CardinalDirection d = getDirection(degreeRotation);
					switch (d) {
						case North:
							v.setX(-8);
							break;
						case East:
							v.setZ(-8);
							break;
						case South:
							v.setX(8);
							break;
						case West:
							v.setZ(8);
							break;
					}
					m.setVelocity(v);
				}
			}
			return true;
		}
		return true;
	}

	private enum CardinalDirection {
		North,
		East,
		South,
		West,
		Unknown
	}

	private CardinalDirection getDirection(double degrees) {
		if (degrees <= 45.0D || degrees > 315.0D) {
			return CardinalDirection.North;
		}
		if (degrees > 45.0D && degrees <= 135.0D) {
			return CardinalDirection.East;
		}
		if (degrees > 135.0D && degrees <= 225.0D) {
			return CardinalDirection.South;
		}
		if (degrees > 225.0D && degrees <= 315.0D) {
			return CardinalDirection.West;
		}
		return CardinalDirection.Unknown;
	}
}
