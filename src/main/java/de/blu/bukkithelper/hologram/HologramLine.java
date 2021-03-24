package de.blu.bukkithelper.hologram;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.UUID;

@Getter
public class HologramLine {

  private UUID armorStandEntityUniqueId;
  private Location location;
  private String text = "";
  private boolean visible = true;

  HologramLine(Location location, String text) {
    this.location = location;

    // Load Chunk if location not loaded
    location.getChunk().load();

    // Set invisible ArmorStand
    this.spawnArmorStand();

    this.setText(text);
  }

  private void spawnArmorStand() {
    ArmorStand armorStand =
        (ArmorStand)
            this.getLocation()
                .getWorld()
                .spawnEntity(this.getLocation().clone().subtract(0, 2, 0), EntityType.ARMOR_STAND);
    armorStand.setArms(false);
    armorStand.setGravity(false);
    armorStand.setVisible(false);
    armorStand.setCustomNameVisible(true);
    this.armorStandEntityUniqueId = armorStand.getUniqueId();
  }

  public void setText(String text) {
    this.text = text;

    if (this.getArmorStand() != null) {
      this.getArmorStand().setCustomName(this.getText());
    }
  }

  public void hide() {
    if (!this.isVisible()) {
      return;
    }

    if (this.getArmorStand() != null) {
      this.getArmorStand().remove();
    }
    this.visible = false;
  }

  public void show() {
    if (this.isVisible()) {
      return;
    }

    if (this.getArmorStand() != null) {
      this.getArmorStand().setCustomName(this.getText());
    } else {
      this.spawnArmorStand();
      this.getArmorStand().setCustomName(this.getText());
    }
    this.visible = true;
  }

  private ArmorStand getArmorStand() {
    for (Entity entity : this.getLocation().getChunk().getEntities()) {
      if (entity.getUniqueId().equals(this.getArmorStandEntityUniqueId())) {
        return (ArmorStand) entity;
      }
    }

    return null;
  }
}
