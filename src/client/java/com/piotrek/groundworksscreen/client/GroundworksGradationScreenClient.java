package com.piotrek.groundworksscreen.client;

import com.piotrek.groundworksscreen.GroundworksGradationScreenMod;
import com.piotrek.groundworksscreen.client.model.GradationScreenModel;
import com.piotrek.groundworksscreen.client.render.GradationScreenRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;

public class GroundworksGradationScreenClient implements ClientModInitializer {

    public static final ModelLayerLocation GRADATION_SCREEN_LAYER =
            new ModelLayerLocation(
                    GroundworksGradationScreenMod.id("gradation_screen"),
                    "main"
            );

    @Override
    public void onInitializeClient() {
        ModelLayerRegistry.registerModelLayer(
                GRADATION_SCREEN_LAYER,
                GradationScreenModel::createBodyLayer
        );
        EntityRendererRegistry.register(
                GroundworksGradationScreenMod.GRADATION_SCREEN,
                GradationScreenRenderer::new
        );
    }
}
