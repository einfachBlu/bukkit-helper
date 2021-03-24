package de.blu.bukkithelper.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.blu.bukkithelper.repository.ChatMessageReaderRepository;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

@Singleton
public final class ChatMessageReader {

  @Inject
  @Getter(AccessLevel.PRIVATE)
  private ChatMessageReaderRepository chatMessageReaderRepository;

  public void read(Player player, Consumer<String> callback) {
    this.getChatMessageReaderRepository().addCallback(player.getUniqueId(), callback);
  }

  public void clear(Player player) {
    this.getChatMessageReaderRepository().remove(player.getUniqueId());
  }
}
