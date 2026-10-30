package dev.wersize;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class WersizeClient implements ClientModInitializer {
    private static boolean openRequested;

    @Override
    public void onInitializeClient() {
        WersizeConfig.load();

        // /wersize opens the editor (deferred to the next tick, because the chat screen closes right after a command)
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("wersize").executes(ctx -> {
                    openRequested = true;
                    return 1;
                })));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (openRequested && mc.screen == null) {
                openRequested = false;
                mc.setScreen(new WersizeScreen(null));
            }
        });
    }
}
