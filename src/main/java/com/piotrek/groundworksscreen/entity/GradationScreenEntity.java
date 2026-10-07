package com.piotrek.groundworksscreen.entity;

import com.piotrek.groundworks.api.GroundworksApi;
import com.piotrek.groundworks.api.container.GranularContainerTransferApi;
import com.piotrek.groundworks.api.container.IWorldGranularContainer;
import com.piotrek.groundworks.api.deposit.DepositResult;
import com.piotrek.groundworks.api.material.GranularComposition;
import com.piotrek.groundworks.api.material.GranularMaterial;
import com.piotrek.groundworks.api.material.GranularMaterialRegistry;
import com.piotrek.groundworksscreen.GroundworksGradationScreenMod;
import com.piotrek.groundworksscreen.logic.GradationRouting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class GradationScreenEntity extends Entity implements IWorldGranularContainer {
    public static final int CAPACITY = 8 * 512;
    public static final int OUTPUT_UNITS_PER_TICK = 32;
    public static final int DEFAULT_SELECTED_MATERIAL_ID = 3;

    private static final int OUTPUT_SURFACE_SEARCH_DEPTH = 16;
    private static final double RECEIVER_SEARCH_RADIUS = 5.50D;

    private static final EntityDataAccessor<Integer> SELECTED_MATERIAL_ID =
            SynchedEntityData.defineId(
                    GradationScreenEntity.class,
                    EntityDataSerializers.INT
            );
    private static final EntityDataAccessor<Integer> STORED_UNITS =
            SynchedEntityData.defineId(
                    GradationScreenEntity.class,
                    EntityDataSerializers.INT
            );
    private static final EntityDataAccessor<Boolean> ACTIVE =
            SynchedEntityData.defineId(
                    GradationScreenEntity.class,
                    EntityDataSerializers.BOOLEAN
            );

    private final GranularComposition buffer = new GranularComposition();

    public GradationScreenEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SELECTED_MATERIAL_ID, DEFAULT_SELECTED_MATERIAL_ID);
        builder.define(STORED_UNITS, 0);
        builder.define(ACTIVE, false);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) {
            return;
        }

        ServerLevel serverLevel = (ServerLevel) level();
        boolean moved = processMaterial(serverLevel);
        entityData.set(ACTIVE, moved);

        if (moved && tickCount % 6 == 0) {
            serverLevel.playSound(
                    null,
                    getX(),
                    getY() + 1.2D,
                    getZ(),
                    SoundEvents.GRAVEL_STEP,
                    SoundSource.BLOCKS,
                    0.55F,
                    0.80F
            );
        }
    }

    private boolean processMaterial(ServerLevel level) {
        if (buffer.isEmpty()) {
            syncState();
            return false;
        }

        int selectedId = getSelectedMaterialId();
        GradationRouting.Batch batch = GradationRouting.plan(
                buffer.toArray(),
                selectedId,
                OUTPUT_UNITS_PER_TICK
        );

        int moved = 0;

        if (batch.selectedUnits() > 0) {
            GranularMaterial selected =
                    GranularMaterialRegistry.byId(selectedId);
            moved += transferMaterial(
                    level,
                    selectedOutputPoint(),
                    selected,
                    batch.selectedUnits()
            );
        }

        int[] remainder = batch.remainderUnits();
        for (int id = 1; id < remainder.length; id++) {
            int amount = remainder[id];
            if (amount <= 0) {
                continue;
            }

            GranularMaterial material =
                    GranularMaterialRegistry.byId(id);

            moved += transferMaterial(
                    level,
                    remainderOutputPoint(),
                    material,
                    amount
            );
        }

        if (moved > 0) {
            syncState();
        }

        return moved > 0;
    }

    private int transferMaterial(
            ServerLevel level,
            Vec3 outputPoint,
            GranularMaterial material,
            int requested
    ) {
        if (requested <= 0
                || material == null
                || material == GranularMaterial.EMPTY) {
            return 0;
        }

        int available = buffer.unitsOf(material);
        int request = Math.min(requested, available);
        if (request <= 0) {
            return 0;
        }

        IWorldGranularContainer receiver =
                GranularContainerTransferApi.findReceiver(
                        level,
                        outputPoint,
                        this,
                        RECEIVER_SEARCH_RADIUS
                );

        int consumed = 0;

        if (receiver != null) {
            consumed = Mth.clamp(
                    receiver.receiveMaterialAt(
                            level,
                            outputPoint,
                            material,
                            request
                    ),
                    0,
                    request
            );
        } else {
            BlockPos target = findDepositSurface(level, outputPoint);
            if (target != null) {
                DepositResult result =
                        GroundworksApi.depositWithOverflow(
                                level,
                                target,
                                material,
                                request
                        );
                consumed = Mth.clamp(
                        result.unitsDeposited(),
                        0,
                        request
                );

                for (BlockPos affected : result.affectedCells()) {
                    GroundworksApi.markForSimulation(level, affected);
                }
            }
        }

        if (consumed > 0) {
            buffer.remove(material.id(), consumed);
        }

        return consumed;
    }

    @Nullable
    private static BlockPos findDepositSurface(
            ServerLevel level,
            Vec3 point
    ) {
        BlockPos start =
                BlockPos.containing(point.x, point.y, point.z);

        int minY = Math.max(
                level.getMinY(),
                start.getY() - OUTPUT_SURFACE_SEARCH_DEPTH
        );

        for (int y = start.getY(); y >= minY; y--) {
            BlockPos check =
                    new BlockPos(start.getX(), y, start.getZ());

            GranularMaterial terrainMaterial =
                    GroundworksApi.getMaterial(level, check);

            if (terrainMaterial != null
                    && terrainMaterial != GranularMaterial.EMPTY) {
                return check;
            }

            BlockState state = level.getBlockState(check);
            if (!state.isAir()) {
                return check.above();
            }
        }

        return null;
    }

    private Vec3 selectedOutputPoint() {
        return position()
                .add(rightVector().scale(3.80D))
                .add(forwardVector().scale(0.30D))
                .add(0.0D, 4.05D, 0.0D);
    }

    private Vec3 remainderOutputPoint() {
        return position()
                .add(forwardVector().scale(-4.45D))
                .add(rightVector().scale(-0.15D))
                .add(0.0D, 4.10D, 0.0D);
    }

    private Vec3 forwardVector() {
        double yawRad = Math.toRadians(getYRot());
        return new Vec3(
                -Math.sin(yawRad),
                0.0D,
                Math.cos(yawRad)
        );
    }

    private Vec3 rightVector() {
        double yawRad = Math.toRadians(getYRot());
        return new Vec3(
                Math.cos(yawRad),
                0.0D,
                Math.sin(yawRad)
        );
    }

    @Override
    public int receiveMaterialAt(
            ServerLevel level,
            Vec3 worldPoint,
            GranularMaterial material,
            int units
    ) {
        if (!canReceiveAt(worldPoint)) {
            return 0;
        }
        return acceptMaterial(material, units);
    }

    @Override
    public int receiveCompositionAt(
            ServerLevel level,
            Vec3 worldPoint,
            GranularComposition composition
    ) {
        if (!canReceiveAt(worldPoint)) {
            return 0;
        }
        return acceptComposition(composition);
    }

    @Override
    public int capacity() {
        return CAPACITY;
    }

    @Override
    public int storedUnits() {
        return buffer.totalUnits();
    }

    @Override
    public GranularMaterial storedMaterial() {
        int id = buffer.dominantMaterialId();
        return id > 0
                ? GranularMaterialRegistry.byId(id)
                : GranularMaterial.EMPTY;
    }

    @Override
    public GranularComposition storedComposition() {
        return buffer.copy();
    }

    @Override
    public int acceptMaterial(
            GranularMaterial material,
            int units
    ) {
        if (material == null
                || material == GranularMaterial.EMPTY
                || units <= 0) {
            return 0;
        }

        int room = CAPACITY - buffer.totalUnits();
        int accepted = Math.min(units, Math.max(0, room));

        if (accepted > 0) {
            buffer.add(material, accepted);
            syncState();
        }

        return accepted;
    }

    @Override
    public int acceptComposition(GranularComposition composition) {
        if (composition == null
                || composition.isEmpty()
                || !hasRoom()) {
            return 0;
        }

        int room = CAPACITY - buffer.totalUnits();
        GranularComposition accepted = composition.copy();

        if (accepted.totalUnits() > room) {
            accepted = accepted.extractProportional(room);
        }

        int added = buffer.addAll(accepted);
        syncState();
        return added;
    }

    @Override
    public int extractMaterial(int maxUnits) {
        if (maxUnits <= 0 || buffer.isEmpty()) {
            return 0;
        }

        int selected = buffer.dominantMaterialId();
        int removed = buffer.remove(selected, maxUnits);

        if (removed > 0) {
            syncState();
        }

        return removed;
    }

    @Override
    public GranularComposition extractComposition(int maxUnits) {
        GranularComposition extracted =
                buffer.extractProportional(maxUnits);

        if (!extracted.isEmpty()) {
            syncState();
        }

        return extracted;
    }

    @Override
    public boolean canReceiveAt(Vec3 worldPoint) {
        Vec3 delta = worldPoint.subtract(position());

        double localForward =
                delta.dot(forwardVector());
        double localRight =
                delta.dot(rightVector());

        return Math.abs(localRight) <= 1.12D
                && localForward >= -1.55D
                && localForward <= 1.55D
                && delta.y >= 2.15D
                && delta.y <= 7.00D;
    }

    @Override
    public InteractionResult interact(
            Player player,
            InteractionHand hand,
            Vec3 location
    ) {
        ItemStack held = player.getItemInHand(hand);

        if (player.isSecondaryUseActive()
                && held.isEmpty()) {
            if (!isEmpty()) {
                if (!level().isClientSide()) {
                    player.displayClientMessage(
                            Component.translatable(
                                    "message.pw_groundworks_gradation_screen.not_empty"
                            ),
                            true
                    );
                }
                return InteractionResult.FAIL;
            }

            if (!level().isClientSide()) {
                if (!player.getAbilities().instabuild) {
                    player.getInventory().add(
                            new ItemStack(
                                    GroundworksGradationScreenMod.GRADATION_SCREEN_ITEM
                            )
                    );
                }

                level().playSound(
                        null,
                        getX(),
                        getY(),
                        getZ(),
                        SoundEvents.ITEM_PICKUP,
                        SoundSource.PLAYERS,
                        1.0F,
                        1.0F
                );

                discard();
            }

            return InteractionResult.SUCCESS;
        }

        if (!level().isClientSide()) {
            GranularMaterial material = materialFromHeldBlock(held);

            if (material == null
                    || material == GranularMaterial.EMPTY) {
                material = nextMaterial();
            }

            setSelectedMaterial(material.id());

            player.displayClientMessage(
                    Component.translatable(
                            "message.pw_groundworks_gradation_screen.selected_material",
                            material.name()
                    ),
                    true
            );

            level().playSound(
                    null,
                    getX(),
                    getY() + 1.0D,
                    getZ(),
                    SoundEvents.LEVER_CLICK,
                    SoundSource.BLOCKS,
                    0.65F,
                    1.10F
            );
        }

        return InteractionResult.SUCCESS;
    }

    @Nullable
    private static GranularMaterial materialFromHeldBlock(
            ItemStack stack
    ) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return null;
        }

        return GranularMaterialRegistry.forBlockState(
                blockItem.getBlock().defaultBlockState()
        );
    }

    private GranularMaterial nextMaterial() {
        int count = Math.max(GranularMaterialRegistry.count(), 2);
        int current = getSelectedMaterialId();

        for (int offset = 1; offset < count; offset++) {
            int id = 1 + Math.floorMod(
                    current - 1 + offset,
                    count - 1
            );
            GranularMaterial material =
                    GranularMaterialRegistry.byId(id);
            if (material != null
                    && material != GranularMaterial.EMPTY) {
                return material;
            }
        }

        return GranularMaterialRegistry.GRAVEL != null
                ? GranularMaterialRegistry.GRAVEL
                : GranularMaterialRegistry.byId(DEFAULT_SELECTED_MATERIAL_ID);
    }

    private void setSelectedMaterial(int materialId) {
        int valid = materialId > 0
                && materialId < GranularMaterialRegistry.count()
                ? materialId
                : DEFAULT_SELECTED_MATERIAL_ID;
        entityData.set(SELECTED_MATERIAL_ID, valid);
    }

    private void syncState() {
        entityData.set(
                STORED_UNITS,
                Mth.clamp(buffer.totalUnits(), 0, CAPACITY)
        );
    }

    public int getSelectedMaterialId() {
        return entityData.get(SELECTED_MATERIAL_ID);
    }

    public int getStoredUnitsForRender() {
        return entityData.get(STORED_UNITS);
    }

    public boolean isActive() {
        return entityData.get(ACTIVE);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        setSelectedMaterial(
                input.getIntOr(
                        "SelectedMaterialId",
                        DEFAULT_SELECTED_MATERIAL_ID
                )
        );

        int count = Math.max(
                GranularMaterialRegistry.count(),
                DEFAULT_SELECTED_MATERIAL_ID + 1
        );
        int[] units = new int[count];

        for (int id = 1; id < count; id++) {
            units[id] = Math.max(
                    0,
                    input.getIntOr("BufferMaterial_" + id, 0)
            );
        }

        buffer.replaceWith(units);

        if (buffer.totalUnits() > CAPACITY) {
            GranularComposition trimmed =
                    buffer.extractProportional(CAPACITY);
            buffer.replaceWith(trimmed.toArray());
        }

        syncState();
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putInt(
                "SelectedMaterialId",
                getSelectedMaterialId()
        );

        int[] units = buffer.toArray();
        for (int id = 1; id < units.length; id++) {
            if (units[id] > 0) {
                output.putInt(
                        "BufferMaterial_" + id,
                        units[id]
                );
            }
        }
    }

    @Override
    public boolean hurtClient(DamageSource source) {
        return true;
    }

    @Override
    public boolean hurtServer(
            ServerLevel level,
            DamageSource source,
            float amount
    ) {
        if (isInvulnerableToBase(source)) {
            return false;
        }

        if (!(source.getEntity() instanceof Player player)) {
            return false;
        }

        if (!isEmpty()) {
            return false;
        }

        level.playSound(
                null,
                getX(),
                getY(),
                getZ(),
                SoundEvents.ANVIL_HIT,
                SoundSource.PLAYERS,
                0.8F,
                1.0F
        );

        if (!player.getAbilities().instabuild) {
            spawnAtLocation(
                    level,
                    GroundworksGradationScreenMod.GRADATION_SCREEN_ITEM
            );
        }

        discard();
        return true;
    }

    @Override
    public boolean isPickable() {
        return !isRemoved();
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith(@Nullable Entity other) {
        return other != null;
    }

    @Override
    public boolean canCollideWith(Entity other) {
        return other != null;
    }

    @Override
    public boolean isClientAuthoritative() {
        return false;
    }

    @Override
    protected boolean isLocalClientAuthoritative() {
        return false;
    }

    @Override
    public float maxUpStep() {
        return 0.0F;
    }
}
