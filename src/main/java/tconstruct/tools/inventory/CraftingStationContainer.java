package tconstruct.tools.inventory;

import com.google.common.primitives.Ints;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;
import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.library.modifier.IModifyable;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.gui.ChestSlot;
import tconstruct.tools.logic.CraftingStationLogic;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

public class CraftingStationContainer extends Container {
    private final World worldObj;
    private final int posX;
    private final int posY;
    private final int posZ;
    
    @SuppressWarnings("rawtypes")
    private final WeakReference [] inventories;
    
    /**
     * The crafting matrix inventory (3x3).
     */
    public InventoryCrafting craftMatrix;
    public IInventory craftResult;
    public CraftingStationLogic logic;
    EntityPlayer player;

    public CraftingStationContainer(InventoryPlayer inventoryplayer, CraftingStationLogic logic, int x, int y, int z) {
        this.worldObj = logic.getWorldObj();
        this.player = inventoryplayer.player;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.logic = logic;
        craftMatrix = new InventoryCraftingStation(this, 3, 3, logic);
        craftResult = new InventoryCraftingStationResult(logic);
        this.inventories = logic.getInventories();

        int row, col;

        int craftingOffsetX = 30;
        int inventoryOffsetX = 8;

        if (logic.chest != null) {
            craftingOffsetX += 116;
            inventoryOffsetX += 116;
        }
        
        // 0 - crafting slot
        this.addSlotToContainer(new SlotCraftingStation(inventoryplayer.player, this.craftMatrix, this.craftResult, 0, craftingOffsetX + 94, 35));

        // 1 - 9 - Crafting Matrix
        for (row = 0; row < 3; ++row) {
            for (col = 0; col < 3; ++col) {
                this.addSlotToContainer(new Slot(this.craftMatrix, col + row * 3, craftingOffsetX + col * 18, 17 + row * 18));
            }
        }

        // Player Inventory 10 - 36
        for (row = 0; row < 3; ++row) {
            for (col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(inventoryplayer, col + row * 9 + 9, inventoryOffsetX + col * 18, 84 + row * 18));
            }
        }
        // Player Hotbar - 37 - 46
        for (col = 0; col < 9; ++col) {
            this.addSlotToContainer(new Slot(inventoryplayer, col, inventoryOffsetX + col * 18, 142));
        }

        // Side inventory - 47+
        if (logic.chest != null) {
            IInventory inv = logic.getFirstInventory();
            IInventory secondInv = logic.getSecondInventory();

            final Set<Integer> accessibleSlots = inv instanceof ISidedInventory ? new HashSet<>(Ints.asList(((ISidedInventory) inv).getAccessibleSlotsFromSide(logic.chestDirection.getOpposite().ordinal()))) : null;
            
            int index = 0, curIndex = 0;
            IInventory curInv;
            final int invSize = inv.getSizeInventory() * (secondInv != null ? 2 : 1); 
            for (row = 0; row < logic.invRows; row++) {
                for (col = 0; col < logic.invColumns; col++) {
                    if(index >= invSize) break;
                    // Adjust the inventory to account for double chests
                    curInv = secondInv != null && index >= 27 ? secondInv : inv;
                    // Adjust the index for the inventory
                    curIndex = secondInv != null && index >= 27 ? index - 27 : index;
                    
                    if(accessibleSlots == null || accessibleSlots.contains(curIndex)) {
                        this.addSlotToContainer(new ChestSlot(curInv, curIndex, 8 + col * 18, 19 + row * 18));
                    }
                    index++;
                }
            }
        }

        this.onCraftMatrixChanged(this.craftMatrix);
    }

    public ItemStack modifyItem() {
        ItemStack input = craftMatrix.getStackInSlot(4);
        if (input != null) {
            Item item = input.getItem();
            if (item instanceof IModifyable) {
                ItemStack[] slots = new ItemStack[8];
                for (int i = 0; i < 4; i++) {
                    slots[i] = craftMatrix.getStackInSlot(i);
                    slots[i + 4] = craftMatrix.getStackInSlot(i + 5);
                }
                return ModifyBuilder.instance.modifyItem(input, slots);
            }
        }
        return null;
    }

    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int index) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 0) {
                if (itemstack.getItem() instanceof IModifyable) {
                    if (!this.mergeCraftedStack(itemstack1, logic.getSizeInventory(), this.inventorySlots.size(), true, entityPlayer)) {
                        return null;
                    }
                } else {
                    if (!this.mergeItemStack(itemstack1, 10, 46, true)) {
                        return null;
                    }
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (index >= 10 && index < 37) {
                // From Player Inv
                if (!this.mergeItemStack(itemstack1, 37, 46, false)) {
                    // To Hotbar
                    return null;
                }
            } else if (index >= 37 && index < 46) {
                // From Hotbar
                if (!this.mergeItemStack(itemstack1, 10, 37, false)) {
                    // To Player Inv
                    return null;
                }
            } else if (!this.mergeItemStack(itemstack1, 10, 46, false)) {
                // To Player Inv or Hotbar
                return null;
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack((ItemStack) null);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(entityPlayer, itemstack1);
        }

        return itemstack;
    }

    protected boolean mergeCraftedStack(ItemStack stack, int slotsStart, int slotsTotal, boolean playerInventory, EntityPlayer player) {
        boolean failedToMerge = false;
        int slotIndex = slotsStart;

        if (playerInventory) {
            slotIndex = slotsTotal - 1;
        }

        Slot otherInventorySlot;
        ItemStack copyStack = null;

        if (stack.stackSize > 0) {
            while (!playerInventory && slotIndex < slotsTotal || playerInventory && slotIndex >= slotsStart) {
                otherInventorySlot = (Slot) this.inventorySlots.get(slotIndex);
                copyStack = otherInventorySlot.getStack();

                if (copyStack == null) {
                    otherInventorySlot.putStack(stack.copy());
                    otherInventorySlot.onSlotChanged();
                    stack.stackSize = 0;
                    failedToMerge = true;
                    break;
                }

                if (playerInventory) {
                    --slotIndex;
                } else {
                    ++slotIndex;
                }
            }
        }

        return failedToMerge;
    }

    public boolean func_94530_a/*canMergeSlot*/(ItemStack par1ItemStack, Slot par2Slot) {
        return par2Slot.inventory != this.craftResult && super.func_94530_a(par1ItemStack, par2Slot);
    }

    @Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);

        if (!this.worldObj.isRemote) {
            for (int i = 0; i < 9; ++i) {
                ItemStack itemstack = this.craftMatrix.getStackInSlotOnClosing(i);

                if (itemstack != null) {
                    par1EntityPlayer.dropPlayerItemWithRandomChoice(itemstack, false);
                }
            }
        }
    }

    public void onCraftMatrixChanged(IInventory par1IInventory) {
        ItemStack tool = modifyItem();
        if (tool != null)
            this.craftResult.setInventorySlotContents(0, tool);
        else
            this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.worldObj));
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {

        Block block = worldObj.getBlock(this.posX, this.posY, this.posZ);
        if (block != TinkerTools.craftingStationWood && block != TinkerTools.craftingSlabWood)
            return false;

        if (!this.logic.isUseableByPlayer(player) || !CraftingStationLogic.isUseableByPlayer(player, this.inventories))
            return false;

        return player.getDistanceSq((double) this.posX + 0.5D, (double) this.posY + 0.5D, (double) this.posZ + 0.5D) <= 64.0D;
    }
}
