package me.wesley1808.servercore.mixin.optimizations.players;

import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/**
 * Mixin for optimizing player spawn handling.
 */
@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Inject(
            method = "placeNewPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;setServerLevel(Lnet/minecraft/server/level/ServerLevel;)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void servercore$moveToSpawn(Connection netManager, ServerPlayer player, CallbackInfo ci) {
        ServerLevel level = (ServerLevel) player.getCommandSenderWorld(); // Используем getCommandSenderWorld()
        CustomPlayerData data = CustomPlayerData.get(level);
        UUID playerUUID = player.getUUID();
        BlockPos spawnPos = data.getPlayerSpawnLocation(playerUUID);

        if (spawnPos == null) {
            player.fudgeSpawnLocation(level);
        } else {
            player.teleportTo(level, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, player.getYRot(), player.getXRot());
        }
    }

    @Inject(
            method = "respawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;restoreFrom(Lnet/minecraft/server/level/ServerPlayer;Z)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void servercore$onPlayerRespawn(ServerPlayer oldPlayer, boolean alive, CallbackInfoReturnable<ServerPlayer> cir) {
        ServerPlayer newPlayer = cir.getReturnValue();
        if (newPlayer != null) {
            ServerLevel level = (ServerLevel) newPlayer.getCommandSenderWorld();
            CustomPlayerData data = CustomPlayerData.get(level);
            UUID playerUUID = newPlayer.getUUID();
            BlockPos spawnPos = data.getPlayerSpawnLocation(playerUUID);

            if (spawnPos == null) {
                spawnPos = level.getSharedSpawnPos();
            }

            newPlayer.teleportTo(level, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, newPlayer.getYRot(), newPlayer.getXRot());
        }
    }
}
