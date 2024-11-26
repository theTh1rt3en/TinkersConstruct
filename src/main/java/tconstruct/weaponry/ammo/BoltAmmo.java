package tconstruct.weaponry.ammo;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.tools.CustomMaterial;
import tconstruct.library.tools.DualMaterialToolPart;
import tconstruct.library.tools.FletchingMaterial;
import tconstruct.library.tools.FletchlingLeafMaterial;
import tconstruct.library.weaponry.AmmoItem;
import tconstruct.tools.TinkerTools;
import tconstruct.weaponry.TinkerWeaponry;

public class BoltAmmo extends AmmoItem {

    public BoltAmmo() {
        super(0, "Bolts");
    }

    @Override
    public String getIconSuffix(int partType) {
        return switch (partType) {
            case 0 -> "_bolt_head";
            case 1 -> ""; // Doesn't break
            case 2 -> "_bolt_shaft";
            case 3 -> "_bolt_fletching";
            default -> "";
        };
    }

    @Override
    public String getEffectSuffix() {
        return "_bolt_effect";
    }

    @Override
    public String getDefaultFolder() {
        return "bolt";
    }

    @Override
    public void registerPartPaths(int index, String[] location) {
        headStrings.put(index, location[0]);
        handleStrings.put(index, location[2]);
    }

    @Override
    public void registerAlternatePartPaths(int index, String[] location) {
        accessoryStrings.put(index, location[3]);
    }

    @Override
    public Item getHeadItem() {
        return TinkerWeaponry.partBolt;
    }

    @Override
    public Item getHandleItem() {
        return TinkerWeaponry.partBolt;
    }

    @Override
    public Item getAccessoryItem() {
        return TinkerWeaponry.fletching;
    }

    @Override
    public String[] getTraits() {
        return new String[] { "ammo", "projectile", "weapon" };
    }

    @Override
    public void buildTool(int id, String name, List list) {
        if (TConstructRegistry.getArrowMaterial(id) == null) return;

        // dual material head: we use wooden shafts
        ItemStack headStack = DualMaterialToolPart.createDualMaterial(getHeadItem(), id, TinkerTools.MaterialID.Iron); // material
                                                                                                                       // shaft
        ItemStack handleStack = new ItemStack(getAccessoryItem(), 1, 0); // feather Fletchling

        ItemStack tool = ToolBuilder.instance.buildTool(headStack, handleStack, null, null, "");
        if (tool != null) {
            tool.getTagCompound().getCompoundTag("InfiTool").setBoolean("Built", true);
            list.add(tool);
        }
    }

    @Override
    protected int getDefaultColor(int renderPass, int materialID) {
        if (renderPass != 2) return super.getDefaultColor(renderPass, materialID);

        CustomMaterial mat = TConstructRegistry.getCustomMaterial(materialID, FletchingMaterial.class);
        if (mat == null) mat = TConstructRegistry.getCustomMaterial(materialID, FletchlingLeafMaterial.class);
        if (mat == null) return 0xffffff;

        return mat.color;
    }

    // fix tooltip custom materials
    @Override
    public String getAbilityNameForType(int type, int part) {
        if (part >= 2) // only head and handle have ability
            return "";
        return super.getAbilityNameForType(type, part);
    }
}
