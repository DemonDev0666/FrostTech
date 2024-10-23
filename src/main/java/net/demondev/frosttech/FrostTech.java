package net.demondev.frosttech;

import com.mojang.logging.LogUtils;
import net.demondev.frosttech.block.ModBlocks;
import net.demondev.frosttech.block.entity.ModBlockEntities;
import net.demondev.frosttech.item.CreativeTab;
import net.demondev.frosttech.item.ModItems;
import net.demondev.frosttech.recipes.ModRecipes;
import net.demondev.frosttech.screen.ModMenuTypes;
import net.demondev.frosttech.screen.OreFreezerScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(FrostTech.MOD_ID)
public class FrostTech
{

    public static final String MOD_ID = "frost_tech";
    private static final Logger LOGGER = LogUtils.getLogger();
    public FrostTech()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();


        modEventBus.addListener(this::commonSetup);

        CreativeTab.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModMenuTypes.register(modEventBus);

          MinecraftForge.EVENT_BUS.register(this);


        modEventBus.addListener(this::addCreative);


    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {}


    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {}


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {}

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            MenuScreens.register(ModMenuTypes.ORE_FREEZER_MENU.get(), OreFreezerScreen::new);
        }
    }
}
