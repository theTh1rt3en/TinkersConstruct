package tconstruct.world;

import static tconstruct.util.Reference.MOD_ID;

import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.potion.Potion;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import mantle.utils.RecipeRemover;
import tconstruct.TConstruct;
import tconstruct.armor.TinkerArmor;
import tconstruct.blocks.SlabBase;
import tconstruct.blocks.slime.SlimeFluid;
import tconstruct.blocks.slime.SlimeGel;
import tconstruct.blocks.slime.SlimeGrass;
import tconstruct.blocks.slime.SlimeLeaves;
import tconstruct.blocks.slime.SlimeSapling;
import tconstruct.blocks.slime.SlimeTallGrass;
import tconstruct.blocks.traps.BarricadeBlock;
import tconstruct.blocks.traps.Punji;
import tconstruct.client.StepSoundSlime;
import tconstruct.common.itemblocks.MetadataItemBlock;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.FluidType;
import tconstruct.smeltery.blocks.MetalOre;
import tconstruct.smeltery.itemblocks.MetalItemBlock;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.blocks.MultiBrick;
import tconstruct.tools.blocks.MultiBrickFancy;
import tconstruct.tools.blocks.MultiBrickMetal;
import tconstruct.tools.entity.ArrowEntity;
import tconstruct.tools.entity.DaggerEntity;
import tconstruct.tools.entity.FancyEntityItem;
import tconstruct.tools.entity.LaunchedPotion;
import tconstruct.tools.itemblocks.MultiBrickFancyItem;
import tconstruct.tools.itemblocks.MultiBrickItem;
import tconstruct.tools.itemblocks.MultiBrickMetalItem;
import tconstruct.util.config.PHConstruct;
import tconstruct.world.blocks.ConveyorBase;
import tconstruct.world.blocks.GravelOre;
import tconstruct.world.blocks.MeatBlock;
import tconstruct.world.blocks.OreberryBush;
import tconstruct.world.blocks.OreberryBushEssence;
import tconstruct.world.blocks.SlimeExplosive;
import tconstruct.world.blocks.SlimePad;
import tconstruct.world.blocks.StoneLadder;
import tconstruct.world.blocks.StoneTorch;
import tconstruct.world.blocks.TMetalBlock;
import tconstruct.world.blocks.WoodRail;
import tconstruct.world.entity.BlueSlime;
import tconstruct.world.entity.Crystal;
import tconstruct.world.entity.KingBlueSlime;
import tconstruct.world.gen.TBaseWorldGenerator;
import tconstruct.world.gen.TerrainGenEventHandler;
import tconstruct.world.itemblocks.BarricadeItem;
import tconstruct.world.itemblocks.GravelOreItem;
import tconstruct.world.itemblocks.HamboneItemBlock;
import tconstruct.world.itemblocks.MetalOreItemBlock;
import tconstruct.world.itemblocks.OreberryBushItem;
import tconstruct.world.itemblocks.OreberryBushSecondItem;
import tconstruct.world.itemblocks.SlimeGelItemBlock;
import tconstruct.world.itemblocks.SlimeGrassItemBlock;
import tconstruct.world.itemblocks.SlimeLeavesItemBlock;
import tconstruct.world.itemblocks.SlimeSaplingItemBlock;
import tconstruct.world.itemblocks.SlimeTallGrassItem;
import tconstruct.world.itemblocks.WoolSlab1Item;
import tconstruct.world.itemblocks.WoolSlab2Item;
import tconstruct.world.items.GoldenHead;
import tconstruct.world.items.OreBerries;
import tconstruct.world.items.StrangeFood;

@ObjectHolder(MOD_ID)
@Pulse(id = "Tinkers' World", description = "Ores, slime islands, essence berries, and the like.", forced = true)
public class TinkerWorld {

    private static final String CLIENT_SIDE_PROXY = "tconstruct.world.TinkerWorldProxyClient";
    private static final String SERVER_SIDE_PROXY = "tconstruct.world.TinkerWorldProxyCommon";

    @Instance("TinkerWorld")
    public static TinkerWorld instance;

    @SidedProxy(clientSide = CLIENT_SIDE_PROXY, serverSide = SERVER_SIDE_PROXY)
    public static TinkerWorldProxyCommon proxy;

    public static Item strangeFood;
    // Decoration
    public static Block stoneTorch;
    public static Block stoneLadder;
    public static Block meatBlock;
    public static Block woolSlab1;
    public static Block woolSlab2;
    public static Block barricadeOak;
    public static Block barricadeSpruce;
    public static Block barricadeBirch;
    public static Block barricadeJungle;
    public static Block slimeExplosive;
    public static Fluid blueSlimeFluid;
    // Slime
    public static SoundType slimeStep;
    public static Block slimePool;
    public static Block slimeGel;
    public static Block slimeGrass;
    public static Block slimeTallGrass;
    public static SlimeLeaves slimeLeaves;
    public static SlimeSapling slimeSapling;
    public static Block slimeChannel;
    public static Block slimePad;
    public static Block bloodChannel;
    // Ores
    public static Block oreSlag;
    public static Block oreGravel;
    public static OreberryBush oreBerry;
    public static OreberryBush oreBerrySecond;
    public static Item oreBerries;
    // Rail-related
    public static Block woodenRail;
    // Chest hooks
    public static ChestGenHooks tinkerHouseChest;
    public static ChestGenHooks tinkerHousePatterns;
    public static Block punji;
    public static Block metalBlock;
    // Morbid
    public static Item goldHead;

    @Handler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new TinkerWorldEvents());

        // Blocks
        TinkerWorld.meatBlock = new MeatBlock().setBlockName("tconstruct.meatblock");
        TinkerWorld.woolSlab1 = new SlabBase(Material.cloth, Blocks.wool, 0, 8).setBlockName("cloth");
        TinkerWorld.woolSlab1.setStepSound(Block.soundTypeCloth).setCreativeTab(CreativeTabs.tabDecorations);
        TinkerWorld.woolSlab2 = new SlabBase(Material.cloth, Blocks.wool, 8, 8).setBlockName("cloth");
        TinkerWorld.woolSlab2.setStepSound(Block.soundTypeCloth).setCreativeTab(CreativeTabs.tabDecorations);
        // Traps
        TinkerWorld.punji = new Punji().setBlockName("trap.punji");
        TinkerWorld.barricadeOak = new BarricadeBlock(Blocks.log, 0).setBlockName("trap.barricade.oak");
        TinkerWorld.barricadeSpruce = new BarricadeBlock(Blocks.log, 1).setBlockName("trap.barricade.spruce");
        TinkerWorld.barricadeBirch = new BarricadeBlock(Blocks.log, 2).setBlockName("trap.barricade.birch");
        TinkerWorld.barricadeJungle = new BarricadeBlock(Blocks.log, 3).setBlockName("trap.barricade.jungle");
        TinkerWorld.slimeExplosive = new SlimeExplosive().setHardness(0.0F).setStepSound(Block.soundTypeGrass)
                .setBlockName("explosive.slime");

        // Slime
        TinkerWorld.slimeStep = new StepSoundSlime("mob.slime", 1.0f, 1.0f);

        TinkerWorld.blueSlimeFluid = new Fluid("slime.blue");
        if (!FluidRegistry.registerFluid(TinkerWorld.blueSlimeFluid))
            TinkerWorld.blueSlimeFluid = FluidRegistry.getFluid("slime.blue");
        TinkerWorld.slimePool = new SlimeFluid(TinkerWorld.blueSlimeFluid, Material.water)
                .setCreativeTab(TConstructRegistry.blockTab).setStepSound(TinkerWorld.slimeStep)
                .setBlockName("liquid.slime");
        GameRegistry.registerBlock(TinkerWorld.slimePool, "liquid.slime");
        TinkerWorld.blueSlimeFluid.setBlock(TinkerWorld.slimePool);

        // Slime Islands
        TinkerWorld.slimeGel = new SlimeGel().setStepSound(TinkerWorld.slimeStep).setLightOpacity(0)
                .setBlockName("slime.gel");
        TinkerWorld.slimeGel.setHarvestLevel("axe", 0, 1);
        TinkerWorld.slimeGrass = new SlimeGrass().setStepSound(Block.soundTypeGrass).setLightOpacity(0)
                .setBlockName("slime.grass");
        TinkerWorld.slimeTallGrass = new SlimeTallGrass().setStepSound(Block.soundTypeGrass)
                .setBlockName("slime.grass.tall");
        TinkerWorld.slimeLeaves = (SlimeLeaves) new SlimeLeaves().setStepSound(TinkerWorld.slimeStep)
                .setBlockName("slime.leaves");
        TinkerWorld.slimeSapling = (SlimeSapling) new SlimeSapling().setStepSound(TinkerWorld.slimeStep)
                .setBlockName("slime.sapling");
        TinkerWorld.slimeChannel = new ConveyorBase(Material.water, "greencurrent").setHardness(0.3f)
                .setStepSound(TinkerWorld.slimeStep).setBlockName("slime.channel");
        TinkerWorld.bloodChannel = new ConveyorBase(Material.water, "liquid_cow").setHardness(0.3f)
                .setStepSound(TinkerWorld.slimeStep).setBlockName("blood.channel");
        TinkerWorld.slimePad = new SlimePad(Material.cloth).setStepSound(TinkerWorld.slimeStep).setHardness(0.3f)
                .setBlockName("slime.pad");

        // Decoration
        TinkerWorld.stoneTorch = new StoneTorch().setBlockName("decoration.stonetorch");
        TinkerWorld.stoneLadder = new StoneLadder().setBlockName("decoration.stoneladder");
        TinkerTools.multiBrick = new MultiBrick().setBlockName("Decoration.Brick");
        TinkerTools.multiBrickFancy = new MultiBrickFancy().setBlockName("Decoration.BrickFancy");
        TinkerTools.multiBrickMetal = new MultiBrickMetal().setBlockName("Decoration.BrickMetal");
        // Iguana Tweaks compat for obsidian
        if (Loader.isModLoaded("IguanaTweaksTConstruct")) {
            TinkerTools.multiBrick.setHarvestLevel("pickaxe", 5, 0);
            TinkerTools.multiBrickFancy.setHarvestLevel("pickaxe", 5, 0);
        }

        // Ores
        String[] berryOres = new String[] { "berry_iron", "berry_gold", "berry_copper", "berry_tin", "berry_iron_ripe",
                "berry_gold_ripe", "berry_copper_ripe", "berry_tin_ripe" };
        TinkerWorld.oreBerry = (OreberryBush) new OreberryBush(
                berryOres,
                0,
                4,
                new String[] { "oreIron", "oreGold", "oreCopper", "oreTin" }).setBlockName("ore.berries.one");
        String[] berryOresTwo = new String[] { "berry_aluminum", "berry_essence", "", "", "berry_aluminum_ripe",
                "berry_essence_ripe", "", "" };
        TinkerWorld.oreBerrySecond = (OreberryBush) new OreberryBushEssence(
                berryOresTwo,
                4,
                2,
                new String[] { "oreAluminum", "oreSilver" }).setBlockName("ore.berries.two");

        String[] oreTypes = new String[] { "nether_slag", "nether_cobalt", "nether_ardite", "ore_copper", "ore_tin",
                "ore_aluminum", "ore_slag" };
        TinkerWorld.oreSlag = new MetalOre(Material.rock, 10.0F, oreTypes).setBlockName("tconstruct.stoneore");
        TinkerWorld.oreSlag.setHarvestLevel("pickaxe", 4, 1);
        TinkerWorld.oreSlag.setHarvestLevel("pickaxe", 4, 2);
        TinkerWorld.oreSlag.setHarvestLevel("pickaxe", 1, 3);
        TinkerWorld.oreSlag.setHarvestLevel("pickaxe", 1, 4);
        TinkerWorld.oreSlag.setHarvestLevel("pickaxe", 1, 5);

        TinkerWorld.oreGravel = new GravelOre().setBlockName("GravelOre").setBlockName("tconstruct.gravelore");
        TinkerWorld.oreGravel.setHarvestLevel("shovel", 1, 0);
        TinkerWorld.oreGravel.setHarvestLevel("shovel", 2, 1);
        TinkerWorld.oreGravel.setHarvestLevel("shovel", 1, 2);
        TinkerWorld.oreGravel.setHarvestLevel("shovel", 1, 3);
        TinkerWorld.oreGravel.setHarvestLevel("shovel", 1, 4);
        TinkerWorld.oreGravel.setHarvestLevel("shovel", 4, 5);
        // Rail
        if (!Loader.isModLoaded("dreamcraft")) {
            TinkerWorld.woodenRail = new WoodRail().setStepSound(Block.soundTypeWood)
                    .setCreativeTab(TConstructRegistry.blockTab).setBlockName("rail.wood");
        }

        GameRegistry.registerBlock(TinkerWorld.meatBlock, HamboneItemBlock.class, "MeatBlock");
        OreDictionary.registerOre("hambone", new ItemStack(TinkerWorld.meatBlock));
        GameRegistry.registerBlock(TinkerWorld.woolSlab1, WoolSlab1Item.class, "WoolSlab1");
        GameRegistry.registerBlock(TinkerWorld.woolSlab2, WoolSlab2Item.class, "WoolSlab2");

        // Traps
        GameRegistry.registerBlock(TinkerWorld.punji, "trap.punji");
        GameRegistry.registerBlock(TinkerWorld.barricadeOak, BarricadeItem.class, "trap.barricade.oak");
        GameRegistry.registerBlock(TinkerWorld.barricadeSpruce, BarricadeItem.class, "trap.barricade.spruce");
        GameRegistry.registerBlock(TinkerWorld.barricadeBirch, BarricadeItem.class, "trap.barricade.birch");
        GameRegistry.registerBlock(TinkerWorld.barricadeJungle, BarricadeItem.class, "trap.barricade.jungle");
        GameRegistry.registerBlock(TinkerWorld.slimeExplosive, MetadataItemBlock.class, "explosive.slime");

        // fluids

        // Slime Islands
        GameRegistry.registerBlock(TinkerWorld.slimeGel, SlimeGelItemBlock.class, "slime.gel");
        GameRegistry.registerBlock(TinkerWorld.slimeGrass, SlimeGrassItemBlock.class, "slime.grass");
        GameRegistry.registerBlock(TinkerWorld.slimeTallGrass, SlimeTallGrassItem.class, "slime.grass.tall");
        GameRegistry.registerBlock(TinkerWorld.slimeLeaves, SlimeLeavesItemBlock.class, "slime.leaves");
        GameRegistry.registerBlock(TinkerWorld.slimeSapling, SlimeSaplingItemBlock.class, "slime.sapling");
        GameRegistry.registerBlock(TinkerWorld.slimeChannel, "slime.channel");
        GameRegistry.registerBlock(TinkerWorld.bloodChannel, "blood.channel");
        GameRegistry.registerBlock(TinkerWorld.slimePad, "slime.pad");
        // TODO fix this
        /*
         * TConstructRegistry.drawbridgeState[TRepo.slimePad] = 1;
         * TConstructRegistry.drawbridgeState[TRepo.bloodChannel] = 1;
         */

        // Decoration
        GameRegistry.registerBlock(TinkerWorld.stoneTorch, "decoration.stonetorch");
        GameRegistry.registerBlock(TinkerWorld.stoneLadder, "decoration.stoneladder");
        GameRegistry.registerBlock(TinkerTools.multiBrick, MultiBrickItem.class, "decoration.multibrick");
        GameRegistry
                .registerBlock(TinkerTools.multiBrickFancy, MultiBrickFancyItem.class, "decoration.multibrickfancy");
        GameRegistry
                .registerBlock(TinkerTools.multiBrickMetal, MultiBrickMetalItem.class, "decoration.multibrickmetal");

        // Ores
        GameRegistry.registerBlock(TinkerWorld.oreBerry, OreberryBushItem.class, "ore.berries.one");
        GameRegistry.registerBlock(TinkerWorld.oreBerrySecond, OreberryBushSecondItem.class, "ore.berries.two");
        GameRegistry.registerBlock(TinkerWorld.oreSlag, MetalOreItemBlock.class, "SearedBrick");
        GameRegistry.registerBlock(TinkerWorld.oreGravel, GravelOreItem.class, "GravelOre");

        // Rail
        if (TinkerWorld.woodenRail != null) {
            GameRegistry.registerBlock(TinkerWorld.woodenRail, "rail.wood");
        }

        // Items
        goldHead = new GoldenHead(4, 1.2F, false).setAlwaysEdible().setPotionEffect(Potion.regeneration.id, 10, 0, 1.0F)
                .setUnlocalizedName("goldenhead");
        GameRegistry.registerItem(goldHead, "goldHead");

        TinkerWorld.strangeFood = new StrangeFood().setUnlocalizedName("tconstruct.strangefood");
        TinkerWorld.oreBerries = new OreBerries().setUnlocalizedName("oreberry");
        GameRegistry.registerItem(TinkerWorld.strangeFood, "strangeFood");
        GameRegistry.registerItem(TinkerWorld.oreBerries, "oreBerries");
        String[] oreberries = { "Iron", "Gold", "Copper", "Tin", "Aluminum", "Essence" };

        for (int i = 0; i < oreberries.length; i++) {
            TConstructRegistry
                    .addItemStackToDirectory("oreberry" + oreberries[i], new ItemStack(TinkerWorld.oreBerries, 1, i));
        }
        TConstructRegistry.addItemStackToDirectory("blueSlimeFood", new ItemStack(TinkerWorld.strangeFood, 1, 0));

        // Vanilla stack sizes
        Items.wooden_door.setMaxStackSize(16);
        Items.iron_door.setMaxStackSize(16);
        Items.boat.setMaxStackSize(16);
        Items.minecart.setMaxStackSize(3);
        Items.cake.setMaxStackSize(16);
        // Block.torchWood.setTickRandomly(false);

        TinkerWorld.metalBlock = new TMetalBlock(Material.iron, 10.0F).setBlockName("tconstruct.metalblock");
        TinkerWorld.metalBlock.stepSound = Block.soundTypeMetal;
        GameRegistry.registerBlock(TinkerWorld.metalBlock, MetalItemBlock.class, "MetalBlock");
        FluidType.registerFluidType("Slime", TinkerWorld.slimeGel, 0, 250, TinkerWorld.blueSlimeFluid, false);

        oreRegistry();
    }

    @Handler
    public void init(FMLInitializationEvent event) {
        if (!PHConstruct.disableAllRecipes) {
            craftingTableRecipes();
            addRecipesForFurnace();
        }
        addLoot();
        createEntities();
        proxy.initialize();

        GameRegistry.registerWorldGenerator(new TBaseWorldGenerator(), 0);
        MinecraftForge.TERRAIN_GEN_BUS.register(new TerrainGenEventHandler());
    }

    @Handler
    public void postInit(FMLPostInitializationEvent evt) {}

    public void createEntities() {
        EntityRegistry.registerModEntity(FancyEntityItem.class, "Fancy Item", 0, TConstruct.instance, 32, 5, true);
        EntityRegistry.registerModEntity(DaggerEntity.class, "Dagger", 1, TConstruct.instance, 32, 5, true);
        EntityRegistry.registerModEntity(Crystal.class, "Crystal", 2, TConstruct.instance, 32, 3, true);
        EntityRegistry.registerModEntity(LaunchedPotion.class, "Launched Potion", 3, TConstruct.instance, 32, 3, true);
        EntityRegistry.registerModEntity(ArrowEntity.class, "Arrow", 4, TConstruct.instance, 32, 5, true);

        EntityRegistry.registerModEntity(BlueSlime.class, "EdibleSlime", 12, TConstruct.instance, 64, 5, true);
        EntityRegistry.registerModEntity(KingBlueSlime.class, "KingSlime", 14, TConstruct.instance, 64, 5, true);
        // EntityRegistry.registerModEntity(MetalSlime.class, "MetalSlime", 13,
        // TConstruct.instance, 64, 5, true);

        if (PHConstruct.naturalSlimeSpawn > 0) {
            EntityRegistry.addSpawn(
                    BlueSlime.class,
                    PHConstruct.naturalSlimeSpawn,
                    4,
                    20,
                    EnumCreatureType.monster,
                    BiomeDictionary.getBiomesForType(BiomeDictionary.Type.FOREST));
            EntityRegistry.addSpawn(
                    BlueSlime.class,
                    PHConstruct.naturalSlimeSpawn,
                    4,
                    20,
                    EnumCreatureType.monster,
                    BiomeDictionary.getBiomesForType(BiomeDictionary.Type.PLAINS));
            EntityRegistry.addSpawn(
                    BlueSlime.class,
                    PHConstruct.naturalSlimeSpawn,
                    4,
                    20,
                    EnumCreatureType.monster,
                    BiomeDictionary.getBiomesForType(BiomeDictionary.Type.MOUNTAIN));
            EntityRegistry.addSpawn(
                    BlueSlime.class,
                    PHConstruct.naturalSlimeSpawn,
                    4,
                    20,
                    EnumCreatureType.monster,
                    BiomeDictionary.getBiomesForType(BiomeDictionary.Type.HILLS));
            EntityRegistry.addSpawn(
                    BlueSlime.class,
                    PHConstruct.naturalSlimeSpawn,
                    4,
                    20,
                    EnumCreatureType.monster,
                    BiomeDictionary.getBiomesForType(BiomeDictionary.Type.SWAMP));
            EntityRegistry.addSpawn(
                    BlueSlime.class,
                    PHConstruct.naturalSlimeSpawn,
                    4,
                    20,
                    EnumCreatureType.monster,
                    BiomeDictionary.getBiomesForType(BiomeDictionary.Type.JUNGLE));
            EntityRegistry.addSpawn(
                    BlueSlime.class,
                    PHConstruct.naturalSlimeSpawn,
                    4,
                    20,
                    EnumCreatureType.monster,
                    BiomeDictionary.getBiomesForType(BiomeDictionary.Type.WASTELAND));
        }
    }

    private void craftingTableRecipes() {
        String[] patBlock = { "###", "###", "###" };
        String[] patSurround = { "###", "#m#", "###" };

        // Metal conversion Recipes
        GameRegistry.addRecipe(
                new ItemStack(TinkerWorld.metalBlock, 1, 3),
                patBlock,
                '#',
                new ItemStack(TinkerTools.materials, 1, 9)); // Copper
        GameRegistry.addRecipe(
                new ItemStack(TinkerWorld.metalBlock, 1, 5),
                patBlock,
                '#',
                new ItemStack(TinkerTools.materials, 1, 10)); // Tin
        GameRegistry.addRecipe(
                new ItemStack(TinkerWorld.metalBlock, 1, 6),
                patBlock,
                '#',
                new ItemStack(TinkerTools.materials, 1, 11)); // Aluminum
        // GameRegistry.addRecipe(new ItemStack(TRepo.metalBlock, 1, 6),
        // patBlock, '#', new ItemStack(TRepo.materials, 1, 12)); // Aluminum
        GameRegistry.addRecipe(
                new ItemStack(TinkerWorld.metalBlock, 1, 4),
                patBlock,
                '#',
                new ItemStack(TinkerTools.materials, 1, 13)); // Bronze
        GameRegistry.addRecipe(
                new ItemStack(TinkerWorld.metalBlock, 1, 7),
                patBlock,
                '#',
                new ItemStack(TinkerTools.materials, 1, 14)); // AluBrass
        GameRegistry.addRecipe(
                new ItemStack(TinkerWorld.metalBlock, 1, 0),
                patBlock,
                '#',
                new ItemStack(TinkerTools.materials, 1, 3)); // Cobalt
        GameRegistry.addRecipe(
                new ItemStack(TinkerWorld.metalBlock, 1, 1),
                patBlock,
                '#',
                new ItemStack(TinkerTools.materials, 1, 4)); // Ardite
        GameRegistry.addRecipe(
                new ItemStack(TinkerWorld.metalBlock, 1, 2),
                patBlock,
                '#',
                new ItemStack(TinkerTools.materials, 1, 5)); // Manyullyn
        GameRegistry.addRecipe(
                new ItemStack(TinkerWorld.metalBlock, 1, 8),
                patBlock,
                '#',
                new ItemStack(TinkerTools.materials, 1, 15)); // Alumite
        GameRegistry.addRecipe(
                new ItemStack(TinkerWorld.metalBlock, 1, 9),
                patBlock,
                '#',
                new ItemStack(TinkerTools.materials, 1, 16)); // Steel
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 1, 11),
                "#",
                '#',
                new ItemStack(TinkerTools.materials, 1, 12)); // Aluminum raw ->
        // ingot

        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 9, 9),
                "m",
                'm',
                new ItemStack(TinkerWorld.metalBlock, 1, 3)); // Copper
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 9, 10),
                "m",
                'm',
                new ItemStack(TinkerWorld.metalBlock, 1, 5)); // Tin
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 9, 11),
                "m",
                'm',
                new ItemStack(TinkerWorld.metalBlock, 1, 6)); // Aluminum
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 9, 13),
                "m",
                'm',
                new ItemStack(TinkerWorld.metalBlock, 1, 4)); // Bronze
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 9, 14),
                "m",
                'm',
                new ItemStack(TinkerWorld.metalBlock, 1, 7)); // AluBrass
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 9, 3),
                "m",
                'm',
                new ItemStack(TinkerWorld.metalBlock, 1, 0)); // Cobalt
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 9, 4),
                "m",
                'm',
                new ItemStack(TinkerWorld.metalBlock, 1, 1)); // Ardite
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 9, 5),
                "m",
                'm',
                new ItemStack(TinkerWorld.metalBlock, 1, 2)); // Manyullyn
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 9, 15),
                "m",
                'm',
                new ItemStack(TinkerWorld.metalBlock, 1, 8)); // Alumite
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 9, 16),
                "m",
                'm',
                new ItemStack(TinkerWorld.metalBlock, 1, 9)); // Steel

        GameRegistry
                .addRecipe(new ItemStack(Items.iron_ingot), patBlock, '#', new ItemStack(TinkerTools.materials, 1, 19)); // Iron
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 1, 9),
                patBlock,
                '#',
                new ItemStack(TinkerTools.materials, 1, 20)); // Copper
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 1, 10),
                patBlock,
                '#',
                new ItemStack(TinkerTools.materials, 1, 21)); // Tin
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 1, 11),
                patBlock,
                '#',
                new ItemStack(TinkerTools.materials, 1, 22)); // Aluminum
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 1, 14),
                patBlock,
                '#',
                new ItemStack(TinkerTools.materials, 1, 24)); // Aluminum Brass
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 1, 18),
                patBlock,
                '#',
                new ItemStack(TinkerTools.materials, 1, 27)); // Obsidian
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 1, 3),
                patBlock,
                '#',
                new ItemStack(TinkerTools.materials, 1, 28)); // Cobalt
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 1, 4),
                patBlock,
                '#',
                new ItemStack(TinkerTools.materials, 1, 29)); // Ardite
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 1, 5),
                patBlock,
                '#',
                new ItemStack(TinkerTools.materials, 1, 30)); // Manyullyn
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 1, 13),
                patBlock,
                '#',
                new ItemStack(TinkerTools.materials, 1, 31)); // Bronze
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 1, 15),
                patBlock,
                '#',
                new ItemStack(TinkerTools.materials, 1, 32)); // Alumite
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 1, 16),
                patBlock,
                '#',
                new ItemStack(TinkerTools.materials, 1, 33)); // Steel

        GameRegistry.addRecipe(new ItemStack(TinkerTools.materials, 9, 19), "m", 'm', new ItemStack(Items.iron_ingot)); // Iron
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 9, 20),
                "m",
                'm',
                new ItemStack(TinkerTools.materials, 1, 9)); // Copper
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 9, 21),
                "m",
                'm',
                new ItemStack(TinkerTools.materials, 1, 10)); // Tin
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 9, 22),
                "m",
                'm',
                new ItemStack(TinkerTools.materials, 1, 11)); // Aluminum
        // GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 22), "m",
        // 'm', new ItemStack(TRepo.materials, 1, 12)); //Aluminum
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 9, 24),
                "m",
                'm',
                new ItemStack(TinkerTools.materials, 1, 14)); // Aluminum Brass
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 9, 27),
                "m",
                'm',
                new ItemStack(TinkerTools.materials, 1, 18)); // Obsidian
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 9, 28),
                "m",
                'm',
                new ItemStack(TinkerTools.materials, 1, 3)); // Cobalt
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 9, 29),
                "m",
                'm',
                new ItemStack(TinkerTools.materials, 1, 4)); // Ardite
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 9, 30),
                "m",
                'm',
                new ItemStack(TinkerTools.materials, 1, 5)); // Manyullyn
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 9, 31),
                "m",
                'm',
                new ItemStack(TinkerTools.materials, 1, 13)); // Bronze
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 9, 32),
                "m",
                'm',
                new ItemStack(TinkerTools.materials, 1, 15)); // Alumite
        GameRegistry.addRecipe(
                new ItemStack(TinkerTools.materials, 9, 33),
                "m",
                'm',
                new ItemStack(TinkerTools.materials, 1, 16)); // Steel

        String[] dyeTypes = { "dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan",
                "dyeLightGray", "dyeGray", "dyePink", "dyeLime", "dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange",
                "dyeWhite" };
        String color;
        for (int i = 0; i < 16; i++) {
            color = dyeTypes[15 - i];
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(Blocks.wool, 8, i),
                            patSurround,
                            'm',
                            color,
                            '#',
                            new ItemStack(Blocks.wool, 1, Short.MAX_VALUE)));
        }

        // Jack o'Latern Recipe - Stone Torch
        GameRegistry.addRecipe(
                new ItemStack(Blocks.lit_pumpkin, 1, 0),
                "p",
                "s",
                'p',
                new ItemStack(Blocks.pumpkin),
                's',
                new ItemStack(TinkerWorld.stoneTorch));
        // Stone Torch Recipe
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(TinkerWorld.stoneTorch, 4),
                        "p",
                        "w",
                        'p',
                        new ItemStack(Items.coal, 1, Short.MAX_VALUE),
                        'w',
                        "rodStone"));
        // Stone Ladder Recipe
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(TinkerWorld.stoneLadder, 3), "w w", "www", "w w", 'w', "rodStone"));
        // Wooden Rail (if registered) Recipe
        if (TinkerWorld.woodenRail != null) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(TinkerWorld.woodenRail, 4, 0),
                            "b b",
                            "bxb",
                            "b b",
                            'b',
                            "plankWood",
                            'x',
                            "stickWood"));
        }
        // Stonesticks Recipes
        GameRegistry.addRecipe(new ItemStack(TinkerTools.toolRod, 4, 1), "c", "c", 'c', new ItemStack(Blocks.stone));
        GameRegistry
                .addRecipe(new ItemStack(TinkerTools.toolRod, 2, 1), "c", "c", 'c', new ItemStack(Blocks.cobblestone));
        //
        ItemStack aluBrass = new ItemStack(TinkerTools.materials, 1, 14);
        // Clock Recipe - Vanilla alternative
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.clock),
                        " i ",
                        "iri",
                        " i ",
                        'i',
                        aluBrass,
                        'r',
                        "dustRedstone"));
        // Gold Pressure Plate - Vanilla alternative
        // todo: temporarily disabled due to light weighted pressure plate being smeltable to gold
        // GameRegistry.addRecipe(new ItemStack(Blocks.light_weighted_pressure_plate, 0, 1), "ii", 'i', aluBrass);

        // Ultra hardcore recipes
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(goldHead),
                        patSurround,
                        '#',
                        "ingotGold",
                        'm',
                        new ItemStack(Items.skull, 1, 3)));

        // Wool Slab Recipes
        for (int sc = 0; sc <= 7; sc++) {
            GameRegistry.addRecipe(
                    new ItemStack(TinkerWorld.woolSlab1, 6, sc),
                    "www",
                    'w',
                    new ItemStack(Blocks.wool, 1, sc));
            GameRegistry.addRecipe(
                    new ItemStack(TinkerWorld.woolSlab2, 6, sc),
                    "www",
                    'w',
                    new ItemStack(Blocks.wool, 1, sc + 8));

            GameRegistry.addShapelessRecipe(
                    new ItemStack(Blocks.wool, 1, sc),
                    new ItemStack(TinkerWorld.woolSlab1, 1, sc),
                    new ItemStack(TinkerWorld.woolSlab1, 1, sc));
            GameRegistry.addShapelessRecipe(
                    new ItemStack(Blocks.wool, 1, sc + 8),
                    new ItemStack(TinkerWorld.woolSlab2, 1, sc),
                    new ItemStack(TinkerWorld.woolSlab2, 1, sc));
        }
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Blocks.wool, 1, 0), "slabCloth", "slabCloth"));
        // Trap Recipes
        GameRegistry.addRecipe(
                new ItemStack(TinkerWorld.punji, 5, 0),
                "b b",
                " b ",
                "b b",
                'b',
                new ItemStack(Items.reeds));
        GameRegistry.addRecipe(
                new ItemStack(TinkerWorld.barricadeSpruce, 1, 0),
                "b",
                "b",
                'b',
                new ItemStack(Blocks.log, 1, 1));
        GameRegistry.addRecipe(
                new ItemStack(TinkerWorld.barricadeBirch, 1, 0),
                "b",
                "b",
                'b',
                new ItemStack(Blocks.log, 1, 2));
        GameRegistry.addRecipe(
                new ItemStack(TinkerWorld.barricadeJungle, 1, 0),
                "b",
                "b",
                'b',
                new ItemStack(Blocks.log, 1, 3));
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(TinkerWorld.barricadeOak, 1, 0), "b", "b", 'b', "logWood"));

        // Slime Recipes
        GameRegistry.addRecipe(new ItemStack(TinkerWorld.slimeGel, 1, 0), "##", "##", '#', TinkerWorld.strangeFood);
        GameRegistry.addRecipe(
                new ItemStack(TinkerWorld.strangeFood, 4, 0),
                "#",
                '#',
                new ItemStack(TinkerWorld.slimeGel, 1, 0));
        GameRegistry.addRecipe(new ItemStack(TinkerWorld.slimeGel, 1, 1), "##", "##", '#', Items.slime_ball);
        GameRegistry
                .addRecipe(new ItemStack(Items.slime_ball, 4, 0), "#", '#', new ItemStack(TinkerWorld.slimeGel, 1, 1));
        // slimeExplosive
        GameRegistry.addShapelessRecipe(new ItemStack(TinkerWorld.slimeExplosive, 1, 0), Items.slime_ball, Blocks.tnt);
        GameRegistry.addShapelessRecipe(
                new ItemStack(TinkerWorld.slimeExplosive, 1, 2),
                TinkerWorld.strangeFood,
                Blocks.tnt);
        GameRegistry.addRecipe(
                new ShapelessOreRecipe(new ItemStack(TinkerWorld.slimeExplosive, 1, 0), "slimeball", Blocks.tnt));

        GameRegistry.addRecipe(
                new ShapelessOreRecipe(
                        new ItemStack(TinkerWorld.slimeChannel, 1, 0),
                        new ItemStack(TinkerWorld.slimeGel, 1, Short.MAX_VALUE),
                        "dustRedstone"));
        GameRegistry.addRecipe(
                new ShapelessOreRecipe(
                        new ItemStack(TinkerWorld.bloodChannel, 1, 0),
                        new ItemStack(TinkerWorld.strangeFood, 1, 1),
                        new ItemStack(TinkerWorld.strangeFood, 1, 1),
                        new ItemStack(TinkerWorld.strangeFood, 1, 1),
                        new ItemStack(TinkerWorld.strangeFood, 1, 1),
                        "dustRedstone"));
        GameRegistry.addRecipe(
                new ShapelessOreRecipe(
                        new ItemStack(TinkerWorld.slimeChannel, 1, 0),
                        "slimeball",
                        "slimeball",
                        "slimeball",
                        "slimeball",
                        "dustRedstone"));
        GameRegistry.addRecipe(
                new ShapelessOreRecipe(
                        new ItemStack(TinkerWorld.slimePad, 1, 0),
                        TinkerWorld.slimeChannel,
                        "slimeball"));

        GameRegistry.addRecipe(
                new ItemStack(TinkerWorld.meatBlock),
                "mmm",
                "mbm",
                "mmm",
                'b',
                new ItemStack(Items.bone),
                'm',
                new ItemStack(Items.porkchop));
    }

    private void addRecipesForFurnace() {
        FurnaceRecipes.smelting().func_151394_a(
                new ItemStack(TinkerTools.craftedSoil, 1, 3),
                new ItemStack(TinkerTools.craftedSoil, 1, 4),
                0.2f); // Concecrated
        // Soil

        FurnaceRecipes.smelting().func_151394_a(
                new ItemStack(TinkerTools.craftedSoil, 1, 0),
                new ItemStack(TinkerTools.materials, 1, 1),
                2f); // Slime
        FurnaceRecipes.smelting().func_151394_a(
                new ItemStack(TinkerTools.craftedSoil, 1, 1),
                new ItemStack(TinkerTools.materials, 1, 2),
                2f); // Seared brick item
        FurnaceRecipes.smelting().func_151394_a(
                new ItemStack(TinkerTools.craftedSoil, 1, 2),
                new ItemStack(TinkerTools.materials, 1, 17),
                2f); // Blue Slime
        FurnaceRecipes.smelting().func_151394_a(
                new ItemStack(TinkerTools.craftedSoil, 1, 6),
                new ItemStack(TinkerTools.materials, 1, 37),
                2f); // Nether seared
        // brick

        // FurnaceRecipes.smelting().func_151394_a(new ItemStack(TRepo.oreSlag,
        // 1, new ItemStack(TRepo.materials, 1, 3), 3f);
        // FurnaceRecipes.smelting().func_151394_a(new ItemStack(TRepo.oreSlag,
        // 2, new ItemStack(TRepo.materials, 1, 4), 3f);
        FurnaceRecipes.smelting().func_151394_a(
                new ItemStack(TinkerWorld.oreSlag, 1, 3),
                new ItemStack(TinkerTools.materials, 1, 9),
                0.5f);
        FurnaceRecipes.smelting().func_151394_a(
                new ItemStack(TinkerWorld.oreSlag, 1, 4),
                new ItemStack(TinkerTools.materials, 1, 10),
                0.5f);
        FurnaceRecipes.smelting().func_151394_a(
                new ItemStack(TinkerWorld.oreSlag, 1, 5),
                new ItemStack(TinkerTools.materials, 1, 11),
                0.5f);

        FurnaceRecipes.smelting().func_151394_a(
                new ItemStack(TinkerWorld.oreBerries, 1, 0),
                new ItemStack(TinkerTools.materials, 1, 19),
                0.2f);
        FurnaceRecipes.smelting()
                .func_151394_a(new ItemStack(TinkerWorld.oreBerries, 1, 1), new ItemStack(Items.gold_nugget), 0.2f);
        FurnaceRecipes.smelting().func_151394_a(
                new ItemStack(TinkerWorld.oreBerries, 1, 2),
                new ItemStack(TinkerTools.materials, 1, 20),
                0.2f);
        FurnaceRecipes.smelting().func_151394_a(
                new ItemStack(TinkerWorld.oreBerries, 1, 3),
                new ItemStack(TinkerTools.materials, 1, 21),
                0.2f);
        FurnaceRecipes.smelting().func_151394_a(
                new ItemStack(TinkerWorld.oreBerries, 1, 4),
                new ItemStack(TinkerTools.materials, 1, 22),
                0.2f);
        // FurnaceRecipes.smelting().func_151394_a(new
        // ItemStack(TRepo.oreBerries, 5, new ItemStack(TRepo.materials, 1, 23),
        // 0.2f);

        FurnaceRecipes.smelting()
                .func_151394_a(new ItemStack(TinkerWorld.oreGravel, 1, 0), new ItemStack(Items.iron_ingot), 0.2f);
        FurnaceRecipes.smelting()
                .func_151394_a(new ItemStack(TinkerWorld.oreGravel, 1, 1), new ItemStack(Items.gold_ingot), 0.2f);
        FurnaceRecipes.smelting().func_151394_a(
                new ItemStack(TinkerWorld.oreGravel, 1, 2),
                new ItemStack(TinkerTools.materials, 1, 9),
                0.2f);
        FurnaceRecipes.smelting().func_151394_a(
                new ItemStack(TinkerWorld.oreGravel, 1, 3),
                new ItemStack(TinkerTools.materials, 1, 10),
                0.2f);
        FurnaceRecipes.smelting().func_151394_a(
                new ItemStack(TinkerWorld.oreGravel, 1, 4),
                new ItemStack(TinkerTools.materials, 1, 11),
                0.2f);

        FurnaceRecipes.smelting().func_151394_a(
                new ItemStack(TinkerTools.materials, 1, 38),
                new ItemStack(TinkerTools.materials, 1, 4),
                0.2f);
        FurnaceRecipes.smelting().func_151394_a(
                new ItemStack(TinkerTools.materials, 1, 39),
                new ItemStack(TinkerTools.materials, 1, 3),
                0.2f);
        FurnaceRecipes.smelting().func_151394_a(
                new ItemStack(TinkerTools.materials, 1, 40),
                new ItemStack(TinkerTools.materials, 1, 11),
                0.2f);
        FurnaceRecipes.smelting().func_151394_a(
                new ItemStack(TinkerTools.materials, 1, 41),
                new ItemStack(TinkerTools.materials, 1, 5),
                0.2f);
        FurnaceRecipes.smelting().func_151394_a(
                new ItemStack(TinkerTools.materials, 1, 42),
                new ItemStack(TinkerTools.materials, 1, 14),
                0.2f);
    }

    public void oreRegistry() {
        OreDictionary.registerOre("oreCobalt", new ItemStack(TinkerWorld.oreSlag, 1, 1));
        OreDictionary.registerOre("oreArdite", new ItemStack(TinkerWorld.oreSlag, 1, 2));
        OreDictionary.registerOre("oreCopper", new ItemStack(TinkerWorld.oreSlag, 1, 3));
        OreDictionary.registerOre("oreTin", new ItemStack(TinkerWorld.oreSlag, 1, 4));
        OreDictionary.registerOre("oreAluminum", new ItemStack(TinkerWorld.oreSlag, 1, 5));
        OreDictionary.registerOre("oreAluminium", new ItemStack(TinkerWorld.oreSlag, 1, 5));

        OreDictionary.registerOre("oreIron", new ItemStack(TinkerWorld.oreGravel, 1, 0));
        OreDictionary.registerOre("oreGold", new ItemStack(TinkerWorld.oreGravel, 1, 1));
        OreDictionary.registerOre("oreCobalt", new ItemStack(TinkerWorld.oreGravel, 1, 5));
        OreDictionary.registerOre("oreCopper", new ItemStack(TinkerWorld.oreGravel, 1, 2));
        OreDictionary.registerOre("oreTin", new ItemStack(TinkerWorld.oreGravel, 1, 3));
        OreDictionary.registerOre("oreAluminum", new ItemStack(TinkerWorld.oreGravel, 1, 4));
        OreDictionary.registerOre("oreAluminium", new ItemStack(TinkerWorld.oreGravel, 1, 4));

        OreDictionary.registerOre("blockCobalt", new ItemStack(TinkerWorld.metalBlock, 1, 0));
        OreDictionary.registerOre("blockArdite", new ItemStack(TinkerWorld.metalBlock, 1, 1));
        OreDictionary.registerOre("blockManyullyn", new ItemStack(TinkerWorld.metalBlock, 1, 2));
        OreDictionary.registerOre("blockCopper", new ItemStack(TinkerWorld.metalBlock, 1, 3));
        OreDictionary.registerOre("blockBronze", new ItemStack(TinkerWorld.metalBlock, 1, 4));
        OreDictionary.registerOre("blockTin", new ItemStack(TinkerWorld.metalBlock, 1, 5));
        OreDictionary.registerOre("blockAluminum", new ItemStack(TinkerWorld.metalBlock, 1, 6));
        OreDictionary.registerOre("blockAluminium", new ItemStack(TinkerWorld.metalBlock, 1, 6));
        OreDictionary.registerOre("blockAluminumBrass", new ItemStack(TinkerWorld.metalBlock, 1, 7));
        OreDictionary.registerOre("blockAluminiumBrass", new ItemStack(TinkerWorld.metalBlock, 1, 7));
        OreDictionary.registerOre("blockAlumite", new ItemStack(TinkerWorld.metalBlock, 1, 8));
        OreDictionary.registerOre("blockSteel", new ItemStack(TinkerWorld.metalBlock, 1, 9));
        OreDictionary.registerOre("blockEnder", new ItemStack(TinkerWorld.metalBlock, 1, 10));

        OreDictionary.registerOre("nuggetIron", new ItemStack(TinkerWorld.oreBerries, 1, 0));
        OreDictionary.registerOre("nuggetCopper", new ItemStack(TinkerWorld.oreBerries, 1, 2));
        OreDictionary.registerOre("nuggetTin", new ItemStack(TinkerWorld.oreBerries, 1, 3));
        OreDictionary.registerOre("nuggetAluminum", new ItemStack(TinkerWorld.oreBerries, 1, 4));
        OreDictionary.registerOre("nuggetAluminium", new ItemStack(TinkerWorld.oreBerries, 1, 4));
        OreDictionary.registerOre("nuggetGold", new ItemStack(TinkerWorld.oreBerries, 1, 1));

        // also register berries as berries. durr
        OreDictionary.registerOre("oreberryIron", new ItemStack(TinkerWorld.oreBerries, 1, 0));
        OreDictionary.registerOre("oreberryCopper", new ItemStack(TinkerWorld.oreBerries, 1, 2));
        OreDictionary.registerOre("oreberryTin", new ItemStack(TinkerWorld.oreBerries, 1, 3));
        OreDictionary.registerOre("oreberryAluminum", new ItemStack(TinkerWorld.oreBerries, 1, 4));
        OreDictionary.registerOre("oreberryAluminium", new ItemStack(TinkerWorld.oreBerries, 1, 4));
        OreDictionary.registerOre("oreberryGold", new ItemStack(TinkerWorld.oreBerries, 1, 1));
        OreDictionary.registerOre("oreberryEssence", new ItemStack(TinkerWorld.oreBerries, 1, 5));

        OreDictionary.registerOre("orebushIron", new ItemStack(TinkerWorld.oreBerry, 1, 0));
        OreDictionary.registerOre("orebushGold", new ItemStack(TinkerWorld.oreBerry, 1, 1));
        OreDictionary.registerOre("orebushCopper", new ItemStack(TinkerWorld.oreBerry, 1, 2));
        OreDictionary.registerOre("orebushTin", new ItemStack(TinkerWorld.oreBerry, 1, 3));

        OreDictionary.registerOre("orebushAluminum", new ItemStack(TinkerWorld.oreBerrySecond, 1, 4));
        OreDictionary.registerOre("orebushAluminium", new ItemStack(TinkerWorld.oreBerrySecond, 1, 4));
        OreDictionary.registerOre("orebushEssence", new ItemStack(TinkerWorld.oreBerrySecond, 1, 5));

        OreDictionary.registerOre("slabCloth", new ItemStack(TinkerWorld.woolSlab1, 1, Short.MAX_VALUE));
        OreDictionary.registerOre("slabCloth", new ItemStack(TinkerWorld.woolSlab2, 1, Short.MAX_VALUE));

        ensureOreIsRegistered("stoneMossy", new ItemStack(Blocks.stonebrick, 1, 1));
        ensureOreIsRegistered("stoneMossy", new ItemStack(Blocks.mossy_cobblestone));

        OreDictionary.registerOre("crafterWood", new ItemStack(Blocks.crafting_table, 1));
        OreDictionary.registerOre("craftingTableWood", new ItemStack(Blocks.crafting_table, 1));

        OreDictionary.registerOre("torchStone", new ItemStack(TinkerWorld.stoneTorch));

        // Vanilla stuff
        OreDictionary.registerOre("slimeball", new ItemStack(Items.slime_ball));
        OreDictionary.registerOre("slimeball", new ItemStack(TinkerWorld.strangeFood, 1, 0));
        OreDictionary.registerOre("slimeball", new ItemStack(TinkerWorld.strangeFood, 1, 1));
        OreDictionary.registerOre("blockGlass", new ItemStack(Blocks.glass));
        RecipeRemover.removeShapedRecipe(new ItemStack(Blocks.sticky_piston));
        RecipeRemover.removeShapedRecipe(new ItemStack(Items.magma_cream));
        RecipeRemover.removeShapedRecipe(new ItemStack(Items.lead));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Blocks.sticky_piston), "slimeball", Blocks.piston));
        GameRegistry
                .addRecipe(new ShapelessOreRecipe(new ItemStack(Items.magma_cream), "slimeball", Items.blaze_powder));
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.lead, 2),
                        "ss ",
                        "sS ",
                        "  s",
                        's',
                        Items.string,
                        'S',
                        "slimeball"));
    }

    public static void ensureOreIsRegistered(String oreName, ItemStack is) {
        int oreId = OreDictionary.getOreID(is);
        if (oreId == -1) {
            OreDictionary.registerOre(oreName, is);
        }
    }

    public void addLoot() {
        // Item, min, max, weight
        ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST)
                .addItem(new WeightedRandomChestContent(new ItemStack(TinkerArmor.heartCanister, 1, 1), 1, 1, 5));
        ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_DESERT_CHEST)
                .addItem(new WeightedRandomChestContent(new ItemStack(TinkerArmor.heartCanister, 1, 1), 1, 1, 10));
        ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST)
                .addItem(new WeightedRandomChestContent(new ItemStack(TinkerArmor.heartCanister, 1, 1), 1, 1, 10));

        TinkerWorld.tinkerHouseChest = new ChestGenHooks("TinkerHouse", new WeightedRandomChestContent[0], 3, 27);
        TinkerWorld.tinkerHouseChest
                .addItem(new WeightedRandomChestContent(new ItemStack(TinkerArmor.heartCanister, 1, 1), 1, 1, 1));
        int[] validTypes = { 0, 1, 2, 3, 4, 5, 6, 8, 9, 13, 14, 17 };
        Item[] partTypes = { TinkerTools.pickaxeHead, TinkerTools.shovelHead, TinkerTools.hatchetHead,
                TinkerTools.binding, TinkerTools.swordBlade, TinkerTools.wideGuard, TinkerTools.handGuard,
                TinkerTools.crossbar, TinkerTools.knifeBlade, TinkerTools.frypanHead, TinkerTools.signHead,
                TinkerTools.chiselHead };

        for (Item partType : partTypes) {
            for (int validType : validTypes) {
                TinkerWorld.tinkerHouseChest
                        .addItem(new WeightedRandomChestContent(new ItemStack(partType, 1, validType), 1, 1, 15));
            }
        }

        TinkerWorld.tinkerHousePatterns = new ChestGenHooks("TinkerPatterns", new WeightedRandomChestContent[0], 5, 30);
        for (int i = 0; i < 13; i++) {
            TinkerWorld.tinkerHousePatterns.addItem(
                    new WeightedRandomChestContent(new ItemStack(TinkerTools.woodPattern, 1, i + 1), 1, 3, 20));
        }
        TinkerWorld.tinkerHousePatterns
                .addItem(new WeightedRandomChestContent(new ItemStack(TinkerTools.woodPattern, 1, 22), 1, 3, 40));
    }
}
