package tconstruct.tools.logic;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.modifier.IModifyable;
import tconstruct.tools.inventory.ToolForgeContainer;

/*
 * Simple class for storing items in the block
 */

public class ToolForgeLogic extends ToolStationLogic implements ISidedInventory {

    ItemStack previousTool;
    String toolName;

    public ToolForgeLogic() {
        super(6);
        toolName = "";
    }

    @Override
    public String getDefaultName() {
        return "crafters.ToolForge";
    }

    @Override
    public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new ToolForgeContainer(inventoryplayer, this);
    }

    @Override
    public void buildTool(String name) {
        ItemStack output = null;
        if (inventory[1] != null) {
            if (inventory[1].getItem() instanceof IModifyable) // Modify item
            {
                if (inventory[2] == null && inventory[3] == null && inventory[4] == null) output = inventory[1].copy();
                else {
                    output = ModifyBuilder.instance
                            .modifyItem(inventory[1], new ItemStack[] { inventory[2], inventory[3], inventory[4] });
                }
            } else
            // Build new item
            {
                toolName = name;
                ItemStack tool = ToolBuilder.instance
                        .buildTool(inventory[1], inventory[2], inventory[3], inventory[4], name);
                if (inventory[0] == null) output = tool;
                else if (tool != null) {
                    output = tool;
                }
            }
            if (!name.isEmpty()) // Name item
                output = tryRenameTool(output, name);
        }
        inventory[0] = output;
    }
}
