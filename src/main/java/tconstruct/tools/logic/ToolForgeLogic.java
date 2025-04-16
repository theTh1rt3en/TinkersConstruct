package tconstruct.tools.logic;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;

import tconstruct.tools.inventory.ToolForgeContainer;

/*
 * Simple class for storing items in the block
 */

public class ToolForgeLogic extends ToolStationLogic {

    public ToolForgeLogic() {
        super(6);
    }

    @Override
    public String getDefaultName() {
        return "crafters.ToolForge";
    }

    @Override
    public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new ToolForgeContainer(inventoryplayer, this);
    }
}
