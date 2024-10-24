package net.demondev.frosttech.block.entity;

import net.demondev.frosttech.FrostTech;
import net.demondev.frosttech.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, FrostTech.MOD_ID);

    public static final RegistryObject<BlockEntityType<OreFreezerBlockEntity>> ORE_FREEZER_BE =
            BLOCK_ENTITIES.register("ore_freezer_be", () ->
                    BlockEntityType.Builder.of(OreFreezerBlockEntity::new,
                            ModBlocks.ORE_FREEZER.get()).build(null));
    public static final RegistryObject<BlockEntityType<FrozenOreCrusherBlockEntity>> FROZEN_ORE_CRUSHER_BE =
            BLOCK_ENTITIES.register("frozen_ore_crusher_be", () ->
                    BlockEntityType.Builder.of(FrozenOreCrusherBlockEntity::new,
                            ModBlocks.FROZEN_ORE_CRUSHER.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}