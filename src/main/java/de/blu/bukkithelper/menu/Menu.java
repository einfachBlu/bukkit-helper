package de.blu.bukkithelper.menu;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class Menu implements Listener {

  private Inventory inventory;

  private JavaPlugin plugin;
  @Setter private String title = "";
  @Setter private int size = -1;

  private boolean listenerRegistered = false;

  @Inject
  protected Menu(JavaPlugin plugin) {
    this.plugin = plugin;
    this.registerListener();
  }

  protected void registerListener() {
    if (this.isListenerRegistered()) {
      return;
    }

    this.getPlugin().getServer().getPluginManager().registerEvents(this, this.getPlugin());
    this.listenerRegistered = true;
  }

  protected void unregisterListener() {
    if (!this.isListenerRegistered()) {
      return;
    }

    // Cant disable because sometimes we need to register it back when opening the inventory
    // But this cant be detected without event listener lol
    // HandlerList.unregisterAll(this);
    this.listenerRegistered = false;
  }

  protected void onClose(InventoryCloseEvent e) {}

  protected void onOpen(InventoryOpenEvent e) {}

  protected void onClick(InventoryClickEvent e) {}

  @EventHandler
  public void onInventoryOpen(InventoryOpenEvent e) {
    if (e.getView().getTopInventory() == null) {
      return;
    }

    if (!e.getView().getTopInventory().getTitle().equalsIgnoreCase(this.getTitle())
        || e.getView().getTopInventory().getSize() != this.getSize()) {
      return;
    }

    this.onOpen(e);
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent e) {
    if (e.getView().getTopInventory() == null) {
      return;
    }

    if (!e.getView().getTopInventory().getTitle().equalsIgnoreCase(this.getTitle())
        || e.getView().getTopInventory().getSize() != this.getSize()) {
      return;
    }

    this.onClose(e);
  }

  @EventHandler
  public void onClickEvent(InventoryClickEvent e) {
    if (!this.isListenerRegistered()) {
      return;
    }

    Player player = (Player) e.getWhoClicked();
    if (player.getOpenInventory() == null) {
      return;
    }

    if (player.getOpenInventory().getTopInventory() == null) {
      return;
    }

    if (!player.getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase(this.getTitle())
        || player.getOpenInventory().getTopInventory().getSize() != this.getSize()) {
      return;
    }

    this.onClick(e);
  }

  public Inventory getInventory() {
    if (this.getSize() == -1 || this.getTitle().equalsIgnoreCase("")) {
      return null;
    }

    if (this.inventory == null) {
      this.inventory =
          Bukkit.createInventory(
              null,
              this.getSize(),
              this.getTitle().length() > 32 ? this.getTitle().substring(0, 32) : this.getTitle());
    }

    return this.inventory;
  }

  public void open(Player player) {
    new BukkitRunnable() {
      @Override
      public void run() {
        if (player == null) {
          throw new IllegalArgumentException("player is null");
        }

        if (Menu.this.getInventory() == null) {
          throw new IllegalArgumentException("inventory is null");
        }

        player.openInventory(Menu.this.getInventory());
      }
    }.runTaskLater(this.getPlugin(), 1);
  }
}
