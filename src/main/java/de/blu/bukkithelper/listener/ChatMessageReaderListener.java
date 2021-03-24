package de.blu.bukkithelper.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.blu.bukkithelper.repository.ChatMessageReaderRepository;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.function.Consumer;

@Singleton
@Getter
public class ChatMessageReaderListener implements Listener {

  @Inject private ChatMessageReaderRepository chatMessageReaderRepository;

  @EventHandler(priority = EventPriority.LOWEST)
  public void handleMessageReader(AsyncPlayerChatEvent e) {
    Player player = e.getPlayer();
    String message = e.getMessage();

    if (!this.getChatMessageReaderRepository().containsKey(player.getUniqueId())) {
      return;
    }

    Consumer<String> callback =
        this.getChatMessageReaderRepository().getNextCallback(player.getUniqueId());
    if (callback == null) {
      return;
    }

    this.getChatMessageReaderRepository().removeCallback(player.getUniqueId(), callback);
    callback.accept(message);
    e.setCancelled(true);
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    Player player = e.getPlayer();

    if (!this.getChatMessageReaderRepository().containsKey(player.getUniqueId())) {
      return;
    }

    this.getChatMessageReaderRepository().remove(player.getUniqueId());
  }
}
