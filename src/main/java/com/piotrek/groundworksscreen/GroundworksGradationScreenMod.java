package com.piotrek.groundworksscreen;

import com.piotrek.groundworksscreen.entity.GradationScreenEntity;
import com.piotrek.groundworksscreen.item.GradationScreenItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroundworksGradationScreenMod implements ModInitializer {
    public static final String MOD_ID = "pw_groundworks_gradation_screen";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static final ResourceKey<EntityType<?>> GRADATION_SCREEN_KEY =
            ResourceKey.create(Registries.ENTITY_TYPE, id("gradation_screen"));

    public static final EntityType<GradationScreenEntity> GRADATION_SCREEN = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            GRADATION_SCREEN_KEY,
            EntityType.Builder.of(GradationScreenEntity::new, MobCategory.MISC)
                    .sized(3.0F, 4.5F)
                    .clientTrackingRange(14)
                    .build(GRADATION_SCREEN_KEY)
    );

    public static final ResourceKey<Item> GRADATION_SCREEN_ITEM_KEY =
            ResourceKey.create(Registries.ITEM, id("gradation_screen"));

    public static final GradationScreenItem GRADATION_SCREEN_ITEM = Registry.register(
            BuiltInRegistries.ITEM,
            GRADATION_SCREEN_ITEM_KEY,
            new GradationScreenItem(
                    new Item.Properties()
                            .setId(GRADATION_SCREEN_ITEM_KEY)
                            .stacksTo(1)
            )
    );

    public static final ResourceKey<CreativeModeTab> TOOLS_AND_UTILITIES_TAB =
            ResourceKey.create(
                    Registries.CREATIVE_MODE_TAB,
                    Identifier.withDefaultNamespace("tools_and_utilities")
            );

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Peterwolf's Groundworks Gradation Screen for MC 26.3...");

        CreativeModeTabEvents.modifyOutputEvent(TOOLS_AND_UTILITIES_TAB).register(
                output -> output.accept(GRADATION_SCREEN_ITEM)
        );
    }
}
