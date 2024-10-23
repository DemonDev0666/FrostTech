package net.demondev.frosttech.item;

import net.demondev.frosttech.FrostTech;

import net.demondev.frosttech.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FrostTech.MOD_ID);

    public static final RegistryObject<CreativeModeTab> FC_TAB = CREATIVE_MODE_TABS.register("ft_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.FROZEN_RAW_IRON.get()))
                    .title(Component.translatable("creativetab.ft_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.FROZEN_RAW_IRON.get());
                        pOutput.accept(ModItems.FROZEN_RAW_GOLD.get());
                        pOutput.accept(ModItems.FROZEN_RAW_COPPER.get());
                        pOutput.accept(ModBlocks.FROSTY_STONE.get());
                        pOutput.accept(ModBlocks.ORE_FREEZER.get());
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
