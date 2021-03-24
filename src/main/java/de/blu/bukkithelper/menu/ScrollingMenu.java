package de.blu.bukkithelper.menu;

import com.google.inject.Inject;
import de.blu.bukkithelper.itembuilder.ItemBuilder;
import de.blu.bukkithelper.itembuilder.SkullItemBuilder;
import de.blu.bukkithelper.profile.GameProfileBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/** a provider to simplify scrolling/paging inventories */
@Getter
public class ScrollingMenu extends Menu {

  @Setter private int rows;
  @Setter private int startY;

  private int currentPage = 1;
  private Collection<ItemStack> items = new ArrayList<>();
  private ItemStack currentPageItemStack;
  private ItemStack nextPageItemStack;
  private ItemStack previousPageItemStack;

  @Inject
  protected ScrollingMenu(JavaPlugin plugin) {
    super(plugin);
  }

  public ItemStack getCurrentPageItemStack() {
    if (this.currentPageItemStack == null) {
      this.currentPageItemStack =
          ItemBuilder.normal(Material.PAPER)
              .setDisplayName("§7Site §e" + this.getCurrentPage() + " §7/ §e" + this.getLastPage())
              .setOnInventoryClickListener(
                  event -> {
                    event.setCancelled(true);
                    this.onCurrentPageItemClicked();
                  })
              .build();
    }

    return this.currentPageItemStack;
  }

  protected void onCurrentPageItemClicked() {}

  public ItemStack getNextPageItemStack() {
    if (this.nextPageItemStack == null) {
      this.nextPageItemStack =
          ItemBuilder.skull(SkullType.PLAYER)
              .setOwner(GameProfileBuilder.init(SkullItemBuilder.MHF_ARROWRIGHT))
              .setDisplayName("§eNext Site")
              .setOnInventoryClickListener(
                  event -> {
                    event.setCancelled(true);
                    this.nextPage();
                  },
                  250)
              .build();
    }

    return this.nextPageItemStack;
  }

  public ItemStack getPreviousPageItemStack() {
    if (this.previousPageItemStack == null) {
      this.previousPageItemStack =
          ItemBuilder.skull(SkullType.PLAYER)
              .setOwner(GameProfileBuilder.init(SkullItemBuilder.MHF_ARROWLEFT))
              .setDisplayName("§ePrevious Site")
              .setOnInventoryClickListener(
                  event -> {
                    event.setCancelled(true);
                    this.previousPage();
                  },
                  250)
              .build();
    }

    return this.previousPageItemStack;
  }

  private void updateCurrentPageItemStack() {
    Collection<Integer> slots = new ArrayList<>();
    if (this.getCurrentPageItemStack() != null) {
      // Search for ItemStack in current Inventory to update
      for (int i = 0; i < this.getInventory().getSize(); i++) {
        if (this.getInventory().getItem(i) == null) {
          continue;
        }

        if (this.getInventory().getItem(i).equals(this.getCurrentPageItemStack())) {
          slots.add(i);
        }
      }
    }

    this.currentPageItemStack =
        ItemBuilder.normal(Material.PAPER)
            .setDisplayName("§7Site §e" + this.getCurrentPage() + " §7/ §e" + this.getLastPage())
            .setOnInventoryClickListener(event -> event.setCancelled(true))
            .build();

    for (Integer slot : slots) {
      this.getInventory().setItem(slot, this.getCurrentPageItemStack());
    }
  }

  public void previousPage() {
    if (!this.canPrevious()) {
      return;
    }

    this.currentPage--;
    this.updateCurrentPageItemStack();
    this.updateContent();
  }

  public void nextPage() {
    if (!this.canNext()) {
      return;
    }

    this.currentPage++;
    this.updateCurrentPageItemStack();
    this.updateContent();
  }

  public boolean canNext() {
    return this.getCurrentPage() * getItemsPerPage() < this.getItems().size();
  }

  public int getLastPage() {
    return ((int) Math.ceil(this.getItems().size() / this.getItemsPerPage())) + 1;
  }

  public boolean canPrevious() {
    return this.getCurrentPage() > 1;
  }

  private int getItemsPerPage() {
    return this.getRows() * 9;
  }

  public void updateContent() {
    this.updateCurrentPageItemStack();

    List<ItemStack> visibleItems =
        this.getItems().stream()
            .skip(this.getItemsPerPage() * (this.getCurrentPage() - 1))
            .limit(this.getItemsPerPage() + (this.getStartY() * 9))
            .collect(Collectors.toList());

    for (int y = this.getStartY(); y < (this.getItemsPerPage() / 9) + this.getStartY(); y++) {
      for (int x = 0; x < 9; x++) {
        int itemIndex = (y * 9 + x);
        this.getInventory().clear(itemIndex);

        if (itemIndex >= visibleItems.size() + (this.getStartY() * 9)) {
          continue;
        }

        ItemStack item = visibleItems.get(itemIndex - (this.getStartY() * 9));
        this.getInventory().setItem(itemIndex, item);
      }
    }
  }
}
