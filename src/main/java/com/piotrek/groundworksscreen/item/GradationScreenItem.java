package com.piotrek.groundworksscreen.item;

import com.piotrek.groundworksscreen.GroundworksGradationScreenMod;
import com.piotrek.groundworksscreen.entity.GradationScreenEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class GradationScreenItem extends Item {
    public GradationScreenItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockPos spawnPos = context.getClickedPos().relative(context.getClickedFace());
        Vec3 spawnVec = new Vec3(
                spawnPos.getX() + 0.5D,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5D
        );

        AABB bounds = GroundworksGradationScreenMod.GRADATION_SCREEN
                .getDimensions()
                .makeBoundingBox(spawnVec);

        if (!level.noCollision(bounds)) {
            return InteractionResult.FAIL;
        }

        ServerLevel serverLevel = (ServerLevel) level;
        GradationScreenEntity screen =
                GroundworksGradationScreenMod.GRADATION_SCREEN.create(
                        serverLevel,
                        EntitySpawnReason.SPAWN_ITEM_USE
                );

        if (screen == null) {
            return InteractionResult.FAIL;
        }

        float playerYaw = context.getPlayer() != null
                ? context.getPlayer().getYRot()
                : 0.0F;
        float snappedYaw = Mth.wrapDegrees(Math.round(playerYaw / 90.0F) * 90.0F);

        screen.snapTo(
                spawnVec.x,
                spawnVec.y,
                spawnVec.z,
                snappedYaw,
                0.0F
        );
        screen.setYHeadRot(snappedYaw);
        screen.setYBodyRot(snappedYaw);
        serverLevel.addFreshEntity(screen);

        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if (player != null && !player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return InteractionResult.CONSUME;
    }
}
