package net.demondev.frosttech.datagen;

import net.demondev.frosttech.FrostTech;
import net.demondev.frosttech.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.*;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, FrostTech.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

    }
}