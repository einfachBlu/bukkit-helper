package de.blu.bukkithelper.util;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class GlowEnchantment extends Enchantment {

  public GlowEnchantment(int id) {
    super(id);
  }

  public boolean canEnchantItem(ItemStack item) {
    return true;
  }

  @Override
  public boolean conflictsWith(Enchantment other) {
    return false;
  }

  @Override
  public EnchantmentTarget getItemTarget() {
    return null;
  }

  @Override
  public int getMaxLevel() {
    return 1;
  }

  @Override
  public String getName() {
    return " ";
  }

  @Override
  public int getId() {
    return 69;
  }

  @Override
  public int getStartLevel() {
    return 1;
  }
}
