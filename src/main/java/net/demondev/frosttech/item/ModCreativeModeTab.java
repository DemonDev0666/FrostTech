package net.demondev.frosttech.item;

import net.demondev.frosttech.FrostTech;
import net.demondev.frosttech.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FrostTech.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TUTORIAL_TAB = CREATIVE_MODE_TABS.register("tutorial_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.FROST_WRENCH.get()))
                    .title(Component.translatable("creativetab.frost_tech_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.FROST_WRENCH.get());
                        pOutput.accept(ModBlocks.FROSTY_STONE.get());
                        pOutput.accept(ModBlocks.ORE_FREEZER.get());




                    })
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
