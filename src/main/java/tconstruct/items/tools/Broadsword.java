package tconstruct.items.tools;

import net.minecraft.item.Item;

import tconstruct.library.tools.Weapon;
import tconstruct.tools.TinkerTools;

public class Broadsword extends Weapon {

    public Broadsword() {
        super(4);
        this.setUnlocalizedName("InfiTool.Broadsword");
    }

    @Override
    public Item getHeadItem() {
        return TinkerTools.swordBlade;
    }

    @Override
    public Item getAccessoryItem() {
        return TinkerTools.wideGuard;
    }

    @Override
    public float getDurabilityModifier() {
        return 1.2f;
    }

    @Override
    public String getIconSuffix(int partType) {
        return switch (partType) {
            case 0 -> "_sword_blade";
            case 1 -> "_sword_blade_broken";
            case 2 -> "_sword_handle";
            case 3 -> "_sword_accessory";
            default -> "";
        };
    }

    @Override
    public String getEffectSuffix() {
        return "_sword_effect";
    }

    @Override
    public String getDefaultFolder() {
        return "broadsword";
    }
}
