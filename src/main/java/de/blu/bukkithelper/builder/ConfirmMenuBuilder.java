package de.blu.bukkithelper.builder;

import com.google.inject.Injector;
import de.blu.bukkithelper.menu.ConfirmMenu;
import de.blu.bukkithelper.util.InjectorProvider;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;

public final class ConfirmMenuBuilder {

  private final ConfirmMenu confirmMenu;

  public ConfirmMenuBuilder() {
    Injector injector = InjectorProvider.get();
    this.confirmMenu = injector.getInstance(ConfirmMenu.class);
  }

  public ConfirmMenuBuilder setPlayer(Player player) {
    this.confirmMenu.setPlayer(player);
    return this;
  }

  public ConfirmMenuBuilder setCallback(Consumer<ConfirmMenu.Result> callback) {
    this.confirmMenu.setCallback(callback);
    return this;
  }

  public ConfirmMenuBuilder setInformationLines(List<String> informationLines) {
    this.confirmMenu.setInformationLines(informationLines);
    return this;
  }

  public ConfirmMenuBuilder addInformationLine(String informationLine) {
    this.confirmMenu.getInformationLines().add(informationLine);
    return this;
  }

  public ConfirmMenu build() {
    return this.confirmMenu;
  }
}
