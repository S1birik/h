package dev.wersize;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/** Pack editor: pick a category on the left, click an element to cycle its style, press Apply. */
public class WersizeScreen extends Screen {
    private final Screen parent;
    private final Map<String, Style> pending = new LinkedHashMap<>();
    private String category = Elements.CATEGORIES.get(0);
    private String status = "Click an item to cycle its style, then press Apply.";

    public WersizeScreen(Screen parent) {
        super(Component.literal("Wersize - Pack Editor"));
        this.parent = parent;
        for (Elements.Element e : Elements.ALL) pending.put(e.id(), WersizeConfig.get(e.id()));
    }

    private Component label(Elements.Element e) {
        return Component.literal(e.label() + ":  " + pending.get(e.id()).label);
    }

    @Override
    protected void init() {
        int left = 20, top = 40, y = top;
        for (String cat : Elements.CATEGORIES) {
            String text = cat.equals(category) ? "> " + cat : cat;
            addRenderableWidget(Button.builder(Component.literal(text), b -> {
                category = cat;
                rebuildWidgets();
            }).bounds(left, y, 90, 20).build());
            y += 24;
        }

        int x = left + 110;
        y = top;
        for (Elements.Element e : Elements.inCategory(category)) {
            addRenderableWidget(Button.builder(label(e), b -> {
                Style[] all = Style.values();
                pending.put(e.id(), all[(pending.get(e.id()).ordinal() + 1) % all.length]);
                b.setMessage(label(e));
            }).bounds(x, y, 220, 20).build());
            y += 24;
        }

        int by = this.height - 30;
        int bx = this.width / 2 - 155;
        addRenderableWidget(Button.builder(Component.literal("Apply"), b -> apply()).bounds(bx, by, 100, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Reset all"), b -> {
            pending.replaceAll((k, v) -> Style.VANILLA);
            rebuildWidgets();
        }).bounds(bx + 105, by, 100, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Close"), b -> onClose()).bounds(bx + 210, by, 100, 20).build());
    }

    private void apply() {
        pending.forEach(WersizeConfig::set);
        WersizeConfig.save();
        try {
            int n = PackBuilder.apply(this.minecraft);
            status = "Applied: " + n + " textures recoloured.";
        } catch (Exception ex) {
            status = "Error: " + ex.getMessage();
            ex.printStackTrace();
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        g.drawCenteredString(this.font, this.title, this.width / 2, 14, 0xFFFFFFFF);

        // colour swatch next to each element button
        int x = 20 + 110 + 226, y = 40;
        for (Elements.Element e : Elements.inCategory(category)) {
            g.fill(x, y, x + 20, y + 20, 0xFF000000);
            g.fill(x + 1, y + 1, x + 19, y + 19, pending.get(e.id()).preview);
            y += 24;
        }
        g.drawString(this.font, status, 20, this.height - 48, 0xFFAAAAAA);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
}
