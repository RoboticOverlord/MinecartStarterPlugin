package com.roboticoverlord.micro.bukkit;

//Bukkit imports
import java.util.logging.Level;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.vehicle.VehicleListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

//Permissions imports
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

//Java imports
import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.World;
import org.bukkit.event.player.PlayerListener;

public class MinecartStarter extends JavaPlugin {

	/**
	 * Minecart listener class
	 */
	VehicleListener vehicleListener = new MinecartStarterVehicleListener(this);
	PlayerListener playerListener = new MinecartStarterPlayerListener(this);
	/**
	 * Logger magic
	 */
	public static final Logger log = Logger.getLogger("Minecraft");
	/**
	 * List of minecart entities
	 */
	public ArrayList mcarts;
	/**
	 * Permission plugin
	 */
	public static PermissionHandler Permissions = null;
	/**
	 * Whether or not to use permissions
	 */
	public boolean permsPluginAvailable;

	public MinecartStarter() {
		this.mcarts = new ArrayList();
	}

	/**
	 * Outputs a message when disabled
	 */
	public void onDisable() {
		this.mcarts.clear();
		log.log(Level.INFO,"[{0}] Plugin disabled. (version{1})", new Object[]{this.getDescription().getName(), this.getDescription().getVersion()});
	}

	/**
	 * Enables the plugin
	 */
	public void onEnable() {

		this.setupPermissions();
		this.mcarts.clear();

		PluginManager pm = getServer().getPluginManager();

		//this.cartMonitor.start();

		//Event updates the database file on quit
		pm.registerEvent(Event.Type.VEHICLE_EXIT, this.vehicleListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_TELEPORT, this.playerListener, Event.Priority.Normal, this);

		//Print that the plugin has been enabled!
		log.log(Level.INFO,"[" + this.getDescription().getName() + "] Plugin enabled! (version " + this.getDescription().getVersion() + ")");

	}

	private boolean hasPermission(Player p) {
		if (this.permsPluginAvailable) {
			return Permissions.has(p, "micro.minecartstarter.can") || p.isOp();
		} else {
			return true;
		}
	}

	/**
	 * Checks that Permissions is installed.
	 */
	public void setupPermissions() {

		Plugin perm_plugin = this.getServer().getPluginManager().getPlugin("Permissions");
		PluginDescriptionFile pdfFile = this.getDescription();

		if (Permissions == null) {
			if (perm_plugin != null) {
				//Permissions found, enable it now
				this.getServer().getPluginManager().enablePlugin(perm_plugin);
				Permissions = ((Permissions) perm_plugin).getHandler();
				this.permsPluginAvailable = true;
			} else {
				//Permissions not found. Disable plugin
				log.log(Level.INFO, "{0} version {1} using op-code, Permissions not detected", new Object[]{pdfFile.getName(), pdfFile.getVersion()});
				this.permsPluginAvailable = false;
			}
		}
	}

	private boolean isAnonymous(Object t) {
		return !(t instanceof Player);
	}

	/**
	 * Called when a user performs a command
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		String commandName = command.getName().toLowerCase();

		if (sender instanceof Player) {
			Player player = (Player) sender;

			if (commandName.equals("minecart")) {

				if (this.isAnonymous(sender)) {
					return true;
				}
				if (!this.hasPermission(player)) {
					return true;
				}
				
				World w = ((Player) sender).getWorld();
				int amion = w.getBlockTypeIdAt(((Player) sender).getLocation());
				if (amion == 66) {
					Minecart m = w.spawnMinecart(((Player) sender).getLocation());
					m.setPassenger(((Player) sender));

					this.mcarts.add(m);

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

				return false;
			}


		}
		return false;
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
