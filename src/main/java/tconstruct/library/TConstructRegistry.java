package tconstruct.library;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tconstruct.TConstruct;
import tconstruct.library.crafting.Detailing;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.modifier.ActiveArmorMod;
import tconstruct.library.tools.ArrowMaterial;
import tconstruct.library.tools.BowMaterial;
import tconstruct.library.tools.BowstringMaterial;
import tconstruct.library.tools.CustomMaterial;
import tconstruct.library.tools.FletchingMaterial;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.tools.ToolMaterial;
import tconstruct.tools.TinkerTools;

/**
 * A registry to store any relevant API work
 *
 * @author mDiyo
 */
public class TConstructRegistry {

    public static TConstructRegistry instance = new TConstructRegistry();

    public static Logger logger = LogManager.getLogger("TCon-API");

    /* Creative tabs */
    public static TConstructCreativeTab toolTab;
    public static TConstructCreativeTab partTab;
    public static TConstructCreativeTab materialTab;
    public static TConstructCreativeTab blockTab;
    public static TConstructCreativeTab equipableTab;
    public static TConstructCreativeTab weaponryTab;
    public static TConstructCreativeTab gadgetsTab;

    /* Items */

    /**
     * A directory of crafting items and tools used by the mod.
     *
     * Tools: pickaxe, shovel, hatchet, broadsword, longsword, rapier, dagger, cutlass frypan, battlesign, mattock,
     * chisel lumberaxe, cleaver, scythe, excavator, hammer, battleaxe
     *
     * Patterns: blankPattern, woodPattern, metalPattern, clayPattern
     *
     * Tool crafting parts: toolRod, toolShard, binding, toughBinding, toughRod, heavyPlate pickaxeHead, shovelhead,
     * hatchetHead, swordBlade, wideguard, handGuard, crossbar, knifeBlade, fullGuard, frypanHead, signHead, chiselHead
     * scytheBlade, broadAxeHead, excavatorHead, largeSwordBlade, hammerHead bowstring, fletching, arrowhead
     */
    public static HashMap<String, Item> itemDirectory = new HashMap<>();

    /**
     * Adds an item to the directory
     *
     * @param name      Associates the name with the stack
     * @param itemstack The stack to add to the directory
     */
    public static void addItemToDirectory(String name, Item itemstack) {
        Item add = itemDirectory.get(name);
        if (add != null) logger.warn(name + " is already present in the Item directory");

        itemDirectory.put(name, itemstack);
    }

    /**
     * Retrieves an itemstack from the directory
     *
     * @param name The name of the item to get
     * @return Item associated with the name, or null if not present.
     */
    public static Item getItem(String name) {
        Item ret = itemDirectory.get(name);
        if (ret == null) logger.warn("Could not find " + name + " in the Item directory");

        return ret;
    }

    /**
     * A directory of ItemStacks. Contains mostly crafting items
     *
     * Materials: paperStack, greenSlimeCrystal, blueSlimeCrystal, searedBrick, mossBall, lavaCrystal, necroticBone,
     * silkyCloth, silkyJewel ingotCobalt, ingotArdite, ingotManyullyn, ingotCopper, ingotTin, ingotAluminum,
     * rawAluminum, ingotBronze, ingotAluminumBrass, ingotAlumite, ingotSteel, ingotObsidian nuggetIron, nuggetCopper,
     * nuggetTin, nuggetAluminum, nuggetSilver, nuggetAluminumBrass oreberryIron, oreberryGold, oreberryCopper,
     * oreberryTin, oreberryTin, oreberrySilver, diamondApple, blueSlimeFood, canisterEmpty, miniRedHeart,
     * canisterRedHeart
     *
     * Patterns - These have a suffix of Pattern or Cast. ex: hatchetHeadPattern ingot, toolRod, pickaxeHead,
     * shovelHead, hatchetHead, swordBlade, wideGuard, handGuard, crossbar, binding, frypanHead, signHead, knifeBlade,
     * chiselHead, toughRod, toughBinding, largePlate, broadAxeHead, scytheHead, excavatorHead, largeBlade, hammerHead,
     * fullGuard, bowString, fletching, arrowHead
     */
    static HashMap<String, ItemStack> itemstackDirectory = new HashMap<>();

    /**
     * Adds an itemstack to the directory
     *
     * @param name      Associates the name with the stack
     * @param itemstack The stack to add to the directory
     */
    public static void addItemStackToDirectory(String name, ItemStack itemstack) {
        ItemStack add = itemstackDirectory.get(name);
        if (add != null) logger.warn(name + " is already present in the ItemStack directory");

        itemstackDirectory.put(name, itemstack);
    }

    /**
     * Retrieves an itemstack from the directory
     *
     * @param name The name of the item to get
     * @return Item associated with the name, or null if not present.
     */
    public static ItemStack getItemStack(String name) {
        ItemStack ret = itemstackDirectory.get(name);
        if (ret == null) logger.warn("Could not find " + name + " in the ItemStack directory");

        return ret;
    }

    public static ArrayList<ToolCore> tools = new ArrayList<>(20);

    // Parts

    /**
     * List: Item ID, metadata, material ID ItemStack: Output. Ex: Cactus Binding
     */
    public static HashMap<List, ItemStack> patternPartMapping = new HashMap<>();

    /**
     * Maps an item and a material ID to an output part
     *
     * @param woodPattern ID to check against
     * @param patternMeta Metadata to check against
     * @param materialID  Material that goes with the item
     * @param output      The resulting part
     */
    public static void addPartMapping(Item woodPattern, int patternMeta, int materialID, ItemStack output) {
        patternPartMapping.put(Arrays.asList(woodPattern, patternMeta, materialID), output);
    }

    public static ItemStack getPartMapping(Item item, int metadata, int materialID) {
        ItemStack stack = patternPartMapping.get(Arrays.asList(item, metadata, materialID));
        if (stack != null) return stack.copy();
        return null;
    }

    // Tools

    /**
     * Internal tool mapping, used for adding textures
     *
     * @param tool
     */
    public static void addToolMapping(ToolCore tool) {
        tools.add(tool);
    }

    /**
     * Internal tool mapping, used for adding textures
     *
     * @return List of tools
     */
    public static ArrayList<ToolCore> getToolMapping() {
        return tools;
    }

    /**
     * Registers a tool to its crafting parts. If an output is registered multiple times the parts are added to the
     * recipe's input list Valid part amounts are 2, 3, and 4.
     *
     * @see ToolBuilder
     * @param output The ToolCore to craft
     * @param parts  Pieces to make the tool with
     */
    public static void addToolRecipe(ToolCore output, Item... parts) {
        ToolBuilder tb = ToolBuilder.instance;
        if (parts.length < 2 || parts.length > 4) logger.warn("Wrong amount of items to craft into a tool");

        ToolBuilder.addToolRecipe(output, parts);
    }

    // Materials
    public static HashMap<Integer, ToolMaterial> toolMaterials = new HashMap<>(40);
    public static HashMap<String, ToolMaterial> toolMaterialStrings = new HashMap<>(40);
    public static List<Integer> defaultToolPartMaterials = new LinkedList<>();
    public static List<Integer> defaultShardMaterials = new LinkedList<>();

    public static void addDefaultToolPartMaterial(int materialID) {
        if (!toolMaterials.containsKey(materialID))
            logger.error("[TCon API] Can't add default toolpart: Material ID " + materialID + " is unknown.");
        else defaultToolPartMaterials.add(materialID);
    }

    public static void addDefaultShardMaterial(int materialID) {
        if (!toolMaterials.containsKey(materialID))
            logger.error("[TCon API] Can't add default shard: Material ID " + materialID + " is unknown.");
        else defaultShardMaterials.add(materialID);
    }

    /**
     * Adds a tool material to the registry
     *
     * @param materialID     Unique ID, stored for each part
     * @param materialName   Unique name for data lookup purposes
     * @param harvestLevel   The materials which the tool can harvest. Pickaxe levels - 0: Wood, 1: Stone, 2:
     *                       Redstone/Diamond, 3: Obsidian, 4: Cobalt/Ardite, 5: Manyullyn
     * @param durability     Base durability of the tool, affects tool heads.
     * @param miningspeed    Base mining speed, divided by 100 in use
     * @param attack         Base attack
     * @param handleModifier Durability multiplier on the tool
     * @param reinforced     Reinforced level
     * @param stonebound     Amount of Stonebound to put on the tool. Negative numbers are Spiny.
     */
    public static void addToolMaterial(int materialID, String materialName, int harvestLevel, int durability,
            int miningspeed, int attack, float handleModifier, int reinforced, float stonebound, String style,
            int primaryColor) {
        ToolMaterial mat = toolMaterials.get(materialID);
        if (mat == null) {
            mat = new ToolMaterial(
                    materialName,
                    harvestLevel,
                    durability,
                    miningspeed,
                    attack,
                    handleModifier,
                    reinforced,
                    stonebound,
                    style,
                    primaryColor);
            toolMaterials.put(materialID, mat);
            toolMaterialStrings.put(materialName, mat);
        } else throw new IllegalArgumentException(
                "[TCon API] Material ID " + materialID + " is already occupied by " + mat.materialName);
    }

    /**
     * Adds a tool material to the registry
     *
     * @param materialID       Unique ID, stored for each part
     * @param materialName     Unique name for data lookup purposes
     * @param localizationName The string used to localize the material name
     * @param harvestLevel     The materials which the tool can harvest. Pickaxe levels - 0: Wood, 1: Stone, 2:
     *                         Redstone/Diamond, 3: Obsidian, 4: Cobalt/Ardite, 5: Manyullyn
     * @param durability       Base durability of the tool, affects tool heads.
     * @param miningspeed      Base mining speed, divided by 100 in use
     * @param attack           Base attack
     * @param handleModifier   Durability multiplier on the tool
     * @param reinforced       Reinforced level
     * @param stonebound       Amount of Stonebound to put on the tool. Negative numbers are Spiny.
     */
    public static void addToolMaterial(int materialID, String materialName, String localizationName, int harvestLevel,
            int durability, int miningspeed, int attack, float handleModifier, int reinforced, float stonebound,
            String style, int primaryColor) {
        ToolMaterial mat = toolMaterials.get(materialID);
        if (mat == null) {
            mat = new ToolMaterial(
                    materialName,
                    localizationName,
                    harvestLevel,
                    durability,
                    miningspeed,
                    attack,
                    handleModifier,
                    reinforced,
                    stonebound,
                    style,
                    primaryColor);
            toolMaterials.put(materialID, mat);
            toolMaterialStrings.put(materialName, mat);
        } else throw new IllegalArgumentException(
                "[TCon API] Material ID " + materialID + " is already occupied by " + mat.materialName);
    }

    @Deprecated
    public static void addToolMaterial(int materialID, String materialName, int harvestLevel, int durability,
            int miningspeed, int attack, float handleModifier, int reinforced, float stonebound, String style) {
        logger.warn(
                "[TCon API] Using deprecated addToolMaterial with no primary color. A fallback of white will be used.");
        addToolMaterial(
                materialID,
                materialName,
                harvestLevel,
                durability,
                miningspeed,
                attack,
                handleModifier,
                reinforced,
                stonebound,
                style,
                0xFFFFFF);
    }

    @Deprecated
    public static void addToolMaterial(int materialID, String materialName, int harvestLevel, int durability,
            int miningspeed, int attack, float handleModifier, int reinforced, float stonebound, String style,
            String ability) {
        logger.warn(
                "[TCon API] Using deprecated addToolMaterial with ability name. ability will be ignored, use languages files for that.");
        addToolMaterial(
                materialID,
                materialName,
                harvestLevel,
                durability,
                miningspeed,
                attack,
                handleModifier,
                reinforced,
                stonebound,
                style);
    }

    @Deprecated
    public static void addToolMaterial(int materialID, String materialName, String displayName, int harvestLevel,
            int durability, int miningspeed, int attack, float handleModifier, int reinforced, float stonebound,
            String style, String ability) {
        logger.warn(
                "[TCon API] Using deprecated addToolMaterial with display and ability name. displayName and ability will be ignored, use languages files for that.");
        addToolMaterial(
                materialID,
                materialName,
                harvestLevel,
                durability,
                miningspeed,
                attack,
                handleModifier,
                reinforced,
                stonebound,
                style);
    }

    /**
     * Adds a tool material to the registry
     *
     * @param materialID Unique ID, stored for each part
     * @param material   Complete tool material to add. Uses the name in the material for lookup purposes.
     */
    public static void addtoolMaterial(int materialID, ToolMaterial material) {
        ToolMaterial mat = toolMaterials.get(materialID);
        if (mat == null) {
            toolMaterials.put(materialID, material);
            toolMaterialStrings.put(material.name(), material);
        } else throw new IllegalArgumentException(
                "[TCon API] Material ID " + materialID + " is already occupied by " + mat.materialName);
    }

    /**
     * Looks up a tool material by ID
     *
     * @param key The ID to look up
     * @return Tool Material
     */
    public static ToolMaterial getMaterial(int key) {
        if (toolMaterials.containsKey(key)) {
            return (toolMaterials.get(key));
        }

        // This is probably an old tool whose material has been removed from the game.
        // Fall back to wood.
        return toolMaterials.get(TinkerTools.MaterialID.Wood);
    }

    /**
     * Looks up a tool material by name
     *
     * @param key the name to look up
     * @return Tool Material
     */
    public static ToolMaterial getMaterial(String key) {
        return (toolMaterialStrings.get(key));
    }

    // Bow materials
    public static HashMap<Integer, BowMaterial> bowMaterials = new HashMap<>(40);

    @Deprecated
    public static void addBowMaterial(int materialID, int durability, int drawSpeed, float speedMax) {
        addBowMaterial(materialID, drawSpeed, speedMax);
    }

    public static void addBowMaterial(int materialID, int drawSpeed, float speedMax) {
        BowMaterial mat = bowMaterials.get(materialID);
        if (mat == null) {
            mat = new BowMaterial(drawSpeed, speedMax);
            bowMaterials.put(materialID, mat);
        } else throw new IllegalArgumentException("[TCon API] Bow Material ID " + materialID + " is already occupied");
    }

    public static boolean validBowMaterial(int materialID) {
        return bowMaterials.containsKey(materialID);
    }

    public static BowMaterial getBowMaterial(int materialID) {
        return bowMaterials.get(materialID);
    }

    public static HashMap<Integer, ArrowMaterial> arrowMaterials = new HashMap<>(40);

    @Deprecated
    public static void addArrowMaterial(int materialID, float mass, float breakChance, float accuracy) {
        addArrowMaterial(materialID, mass, breakChance);
    }

    public static void addArrowMaterial(int materialID, float mass, float breakChance) {
        ArrowMaterial mat = arrowMaterials.get(materialID);
        if (mat == null) {
            mat = new ArrowMaterial(mass, breakChance);
            arrowMaterials.put(materialID, mat);
        } else
            throw new IllegalArgumentException("[TCon API] Arrow Material ID " + materialID + " is already occupied");
    }

    public static boolean validArrowMaterial(int materialID) {
        return arrowMaterials.containsKey(materialID);
    }

    public static ArrowMaterial getArrowMaterial(int materialID) {
        return arrowMaterials.get(materialID);
    }

    // Custom materials - bowstrings, fletching, etc
    public static ArrayList<CustomMaterial> customMaterials = new ArrayList<>();

    public static void addCustomMaterial(CustomMaterial mat) {
        if (mat != null) customMaterials.add(mat);
    }

    public static void addBowstringMaterial(int materialID, int value, ItemStack input, ItemStack craftingMaterial,
            float durability, float drawSpeed, float flightSpeed, int color) {
        BowstringMaterial mat = new BowstringMaterial(
                materialID,
                value,
                input,
                craftingMaterial,
                durability,
                drawSpeed,
                flightSpeed,
                color);
        customMaterials.add(mat);
    }

    public static void addFletchingMaterial(int materialID, int value, ItemStack input, ItemStack craftingMaterial,
            float accuracy, float breakChance, float durabilityModifier, int color) {
        FletchingMaterial mat = new FletchingMaterial(
                materialID,
                value,
                input,
                craftingMaterial,
                accuracy,
                breakChance,
                durabilityModifier,
                color);
        customMaterials.add(mat);
    }

    @Deprecated
    public static void addBowstringMaterial(int materialID, int value, ItemStack input, ItemStack craftingMaterial,
            float durability, float drawSpeed, float flightSpeed) {
        BowstringMaterial mat = new BowstringMaterial(
                materialID,
                value,
                input,
                craftingMaterial,
                durability,
                drawSpeed,
                flightSpeed);
        customMaterials.add(mat);
    }

    @Deprecated
    public static void addFletchingMaterial(int materialID, int value, ItemStack input, ItemStack craftingMaterial,
            float accuracy, float breakChance, float mass) {
        FletchingMaterial mat = new FletchingMaterial(
                materialID,
                value,
                input,
                craftingMaterial,
                accuracy,
                breakChance,
                mass);
        customMaterials.add(mat);
    }

    public static CustomMaterial getCustomMaterial(int materialID, Class<? extends CustomMaterial> clazz) {
        for (CustomMaterial mat : customMaterials) {
            if (mat.getClass().equals(clazz) && mat.materialID == materialID) return mat;
        }
        return null;
    }

    public static CustomMaterial getCustomMaterial(ItemStack input, Class<? extends CustomMaterial> clazz) {
        for (CustomMaterial mat : customMaterials) {
            if (mat.getClass().equals(clazz) && mat.matches(input)) return mat;
        }
        return null;
    }

    public static LiquidCasting getTableCasting() {
        return TConstruct.getTableCasting();
    }

    public static LiquidCasting getBasinCasting() {
        return TConstruct.getBasinCasting();
    }

    public static Detailing getChiselDetailing() {
        return TConstruct.getChiselDetailing();
    }

    public static ArrayList<ActiveToolMod> activeModifiers = new ArrayList<>();
    public static LinkedList<ActiveArmorMod> activeArmorModifiers = new LinkedList<>();

    public static void registerActiveToolMod(ActiveToolMod mod) {
        activeModifiers.add(mod);
    }

    public static void registerActiveArmorMod(ActiveArmorMod mod) {
        activeArmorModifiers.add(mod);
    }

    /**
     * Default Material Index 0: Wood 1: Stone 2: Iron 3: Flint 4: Cactus 5: Bone 6: Obsidian 7: Netherrack 8: Green
     * Slime 9: Paper 10: Cobalt 11: Ardite 12: Manyullyn 13: Copper 14: Bronze 15: Alumite 16: Steel 17: Blue Slime
     */
}
