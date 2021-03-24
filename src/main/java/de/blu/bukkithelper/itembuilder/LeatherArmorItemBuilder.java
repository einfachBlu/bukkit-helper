package de.blu.bukkithelper.itembuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public final class LeatherArmorItemBuilder extends ItemBuilder {

  public LeatherArmorItemBuilder() {
    super();
  }

  public LeatherArmorItemBuilder dye(Color color) {
    if (this.getItem().getType() != Material.LEATHER_HELMET
        && this.getItem().getType() != Material.LEATHER_CHESTPLATE
        && this.getItem().getType() != Material.LEATHER_LEGGINGS
        && this.getItem().getType() != Material.LEATHER_BOOTS) {
      return this;
    }

    LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) this.getItemMeta();
    leatherArmorMeta.setColor(color);
    return this;
  }

  @Getter
  @AllArgsConstructor
  public enum ArmorElement {
    HELMET(Material.LEATHER_HELMET),
    CHESTPLATE(Material.LEATHER_CHESTPLATE),
    LEGGINGS(Material.LEATHER_LEGGINGS),
    BOOTS(Material.LEATHER_BOOTS);

    private Material material;
  }
}
