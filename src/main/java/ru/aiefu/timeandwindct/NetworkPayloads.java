package ru.aiefu.timeandwindct;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import ru.aiefu.timeandwindct.config.SystemTimeConfig;
import ru.aiefu.timeandwindct.config.TimeDataStorage;

import java.util.HashMap;
import java.util.Map;

/** Typed payloads used for the 1.20.5+ networking API. */
public final class NetworkPayloads {
    private NetworkPayloads() {
    }

    public record SyncConfig(String configJson, String systemConfigJson,
                             Map<String, TimeDataStorage> timeData,
                             Map<String, SystemTimeConfig> systemTimeData) implements CustomPacketPayload {
        public static final Type<SyncConfig> TYPE = new Type<>(NetworkPacketsID.id("sync_config"));
        public static final StreamCodec<RegistryFriendlyByteBuf, SyncConfig> CODEC = StreamCodec.of(
                (buffer, payload) -> {
                    buffer.writeUtf(payload.configJson());
                    buffer.writeUtf(payload.systemConfigJson());
                    buffer.writeMap(payload.timeData(), (out, key) -> out.writeUtf(key), (out, value) -> {
                        out.writeInt(value.dayDuration);
                        out.writeInt(value.nightDuration);
                    });
                    buffer.writeMap(payload.systemTimeData(), (out, key) -> out.writeUtf(key), (out, value) -> {
                        out.writeUtf(value.sunrise);
                        out.writeUtf(value.sunset);
                        out.writeUtf(value.timeZone);
                    });
                },
                buffer -> new SyncConfig(
                        buffer.readUtf(),
                        buffer.readUtf(),
                        new HashMap<>(buffer.readMap(in -> in.readUtf(),
                                in -> new TimeDataStorage(in.readInt(), in.readInt()))),
                        new HashMap<>(buffer.readMap(in -> in.readUtf(),
                                in -> new SystemTimeConfig(in.readUtf(), in.readUtf(), in.readUtf())))
                )
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record SetupTime() implements CustomPacketPayload {
        public static final Type<SetupTime> TYPE = new Type<>(NetworkPacketsID.id("setup_time"));
        public static final StreamCodec<RegistryFriendlyByteBuf, SetupTime> CODEC = StreamCodec.of(
                (buffer, payload) -> { },
                buffer -> new SetupTime()
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record DebugInfo() implements CustomPacketPayload {
        public static final Type<DebugInfo> TYPE = new Type<>(NetworkPacketsID.id("cfg_debug_info"));
        public static final StreamCodec<RegistryFriendlyByteBuf, DebugInfo> CODEC = StreamCodec.of(
                (buffer, payload) -> { },
                buffer -> new DebugInfo()
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record WorldIdClipboard(String value) implements CustomPacketPayload {
        public static final Type<WorldIdClipboard> TYPE = new Type<>(NetworkPacketsID.id("world_id_clipboard"));
        public static final StreamCodec<RegistryFriendlyByteBuf, WorldIdClipboard> CODEC = StreamCodec.of(
                (buffer, payload) -> buffer.writeUtf(payload.value()),
                buffer -> new WorldIdClipboard(buffer.readUtf())
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record NightSkipInfo(boolean active, int speed) implements CustomPacketPayload {
        public static final Type<NightSkipInfo> TYPE = new Type<>(NetworkPacketsID.id("nskip_info"));
        public static final StreamCodec<RegistryFriendlyByteBuf, NightSkipInfo> CODEC = StreamCodec.of(
                (buffer, payload) -> {
                    buffer.writeBoolean(payload.active());
                    buffer.writeInt(payload.speed());
                },
                buffer -> new NightSkipInfo(buffer.readBoolean(), buffer.readInt())
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
