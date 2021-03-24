package de.blu.bukkithelper.util;

public class Random {

  public static double randomDouble(double min, double max) {
    double value = min + Math.random() * (max - min);
    return Math.round(value * 100.0) / 100.0;
  }

  public static int randomInt(double min, double max) {
    return (int) Math.round(min + Math.random() * (max - min));
  }

  public static boolean chance(double percent) {
    return Random.randomInt(0, 100) <= percent;
  }
}
