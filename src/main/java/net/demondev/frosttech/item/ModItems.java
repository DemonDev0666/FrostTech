package net.demondev.frosttech.item;

import net.demondev.frosttech.FrostTech;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, FrostTech.MOD_ID);


    public static final RegistryObject<Item> FROST_WRENCH = ITEMS.register("frost_wrench",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
            ));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
