package tconstruct.weaponry.ammo;

import static tconstruct.util.Reference.MOD_ID;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import lombok.extern.log4j.Log4j2;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.tools.CustomMaterial;
import tconstruct.library.tools.FletchingMaterial;
import tconstruct.library.tools.FletchlingLeafMaterial;
import tconstruct.library.weaponry.AmmoItem;
import tconstruct.tools.TinkerTools;
import tconstruct.weaponry.TinkerWeaponry;

@Log4j2(topic = MOD_ID)
public class ArrowAmmo extends AmmoItem {

    public static ItemStack vanillaArrow;

    public ArrowAmmo() {
        super(0, "Arrows");
    }

    @Override
    public String getIconSuffix(int partType) {
        return switch (partType) {
            case 0 -> "_arrow_head";
            case 1 -> ""; // Doesn't break
            case 2 -> "_arrow_shaft";
            case 3 -> "_arrow_fletching";
            default -> "";
        };
    }

    @Override
    public String getEffectSuffix() {
        return "_arrow_effect";
    }

    @Override
    public String getDefaultFolder() {
        return "arrow";
    }

    @Override
    public void registerPartPaths(int index, String[] location) {
        headStrings.put(index, location[0]);
    }

    @Override
    public void registerAlternatePartPaths(int index, String[] location) {
        handleStrings.put(index, location[2]);
        accessoryStrings.put(index, location[3]);
    }

    @Override
    public Item getHeadItem() {
        return TinkerWeaponry.arrowhead;
    }

    @Override
    public Item getHandleItem() {
        return TinkerWeaponry.partArrowShaft;
    }

    @Override
    public Item getAccessoryItem() {
        return TinkerWeaponry.fletching;
    }

    // handle is custom material
    @Override
    public int durabilityTypeHandle() {
        return 0;
    }

    @Override
    public String[] getTraits() {
        return new String[] { "ammo", "projectile", "weapon" };
    }

    @Override
    public void buildTool(int id, String name, List list) {
        if (TConstructRegistry.getArrowMaterial(id) == null) return;

        ItemStack handleStack = new ItemStack(getHandleItem(), 1, 0); // wooden shaft
        ItemStack accessoryStack = new ItemStack(getAccessoryItem(), 1, 0); // feather fletchling

        ItemStack tool = ToolBuilder.instance
                .buildTool(new ItemStack(getHeadItem(), 1, id), handleStack, accessoryStack, null, "");
        if (tool != null) {
            tool.getTagCompound().getCompoundTag("InfiTool").setBoolean("Built", true);
            list.add(tool);
        }
    }

    @Override
    public void getSubItems(Item id, CreativeTabs tab, List list) {
        super.getSubItems(id, tab, list);

        // vanilla arrow
        ItemStack headStack = new ItemStack(getHeadItem(), 1, TinkerTools.MaterialID.Flint); // flint arrow head
        ItemStack handleStack = new ItemStack(getHandleItem(), 1, 0); // wooden shaft
        ItemStack accessoryStack = new ItemStack(getAccessoryItem(), 1, 0); // feather fletchling

        ItemStack tool = ToolBuilder.instance.buildTool(headStack, handleStack, accessoryStack, null, "");
        if (tool != null) {
            tool.getTagCompound().getCompoundTag("InfiTool").setBoolean("Built", true);
            vanillaArrow = tool;
        } else log.error("Couldn't build vanilla equivalent of Tinker Arrow");
    }

    @Override
    protected int getDefaultColor(int renderPass, int materialID) {
        if (renderPass != 2) return super.getDefaultColor(renderPass, materialID);

        CustomMaterial mat = TConstructRegistry.getCustomMaterial(materialID, FletchingMaterial.class);
        if (mat == null) TConstructRegistry.getCustomMaterial(materialID, FletchlingLeafMaterial.class);
        if (mat == null) return 0xffffff;

        return mat.color;
    }

    // fix tooltip custom materials
    @Override
    public String getAbilityNameForType(int type, int part) {
        // blaze shaft?
        if (part == 1 && type == 3) return "\u00a76" + StatCollector.translateToLocal("modifier.tool.blaze");
        if (part >= 1) // only head has ability otherwise
            return "";
        return super.getAbilityNameForType(type, part);
    }
}
