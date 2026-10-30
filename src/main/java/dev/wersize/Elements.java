package dev.wersize;

import java.util.ArrayList;
import java.util.List;

/** Everything that can be recoloured, grouped by editor category. Missing textures are skipped at build time. */
public final class Elements {
    public record Element(String id, String category, String label, List<String> textures) {}

    public static final List<String> CATEGORIES = List.of("PvP", "Blocks", "Gear", "HUD", "Misc");
    public static final List<Element> ALL = new ArrayList<>();

    private static void add(String id, String category, String label, String... names) {
        List<String> list = new ArrayList<>();
        for (String n : names) list.add("textures/" + n + ".png");
        ALL.add(new Element(id, category, label, list));
    }

    static {
        // ---- PvP ----
        add("end_crystals", "PvP", "End Crystals", "item/end_crystal", "entity/end_crystal/end_crystal");
        add("respawn_anchors", "PvP", "Respawn Anchors",
                "block/respawn_anchor_top", "block/respawn_anchor_top_off", "block/respawn_anchor_bottom",
                "block/respawn_anchor_side0", "block/respawn_anchor_side1", "block/respawn_anchor_side2",
                "block/respawn_anchor_side3", "block/respawn_anchor_side4");
        add("totems", "PvP", "Totems", "item/totem_of_undying");
        add("ender_pearls", "PvP", "Ender Pearls", "item/ender_pearl");
        add("golden_apples", "PvP", "Golden Apples", "item/golden_apple", "item/enchanted_golden_apple");
        add("shields", "PvP", "Shields", "entity/shield_base_nopattern", "entity/shield/shield_base_nopattern");

        // ---- Blocks ----
        add("obsidian", "Blocks", "Obsidian", "block/obsidian");
        add("glowstone", "Blocks", "Glowstone", "block/glowstone");
        add("cobwebs", "Blocks", "Cobwebs", "block/cobweb");
        add("fire", "Blocks", "Fire", "block/fire_0", "block/fire_1");

        // ---- Gear ----
        add("diamond_tools", "Gear", "Diamond Tools", tools("diamond"));
        add("netherite_tools", "Gear", "Netherite Tools", tools("netherite"));
        add("diamond_armour", "Gear", "Diamond Armour", armour("diamond"));
        add("netherite_armour", "Gear", "Netherite Armour", armour("netherite"));
        add("mace", "Gear", "Mace", "item/mace");
        add("spears", "Gear", "Spears", spears());

        // ---- HUD ----
        add("crosshair", "HUD", "Crosshair", "gui/sprites/hud/crosshair");
        String h = "gui/sprites/hud/heart/";
        add("hearts", "HUD", "Hearts", h + "full", h + "half", h + "full_blinking", h + "half_blinking",
                h + "hardcore_full", h + "hardcore_half", h + "hardcore_full_blinking", h + "hardcore_half_blinking");
        String f = "gui/sprites/hud/";
        add("hunger", "HUD", "Hunger", f + "food_full", f + "food_half", f + "food_full_hunger", f + "food_half_hunger");

        // ---- Misc ----
        add("enchant_glint", "Misc", "Enchant Glint", "misc/enchanted_glint_item", "misc/enchanted_glint_entity");
    }

    private static String[] tools(String mat) {
        String[] t = {"sword", "pickaxe", "axe", "shovel", "hoe"};
        String[] out = new String[t.length];
        for (int i = 0; i < t.length; i++) out[i] = "item/" + mat + "_" + t[i];
        return out;
    }

    private static String[] armour(String mat) {
        String[] p = {"helmet", "chestplate", "leggings", "boots"};
        String[] out = new String[p.length + 2];
        for (int i = 0; i < p.length; i++) out[i] = "item/" + mat + "_" + p[i];
        out[p.length] = "entity/equipment/humanoid/" + mat;
        out[p.length + 1] = "entity/equipment/humanoid_leggings/" + mat;
        return out;
    }

    private static String[] spears() {
        String[] m = {"wooden", "stone", "copper", "iron", "golden", "diamond", "netherite"};
        String[] out = new String[m.length];
        for (int i = 0; i < m.length; i++) out[i] = "item/" + m[i] + "_spear";
        return out;
    }

    public static List<Element> inCategory(String category) {
        List<Element> out = new ArrayList<>();
        for (Element e : ALL) if (e.category().equals(category)) out.add(e);
        return out;
    }

    private Elements() {}
}
