package com.piotrek.groundworksscreen.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.piotrek.groundworks.api.material.GranularMaterial;
import com.piotrek.groundworks.api.material.GranularMaterialRegistry;
import com.piotrek.groundworksscreen.client.GroundworksGradationScreenClient;
import com.piotrek.groundworksscreen.client.model.GradationScreenModel;
import com.piotrek.groundworksscreen.entity.GradationScreenEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;

public class GradationScreenRenderer
        extends EntityRenderer<GradationScreenEntity, GradationScreenRenderState> {

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(
                    "pw_groundworks_gradation_screen",
                    "textures/entity/gradation_screen.png"
            );

    private final GradationScreenModel model;

    public GradationScreenRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new GradationScreenModel(
                context.bakeLayer(
                        GroundworksGradationScreenClient.GRADATION_SCREEN_LAYER
                )
        );
        this.shadowRadius = 2.25F;
    }

    @Override
    public GradationScreenRenderState createRenderState() {
        return new GradationScreenRenderState();
    }

    @Override
    public void extractRenderState(
            GradationScreenEntity entity,
            GradationScreenRenderState state,
            float partialTick
    ) {
        super.extractRenderState(entity, state, partialTick);
        state.baseYaw = entity.getYRot(partialTick);
        state.active = entity.isActive();
        state.beltPhase = (entity.tickCount + partialTick) * 0.34F;
        state.selectedMaterialId = entity.getSelectedMaterialId();
        state.remainderMaterialId = entity.getRemainderMaterialId();
        state.storedUnits = entity.getStoredUnitsForRender();
        state.selectedUnits = entity.getSelectedUnitsForRender();
        state.hasSelectedStream = entity.hasSelectedStream();
        state.hasRemainderStream = entity.hasRemainderStream();
    }

    @Override
    public boolean shouldRender(
            GradationScreenEntity entity,
            Frustum frustum,
            double camX,
            double camY,
            double camZ,
            float partialTick
    ) {
        // Inflate culling box so long elevated conveyor booms are never clipped
        AABB bounds = entity.getBoundingBox().inflate(9.0D, 4.5D, 9.0D);
        return frustum.isVisible(bounds);
    }

    @Override
    public void submit(
            GradationScreenRenderState state,
            PoseStack stack,
            SubmitNodeCollector collector,
            CameraRenderState camera
    ) {
        stack.pushPose();
        stack.rotateDegrees(Axis.YP, -state.baseYaw);
        stack.scale(-1.0F, -1.0F, 1.0F);
        stack.translate(0.0F, -1.5F, 0.0F);

        model.setupAnim(state);

        collector.submitModel(
                model,
                state,
                stack,
                RenderTypes.entityCutout(TEXTURE),
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                state.outlineColor
        );

        // 1. Vibrating material bed on the central screen deck (only while actively screening)
        if (state.active && state.storedUnits > 0) {
            int deckMatId = state.selectedUnits > 0
                    ? state.selectedMaterialId
                    : state.remainderMaterialId;
            if (deckMatId > 0) {
                GranularMaterial deckMat = GranularMaterialRegistry.byId(deckMatId);
                Identifier deckTex = GranularConveyorStreamRenderer.textureFor(deckMat);
                stack.pushPose();
                stack.rotateDegrees(Axis.XP, -5.0F);
                GranularConveyorStreamRenderer.renderScreenDeckMaterial(
                        collector,
                        stack,
                        deckTex,
                        state.beltPhase,
                        state.active,
                        state.lightCoords
                );
                stack.popPose();
            }
        }

        // 2. Material stream on the elevated selected conveyor (-18 deg around Z)
        // Strictly only when actively screening and the hopper contains selected material!
        if (state.active && state.storedUnits > 0 && state.hasSelectedStream && state.selectedMaterialId > 0) {
            GranularMaterial selectedMat = GranularMaterialRegistry.byId(state.selectedMaterialId);
            Identifier selectedTex = GranularConveyorStreamRenderer.textureFor(selectedMat);
            stack.pushPose();
            stack.rotateDegrees(Axis.ZP, -18.0F);
            GranularConveyorStreamRenderer.renderSelectedStream(
                    collector,
                    stack,
                    selectedTex,
                    state.beltPhase,
                    state.active,
                    state.lightCoords
            );
            stack.popPose();
        }

        // 3. Material stream on the elevated remainder conveyor (-18 deg around X)
        // Strictly only when actively screening and the hopper contains remainder material!
        if (state.active && state.storedUnits > 0 && state.hasRemainderStream && state.remainderMaterialId > 0) {
            GranularMaterial remainderMat = GranularMaterialRegistry.byId(state.remainderMaterialId);
            Identifier remainderTex = GranularConveyorStreamRenderer.textureFor(remainderMat);
            stack.pushPose();
            stack.rotateDegrees(Axis.XP, -18.0F);
            GranularConveyorStreamRenderer.renderRemainderStream(
                    collector,
                    stack,
                    remainderTex,
                    state.beltPhase,
                    state.active,
                    state.lightCoords
            );
            stack.popPose();
        }

        stack.popPose();
    }
}
