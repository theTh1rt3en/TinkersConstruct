package tconstruct.armor.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mantle.items.abstracts.CraftingItem;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.accessory.IAccessory;

public class Knapsack extends CraftingItem implements IAccessory {

    public Knapsack() {
        super(
                new String[] { "knapsack" },
                new String[] { "knapsack" },
                "armor/",
                "tinker",
                TConstructRegistry.materialTab);
        this.setMaxStackSize(10);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        if (stack.getItemDamage() == 0) {
            list.add(StatCollector.translateToLocal("knapsack.tooltip"));
        }
    }

    @Override
    public boolean canEquipAccessory(ItemStack item, int slot) {
        return slot == 2;
    }

}
