package net.demondev.frosttech.block;

import net.demondev.frosttech.FrostTech;
import net.demondev.frosttech.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, FrostTech.MOD_ID);

    public static final RegistryObject<Block> FROSTY_STONE = registerBlock("frosty_stone", () ->
            new Block(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .strength(5.0f, 15f)
                    .sound(SoundType.GLASS)
                    .friction(0.98f)
            ));
    public static final RegistryObject<Block> ORE_FREEZER = registerBlock("ore_freezer", () ->
            new Block(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .noOcclusion()
            ));
    public static final RegistryObject<Block> FROST_GENERATOR = registerBlock("frost_generator", () ->
            new Block(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .noOcclusion()
            ));



    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}

