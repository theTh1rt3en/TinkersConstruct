package tconstruct;

import static tconstruct.util.Reference.DEPENDENCIES;
import static tconstruct.util.Reference.MOD_ID;
import static tconstruct.util.Reference.MOD_NAME;
import static tconstruct.util.Reference.MOD_VERSION;

import java.io.File;
import java.util.Map;
import java.util.Random;

import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;
import lombok.Getter;
import mantle.pulsar.config.ForgeCFG;
import mantle.pulsar.control.PulseManager;
import tconstruct.achievements.AchievementEvents;
import tconstruct.achievements.TAchievements;
import tconstruct.api.TConstructAPI;
import tconstruct.armor.TinkerArmor;
import tconstruct.armor.player.TPlayerHandler;
import tconstruct.armor.player.TPlayerStats;
import tconstruct.common.TProxyCommon;
import tconstruct.gadgets.TinkerGadgets;
import tconstruct.library.SlimeBounceHandler;
import tconstruct.library.TConstructCreativeTab;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.Detailing;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.library.util.AoEExclusionList;
import tconstruct.mechworks.TinkerMechworks;
import tconstruct.mechworks.landmine.behavior.Behavior;
import tconstruct.mechworks.landmine.behavior.stackCombo.SpecialStackHandler;
import tconstruct.plugins.TinkerThaumcraft;
import tconstruct.plugins.fmp.TinkerFMP;
import tconstruct.plugins.gears.TinkerGears;
import tconstruct.plugins.ic2.TinkerIC2;
import tconstruct.plugins.imc.TinkerAE2;
import tconstruct.plugins.imc.TinkerBuildCraft;
import tconstruct.plugins.imc.TinkerMystcraft;
import tconstruct.plugins.imc.TinkerRfTools;
import tconstruct.plugins.mfr.TinkerMFR;
import tconstruct.plugins.te4.TinkerTE4;
import tconstruct.plugins.te4.TinkersThermalFoundation;
import tconstruct.plugins.ubc.TinkerUBC;
import tconstruct.plugins.waila.TinkerWaila;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.tools.TinkerTools;
import tconstruct.util.IMCHandler;
import tconstruct.util.SpawnInterceptor;
import tconstruct.util.config.DimensionBlacklist;
import tconstruct.util.config.PHConstruct;
import tconstruct.util.network.PacketPipeline;
import tconstruct.weaponry.TinkerWeaponry;
import tconstruct.world.TinkerWorld;
import tconstruct.world.gen.SlimeIslandGen;
import tconstruct.world.village.ComponentSmeltery;
import tconstruct.world.village.ComponentToolWorkshop;
import tconstruct.world.village.TVillageTrades;
import tconstruct.world.village.VillageSmelteryHandler;
import tconstruct.world.village.VillageToolStationHandler;

/**
 * TConstruct, the tool mod. Craft your tools with style, then modify until the original is gone!
 *
 * @author mDiyo
 */
@Mod(modid = MOD_ID, name = MOD_NAME, version = MOD_VERSION, dependencies = DEPENDENCIES)
public class TConstruct {

    public static final Logger logger = LogManager.getLogger(MOD_ID);
    public static final PacketPipeline packetPipeline = new PacketPipeline();
    private static final String PROXY_CLIENT = "tconstruct.client.TProxyClient";
    private static final String PROXY_SERVER = "tconstruct.common.TProxyCommon";
    public static Random random = new Random();
    /* Instance of this mod, used for grabbing prototype fields */
    @Instance(MOD_ID)
    public static TConstruct instance;
    /* Proxies for sides, used for graphics processing and client controls */
    @SidedProxy(clientSide = PROXY_CLIENT, serverSide = PROXY_SERVER)
    public static TProxyCommon proxy;

    /* Loads modules in a way that doesn't clutter the @Mod list */
    public static PulseManager pulsar = new PulseManager(
            MOD_ID,
            new ForgeCFG("TinkersModules", "Modules: Disabling these will disable a chunk of the mod"));
    @Getter
    public static LiquidCasting tableCasting;
    @Getter
    public static LiquidCasting basinCasting;
    @Getter
    public static Detailing chiselDetailing;

    public TConstruct() {
        if (Loader.isModLoaded("Natura")) {
            logger.info("Natura, what are we going to do tomorrow night?");
            LogManager.getLogger("Natura").info("TConstruct, we're going to take over the world!");
        } else {
            logger.info("Preparing to take over the world");
        }
    }

    private static void registerMaterialTabs() {
        TConstructRegistry.materialTab = new TConstructCreativeTab("TConstructMaterials");
        TConstructRegistry.toolTab = new TConstructCreativeTab("TConstructTools");
        TConstructRegistry.partTab = new TConstructCreativeTab("TConstructParts");
        TConstructRegistry.blockTab = new TConstructCreativeTab("TConstructBlocks");
        TConstructRegistry.equipableTab = new TConstructCreativeTab("TConstructEquipables");
        TConstructRegistry.weaponryTab = new TConstructCreativeTab("TConstructWeaponry");
        TConstructRegistry.gadgetsTab = new TConstructCreativeTab("TConstructGadgets");
    }

    private static void registerPulses() {
        pulsar.registerPulse(new TinkerWorld());
        pulsar.registerPulse(new TinkerTools());
        pulsar.registerPulse(new TinkerSmeltery());
        pulsar.registerPulse(new TinkerMechworks());
        pulsar.registerPulse(new TinkerArmor());
        pulsar.registerPulse(new TinkerWeaponry());
        pulsar.registerPulse(new TinkerGadgets());
        pulsar.registerPulse(new TinkerThaumcraft());
        pulsar.registerPulse(new TinkerWaila());
        pulsar.registerPulse(new TinkerBuildCraft());
        pulsar.registerPulse(new TinkerAE2());
        pulsar.registerPulse(new TinkerIC2());
        pulsar.registerPulse(new TinkerMystcraft());
        pulsar.registerPulse(new TinkerMFR());
        pulsar.registerPulse(new TinkerTE4());
        pulsar.registerPulse(new TinkersThermalFoundation());
        pulsar.registerPulse(new TinkerFMP());
        pulsar.registerPulse(new TinkerUBC());
        pulsar.registerPulse(new TinkerGears());
        pulsar.registerPulse(new TinkerRfTools());
    }

    // Force the client and server to have or not have this mod
    @NetworkCheckHandler()
    public boolean matchModVersions(Map<String, String> remoteVersions, Side side) {
        return remoteVersions.containsKey(MOD_ID) && MOD_VERSION.equals(remoteVersions.get(MOD_ID));
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        PHConstruct.initProps(event.getModConfigurationDirectory());

        registerPulses();
        registerMaterialTabs();

        tableCasting = new LiquidCasting();
        basinCasting = new LiquidCasting();
        chiselDetailing = new Detailing();

        AoEExclusionList.init(new File(event.getModConfigurationDirectory(), "TConstruct_AOEExclusions.cfg"));

        var playerTracker = new TPlayerHandler();
        FMLCommonHandler.instance().bus().register(playerTracker);
        MinecraftForge.EVENT_BUS.register(playerTracker);
        NetworkRegistry.INSTANCE.registerGuiHandler(TConstruct.instance, proxy);

        if (PHConstruct.globalDespawn != 6000 && PHConstruct.globalDespawn != 0) {
            MinecraftForge.EVENT_BUS.register(new SpawnInterceptor());
        }

        pulsar.preInit(event);

        if (PHConstruct.achievementsEnabled) {
            TAchievements.addDefaultAchievements();
        }

        if (PHConstruct.addToVillages) {
            addToVillages();
        }

        TConstructAPI.PROP_NAME = TPlayerStats.PROP_NAME;
    }

    private static void addToVillages() {
        // adds to the villager spawner egg
        VillagerRegistry.instance().registerVillagerId(78943);
        // moved down, not needed if 'addToVillages' is false
        if (PHConstruct.allowVillagerTrading)
            VillagerRegistry.instance().registerVillageTradeHandler(78943, new TVillageTrades());

        VillagerRegistry.instance().registerVillageCreationHandler(new VillageToolStationHandler());
        MapGenStructureIO.func_143031_a(ComponentToolWorkshop.class, "TConstruct:ToolWorkshopStructure");
        if (pulsar.isPulseLoaded("Tinkers' Smeltery")) {
            VillagerRegistry.instance().registerVillageCreationHandler(new VillageSmelteryHandler());
            MapGenStructureIO.func_143031_a(ComponentSmeltery.class, "TConstruct:SmelteryStructure");
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        packetPipeline.initalise();
        DimensionBlacklist.getBadBimensions();
        GameRegistry.registerWorldGenerator(new SlimeIslandGen(TinkerWorld.slimePool, 2), 2);

        pulsar.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        packetPipeline.postInitialise();
        Behavior.registerBuiltInBehaviors();
        SpecialStackHandler.registerBuiltInStackHandlers();

        proxy.initialize();
        pulsar.postInit(event);

        if (PHConstruct.achievementsEnabled) {
            TAchievements.registerAchievementPane();
            MinecraftForge.EVENT_BUS.register(new AchievementEvents());
        }
    }

    /**
     * Called on server shutdown to prevent memory leaks
     */
    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        TinkerGadgets.log.info("Cleaning up SlimeBounceHandler data.");
        SlimeBounceHandler.BOUNCING_ENTITIES.clear();
    }

    /* IMC Mod Support */
    @EventHandler
    public void handleIMC(FMLInterModComms.IMCEvent e) {
        IMCHandler.processIMC(e.getMessages());
    }

    @EventHandler
    public void loadComplete(FMLLoadCompleteEvent evt) {
        IMCHandler.processIMC(FMLInterModComms.fetchRuntimeMessages(this));
    }

    @EventHandler
    public void missingMapping(FMLMissingMappingsEvent event) {
        // this will be called because the air-block got removed
        for (FMLMissingMappingsEvent.MissingMapping mapping : event.get()) {
            if (mapping.name.equals("TConstruct:TankAir")) mapping.ignore();
        }
    }
}
