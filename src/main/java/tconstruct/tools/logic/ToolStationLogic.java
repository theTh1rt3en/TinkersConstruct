package tconstruct.tools.logic;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import mantle.blocks.abstracts.InventoryLogic;
import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.modifier.IModifyable;
import tconstruct.tools.inventory.ToolStationContainer;

/*
 * Simple class for storing items in the block
 */

public class ToolStationLogic extends InventoryLogic implements ISidedInventory {

    public ItemStack previousTool;
    public String toolName;

    public ToolStationLogic() {
        super(4);
        toolName = "";
    }

    public ToolStationLogic(int slots) {
        super(slots);
        toolName = "";
    }

    @Override
    public boolean canDropInventorySlot(int slot) {
        return slot != 0;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int var1) {
        return null;
    }

    @Override
    public String getDefaultName() {
        return "crafters.ToolStation";
    }

    @Override
    public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new ToolStationContainer(inventoryplayer, this);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        super.setInventorySlotContents(slot, stack);
        if (slot != 0) {
            buildTool(toolName);
        }
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack itemstack = super.decrStackSize(slot, amount);
        if (slot != 0) {
            buildTool(toolName);
        }
        return itemstack;
    }

    @Override
    public void markDirty() {
        if (this.worldObj != null) {
            this.blockMetadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
            this.worldObj.markTileEntityChunkModified(this.xCoord, this.yCoord, this.zCoord, this);
        }
    }

    public void buildTool(String name) {
        ItemStack output = null;
        if (inventory[1] != null) {
            if (inventory[1].getItem() instanceof IModifyable) // Modify item
            {
                if (inventory[2] == null && inventory[3] == null) output = inventory[1].copy();
                else {
                    output = ModifyBuilder.instance
                            .modifyItem(inventory[1], new ItemStack[] { inventory[2], inventory[3] });
                }
            } else
            // Build new item
            {
                toolName = name;
                ItemStack tool = ToolBuilder.instance.buildTool(inventory[1], inventory[2], inventory[3], name);
                if (inventory[0] == null) output = tool;
                else if (tool != null) {
                    NBTTagCompound tags = tool.getTagCompound();
                    if (!tags.getCompoundTag(((IModifyable) tool.getItem()).getBaseTagName()).hasKey("Built")) {
                        output = tool;
                    }
                }
            }
            if (!name.isEmpty()) // Name item
                output = tryRenameTool(output, name);
        }
        inventory[0] = output;
    }

    public void setToolname(String name) {
        toolName = name;
        buildTool(name);
    }

    protected ItemStack tryRenameTool(ItemStack output, String name) {
        ItemStack temp;
        if (output != null) temp = output;
        else temp = inventory[1].copy();

        NBTTagCompound tags = temp.getTagCompound();
        if (tags == null) {
            tags = new NBTTagCompound();
            temp.setTagCompound(tags);
        }

        NBTTagCompound display = null;
        if (tags.hasKey("display") && tags.getCompoundTag("display").hasKey("Name"))
            display = tags.getCompoundTag("display");

        boolean doRename = false;
        if (display == null) {
            display = new NBTTagCompound();
            doRename = true;
        }
        // we only allow renaming with a nametag otherwise
        else if (!name.equals(display.getString("Name"))) {
            int nametagCount = 0;
            for (ItemStack itemStack : inventory)
                if (itemStack != null && itemStack.getItem() == Items.name_tag) nametagCount++;

            doRename = nametagCount == 1;
        }

        if (!doRename) return output;

        display.setString("Name", name);
        tags.setTag("display", display);
        temp.setRepairCost(2);
        output = temp;

        return output;
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemstack, int j) {
        return false;
    }

    @Override
    public boolean canExtractItem(int i, ItemStack itemstack, int j) {
        return false;
    }

    @Override
    public String getInventoryName() {
        return "null";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    public static boolean canRename(NBTTagCompound tags, ItemStack tool) {
        return tags != null && (!tags.hasKey("Name")
                || tags.getString("Name").equals("\u00A7f" + ToolBuilder.defaultToolName(tool)));
    }
}
