package tconstruct.items.tools;

import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

import tconstruct.library.tools.HarvestTool;
import tconstruct.tools.TinkerTools;

public class Pickaxe extends HarvestTool {

    public Pickaxe() {
        super(1);
        this.setUnlocalizedName("InfiTool.Pickaxe");
    }

    @Override
    protected String getHarvestType() {
        return "pickaxe";
    }

    @Override
    protected Material[] getEffectiveMaterials() {
        return materials;
    }

    static Material[] materials = new Material[] { Material.rock, Material.iron, Material.ice, Material.glass,
            Material.piston, Material.anvil, Material.circuits };

    @Override
    public Item getHeadItem() {
        return TinkerTools.pickaxeHead;
    }

    @Override
    public Item getAccessoryItem() {
        return TinkerTools.binding;
    }

    @Override
    public String getIconSuffix(int partType) {
        return switch (partType) {
            case 0 -> "_pickaxe_head";
            case 1 -> "_pickaxe_head_broken";
            case 2 -> "_pickaxe_handle";
            case 3 -> "_pickaxe_accessory";
            default -> "";
        };
    }

    @Override
    public String getEffectSuffix() {
        return "_pickaxe_effect";
    }

    @Override
    public String getDefaultFolder() {
        return "pickaxe";
    }
}
