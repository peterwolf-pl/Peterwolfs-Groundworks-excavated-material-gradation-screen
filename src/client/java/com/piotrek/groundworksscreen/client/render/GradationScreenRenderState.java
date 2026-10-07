package com.piotrek.groundworksscreen.client.render;

import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class GradationScreenRenderState extends EntityRenderState {
    public float baseYaw;
    public boolean active;
    public float beltPhase;
    public int selectedMaterialId;
    public int storedUnits;
}
