package tconstruct.tools.gui;

import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChestSlot extends Slot {

    /*
     * A Slot, used for adjacent Chest Inventories, that can be disabled.
     */
    public boolean enabled = true;
    private final int accessSide;
    @Getter
    private final int visualIndex;

    public ChestSlot(IInventory inventory, int index, int visualIndex, int xPosition, int yPosition, int accessSide) {
        super(inventory, index, xPosition, yPosition);
        this.accessSide = accessSide;
        this.visualIndex = visualIndex;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean func_111238_b /* isEnabled */() {
        return enabled;
    }

    public void disable() {
        enabled = false;
    }

    public void enable() {
        enabled = true;
    }

    @Override
    public boolean isItemValid(ItemStack itemStack) {
        if (inventory instanceof ISidedInventory sided) {
            return sided.canInsertItem(this.getSlotIndex(), itemStack, accessSide);
        }
        return inventory.isItemValidForSlot(this.getSlotIndex(), itemStack);
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        if (inventory instanceof ISidedInventory sided) {
            return sided.canExtractItem(this.getSlotIndex(), this.getStack(), accessSide);
        }
        return super.canTakeStack(player);
    }

    @Override
    public ItemStack getStack() {
        ItemStack stack = super.getStack();
        return (stack != null && stack.stackSize > 0) ? stack : null;
    }
}
