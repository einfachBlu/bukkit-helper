package de.blu.bukkithelper.itembuilder;

import com.google.common.base.Suppliers;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.blu.bukkithelper.profile.GameProfileBuilder;
import de.blu.bukkithelper.profile.UUIDFetcher;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.function.Supplier;

public final class SkullItemBuilder extends ItemBuilder {

  public static final String MHF_ARROWLEFT =
      "{\"id\":\"a68f0b648d144000a95f4b9ba14f8df9\",\"name\":\"MHF_ArrowLeft\",\"properties\":[{\"name\":\"textures\",\"value\":\"eyJ0aW1lc3RhbXAiOjE1ODE4MDM1NTY0ODUsInByb2ZpbGVJZCI6ImE2OGYwYjY0OGQxNDQwMDBhOTVmNGI5YmExNGY4ZGY5IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dMZWZ0IiwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Y3YWFjYWQxOTNlMjIyNjk3MWVkOTUzMDJkYmE0MzM0MzhiZTQ2NDRmYmFiNWViZjgxODA1NDA2MTY2N2ZiZTIifX19\"}]}";
  public static final String MHF_ARROWRIGHT =
      "{\"id\":\"50c8510b5ea04d60be9a7d542d6cd156\",\"name\":\"MHF_ArrowRight\",\"properties\":[{\"name\":\"textures\",\"value\":\"eyJ0aW1lc3RhbXAiOjE1ODE4MDM3MDUyNTIsInByb2ZpbGVJZCI6IjUwYzg1MTBiNWVhMDRkNjBiZTlhN2Q1NDJkNmNkMTU2IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dSaWdodCIsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kMzRlZjA2Mzg1MzcyMjJiMjBmNDgwNjk0ZGFkYzBmODVmYmUwNzU5ZDU4MWFhN2ZjZGYyZTQzMTM5Mzc3MTU4In19fQ==\"}]}";
  public static final String MHF_ARROWUP =
      "{\"id\":\"fef039efe6cd49879c8426a3e6134277\",\"name\":\"MHF_ArrowUp\",\"properties\":[{\"name\":\"textures\",\"value\":\"ewogICJ0aW1lc3RhbXAiIDogMTYxMzEyODA2MzczNiwKICAicHJvZmlsZUlkIiA6ICJmZWYwMzllZmU2Y2Q0OTg3OWM4NDI2YTNlNjEzNDI3NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQXJyb3dVcCIsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9hMTU2YjMxY2JmOGY3NzQ1NDdkYzNmOTcxM2E3NzBlY2M1YzcyN2Q5NjdjYjAwOTNmMjY1NDZiOTIwNDU3Mzg3IgogICAgfQogIH0KfQ==\"}]}";
  public static final String MHF_ARROWDOWN =
      "{\"id\":\"68f59b9b5b0b4b05a9f2e1d1405aa348\",\"name\":\"MHF_ArrowDown\",\"properties\":[{\"name\":\"textures\",\"value\":\"ewogICJ0aW1lc3RhbXAiIDogMTYxMzEyODE0NDcyMywKICAicHJvZmlsZUlkIiA6ICI2OGY1OWI5YjViMGI0YjA1YTlmMmUxZDE0MDVhYTM0OCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQXJyb3dEb3duIiwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ZlM2Q3NTVjZWNiYjEzYTM5ZThlOTM1NDgyM2E5YTAyYTAxZGNlMGFjYTY4ZmZkNDJlM2VhOWE5ZDI5ZTJkZjIiCiAgICB9CiAgfQp9\"}]}";
  public static final String MHF_EXCLAMATION =
      "{\"id\":\"d3c47f6fae3a45c1ad7ce2c762b03ae6\",\"name\":\"MHF_Exclamation\",\"properties\":[{\"name\":\"textures\",\"value\":\"eyJ0aW1lc3RhbXAiOjE1ODE4MDM4MDYwNDYsInByb2ZpbGVJZCI6ImQzYzQ3ZjZmYWUzYTQ1YzFhZDdjZTJjNzYyYjAzYWU2IiwicHJvZmlsZU5hbWUiOiJNSEZfRXhjbGFtYXRpb24iLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDBiMDVlNjk5ZDI4YjNhMjc4YTkyZDE2OWRjYTlkNTdjMDc5MWQwNzk5NGQ4MmRlM2Y5ZWQ0YTQ4YWZlMGUxZCJ9fX0=\"}]}";
  public static final String MHF_ALEX =
      "{\"id\":\"6ab4317889fd490597f60f67d9d76fd9\",\"name\":\"MHF_Alex\",\"properties\":[{\"name\":\"textures\",\"value\":\"eyJ0aW1lc3RhbXAiOjE1ODE4MDM4ODE1OTcsInByb2ZpbGVJZCI6IjZhYjQzMTc4ODlmZDQ5MDU5N2Y2MGY2N2Q5ZDc2ZmQ5IiwicHJvZmlsZU5hbWUiOiJNSEZfQWxleCIsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84M2NlZTVjYTZhZmNkYjE3MTI4NWFhMDBlODA0OWMyOTdiMmRiZWJhMGVmYjhmZjk3MGE1Njc3YTFiNjQ0MDMyIiwibWV0YWRhdGEiOnsibW9kZWwiOiJzbGltIn19fX0=\"}]}";
  public static final String MHF_STEVE =
      "{\"id\":\"c06f89064c8a49119c29ea1dbd1aab82\",\"name\":\"MHF_Steve\",\"properties\":[{\"name\":\"textures\",\"value\":\"eyJ0aW1lc3RhbXAiOjE1ODE4MDM5ODUxMzUsInByb2ZpbGVJZCI6ImMwNmY4OTA2NGM4YTQ5MTE5YzI5ZWExZGJkMWFhYjgyIiwicHJvZmlsZU5hbWUiOiJNSEZfU3RldmUiLCJ0ZXh0dXJlcyI6e319\"}]}";
  // public static final String MHF_TEMPLATE = "";

  private static final Supplier<Field> skullOwnerField =
      Suppliers.memoize(
              () -> {
                try {
                  ItemStack itemStack =
                      new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
                  Class<?> clazz = itemStack.getItemMeta().getClass();
                  Field field = clazz.getDeclaredField("profile");
                  field.setAccessible(true);
                  return field;
                } catch (Throwable t) {
                  throw new RuntimeException(t);
                }
              })
          ::get;

  public SkullItemBuilder() {
    super();
    this.setType(Material.SKULL_ITEM);
    this.type(SkullType.PLAYER);
  }

  public SkullItemBuilder setOwner(String owner) {
    try {
      ((SkullMeta) this.getItemMeta()).setOwner(owner);
    } catch (Exception e) {
      // too many requests
    }

    return this.setOwner(UUIDFetcher.getUUID(owner));
  }

  public SkullItemBuilder setOwner(UUID owner) {
    if (owner == null) {
      return this.setOwner((GameProfile) null);
    }

    return this.setOwner(GameProfileBuilder.fetch(owner));
  }

  public SkullItemBuilder setOwner(GameProfile profile) {
    this.type(SkullType.PLAYER);

    if (profile == null) {
      return this;
    }

    try {
      SkullItemBuilder.skullOwnerField.get().set(this.getItemMeta(), profile);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return this;
  }

  public SkullItemBuilder texture(String textureUrlBase64) {
    // Sets the textures of this skull
    GameProfile profile = new GameProfile(UUID.randomUUID(), null);
    profile.getProperties().put("textures", new Property("textures", textureUrlBase64));
    return this.setOwner(profile);
  }

  public SkullItemBuilder type(SkullType skullType) {
    this.setDurability((short) skullType.ordinal());
    return this;
  }
}
