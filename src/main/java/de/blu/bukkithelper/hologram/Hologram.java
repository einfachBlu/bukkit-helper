package de.blu.bukkithelper.hologram;

import lombok.Getter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Hologram {

  public static double LINE_DIFFERENCE = 0.3;

  private List<HologramLine> lines = new ArrayList<>();
  private boolean visible = true;

  public Hologram(Location location, String... lines) {
    for (int i = 0; i < lines.length; i++) {
      String line = lines[i];

      HologramLine hologramLine =
          new HologramLine(location.clone().subtract(0, i * LINE_DIFFERENCE, 0), line);
      this.getLines().add(hologramLine);
    }
  }

  public void setLine(int lineId, String text) {
    if (lineId >= this.getLines().size()) {
      throw new IllegalArgumentException("lineId must be <= number of lines");
    }

    this.getLines().get(lineId).setText(text);
  }

  public void hide() {
    if (!this.isVisible()) {
      return;
    }

    for (HologramLine line : this.getLines()) {
      line.hide();
    }
    this.visible = false;
  }

  public void show() {
    if (this.isVisible()) {
      return;
    }

    for (HologramLine line : this.getLines()) {
      line.show();
    }
    this.visible = true;
  }
}
