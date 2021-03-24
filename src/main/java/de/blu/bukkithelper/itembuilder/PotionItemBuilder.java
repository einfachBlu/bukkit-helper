package de.blu.bukkithelper.itembuilder;

import org.bukkit.Material;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public final class PotionItemBuilder extends ItemBuilder {

  public PotionItemBuilder() {
    super();
    this.setType(Material.POTION);
  }

  public PotionItemBuilder setEffect(PotionType potionType, int level, boolean splash) {
    Potion potion = new Potion(potionType, level, splash);
    this.setItem(potion.toItemStack(this.getItem().getAmount()));
    return this;
  }
}
