package de.blu.bukkithelper.repository;

import com.google.inject.Singleton;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
@Singleton
public final class ChatMessageReaderRepository extends MapRepository<UUID, List<Consumer<String>>> {

  public void addCallback(UUID playerUniqueId, Consumer<String> callback) {
    if (!this.containsKey(playerUniqueId)) {
      this.put(playerUniqueId, new ArrayList<>());
    }

    this.get(playerUniqueId).add(callback);
  }

  public void addCallback(UUID playerUniqueId, Consumer<String> callback, long timeoutMillis) {
    if (!this.containsKey(playerUniqueId)) {
      this.put(playerUniqueId, new ArrayList<>());
    }

    this.get(playerUniqueId).add(callback);

    // TODO: add timeout
  }

  public void removeCallback(UUID playerUniqueId, Consumer<String> callback) {
    if (!this.containsKey(playerUniqueId)) {
      return;
    }

    this.get(playerUniqueId).remove(callback);
  }

  public Consumer<String> getNextCallback(UUID playerUniqueId) {
    if (!this.containsKey(playerUniqueId)) {
      return null;
    }

    List<Consumer<String>> callbacks = this.get(playerUniqueId);
    if (callbacks.size() == 0) {
      return null;
    }

    return callbacks.iterator().next();
  }
}
