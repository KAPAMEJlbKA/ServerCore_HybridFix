package me.wesley1808.servercore.mixin.optimizations.players;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomPlayerData extends SavedData {
    private static final String DATA_NAME = "custom_player_data";

    private final Map<UUID, BlockPos> playerSpawnLocations = new HashMap<>();

    public static CustomPlayerData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                nbt -> new CustomPlayerData(),
                CustomPlayerData::new,
                DATA_NAME
        );
    }

    public BlockPos getPlayerSpawnLocation(UUID playerUUID) {
        return this.playerSpawnLocations.get(playerUUID);
    }

    public void setPlayerSpawnLocation(UUID playerUUID, BlockPos pos) {
        this.playerSpawnLocations.put(playerUUID, pos);
        this.setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        return tag;
    }
}
