package net.demondev.frosttech.block.entity;

import net.demondev.frosttech.recipes.OreFreezerRecipe;
import net.demondev.frosttech.screen.OreFreezerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class OreFreezerBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(2);
    private final EnergyStorage energyStorage = new EnergyStorage(10000);  // Energy storage capacity
    private static final int ENERGY_PER_TICK = 20;  // Energy required per tick for crafting

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 78;  // Time it takes to complete crafting

    public OreFreezerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.ORE_FREEZER_BE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> OreFreezerBlockEntity.this.progress;
                    case 1 -> OreFreezerBlockEntity.this.maxProgress;
                    case 2 -> OreFreezerBlockEntity.this.energyStorage.getEnergyStored();  // Track energy stored
                    case 3 -> OreFreezerBlockEntity.this.energyStorage.getMaxEnergyStored();  // Track max energy
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> OreFreezerBlockEntity.this.progress = pValue;
                    case 1 -> OreFreezerBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 4;  // Tracking progress and energy levels
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();  // Expose energy capability
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyHandler = LazyOptional.of(() -> energyStorage);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyEnergyHandler.invalidate();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.frost_tech.ore_freezer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new OreFreezerMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("ore_freezer.progress", progress);
        pTag.putInt("energy", energyStorage.getEnergyStored());  // Save energy stored
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("ore_freezer.progress");

        int storedEnergy = pTag.getInt("energy");
        energyStorage.extractEnergy(energyStorage.getEnergyStored(), false);  // Clear current energy
        energyStorage.receiveEnergy(storedEnergy, false);  // Load energy stored from NBT
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        System.out.println("Energy Stored in Block Entity: " + energyStorage.getEnergyStored());

        if (hasRecipe() && hasEnoughEnergy()) {
            drainEnergyPerTick();  // Drain energy per tick
            increaseCraftingProgress();
            setChanged(pLevel, pPos, pState);  // Sync GUI

            if (hasProgressFinished()) {
                craftItem();
                resetProgress();
                setChanged(pLevel, pPos, pState);
            }
        } else {
            resetProgress();
        }
    }

    private boolean hasEnoughEnergy() {
        return energyStorage.getEnergyStored() >= ENERGY_PER_TICK;  // Ensure block has sufficient energy per tick
    }

    private void drainEnergyPerTick() {
        energyStorage.extractEnergy(ENERGY_PER_TICK, false);  // Drain energy for each crafting tick
    }

    private void craftItem() {
        Optional<OreFreezerRecipe> recipe = getCurrentRecipe();
        if (recipe.isPresent()) {
            ItemStack result = recipe.get().getResultItem(null);
            this.itemHandler.extractItem(INPUT_SLOT, 1, false);
            this.itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(result.getItem(),
                    this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + result.getCount()));
        }
    }

    private boolean hasRecipe() {
        Optional<OreFreezerRecipe> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) {
            return false;
        }
        ItemStack result = recipe.get().getResultItem(getLevel().registryAccess());
        return canInsertAmountIntoOutputSlot(result.getCount()) && canInsertItemIntoOutputSlot(result.getItem());
    }

    private Optional<OreFreezerRecipe> getCurrentRecipe() {
        SimpleContainer inventory = new SimpleContainer(this.itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, this.itemHandler.getStackInSlot(i));
        }
        return this.level.getRecipeManager().getRecipeFor(OreFreezerRecipe.Type.INSTANCE, inventory, level);
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() || this.itemHandler.getStackInSlot(OUTPUT_SLOT).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + count <= this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }

    private boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private void resetProgress() {
        progress = 0;
    }

    public int getEnergyStored() {
        return energyStorage.getEnergyStored();  // Access current energy stored
    }

    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();  // Access maximum energy storage
    }
}
