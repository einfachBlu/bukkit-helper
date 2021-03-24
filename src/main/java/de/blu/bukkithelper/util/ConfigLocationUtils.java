package de.blu.bukkithelper.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

public final class ConfigLocationUtils {

  public static void storeLocation(YamlConfiguration config, String key, Location location) {
    if (location == null) {
      return;
    }

    config.set(key + ".world", location.getWorld().getWorldFolder().getName());
    config.set(key + ".x", location.getX());
    config.set(key + ".y", location.getY());
    config.set(key + ".z", location.getZ());
    config.set(key + ".yaw", location.getYaw());
    config.set(key + ".pitch", location.getPitch());
  }

  public static Location getLocation(String key, YamlConfiguration config) {
    if (!config.contains(key)) {
      System.out.println("Key does not exist: " + key);
      return null;
    }

    String worldName = config.getString(key + ".world");
    double x = config.getDouble(key + ".x");
    double y = config.getDouble(key + ".y");
    double z = config.getDouble(key + ".z");
    float yaw = (float) config.getDouble(key + ".yaw");
    float pitch = (float) config.getDouble(key + ".pitch");

    return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
  }

  public static boolean containsLocation(String key, YamlConfiguration config) {
    return config.contains(key);
  }
}
