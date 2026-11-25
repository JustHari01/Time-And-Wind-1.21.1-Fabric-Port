package ru.aiefu.timeandwindct;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import ru.aiefu.timeandwindct.config.TimeDataStorage;
import ru.aiefu.timeandwindct.tickers.DefaultTicker;
import ru.aiefu.timeandwindct.tickers.SystemTimeTicker;
import ru.aiefu.timeandwindct.tickers.TimeTicker;

import java.util.Map;

public class TimeAndWindCTClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkPacketsID.SYNC_CONFIG, (payload, context) -> {
            TimeAndWindCT.CONFIG = ConfigurationManager.gson_pretty.fromJson(payload.configJson(), ru.aiefu.timeandwindct.config.ModConfig.class);
            TimeAndWindCT.systemTimeConfig = ConfigurationManager.gson_pretty.fromJson(payload.systemConfigJson(), ru.aiefu.timeandwindct.config.SystemTimeConfig.class);
            TimeAndWindCT.timeDataMap = new java.util.HashMap<>(payload.timeData());
            TimeAndWindCT.sysTimeMap = new java.util.HashMap<>(payload.systemTimeData());

            TimeAndWindCT.LOGGER.info("[Time & Wind] Configuration synchronized");
            ClientLevel world = context.client().level;
            if (world != null) {
                applyTicker(world, payload.systemTimeData(), payload.timeData());
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(NetworkPacketsID.CFG_DEBUG_INFO, (payload, context) -> {
            Minecraft client = context.client();
            if (client.level != null && client.player != null) {
                String worldId = client.level.dimension().location().toString();
                if (((ITimeOperations) client.level).time_and_wind_custom_ticker$getTimeTicker() instanceof SystemTimeTicker) {
                    return;
                }
                if (TimeAndWindCT.timeDataMap == null) {
                    client.player.sendSystemMessage(Component.literal("[Client Side] TimeDataMap is NULL, this is a bug"));
                } else if (TimeAndWindCT.timeDataMap.containsKey(worldId)) {
                    TimeDataStorage storage = TimeAndWindCT.timeDataMap.get(worldId);
                    client.player.sendSystemMessage(Component.literal("Client config for current world: Day Duration: " + storage.dayDuration + " Night Duration: " + storage.nightDuration));
                } else {
                    client.player.sendSystemMessage(Component.literal("No Data found for current world on client side"));
                }
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(NetworkPacketsID.WORLD_ID_CLIPBOARD, (payload, context) -> {
            Minecraft client = context.client();
            if (client.player != null) {
                client.keyboardHandler.setClipboard(payload.value());
                client.player.displayClientMessage(Component.literal("Also copied this to clipboard"), false);
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(NetworkPacketsID.SETUP_TIME, (payload, context) -> {
            ClientLevel world = context.client().level;
            if (world != null) {
                applyTicker(world, TimeAndWindCT.sysTimeMap, TimeAndWindCT.timeDataMap);
                TimeAndWindCT.LOGGER.info("[Time & Wind] Timedata reloaded on client");
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(NetworkPacketsID.NIGHT_SKIP_INFO, (payload, context) -> {
            ClientLevel world = context.client().level;
            if (world != null) {
                ITimeOperations operations = (ITimeOperations) world;
                operations.time_and_wind_custom_ticker$setSkipState(payload.active());
                operations.time_and_wind_custom_ticker$setSpeed(payload.speed());
            }
        });
    }

    private static void applyTicker(ClientLevel world,
                                   Map<String, ru.aiefu.timeandwindct.config.SystemTimeConfig> systemTimeData,
                                   Map<String, TimeDataStorage> timeData) {
        String worldId = world.dimension().location().toString();
        ITimeOperations operations = (ITimeOperations) world;
        if (TimeAndWindCT.CONFIG.syncWithSystemTime) {
            if (TimeAndWindCT.CONFIG.systemTimePerDimensions && systemTimeData != null && systemTimeData.containsKey(worldId)) {
                operations.time_and_wind_custom_ticker$setTimeTicker(new SystemTimeTicker(operations, systemTimeData.get(worldId)));
            } else {
                operations.time_and_wind_custom_ticker$setTimeTicker(new SystemTimeTicker(operations, TimeAndWindCT.systemTimeConfig));
            }
            TimeAndWindCT.LOGGER.info("[Time & Wind] System time ticker synchronized");
        } else if (timeData != null && timeData.containsKey(worldId)) {
            TimeDataStorage storage = timeData.get(worldId);
            operations.time_and_wind_custom_ticker$setTimeTicker(new TimeTicker(storage.dayDuration, storage.nightDuration, world));
            TimeAndWindCT.LOGGER.info("[Time & Wind] Custom time ticker for world " + worldId + " synchronized");
        } else {
            operations.time_and_wind_custom_ticker$setTimeTicker(new DefaultTicker());
        }
    }
}
