package de.blu.bukkithelper.profile;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import de.blu.database.connection.redis.RedisConnection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

public class GameProfileBuilder {

  private static final String SERVICE_URL =
      "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";
  private static final String JSON_SKIN =
      "{\"timestamp\":%d,\"profileId\":\"%s\",\"profileName\":\"%s\",\"isPublic\":true,\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}";
  private static final String JSON_CAPE =
      "{\"timestamp\":%d,\"profileId\":\"%s\",\"profileName\":\"%s\",\"isPublic\":true,\"textures\":{\"SKIN\":{\"url\":\"%s\"},\"CAPE\":{\"url\":\"%s\"}}}";

  private static Gson gson =
      new GsonBuilder()
          .disableHtmlEscaping()
          .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
          .registerTypeAdapter(GameProfile.class, new GameProfileSerializer())
          .registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer())
          .create();

  @Setter private static RedisConnection redisConnection;

  @Getter(AccessLevel.PUBLIC)
  private static HashMap<UUID, GameProfile> cachedProfiles = new HashMap<>();

  /**
   * Fetches the GameProfile from the Mojang servers
   *
   * @param uuid The player uuid
   * @return The GameProfile
   * @see GameProfile
   */
  public static GameProfile fetch(UUID uuid) {
    return GameProfileBuilder.fetch(uuid, false);
  }

  /**
   * Fetches the GameProfile from the Mojang servers
   *
   * @param uuid The player uuid
   * @param forceNew If true the cache is ignored
   * @return The GameProfile
   * @see GameProfile
   */
  public static GameProfile fetch(UUID uuid, boolean forceNew) {
    if (!forceNew && GameProfileBuilder.getCachedProfiles().containsKey(uuid)) {
      return GameProfileBuilder.getCachedProfiles().get(uuid);
    }

    if (!forceNew
        && GameProfileBuilder.redisConnection.contains(
            "playerdata.profilecache." + uuid.toString())) {
      String json =
          GameProfileBuilder.redisConnection.get("playerdata.profilecache." + uuid.toString());
      return GameProfileBuilder.init(json);
    }

    StringBuilder json = new StringBuilder();
    try {
      HttpURLConnection connection =
          (HttpURLConnection)
              new URL(String.format(GameProfileBuilder.SERVICE_URL, UUIDTypeAdapter.fromUUID(uuid)))
                  .openConnection();
      connection.setReadTimeout(5000);

      if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
        JsonObject error =
            (JsonObject)
                new JsonParser()
                    .parse(
                        new BufferedReader(new InputStreamReader(connection.getErrorStream()))
                            .readLine());
        throw new IOException(
            error.get("error").getAsString() + ": " + error.get("errorMessage").getAsString());
      }

      BufferedReader bufferedReader =
          new BufferedReader(new InputStreamReader(connection.getInputStream()));
      json = new StringBuilder();
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        json.append(line);
      }

      GameProfile result = GameProfileBuilder.gson.fromJson(json.toString(), GameProfile.class);

      // Cache local
      GameProfileBuilder.getCachedProfiles().put(uuid, result);

      // Cache in Redis
      GameProfileBuilder.redisConnection.set(
          "playerdata.profilecache." + uuid.toString(), json.toString(), 12 * 60 * 60); // 12h

      return result;
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(
          "Debug url='"
              + String.format(GameProfileBuilder.SERVICE_URL, UUIDTypeAdapter.fromUUID(uuid))
              + "'");
      System.out.println("Debug json='" + json.toString() + "'");
    }

    return null;
  }

  public static GameProfile init(String json) {
    GameProfile result = GameProfileBuilder.gson.fromJson(json, GameProfile.class);

    // Cache local
    GameProfileBuilder.getCachedProfiles().put(result.getId(), result);

    // Cache in Redis
    GameProfileBuilder.redisConnection.set(
        "playerdata.profilecache." + result.getId().toString(), json, 12 * 60 * 60); // 12h

    return result;
  }

  public static void cachePlayer(Player player) {
    GameProfile gameProfile = ((CraftPlayer) player).getProfile();

    // Cache local
    GameProfileBuilder.getCachedProfiles().put(gameProfile.getId(), gameProfile);

    String json = GameProfileBuilder.gson.toJson(gameProfile);

    // Cache in Redis
    GameProfileBuilder.redisConnection.set(
        "playerdata.profilecache." + gameProfile.getId().toString(), json, 12 * 60 * 60); // 12h
  }

  /**
   * Builds a GameProfile for the specified args
   *
   * @param uuid The uuid
   * @param name The name
   * @param skin The url from the skin image
   * @return A GameProfile built from the arguments
   * @see GameProfile
   */
  public static GameProfile getProfile(UUID uuid, String name, String skin) {
    return GameProfileBuilder.getProfile(uuid, name, skin, null);
  }

  /**
   * Builds a GameProfile for the specified args
   *
   * @param uuid The uuid
   * @param name The name
   * @param skinUrl Url from the skin image
   * @param capeUrl Url from the cape image
   * @return A GameProfile built from the arguments
   * @see GameProfile
   */
  public static GameProfile getProfile(UUID uuid, String name, String skinUrl, String capeUrl) {
    GameProfile profile = new GameProfile(uuid, name);
    boolean cape = capeUrl != null && !capeUrl.isEmpty();

    List<Object> args = new ArrayList<>();
    args.add(System.currentTimeMillis());
    args.add(UUIDTypeAdapter.fromUUID(uuid));
    args.add(name);
    args.add(skinUrl);
    if (cape) args.add(capeUrl);

    profile
        .getProperties()
        .put(
            "textures",
            new Property(
                "textures",
                Base64Coder.encodeString(
                    String.format(
                        cape ? JSON_CAPE : JSON_SKIN, args.toArray(new Object[args.size()])))));
    return profile;
  }

  public static class GameProfileSerializer
      implements JsonSerializer<GameProfile>, JsonDeserializer<GameProfile> {

    public GameProfile deserialize(JsonElement json, Type type, JsonDeserializationContext context)
        throws JsonParseException {
      JsonObject object = (JsonObject) json;
      UUID id = object.has("id") ? (UUID) context.deserialize(object.get("id"), UUID.class) : null;
      String name = object.has("name") ? object.getAsJsonPrimitive("name").getAsString() : null;
      GameProfile profile = new GameProfile(id, name);

      if (object.has("properties")) {
        for (Entry<String, Property> prop :
            ((PropertyMap) context.deserialize(object.get("properties"), PropertyMap.class))
                .entries()) {
          profile.getProperties().put(prop.getKey(), prop.getValue());
        }
      }
      return profile;
    }

    public JsonElement serialize(GameProfile profile, Type type, JsonSerializationContext context) {
      JsonObject result = new JsonObject();
      if (profile.getId() != null) {
        result.add("id", context.serialize(profile.getId()));
      }
      if (profile.getName() != null) {
        result.addProperty("name", profile.getName());
      }
      if (!profile.getProperties().isEmpty()) {
        result.add("properties", context.serialize(profile.getProperties()));
      }
      return result;
    }
  }
}
