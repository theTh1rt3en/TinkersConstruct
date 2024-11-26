package tconstruct.tools.inventory;

import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;

import tconstruct.tools.logic.CraftingStationLogic;

public class InventoryCraftingStationResult extends InventoryCraftResult {

    CraftingStationLogic logic;

    public InventoryCraftingStationResult(CraftingStationLogic logic) {
        this.logic = logic;
    }

    /**
     * Returns the stack in slot i
     */
    @Override
    public ItemStack getStackInSlot(int par1) {
        return logic.getStackInSlot(0);
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    @Override
    public ItemStack decrStackSize(int par1, int par2) {
        ItemStack stack = logic.getStackInSlot(0);
        if (stack != null) {
            logic.setInventorySlotContents(0, null);
            return stack;
        } else {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    @Override
    public ItemStack getStackInSlotOnClosing(int par1) {
        return null;
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    @Override
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
        logic.setInventorySlotContents(0, par2ItemStack);
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    /**
     * Called when an the contents of an Inventory change, usually
     */
    @Override
    public void markDirty() {}

}
