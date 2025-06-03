package tconstruct.library.crafting;

import static tconstruct.util.Reference.MOD_ID;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;

import tconstruct.library.tools.ToolCore;

public class ShapelessToolRecipe extends ShapelessRecipes {

    static {
        RecipeSorter
                .register(MOD_ID + ":" + "toolrecipe", ShapelessToolRecipe.class, RecipeSorter.Category.SHAPELESS, "");
    }

    public ShapelessToolRecipe(ItemStack par1ItemStack, List par2List) {
        super(par1ItemStack, par2List);
    }

    @Override
    public boolean matches(InventoryCrafting par1InventoryCrafting, World par2World) {
        ArrayList<ItemStack> arraylist = new ArrayList<>(this.recipeItems);

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                ItemStack itemstack = par1InventoryCrafting.getStackInRowAndColumn(j, i);

                if (itemstack != null) {
                    boolean flag = false;

                    for (ItemStack itemstack1 : arraylist) {
                        // TConstruct.logger.info("Rawr! "+itemstack1.getItemDamage());
                        if (itemstack.getItem() == itemstack1.getItem()) {
                            if (itemstack.getItem() instanceof ToolCore) {
                                NBTTagCompound tags = itemstack.getTagCompound().getCompoundTag("InfiTool");
                                if (tags.getBoolean("Broken")) return false;

                                flag = true;
                            } else if ((itemstack1.getItemDamage() == Short.MAX_VALUE
                                    || itemstack.getItemDamage() == itemstack1.getItemDamage()))
                                flag = true;

                            arraylist.remove(itemstack1);
                            break;
                        }
                    }

                    if (!flag) {
                        return false;
                    }
                }
            }
        }

        return arraylist.isEmpty();
    }
}
