package net.demondev.frosttech.item;

import net.demondev.frosttech.FrostTech;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, FrostTech.MOD_ID);

    public static final RegistryObject<Item> FROZEN_RAW_IRON = ITEMS.register("frozen_raw_iron",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> FROZEN_RAW_COPPER = ITEMS.register("frozen_raw_copper",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> FROZEN_RAW_GOLD = ITEMS.register("frozen_raw_gold",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
