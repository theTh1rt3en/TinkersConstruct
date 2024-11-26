package tconstruct.items.tools;

import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

import tconstruct.library.tools.HarvestTool;
import tconstruct.tools.TinkerTools;

public class Shovel extends HarvestTool {

    public Shovel() {
        super(2);
        this.setUnlocalizedName("InfiTool.Shovel");
    }

    @Override
    protected Material[] getEffectiveMaterials() {
        return materials;
    }

    @Override
    protected String getHarvestType() {
        return "shovel";
    }

    static Material[] materials = { Material.grass, Material.ground, Material.sand, Material.snow, Material.craftedSnow,
            Material.clay };

    @Override
    public Item getHeadItem() {
        return TinkerTools.shovelHead;
    }

    @Override
    public Item getAccessoryItem() {
        return null;
    }

    @Override
    public int getPartAmount() {
        return 2;
    }

    @Override
    public void registerPartPaths(int index, String[] location) {
        headStrings.put(index, location[0]);
        brokenPartStrings.put(index, location[1]);
        handleStrings.put(index, location[2]);
    }

    @Override
    public String getIconSuffix(int partType) {
        return switch (partType) {
            case 0 -> "_shovel_head";
            case 1 -> "_shovel_head_broken";
            case 2 -> "_shovel_handle";
            default -> "";
        };
    }

    @Override
    public String getEffectSuffix() {
        return "_shovel_effect";
    }

    @Override
    public String getDefaultFolder() {
        return "shovel";
    }
}
