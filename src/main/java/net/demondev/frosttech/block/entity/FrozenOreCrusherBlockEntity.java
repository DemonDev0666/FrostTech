package net.demondev.frosttech.block.entity;


import net.demondev.frosttech.networking.ModMessages;
import net.demondev.frosttech.networking.packet.EnergySyncS2CPacket;
import net.demondev.frosttech.recipes.OreCrushingRecipe;
import net.demondev.frosttech.recipes.OreFreezerRecipe;
import net.demondev.frosttech.screen.FrozenOreCrusherMenu;
import net.demondev.frosttech.util.ModEnergyStorage;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FrozenOreCrusherBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(2);

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 78;

    public FrozenOreCrusherBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.FROZEN_ORE_CRUSHER_BE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> FrozenOreCrusherBlockEntity.this.progress;
                    case 1 -> FrozenOreCrusherBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> FrozenOreCrusherBlockEntity.this.progress = pValue;
                    case 1 -> FrozenOreCrusherBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    private final ModEnergyStorage ENERGY_STORAGE = new ModEnergyStorage(20000, 256) {
        @Override
        public void onEnergyChanged() {
            setChanged();
            ModMessages.sendToClients(new EnergySyncS2CPacket(this.energy, getBlockPos()));
        }
    };

    private static final int ENERGY_REQ = 10;
    private static final int ENERGY_PER_REDSTONE = 10;

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyHandler = LazyOptional.of(() -> ENERGY_STORAGE);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyEnergyHandler.invalidate();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.frost_tech.frozen_ore_crusher");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new FrozenOreCrusherMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("frozen_ore_crusher.progress", progress);
        pTag.putInt("frozen_ore_crusher.energy", ENERGY_STORAGE.getEnergyStored());

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("frozen_ore_crusher.progress");
        ENERGY_STORAGE.setEnergy(pTag.getInt("frozen_ore_crusher.energy"));
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState, FrozenOreCrusherBlockEntity pEntity) {
        if(hasRedstoneInFirstSlot(pEntity)) {
            pEntity.ENERGY_STORAGE.receiveEnergy(64, false);
            consumeRedstoneForEnergy(pEntity);
        }

        if(hasRecipe() && hasEnoughEnergy(pEntity)) {
            increaseCraftingProgress();
            extractEnergy(pEntity);
            setChanged(pLevel, pPos, pState);

            if(hasProgressFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private boolean hasEnoughEnergy(FrozenOreCrusherBlockEntity pEntity) {
        return pEntity.ENERGY_STORAGE.getEnergyStored() >= ENERGY_REQ * pEntity.maxProgress;
    }

    private void extractEnergy(FrozenOreCrusherBlockEntity pEntity) {
        pEntity.ENERGY_STORAGE.extractEnergy(ENERGY_REQ, false);
    }

    private static boolean hasRedstoneInFirstSlot(FrozenOreCrusherBlockEntity pEntity) {
        return pEntity.itemHandler.getStackInSlot(0).getItem() == Items.REDSTONE;
    }
    private void consumeRedstoneForEnergy(FrozenOreCrusherBlockEntity pEntity) {
        ItemStack redstoneStack = pEntity.itemHandler.getStackInSlot(INPUT_SLOT);
        if (!redstoneStack.isEmpty() && redstoneStack.getItem() == Items.REDSTONE) {
            pEntity.ENERGY_STORAGE.receiveEnergy(ENERGY_PER_REDSTONE, false);
            redstoneStack.shrink(1);
            if (redstoneStack.isEmpty()) {
                pEntity.itemHandler.setStackInSlot(INPUT_SLOT, ItemStack.EMPTY);
            }
        }
    }

    private void resetProgress() {
        progress = 0;
    }

    private void craftItem() {
        Optional<OreCrushingRecipe> recipe = getCurrentRecipe();
        ItemStack result = recipe.get().getResultItem(null);

        this.itemHandler.extractItem(INPUT_SLOT, 1, false);

        this.itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(result.getItem(),
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + result.getCount()));
    }

    private boolean hasRecipe() {
        Optional<OreCrushingRecipe> recipe = getCurrentRecipe();

        if(recipe.isEmpty()) {
            return false;
        }
        ItemStack result = recipe.get().getResultItem(getLevel().registryAccess());

        return canInsertAmountIntoOutputSlot(result.getCount()) && canInsertItemIntoOutputSlot(result.getItem());
    }

    private Optional<OreCrushingRecipe> getCurrentRecipe() {
        SimpleContainer inventory = new SimpleContainer(this.itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, this.itemHandler.getStackInSlot(i));
        }

        return this.level.getRecipeManager().getRecipeFor(OreCrushingRecipe.Type.INSTANCE, inventory, level);
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

    public IEnergyStorage getEnergyStorage() {
        return ENERGY_STORAGE;
    }

    public void setEnergyLevel(int energy) {
        this.ENERGY_STORAGE.setEnergy(energy);
    }
}