package de.blu.bukkithelper.itembuilder;

import com.google.inject.Injector;
import de.blu.bukkithelper.itembuilder.repository.ItemBuilderRepository;
import de.blu.bukkithelper.util.GlowEnchantment;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@Getter
@Setter
public class ItemBuilder {

  @Getter private static GlowEnchantment glowEnchantment = new GlowEnchantment(69);
  @Getter @Setter private static Injector injector;
  @Getter @Setter private static ItemBuilderRepository itemBuilderRepository;

  private ItemStack item;
  private ItemMeta itemMeta;

  public ItemBuilder() {
    this.item = new ItemStack(Material.AIR);
    this.itemMeta = this.getItem().getItemMeta();
  }

  public static ItemBuilder air() {
    return ItemBuilder.getInjector()
        .getInstance(LeatherArmorItemBuilder.class)
        .setType(Material.AIR);
  }

  public static BookItemBuilder book() {
    return ItemBuilder.getInjector().getInstance(BookItemBuilder.class);
  }

  public static BookItemBuilder book(boolean writeable) {
    return ItemBuilder.getInjector().getInstance(BookItemBuilder.class).setWriteable(writeable);
  }

  public static PotionItemBuilder potion() {
    return ItemBuilder.getInjector().getInstance(PotionItemBuilder.class);
  }

  public static DefaultsItemBuilder defaults() {
    return ItemBuilder.getInjector().getInstance(DefaultsItemBuilder.class);
  }

  public static LeatherArmorItemBuilder armor() {
    return ItemBuilder.getInjector().getInstance(LeatherArmorItemBuilder.class);
  }

  public static LeatherArmorItemBuilder armor(LeatherArmorItemBuilder.ArmorElement armorElement) {
    return (LeatherArmorItemBuilder)
        ItemBuilder.getInjector()
            .getInstance(LeatherArmorItemBuilder.class)
            .setType(armorElement.getMaterial());
  }

  public static SkullItemBuilder skull() {
    return ItemBuilder.getInjector().getInstance(SkullItemBuilder.class);
  }

  public static SkullItemBuilder skull(SkullType skullType) {
    return (SkullItemBuilder)
        ItemBuilder.getInjector()
            .getInstance(SkullItemBuilder.class)
            .setDurability((short) skullType.ordinal());
  }

  public static ItemBuilder normal() {
    return ItemBuilder.getInjector().getInstance(LeatherArmorItemBuilder.class);
  }

  public static ItemBuilder normal(Material material) {
    return ItemBuilder.getInjector().getInstance(LeatherArmorItemBuilder.class).setType(material);
  }

  public static ItemBuilder normal(Material material, int amount) {
    return ItemBuilder.getInjector()
        .getInstance(LeatherArmorItemBuilder.class)
        .setType(material)
        .setAmount(amount);
  }

  public static ItemBuilder normal(Material material, int amount, short durability) {
    return ItemBuilder.getInjector()
        .getInstance(LeatherArmorItemBuilder.class)
        .setType(material)
        .setAmount(amount)
        .setDurability(durability);
  }

  public static ItemBuilder wrap(ItemStack itemStack) {
    if (itemStack.getType().equals(Material.SKULL)
        || itemStack.getType().equals(Material.SKULL_ITEM)) {
      SkullItemBuilder skullItemBuilder =
          ItemBuilder.getInjector().getInstance(SkullItemBuilder.class);
      skullItemBuilder.setItem(itemStack);
      skullItemBuilder.setItemMeta(itemStack.getItemMeta());
      return skullItemBuilder;
    }

    ItemBuilder builder = ItemBuilder.getInjector().getInstance(LeatherArmorItemBuilder.class);
    builder.setItem(itemStack);
    builder.setItemMeta(itemStack.getItemMeta());
    return builder;
  }

  public static ItemBuilder clone(ItemStack itemStack) {
    ItemBuilder builder = ItemBuilder.getInjector().getInstance(LeatherArmorItemBuilder.class);
    builder.setItem(itemStack.clone());
    builder.setItemMeta(itemStack.clone().getItemMeta());
    return builder;
  }

  public static ItemBuilder copy(ItemStack itemStack) {
    return ItemBuilder.wrap(itemStack.clone());
  }

  public ItemStack build() {
    this.getItem().setItemMeta(this.getItemMeta());
    return this.getItem();
  }

  public ItemBuilder setType(Material material) {
    this.build();

    this.getItem().setType(material);
    this.setItemMeta(this.getItem().getItemMeta());
    return this;
  }

  public ItemBuilder setAmount(int amount) {
    this.getItem().setAmount(amount);
    return this;
  }

  public ItemBuilder setDurability(short durability) {
    this.build();

    this.getItem().setDurability(durability);
    this.itemMeta = this.getItem().getItemMeta();
    return this;
  }

  public ItemBuilder setData(MaterialData data) {
    this.getItem().setData(data);
    return this;
  }

  public ItemBuilder setDisplayName(String displayName) {
    if (this.getItemMeta() == null) {
      return this;
    }

    this.getItemMeta().setDisplayName(displayName);
    return this;
  }

  public String getLoreEntry(int index) {
    if (this.getItemMeta() == null) {
      return "";
    }

    if (this.getItemMeta().getLore().size() <= index) {
      return "";
    }

    return this.getItemMeta().getLore().get(index);
  }

  public ItemBuilder setLoreEntry(int index, String line) {
    if (this.getItemMeta() == null) {
      return this;
    }

    if (index < 0) {
      return this;
    }

    List<String> lore = this.getItemMeta().getLore();

    if (lore.size() <= index) {
      for (int i = 0; i < index + 1; i++) {
        if (lore.size() <= i) {
          lore.add("");
        }
      }
    }
    lore.set(index, line);
    this.getItemMeta().setLore(lore);

    return this;
  }

  public ItemBuilder clearLore() {
    if (this.getItemMeta() == null) {
      return this;
    }

    this.getItemMeta().setLore(new ArrayList<>());
    return this;
  }

  public List<String> getLore() {
    if (this.getItemMeta() == null) {
      return new ArrayList<>();
    }

    return this.getItemMeta().getLore();
  }

  public ItemBuilder setLore(String... lines) {
    this.setLore(Arrays.asList(lines));
    return this;
  }

  public ItemBuilder setLore(List<String> lines) {
    if (this.getItemMeta() == null) {
      return this;
    }

    this.getItemMeta().setLore(lines);
    return this;
  }

  public ItemBuilder addItemFlags(ItemFlag... itemFlags) {
    this.getItemMeta().addItemFlags(itemFlags);
    return this;
  }

  public ItemBuilder removeItemFlags(ItemFlag... itemFlags) {
    this.getItemMeta().removeItemFlags(itemFlags);
    return this;
  }

  public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
    this.getItemMeta().addEnchant(enchantment, level, true);
    return this;
  }

  public ItemBuilder removeEnchantment(Enchantment enchantment) {
    this.getItemMeta().removeEnchant(enchantment);

    return this;
  }

  public ItemBuilder setGlow() {
    return this.setGlow(true);
  }

  public ItemBuilder setGlow(boolean value) {
    if (value) {
      this.addEnchantment(ItemBuilder.getGlowEnchantment(), 1);
    } else {
      this.removeEnchantment(ItemBuilder.getGlowEnchantment());
    }

    return this;
  }

  public ItemBuilder setUnbreakable() {
    return this.setUnbreakable(true);
  }

  public ItemBuilder setUnbreakable(boolean value) {
    this.storeNBTBoolean("Unbreakable", value);
    return this;
  }

  public ItemBuilder storeNBTBoolean(String key, boolean value) {
    NBTTagCompound tag = this.getOrCreateTag();
    tag.setBoolean(key, value);
    this.updateTag(tag);

    return this;
  }

  public boolean getNBTBoolean(String key) {
    NBTTagCompound tag = this.getOrCreateTag();

    if (!this.hasNBT(key)) {
      return false;
    }

    return tag.getBoolean(key);
  }

  public ItemBuilder storeNBTInt(String key, int value) {
    NBTTagCompound tag = this.getOrCreateTag();
    tag.setInt(key, value);
    this.updateTag(tag);

    return this;
  }

  public int getNBTInt(String key) {
    NBTTagCompound tag = this.getOrCreateTag();

    if (!this.hasNBT(key)) {
      return -1;
    }

    return tag.getInt(key);
  }

  public ItemBuilder storeNBTDouble(String key, double value) {
    NBTTagCompound tag = this.getOrCreateTag();
    tag.setDouble(key, value);
    this.updateTag(tag);

    return this;
  }

  public double getNBTDouble(String key) {
    NBTTagCompound tag = this.getOrCreateTag();

    if (!this.hasNBT(key)) {
      return -1;
    }

    return tag.getDouble(key);
  }

  public ItemBuilder storeNBTString(String key, String value) {
    NBTTagCompound tag = this.getOrCreateTag();
    tag.setString(key, value);
    this.updateTag(tag);

    return this;
  }

  public String getNBTString(String key) {
    NBTTagCompound tag = this.getOrCreateTag();

    if (!this.hasNBT(key)) {
      return "";
    }

    return tag.getString(key);
  }

  public ItemBuilder storeNBTList(String key, List<String> value) {
    // NBTItem nbtItem = new NBTItem(this.getItem());
    NBTTagCompound tag = this.getOrCreateTag();

    // Size
    tag.setInt(key, value.size());

    // Clear old entries
    for (int i = 0; i < 500; i++) {
      tag.remove(key + i);
    }

    // Set new entries
    for (int i = 0; i < value.size(); i++) {
      tag.setString(key + i, value.get(i));
    }

    this.updateTag(tag);
    return this;
  }

  public List<String> getNBTList(String key) {
    NBTTagCompound tag = this.getOrCreateTag();

    List<String> list = new ArrayList<>();

    for (int i = 0; i < 500; i++) {
      if (!this.hasNBT(key + i)) {
        return list;
      }

      list.add(tag.getString(key + i));
    }

    return list;
  }

  public boolean hasNBT(String key) {
    NBTTagCompound tag = this.getOrCreateTag();

    return tag.hasKey(key);
  }

  public ItemBuilder setOnEntityHitListener(Consumer<EntityDamageByEntityEvent> listener) {
    this.build();

    int id = ItemBuilder.getItemBuilderRepository().getClickableItemId(this.getItem());
    if (id == -1) {
      this.item = ItemBuilder.getItemBuilderRepository().initClickableItem(this.getItem());
      this.itemMeta = this.getItem().getItemMeta();
    }

    ItemBuilder.getItemBuilderRepository().getEntityHitItemListener().put(id, listener);
    return this;
  }

  public ItemBuilder setOnEntityHitListener(
      Consumer<EntityDamageByEntityEvent> listener, long cooldown) {
    this.setOnEntityHitListener(listener);

    int id = ItemBuilder.getItemBuilderRepository().getClickableItemId(this.getItem());
    if (id == -1) {
      this.item = ItemBuilder.getItemBuilderRepository().initClickableItem(this.getItem());
      this.itemMeta = this.getItem().getItemMeta();
    }

    ItemBuilder.getItemBuilderRepository()
        .getCooldown()
        .put(ItemBuilder.getItemBuilderRepository().getClickableItemId(this.getItem()), cooldown);
    return this;
  }

  public ItemBuilder setOnRightClickListener(Consumer<PlayerInteractEvent> listener) {
    this.build();

    int id = ItemBuilder.getItemBuilderRepository().getClickableItemId(this.getItem());
    if (id == -1) {
      this.item = ItemBuilder.getItemBuilderRepository().initClickableItem(this.getItem());
      this.itemMeta = this.getItem().getItemMeta();
    }

    ItemBuilder.getItemBuilderRepository()
        .getRightClickItemListener()
        .put(ItemBuilder.getItemBuilderRepository().getClickableItemId(this.getItem()), listener);
    return this;
  }

  public ItemBuilder setOnRightClickListener(
      Consumer<PlayerInteractEvent> listener, long cooldown) {
    this.setOnRightClickListener(listener);

    int id = ItemBuilder.getItemBuilderRepository().getClickableItemId(this.getItem());
    if (id == -1) {
      this.item = ItemBuilder.getItemBuilderRepository().initClickableItem(this.getItem());
      this.itemMeta = this.getItem().getItemMeta();
    }

    ItemBuilder.getItemBuilderRepository()
        .getCooldown()
        .put(ItemBuilder.getItemBuilderRepository().getClickableItemId(this.getItem()), cooldown);
    return this;
  }

  public ItemBuilder setOnLeftClickListener(Consumer<PlayerInteractEvent> listener) {
    this.build();

    int id = ItemBuilder.getItemBuilderRepository().getClickableItemId(this.getItem());
    if (id == -1) {
      this.item = ItemBuilder.getItemBuilderRepository().initClickableItem(this.getItem());
      this.itemMeta = this.getItem().getItemMeta();
    }

    ItemBuilder.getItemBuilderRepository()
        .getLeftClickItemListener()
        .put(ItemBuilder.getItemBuilderRepository().getClickableItemId(this.getItem()), listener);
    return this;
  }

  public ItemBuilder setOnLeftClickListener(Consumer<PlayerInteractEvent> listener, long cooldown) {
    this.setOnLeftClickListener(listener);

    int id = ItemBuilder.getItemBuilderRepository().getClickableItemId(this.getItem());
    if (id == -1) {
      this.item = ItemBuilder.getItemBuilderRepository().initClickableItem(this.getItem());
      this.itemMeta = this.getItem().getItemMeta();
    }

    ItemBuilder.getItemBuilderRepository()
        .getCooldown()
        .put(ItemBuilder.getItemBuilderRepository().getClickableItemId(this.getItem()), cooldown);
    return this;
  }

  public ItemBuilder setOnInventoryClickListener(Consumer<InventoryClickEvent> listener) {
    this.build();

    int id = ItemBuilder.getItemBuilderRepository().getClickableItemId(this.getItem());
    if (id == -1) {
      this.item = ItemBuilder.getItemBuilderRepository().initClickableItem(this.getItem());
      this.itemMeta = this.getItem().getItemMeta();
    }

    id = ItemBuilder.getItemBuilderRepository().getClickableItemId(this.getItem());

    ItemBuilder.getItemBuilderRepository().getInventoryClickItemListener().put(id, listener);
    return this;
  }

  public ItemBuilder setOnInventoryClickListener(
      Consumer<InventoryClickEvent> listener, long cooldown) {
    this.setOnInventoryClickListener(listener);

    int id = ItemBuilder.getItemBuilderRepository().getClickableItemId(this.getItem());
    if (id == -1) {
      this.item = ItemBuilder.getItemBuilderRepository().initClickableItem(this.getItem());
      this.itemMeta = this.getItem().getItemMeta();
    }

    ItemBuilder.getItemBuilderRepository()
        .getCooldown()
        .put(ItemBuilder.getItemBuilderRepository().getClickableItemId(this.getItem()), cooldown);
    return this;
  }

  public NBTTagCompound getOrCreateTag() {
    this.build();

    net.minecraft.server.v1_8_R3.ItemStack stack = CraftItemStack.asNMSCopy(this.getItem());
    if (stack.getTag() != null) {
      return stack.getTag();
    }

    NBTTagCompound tag = new NBTTagCompound();
    stack.setTag(tag);
    stack.save(tag);
    this.item = CraftItemStack.asBukkitCopy(stack);
    // this.item = CraftItemStack.asCraftMirror(stack);
    this.itemMeta = this.getItem().getItemMeta();

    return tag;
  }

  private void updateTag(NBTTagCompound tag) {
    this.build();

    net.minecraft.server.v1_8_R3.ItemStack stack = CraftItemStack.asNMSCopy(this.getItem());
    stack.setTag(tag);
    this.item = CraftItemStack.asBukkitCopy(stack);
    // this.item = CraftItemStack.asCraftMirror(stack);
    this.itemMeta = this.getItem().getItemMeta();
  }
}
