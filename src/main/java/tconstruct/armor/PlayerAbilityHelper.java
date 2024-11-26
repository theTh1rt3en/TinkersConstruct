package tconstruct.armor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import tconstruct.armor.player.ArmorExtended;
import tconstruct.armor.player.TPlayerStats;
import tconstruct.util.config.PHConstruct;

public class PlayerAbilityHelper {

    public static void toggleGoggles(EntityPlayer player, boolean active) {
        TPlayerStats stats = TPlayerStats.get(player);
        stats.activeGoggles = active;
        if (!stats.activeGoggles) {
            player.removePotionEffect(Potion.nightVision.id);
        } else {
            player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 15 * 20, 0, true));
        }
    }

    public static boolean isDimensionAllowed(EntityPlayer player) {
        int currentDimID = player.worldObj.provider.dimensionId;
        for (int id : PHConstruct.cfgForbiddenDim) {
            if (id == currentDimID) return false;
        }
        return true;
    }

    public static void swapBelt(EntityPlayer player, ArmorExtended armor) {
        if (!isDimensionAllowed(player)) return;
        NBTTagList slots = new NBTTagList();
        InventoryPlayer hotbar = player.inventory;
        ItemStack belt = armor.inventory[3];
        if (belt == null) return;

        NBTTagCompound itemTag;

        for (int i = 0; i < 9; ++i) {
            if (hotbar.mainInventory[i] != null) {
                itemTag = new NBTTagCompound();
                itemTag.setByte("Slot", (byte) i);
                hotbar.mainInventory[i].writeToNBT(itemTag);
                slots.appendTag(itemTag);
            }
            hotbar.mainInventory[i] = null;
        }

        NBTTagList replaceSlots = belt.getTagCompound().getTagList("Inventory", 10);
        for (int i = 0; i < replaceSlots.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = replaceSlots.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);

            if (itemstack != null) {
                if (j >= 0 && j < hotbar.mainInventory.length) {
                    hotbar.mainInventory[j] = itemstack;
                }
            }
        }
        belt.getTagCompound().setTag("Inventory", slots);
    }
}
