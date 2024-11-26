package tconstruct.tools.inventory;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import tconstruct.armor.inventory.SlotOnlyTake;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.logic.StencilTableLogic;

public class PatternShaperContainer extends Container {

    public StencilTableLogic logic;

    public PatternShaperContainer(InventoryPlayer inventoryplayer, StencilTableLogic shaper) {
        logic = shaper;
        this.addSlotToContainer(new Slot(shaper, 0, 48, 35));
        this.addSlotToContainer(new SlotOnlyTake(shaper, 1, 106, 35));

        /* Player inventory */
        for (int column = 0; column < 3; column++) {
            for (int row = 0; row < 9; row++) {
                this.addSlotToContainer(
                        new Slot(inventoryplayer, row + column * 9 + 9, 8 + row * 18, 84 + column * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            this.addSlotToContainer(new Slot(inventoryplayer, column, 8 + column * 18, 142));
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
        return null;
    }
}
