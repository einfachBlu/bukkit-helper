package de.blu.bukkithelper.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.blu.database.connection.redis.RedisConnection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@Singleton
public final class TestListener implements Listener {

  @Inject private RedisConnection redisConnection;

  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    Player player = e.getPlayer();
  }
}
