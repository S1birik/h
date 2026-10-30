package dev.wersize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/** Stores the chosen style per element in config/wersize.json. */
public final class WersizeConfig {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("wersize.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Map<String, String> selected = new LinkedHashMap<>();

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                Map<String, String> m = GSON.fromJson(Files.readString(FILE), new TypeToken<Map<String, String>>() {}.getType());
                if (m != null) selected = m;
            }
        } catch (Exception ex) {
            System.err.println("[Wersize] Could not read config: " + ex);
        }
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(selected));
        } catch (IOException ex) {
            System.err.println("[Wersize] Could not save config: " + ex);
        }
    }

    public static Style get(String elementId) {
        return Style.byName(selected.getOrDefault(elementId, "VANILLA"));
    }

    public static void set(String elementId, Style style) {
        selected.put(elementId, style.name());
    }

    private WersizeConfig() {}
}
