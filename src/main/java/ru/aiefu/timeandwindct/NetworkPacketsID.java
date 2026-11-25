package ru.aiefu.timeandwindct;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public final class NetworkPacketsID {
    public static final CustomPacketPayload.Type<NetworkPayloads.SyncConfig> SYNC_CONFIG = NetworkPayloads.SyncConfig.TYPE;
    public static final CustomPacketPayload.Type<NetworkPayloads.SetupTime> SETUP_TIME = NetworkPayloads.SetupTime.TYPE;
    public static final CustomPacketPayload.Type<NetworkPayloads.DebugInfo> CFG_DEBUG_INFO = NetworkPayloads.DebugInfo.TYPE;
    public static final CustomPacketPayload.Type<NetworkPayloads.WorldIdClipboard> WORLD_ID_CLIPBOARD = NetworkPayloads.WorldIdClipboard.TYPE;
    public static final CustomPacketPayload.Type<NetworkPayloads.NightSkipInfo> NIGHT_SKIP_INFO = NetworkPayloads.NightSkipInfo.TYPE;

    private NetworkPacketsID() {
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(TimeAndWindCT.MOD_ID, path);
    }

    public static void registerPayloadTypes() {
        PayloadTypeRegistry.playS2C().register(SYNC_CONFIG, NetworkPayloads.SyncConfig.CODEC);
        PayloadTypeRegistry.playS2C().register(SETUP_TIME, NetworkPayloads.SetupTime.CODEC);
        PayloadTypeRegistry.playS2C().register(CFG_DEBUG_INFO, NetworkPayloads.DebugInfo.CODEC);
        PayloadTypeRegistry.playS2C().register(WORLD_ID_CLIPBOARD, NetworkPayloads.WorldIdClipboard.CODEC);
        PayloadTypeRegistry.playS2C().register(NIGHT_SKIP_INFO, NetworkPayloads.NightSkipInfo.CODEC);
    }
}
