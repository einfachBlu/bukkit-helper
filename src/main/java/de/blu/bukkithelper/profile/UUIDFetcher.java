package de.blu.bukkithelper.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.blu.database.connection.redis.RedisConnection;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UUIDFetcher {

  private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s";
  private static final String NAME_URL = "https://api.mojang.com/user/profiles/%s/names";
  private static Gson gson =
      new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();

  @Setter private static RedisConnection redisConnection;
  @Getter private static Map<String, UUID> uuidCache = new HashMap<>();
  @Getter private static Map<UUID, String> nameCache = new HashMap<>();

  public static String getName(UUID uuid) {
    if (UUIDFetcher.getNameCache().containsKey(uuid)) {
      return UUIDFetcher.getNameCache().get(uuid);
    }

    if (UUIDFetcher.redisConnection.contains("playerdata.namecache." + uuid.toString())) {
      try {
        String name = UUIDFetcher.redisConnection.get("playerdata.namecache." + uuid.toString());
        UUIDFetcher.getNameCache().put(uuid, name);
        UUIDFetcher.getUuidCache().put(name.toLowerCase(), uuid);
        return name;
      } catch (Exception ignored) {
      }
    }

    try {
      HttpURLConnection connection =
          (HttpURLConnection)
              new URL(String.format(UUIDFetcher.NAME_URL, UUIDTypeAdapter.fromUUID(uuid)))
                  .openConnection();
      connection.setReadTimeout(5000);

      Data[] nameHistory =
          UUIDFetcher.gson.fromJson(
              new BufferedReader(new InputStreamReader(connection.getInputStream())), Data[].class);
      Data currentNameData = nameHistory[nameHistory.length - 1];

      if (nameHistory.length == 0 || currentNameData == null) {
        return null;
      }

      // Cache local
      UUIDFetcher.getNameCache().put(uuid, currentNameData.name);
      UUIDFetcher.getUuidCache().put(currentNameData.name.toLowerCase(), uuid);

      // Cache in Redis for 24h
      UUIDFetcher.redisConnection.set(
          "playerdata.namecache." + uuid.toString(), currentNameData.name);
      UUIDFetcher.redisConnection.set(
          "playerdata.uuidcache." + currentNameData.name.toLowerCase(), uuid.toString());

      return currentNameData.name;
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return null;
  }

  public static UUID getUUID(String name) {
    if (UUIDFetcher.getUuidCache().containsKey(name.toLowerCase())) {
      return UUIDFetcher.getUuidCache().get(name.toLowerCase());
    }

    if (UUIDFetcher.redisConnection.contains("playerdata.uuidcache." + name.toLowerCase())) {
      try {
        UUID uuid =
            UUID.fromString(
                UUIDFetcher.redisConnection.get("playerdata.uuidcache." + name.toLowerCase()));
        UUIDFetcher.getUuidCache().put(name.toLowerCase(), uuid);
        UUIDFetcher.getNameCache().put(uuid, name);
        return uuid;
      } catch (Exception ignored) {
      }
    }

    try {
      URL api = new URL(String.format(UUIDFetcher.UUID_URL, name));
      BufferedReader reader = new BufferedReader(new InputStreamReader(api.openStream()));
      Data data = UUIDFetcher.gson.fromJson(reader, Data.class);

      if (data == null) {
        return null;
      }

      // Cache local
      UUIDFetcher.getUuidCache().put(data.name.toLowerCase(), data.id);
      UUIDFetcher.getNameCache().put(data.id, data.name);

      // Cache in Redis for 24h
      UUIDFetcher.redisConnection.set(
          "playerdata.uuidcache." + data.name.toLowerCase(), data.id.toString());
      UUIDFetcher.redisConnection.set("playerdata.namecache." + data.id.toString(), data.name);

      return data.id;
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return null;
  }

  public static void cachePlayer(Player player) {
    // Cache local
    UUIDFetcher.getNameCache().put(player.getUniqueId(), player.getName().toLowerCase());
    UUIDFetcher.getUuidCache().put(player.getName().toLowerCase(), player.getUniqueId());

    // Cache in Redis for 24h
    UUIDFetcher.redisConnection.set(
        "playerdata.uuidcache." + player.getName().toLowerCase(), player.getUniqueId().toString());
    UUIDFetcher.redisConnection.set(
        "playerdata.namecache." + player.getUniqueId().toString(), player.getName().toLowerCase());
  }

  private class Data {

    private String name;
    private UUID id;

    @Override
    public String toString() {
      return "Data{" + "name='" + name + '\'' + ", id=" + id + '}';
    }
  }
}
