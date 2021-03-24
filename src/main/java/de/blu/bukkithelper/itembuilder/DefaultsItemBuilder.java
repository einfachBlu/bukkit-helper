package de.blu.bukkithelper.itembuilder;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;

public final class DefaultsItemBuilder extends ItemBuilder {

  /**
   * get a placeholder-glass
   *
   * @param color color of the glass
   * @return itembuilder
   */
  public DefaultsItemBuilder placeHolderGlass(DyeColor color) {
    this.setType(Material.STAINED_GLASS_PANE);
    this.setDisplayName(ChatColor.GRAY + " ");
    this.setDurability(color.getWoolData());
    this.setOnInventoryClickListener(event -> event.setCancelled(true));
    return this;
  }
}
