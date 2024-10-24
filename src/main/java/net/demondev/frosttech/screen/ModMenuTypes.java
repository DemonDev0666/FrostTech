package net.demondev.frosttech.screen;

import net.demondev.frosttech.FrostTech;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, FrostTech.MOD_ID);

    public static final RegistryObject<MenuType<OreFreezerMenu>> ORE_FREEZER_MENU =
            registerMenuType("ore_freezer_menu", OreFreezerMenu::new);
    public static final RegistryObject<MenuType<FrozenOreCrusherMenu>> FROZEN_ORE_CRUSHER_MENU =
            registerMenuType("frozen_ore_crusher_menu", FrozenOreCrusherMenu::new);


    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
