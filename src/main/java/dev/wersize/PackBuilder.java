package dev.wersize;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/** Generates resourcepacks/Wersize/ (a folder pack, so Windows never locks it) and enables it. */
public final class PackBuilder {
    public static final String PACK_NAME = "Wersize";
    private static final int PACK_FORMAT = 75; // resource pack format of 1.21.11

    /** Returns number of textures written. */
    public static int build(Minecraft mc) throws IOException {
        Path dir = FabricLoader.getInstance().getGameDir().resolve("resourcepacks").resolve(PACK_NAME);
        deleteRecursively(dir);
        Files.createDirectories(dir);
        Files.writeString(dir.resolve("pack.mcmeta"),
                "{\"pack\":{\"pack_format\":" + PACK_FORMAT + ",\"description\":\"Wersize recolour pack (generated)\"}}");

        ResourceManager rm = mc.getResourceManager();
        int written = 0;
        for (Elements.Element e : Elements.ALL) {
            Style style = WersizeConfig.get(e.id());
            if (style == Style.VANILLA) continue;
            for (String path : e.textures()) {
                // index 0 of the stack is the lowest-priority pack = vanilla, so we never recolour our own output twice
                List<Resource> stack = rm.getResourceStack(Identifier.withDefaultNamespace(path));
                if (stack.isEmpty()) continue;
                BufferedImage src;
                try (InputStream in = stack.get(0).open()) {
                    src = ImageIO.read(in);
                }
                if (src == null) continue;

                BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
                for (int y = 0; y < src.getHeight(); y++)
                    for (int x = 0; x < src.getWidth(); x++)
                        out.setRGB(x, y, style.apply(src.getRGB(x, y)));

                Path target = dir.resolve("assets/minecraft/" + path);
                Files.createDirectories(target.getParent());
                ImageIO.write(out, "png", target.toFile());
                written++;

                // animated textures (fire, glint...) need their .mcmeta next to the png
                List<Resource> meta = rm.getResourceStack(Identifier.withDefaultNamespace(path + ".mcmeta"));
                if (!meta.isEmpty()) {
                    try (InputStream in = meta.get(0).open()) {
                        Files.copy(in, target.resolveSibling(target.getFileName() + ".mcmeta"), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        }
        return written;
    }

    /** Build the pack, make sure it is selected (top priority) and reload resources. */
    public static int apply(Minecraft mc) throws IOException {
        int n = build(mc);
        PackRepository repo = mc.getResourcePackRepository();
        repo.reload();
        String id = "file/" + PACK_NAME;
        List<String> selected = new ArrayList<>(repo.getSelectedIds());
        if (repo.getAvailableIds().contains(id) && !selected.contains(id)) {
            selected.add(id);
            repo.setSelected(selected);
            mc.options.updateResourcePacks(repo);
        } else {
            mc.reloadResourcePacks();
        }
        return n;
    }

    private static void deleteRecursively(Path dir) throws IOException {
        if (!Files.exists(dir)) return;
        try (Stream<Path> walk = Files.walk(dir)) {
            for (Path p : walk.sorted(Comparator.reverseOrder()).toList()) Files.delete(p);
        }
    }

    private PackBuilder() {}
}
