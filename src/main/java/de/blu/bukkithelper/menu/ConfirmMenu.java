package de.blu.bukkithelper.menu;

import com.google.inject.Inject;
import com.google.inject.Injector;
import de.blu.bukkithelper.itembuilder.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
public final class ConfirmMenu extends Menu {

  protected boolean initialized = false;

  @Setter private Player player;
  @Setter private Consumer<Result> callback;
  @Setter private Result result = null;
  @Setter private List<String> informationLines = new ArrayList<>();
  @Inject private Injector injector;

  @Inject
  private ConfirmMenu(JavaPlugin plugin) {
    super(plugin);
  }

  @Override
  public void open(Player player) {
    if (!this.initialized) {
      this.initialized = true;
      this.setSize(9 * 3);
      this.setTitle("Are you sure?");

      ItemStack placeHolderGlass = ItemBuilder.defaults().placeHolderGlass(DyeColor.BLACK).build();
      for (int i = 0; i < this.getSize(); i++) {
        this.getInventory().setItem(i, placeHolderGlass);
      }

      this.getInventory().setItem(11, this.getYesItem());
      this.getInventory().setItem(13, this.getInformationItem());
      this.getInventory().setItem(15, this.getNoItem());
    }

    super.open(player);
  }

  private ItemStack getYesItem() {
    return ItemBuilder.normal(Material.WOOL, 1, (short) DyeColor.GREEN.ordinal())
        .setDisplayName("§aYes")
        .setOnInventoryClickListener(
            e -> {
              e.setCancelled(true);
              this.setResult(Result.YES);
              this.getPlayer().closeInventory();
            },
            500)
        .build();
  }

  private ItemStack getNoItem() {
    return ItemBuilder.normal(Material.WOOL, 1, (short) DyeColor.RED.ordinal())
        .setDisplayName("§cNo")
        .setOnInventoryClickListener(
            e -> {
              e.setCancelled(true);
              this.setResult(Result.NO);
              this.getPlayer().closeInventory();
            },
            500)
        .build();
  }

  private ItemStack getInformationItem() {
    List<String> lore = new ArrayList<>();
    for (String informationLine : this.getInformationLines()) {
      lore.add("§7" + informationLine.replaceAll("§r", "§7"));
    }

    return ItemBuilder.normal(Material.PAPER)
        .setDisplayName("§eInformation")
        .setLore(lore)
        .setOnInventoryClickListener(
            e -> {
              e.setCancelled(true);
            },
            500)
        .build();
  }

  @Override
  protected void onClick(InventoryClickEvent e) {
    e.setCancelled(true);
  }

  @Override
  protected void onClose(InventoryCloseEvent e) {
    HandlerList.unregisterAll(this);
    this.getCallback().accept(this.getResult() != null ? this.getResult() : Result.NO);
  }

  public enum Result {
    YES,
    NO
  }
}
