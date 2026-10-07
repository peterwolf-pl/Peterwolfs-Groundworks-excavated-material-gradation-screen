package com.piotrek.groundworksscreen.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.piotrek.groundworksscreen.client.GroundworksGradationScreenClient;
import com.piotrek.groundworksscreen.client.model.GradationScreenModel;
import com.piotrek.groundworksscreen.entity.GradationScreenEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class GradationScreenRenderer
        extends EntityRenderer<GradationScreenEntity, GradationScreenRenderState> {

    private static final Identifier TEXTURE =
            Identifier.withDefaultNamespace("textures/entity/minecart.png");

    private final GradationScreenModel model;

    public GradationScreenRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new GradationScreenModel(
                context.bakeLayer(
                        GroundworksGradationScreenClient.GRADATION_SCREEN_LAYER
                )
        );
        this.shadowRadius = 1.65F;
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
        state.storedUnits = entity.getStoredUnitsForRender();
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

        stack.popPose();
    }
}
