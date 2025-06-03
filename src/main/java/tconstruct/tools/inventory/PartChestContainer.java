package tconstruct.tools.inventory;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import invtweaks.api.container.ChestContainer;
import tconstruct.library.tools.DynamicToolPart;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.logic.PartChestLogic;

@ChestContainer
public class PartChestContainer extends Container {

    public PartChestLogic logic;
    public int progress = 0;
    public int fuel = 0;
    public int fuelGague = 0;

    public PartChestContainer(InventoryPlayer inventoryplayer, PartChestLogic chest) {
        logic = chest;
        for (int column = 0; column < 3; column++) {
            for (int row = 0; row < 10; row++) {
                this.addSlotToContainer(new SlotPart(chest, row + column * 10, 8 + row * 18, 18 + column * 18));
            }
        }

        /* Player inventory */
        for (int column = 0; column < 3; column++) {
            for (int row = 0; row < 9; row++) {
                this.addSlotToContainer(
                        new Slot(inventoryplayer, row + column * 9 + 9, 17 + row * 18, 86 + column * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            this.addSlotToContainer(new Slot(inventoryplayer, column, 17 + column * 18, 144));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        Block block = logic.getWorldObj().getBlock(logic.xCoord, logic.yCoord, logic.zCoord);
        if (block != TinkerTools.toolStationWood && block != TinkerTools.craftingSlabWood) return false;
        return logic.isUseableByPlayer(entityplayer);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
        ItemStack stack = null;
        Slot slot = this.inventorySlots.get(slotID);

        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();

            if (slotID < logic.getSizeInventory()) {
                if (!this.mergeItemStack(slotStack, logic.getSizeInventory(), this.inventorySlots.size(), true)) {
                    return null;
                }
            } else if (!this.mergeItemStack(slotStack, 0, logic.getSizeInventory(), false)) {
                return null;
            }

            if (slotStack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
        }

        return stack;
    }

    @Override
    protected boolean mergeItemStack(ItemStack stack, int inventorySize, int slotSize, boolean par4) {
        if (!(stack.getItem() instanceof DynamicToolPart)) return false;

        return super.mergeItemStack(stack, inventorySize, slotSize, par4);
    }

    public static class SlotPart extends Slot {

        public SlotPart(PartChestLogic inventory, int par2, int par3, int par4) {
            super(inventory, par2, par3, par4);
        }

        @Override
        public boolean isItemValid(ItemStack itemstack) {
            return itemstack != null && itemstack.getItem() instanceof DynamicToolPart;
        }
    }
}
