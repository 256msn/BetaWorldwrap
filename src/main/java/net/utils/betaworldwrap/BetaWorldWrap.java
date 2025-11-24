package net.utils.betaworldwrap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Properties;

public class BetaWorldWrap extends JavaPlugin implements Listener {

    private Properties config = new Properties();

    private String worldName = "world";
    private boolean enableXWrap = true;
    private boolean enableZWrap = true;
    private double xMin = -15000;
    private double xMax = 15000;
    private double zMin = -15000;
    private double zMax = 15000;
    private double offset = 5;

    @Override
    public void onEnable() {
        loadConfig();
        getServer().getPluginManager().registerEvents(this, this);
        System.out.println("[BetaWorldWrap] Enabled.");
    }

    @Override
    public void onDisable() {
        System.out.println("[BetaWorldWrap] Disabled.");
    }

    private void loadConfig() {
        try {
            File folder = getDataFolder();
            if (!folder.exists()) folder.mkdir();

            File file = new File(folder, "config.properties");
            if (!file.exists()) saveDefaultConfig(file);

            FileInputStream in = new FileInputStream(file);
            config.load(in);
            in.close();

            worldName = config.getProperty("world", "world");
            enableXWrap = Boolean.parseBoolean(config.getProperty("enable-x-wrap", "true"));
            enableZWrap = Boolean.parseBoolean(config.getProperty("enable-z-wrap", "true"));
            xMin = Double.parseDouble(config.getProperty("x-min", "-15000"));
            xMax = Double.parseDouble(config.getProperty("x-max", "15000"));
            zMin = Double.parseDouble(config.getProperty("z-min", "-15000"));
            zMax = Double.parseDouble(config.getProperty("z-max", "15000"));
            offset = Double.parseDouble(config.getProperty("wrap-offset", "5"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveDefaultConfig(File file) {
        try {
            FileOutputStream out = new FileOutputStream(file);

            config.setProperty("world", "world");
            config.setProperty("enable-x-wrap", "true");
            config.setProperty("x-min", "-15000");
            config.setProperty("x-max", "15000");
            config.setProperty("enable-z-wrap", "true");
            config.setProperty("z-min", "-15000");
            config.setProperty("z-max", "15000");
            config.setProperty("wrap-offset", "5");

            config.store(out, "BetaWorldWrap Configuration");
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null) return;
        World world = getServer().getWorld(worldName);
        if (world == null) return;

        double x = event.getTo().getX();
        double y = event.getTo().getY();
        double z = event.getTo().getZ();

        boolean wrap = false;
        double newX = x;
        double newZ = z;

        if (enableXWrap) {
            if (x > xMax) {
                newX = xMin + offset;
                wrap = true;
            } else if (x < xMin) {
                newX = xMax - offset;
                wrap = true;
            }
        }

        if (enableZWrap) {
            if (z > zMax) {
                newZ = zMin + offset;
                wrap = true;
            } else if (z < zMin) {
                newZ = zMax - offset;
                wrap = true;
            }
        }

        if (wrap) {
            event.getPlayer().teleport(new Location(world, newX, y, newZ, event.getTo().getYaw(), event.getTo().getPitch()));
        }
    }
}
