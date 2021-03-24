package de.blu.bukkithelper.itembuilder.repository;

import com.google.inject.Singleton;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Singleton
@Getter
public final class ItemBuilderRepository implements Listener {
  public static final AtomicInteger STATIC_ID = new AtomicInteger(0);
  private Map<Integer, Consumer<PlayerInteractEvent>> leftClickItemListener = new HashMap<>();
  private Map<Integer, Consumer<PlayerInteractEvent>> rightClickItemListener = new HashMap<>();
  private Map<Integer, Consumer<EntityDamageByEntityEvent>> entityHitItemListener = new HashMap<>();
  private Map<Integer, Consumer<InventoryClickEvent>> inventoryClickItemListener = new HashMap<>();
  private Map<Integer, Long> lastUsage = new HashMap<>();
  private Map<Integer, Long> cooldown = new HashMap<>();

  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
    if (!(e.getDamager() instanceof Player)) {
      return;
    }

    Player damager = (Player) e.getDamager();

    if (damager.getItemInHand() == null || damager.getItemInHand().getType() == Material.AIR) {
      return;
    }

    Consumer<EntityDamageByEntityEvent> consumer = null;
    if (this.getEntityHitItemListener()
        .containsKey(this.getClickableItemId(damager.getItemInHand()))) {
      consumer =
          this.getEntityHitItemListener().get(this.getClickableItemId(damager.getItemInHand()));
    }

    if (consumer == null) {
      return;
    }

    if (!this.getCooldown().containsKey(this.getClickableItemId(damager.getItemInHand()))) {
      consumer.accept(e);
      return;
    }

    long cooldown = this.getCooldown().get(this.getClickableItemId(damager.getItemInHand()));
    if (!this.getLastUsage().containsKey(this.getClickableItemId(damager.getItemInHand()))) {
      consumer.accept(e);
      this.getLastUsage()
          .put(this.getClickableItemId(damager.getItemInHand()), System.currentTimeMillis());
      return;
    }

    if (System.currentTimeMillis()
            - this.getLastUsage().get(this.getClickableItemId(damager.getItemInHand()))
        < cooldown) {
      e.setCancelled(true);
      return;
    }

    consumer.accept(e);
    this.getLastUsage()
        .put(this.getClickableItemId(damager.getItemInHand()), System.currentTimeMillis());
  }

  @EventHandler
  public void onClick(InventoryClickEvent e) {
    if (e.getClickedInventory() == null) {
      return;
    }

    if (e.getCurrentItem() == null) {
      return;
    }

    ItemStack itemStack = e.getCurrentItem();

    if (this.getInventoryClickItemListener().containsKey(this.getClickableItemId(itemStack))) {
      if (!this.getCooldown().containsKey(this.getClickableItemId(itemStack))) {
        this.getInventoryClickItemListener().get(this.getClickableItemId(itemStack)).accept(e);
        return;
      }

      long cooldown = this.getCooldown().get(this.getClickableItemId(itemStack));
      if (!this.getLastUsage().containsKey(this.getClickableItemId(itemStack))) {
        this.getInventoryClickItemListener().get(this.getClickableItemId(itemStack)).accept(e);
        this.getLastUsage().put(this.getClickableItemId(itemStack), System.currentTimeMillis());
        return;
      }

      if (System.currentTimeMillis() - this.getLastUsage().get(this.getClickableItemId(itemStack))
          < cooldown) {
        e.setCancelled(true);
        return;
      }

      this.getInventoryClickItemListener().get(this.getClickableItemId(itemStack)).accept(e);
      this.getLastUsage().put(this.getClickableItemId(itemStack), System.currentTimeMillis());
    }
  }

  @EventHandler
  public void onInteract(PlayerInteractEvent e) {
    if (e.getPlayer().getItemInHand() == null
        || e.getPlayer().getItemInHand().getType() == Material.AIR) {
      return;
    }

    Consumer<PlayerInteractEvent> consumer = null;
    ItemStack itemStack = e.getPlayer().getItemInHand();

    if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
      if (this.getLeftClickItemListener().containsKey(this.getClickableItemId(itemStack))) {
        consumer = this.getLeftClickItemListener().get(this.getClickableItemId(itemStack));
      }
    }

    if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
      if (this.getRightClickItemListener().containsKey(this.getClickableItemId(itemStack))) {
        consumer = this.getRightClickItemListener().get(this.getClickableItemId(itemStack));
      }
    }

    if (consumer == null) {
      return;
    }

    if (!this.getCooldown().containsKey(this.getClickableItemId(itemStack))) {
      consumer.accept(e);
      return;
    }

    long cooldown = this.getCooldown().get(this.getClickableItemId(itemStack));
    if (!this.getLastUsage().containsKey(this.getClickableItemId(itemStack))) {
      consumer.accept(e);
      this.getLastUsage().put(this.getClickableItemId(itemStack), System.currentTimeMillis());
      return;
    }

    if (System.currentTimeMillis() - this.getLastUsage().get(this.getClickableItemId(itemStack))
        < cooldown) {
      e.setCancelled(true);
      return;
    }

    consumer.accept(e);
    this.getLastUsage().put(this.getClickableItemId(itemStack), System.currentTimeMillis());
  }

  public Integer getClickableItemId(ItemStack itemStack) {
    if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
      return -1;
    }

    net.minecraft.server.v1_8_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
    if (nmsItemStack == null) {
      return -1;
    }

    NBTTagCompound tag = nmsItemStack.getTag();

    if (tag == null || !tag.hasKey("ClickableItemId")) {
      return -1;
    }

    return tag.getInt("ClickableItemId");
  }

  public synchronized ItemStack initClickableItem(ItemStack itemStack) {
    if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
      return null;
    }

    net.minecraft.server.v1_8_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
    if (nmsItemStack == null) {
      System.out.println("ItemStack is not null but cant be initialized by nms!");
      System.out.println(itemStack);
      return null;
    }

    NBTTagCompound tag = nmsItemStack.getTag();

    if (tag == null) {
      tag = new NBTTagCompound();
    }

    if (!tag.hasKey("ClickableItemId")) {
      tag.setInt("ClickableItemId", STATIC_ID.incrementAndGet());
      nmsItemStack.setTag(tag);
    }

    return CraftItemStack.asBukkitCopy(nmsItemStack);
  }
}
