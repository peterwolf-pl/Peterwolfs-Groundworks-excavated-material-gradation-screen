package com.piotrek.groundworksscreen.client.model;

import com.piotrek.groundworksscreen.client.render.GradationScreenRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class GradationScreenModel extends EntityModel<GradationScreenRenderState> {

    private final ModelPart selectedRoller;
    private final ModelPart remainderRoller;
    private final ModelPart eccentricWeight;

    public GradationScreenModel(ModelPart root) {
        super(root);
        this.selectedRoller = root.getChild("selected_roller");
        this.remainderRoller = root.getChild("remainder_roller");
        this.eccentricWeight = root.getChild("eccentric_weight");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild(
                "base",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-17.0F, 8.0F, -25.0F, 34.0F, 5.0F, 50.0F)
                        .texOffs(0, 0)
                        .addBox(-16.0F, -22.0F, -22.0F, 4.0F, 30.0F, 4.0F)
                        .texOffs(0, 0)
                        .addBox(12.0F, -22.0F, -22.0F, 4.0F, 30.0F, 4.0F)
                        .texOffs(0, 0)
                        .addBox(-16.0F, -22.0F, 18.0F, 4.0F, 30.0F, 4.0F)
                        .texOffs(0, 0)
                        .addBox(12.0F, -22.0F, 18.0F, 4.0F, 30.0F, 4.0F),
                PartPose.ZERO
        );

        root.addOrReplaceChild(
                "screen_body",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-15.0F, -25.0F, -20.0F, 30.0F, 23.0F, 40.0F)
                        .texOffs(0, 0)
                        .addBox(-18.0F, -7.0F, -23.0F, 36.0F, 5.0F, 46.0F)
                        .texOffs(0, 0)
                        .addBox(-13.0F, -31.0F, -18.0F, 26.0F, 6.0F, 36.0F),
                PartPose.ZERO
        );

        PartDefinition hopper = root.addOrReplaceChild(
                "hopper",
                CubeListBuilder.create(),
                PartPose.ZERO
        );

        hopper.addOrReplaceChild(
                "left_wall",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-19.0F, -47.0F, -25.0F, 4.0F, 18.0F, 50.0F),
                PartPose.rotation(0.0F, 0.0F, -0.10F)
        );
        hopper.addOrReplaceChild(
                "right_wall",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(15.0F, -47.0F, -25.0F, 4.0F, 18.0F, 50.0F),
                PartPose.rotation(0.0F, 0.0F, 0.10F)
        );
        hopper.addOrReplaceChild(
                "front_wall",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-16.0F, -47.0F, 21.0F, 32.0F, 18.0F, 4.0F),
                PartPose.rotation(-0.10F, 0.0F, 0.0F)
        );
        hopper.addOrReplaceChild(
                "rear_wall",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-16.0F, -47.0F, -25.0F, 32.0F, 18.0F, 4.0F),
                PartPose.rotation(0.10F, 0.0F, 0.0F)
        );

        root.addOrReplaceChild(
                "screen_deck",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-13.0F, -28.0F, -17.0F, 26.0F, 3.0F, 34.0F)
                        .texOffs(0, 0)
                        .addBox(-13.5F, -30.0F, -17.0F, 3.0F, 6.0F, 34.0F)
                        .texOffs(0, 0)
                        .addBox(10.5F, -30.0F, -17.0F, 3.0F, 6.0F, 34.0F),
                PartPose.rotation((float) Math.toRadians(-5.0F), 0.0F, 0.0F)
        );

        root.addOrReplaceChild(
                "selected_conveyor",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(11.0F, -10.0F, -8.0F, 60.0F, 4.0F, 16.0F)
                        .texOffs(0, 0)
                        .addBox(11.0F, -15.0F, -10.0F, 60.0F, 4.0F, 3.0F)
                        .texOffs(0, 0)
                        .addBox(11.0F, -15.0F, 7.0F, 60.0F, 4.0F, 3.0F),
                PartPose.rotation(
                        0.0F,
                        0.0F,
                        (float) Math.toRadians(-18.0F)
                )
        );

        root.addOrReplaceChild(
                "remainder_conveyor",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-8.0F, -10.0F, -84.0F, 16.0F, 4.0F, 68.0F)
                        .texOffs(0, 0)
                        .addBox(-10.0F, -15.0F, -84.0F, 3.0F, 4.0F, 68.0F)
                        .texOffs(0, 0)
                        .addBox(7.0F, -15.0F, -84.0F, 3.0F, 4.0F, 68.0F),
                PartPose.rotation(
                        (float) Math.toRadians(16.0F),
                        0.0F,
                        0.0F
                )
        );

        root.addOrReplaceChild(
                "selected_support",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(42.0F, 0.0F, -5.0F, 4.0F, 40.0F, 4.0F)
                        .texOffs(0, 0)
                        .addBox(59.0F, 0.0F, -5.0F, 4.0F, 45.0F, 4.0F),
                PartPose.ZERO
        );

        root.addOrReplaceChild(
                "remainder_support",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.0F, 0.0F, -58.0F, 4.0F, 42.0F, 4.0F)
                        .texOffs(0, 0)
                        .addBox(-2.0F, 0.0F, -78.0F, 4.0F, 48.0F, 4.0F),
                PartPose.ZERO
        );

        root.addOrReplaceChild(
                "selected_roller",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.0F, -9.0F, -9.0F, 4.0F, 18.0F, 18.0F),
                PartPose.offset(66.0F, -26.0F, 0.0F)
        );

        root.addOrReplaceChild(
                "remainder_roller",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-9.0F, -9.0F, -2.0F, 18.0F, 18.0F, 4.0F),
                PartPose.offset(0.0F, -27.0F, -78.0F)
        );

        root.addOrReplaceChild(
                "eccentric_weight",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-5.0F, -5.0F, -3.0F, 10.0F, 10.0F, 6.0F),
                PartPose.offset(0.0F, -16.0F, 22.0F)
        );

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(GradationScreenRenderState state) {
        selectedRoller.zRot = state.active ? -state.beltPhase : 0.0F;
        remainderRoller.xRot = state.active ? state.beltPhase : 0.0F;
        eccentricWeight.zRot = state.active
                ? state.beltPhase * 1.75F
                : 0.0F;
    }
}
