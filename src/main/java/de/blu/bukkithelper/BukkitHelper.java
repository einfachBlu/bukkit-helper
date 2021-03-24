package de.blu.bukkithelper;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import de.blu.bukkithelper.itembuilder.ItemBuilder;
import de.blu.bukkithelper.itembuilder.repository.ItemBuilderRepository;
import de.blu.bukkithelper.profile.GameProfileBuilder;
import de.blu.bukkithelper.profile.UUIDFetcher;
import de.blu.database.connection.redis.RedisConnection;
import de.blu.database.connection.redis.RedisConnectionProvider;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.Collection;
import java.util.HashSet;

@Singleton
public final class BukkitHelper extends JavaPlugin {
  private Collection<Class<?>> registeredListeners = new HashSet<>();
  private Injector injector;

  @Override
  public void onEnable() {
    if (!this.getConfig().contains("redis")) {
      this.getConfig().set("redis.host", "localhost");
      this.getConfig().set("redis.port", 6379);
      this.getConfig().set("redis.password", "");
      this.saveConfig();
    }

    // Init Guice
    this.injector =
        Guice.createInjector(
            new AbstractModule() {
              @Override
              protected void configure() {
                bind(JavaPlugin.class).toInstance(BukkitHelper.this);

                String host = getConfig().getString("redis.host");
                int port = getConfig().getInt("redis.port");
                String password = getConfig().getString("redis.password");
                RedisConnectionProvider redisConnection = new RedisConnectionProvider();
                redisConnection.init(host, port, password);
                redisConnection.connect();

                UUIDFetcher.setRedisConnection(redisConnection);
                GameProfileBuilder.setRedisConnection(redisConnection);

                // Bind
                bind(RedisConnection.class).toInstance(redisConnection);
              }
            });

    ItemBuilderRepository itemBuilderRepository =
        this.injector.getInstance(ItemBuilderRepository.class);
    ItemBuilder.setItemBuilderRepository(itemBuilderRepository);
    ItemBuilder.setInjector(this.injector);

    // Register Listener recursively
    this.registerListener("de.blu.bukkithelper.listener");
    this.getServer().getPluginManager().registerEvents(itemBuilderRepository, this);
  }

  private void registerListener(String packageName) {
    // Register Listener
    Reflections reflections =
        new Reflections(
            new ConfigurationBuilder()
                .filterInputsBy(
                    input -> {
                      if (input.contains("/")) {
                        return false;
                      }

                      if (!input.startsWith(packageName)) {
                        return false;
                      }

                      return true;
                    })
                .setUrls(ClasspathHelper.forPackage(packageName, this.getClass().getClassLoader()))
                .setScanners(new SubTypesScanner(false)));

    try {
      for (Class<?> listenerClass : reflections.getSubTypesOf(Listener.class)) {
        if (this.registeredListeners.contains(listenerClass)) {
          continue;
        }

        if (!listenerClass.getName().toLowerCase().startsWith((packageName))) {
          continue;
        }

        try {
          Listener listener = (Listener) injector.getInstance(listenerClass);
          injector.injectMembers(listener);

          Bukkit.getPluginManager().registerEvents(listener, this);
          System.out.println("Registered Listener " + listener.getClass().getSimpleName());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } catch (Exception ignored) {
      // Should only happen if no subtype was found
    }
  }
}
