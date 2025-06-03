package tconstruct.tools.items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mantle.items.abstracts.CraftingItem;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.PatternBuilder.MaterialSet;
import tconstruct.library.util.IPattern;

public class Pattern extends CraftingItem implements IPattern {

    public Pattern(String patternType, String folder) {
        this(patternName, getPatternNames(patternType), folder);
    }

    public Pattern(String[] names, String[] patternTypes, String folder) {
        super(names, patternTypes, folder, "tinker", TConstructRegistry.materialTab);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setContainerItem(this);
        this.setMaxStackSize(1);
    }

    protected static String[] getPatternNames(String partType) {
        String[] names = new String[patternName.length];
        for (int i = 0; i < patternName.length; i++) names[i] = partType + patternName[i];
        return names;
    }

    private static final String[] patternName = new String[] { "ingot", "rod", "pickaxe", "shovel", "axe", "swordblade",
            "largeguard", "mediumguard", "crossbar", "binding", "frypan", "sign", "knifeblade", "chisel", "largerod",
            "toughbinding", "largeplate", "broadaxe", "scythe", "excavator", "largeblade", "hammerhead", "fullguard",
            "bowstring", "fletching", "arrowhead" };

    @Getter
    private static final Map<Integer, Integer> patternCosts = buildPatternCostMap();

    @Override
    public void getSubItems(Item b, CreativeTabs tab, List list) {
        for (int i = 1; i < patternName.length; i++) {
            // if (i != 23)
            list.add(new ItemStack(b, 1, i));
        }
    }

    // 2 for full material, 1 for half.
    private static Map<Integer, Integer> buildPatternCostMap() {
        Map<Integer, Integer> map = new HashMap<>();

        map.put(0, 2);
        map.put(1, 1);
        map.put(2, 2);
        map.put(3, 2);
        map.put(4, 2);
        map.put(5, 2);
        map.put(6, 1);
        map.put(7, 1);
        map.put(8, 1);
        map.put(9, 1);
        map.put(10, 2);
        map.put(11, 2);
        map.put(12, 1);
        map.put(13, 1);
        map.put(14, 6);
        map.put(15, 6);
        map.put(16, 16);
        map.put(17, 16);
        map.put(18, 16);
        map.put(19, 16);
        map.put(20, 16);
        map.put(21, 16);
        map.put(22, 6);
        map.put(23, 6);
        map.put(24, 2);
        map.put(25, 2);

        return map;
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        if (stack.stackSize <= 0) return null;
        return stack;
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack) {
        return false;
    }

    /* Tags and information about the pattern */
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean par4) {
        float cost = getPatternCost(stack) / 2f;
        if (cost > 0) {
            if (cost - (int) cost < 0.1) list.add(StatCollector.translateToLocal("pattern1.tooltip") + (int) cost);
            else list.add(StatCollector.translateToLocal("pattern2.tooltip") + cost);
        }
    }

    public static void setPatternCost(Integer index, Integer newCost) {
        if (patternCosts.containsKey(index)) {
            patternCosts.put(index, newCost);
        }
    }

    @Override
    public int getPatternCost(ItemStack pattern) {
        return patternCosts.getOrDefault(pattern.getItemDamage(), 0);
    }

    @Override
    public ItemStack getPatternOutput(ItemStack stack, ItemStack input, MaterialSet set) {
        return TConstructRegistry.getPartMapping(this, stack.getItemDamage(), set.materialID);
    }
}
