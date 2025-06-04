package tconstruct.tools.logic;

import static tconstruct.util.Reference.MOD_ID;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import lombok.extern.log4j.Log4j2;
import mantle.blocks.abstracts.InventoryLogic;
import tconstruct.library.util.IToolPart;
import tconstruct.tools.inventory.PartChestContainer;

@Log4j2(topic = MOD_ID)
public class PartChestLogic extends InventoryLogic {

    public PartChestLogic() {
        super(30);
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public String getDefaultName() {
        return "toolstation.partholder";
    }

    @Override
    public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new PartChestContainer(inventoryplayer, this);
    }

    @Override
    public String getInventoryName() {
        return getDefaultName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
        return itemstack != null && itemstack.getItem() instanceof IToolPart;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}
}
