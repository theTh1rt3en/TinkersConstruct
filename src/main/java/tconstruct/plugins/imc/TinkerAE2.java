package tconstruct.plugins.imc;

import static tconstruct.util.Reference.MOD_ID;

import java.util.Arrays;
import java.util.List;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import lombok.extern.log4j.Log4j2;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;

@Log4j2(topic = MOD_ID)
@ObjectHolder(MOD_ID)
@Pulse(
        id = "Tinkers AE2 Compatibility",
        description = "Tinkers Construct compatibility for Applied Energistics 2",
        modsRequired = "appliedenergistics2",
        forced = true)
public class TinkerAE2 {

    private static final List<String> spatialIOLogics = Arrays.asList(
            "EssenceExtractorLogic",
            "GolemPedestalLogic", // TODO What happened to these?
            "MultiServantLogic"); // TODO Should Mantle handle this?

    private static final List<String> spatialIOSmelteryLogics = Arrays.asList(
            "AdaptiveSmelteryLogic",
            "AqueductLogic",
            "CastingBasinLogic",
            "CastingChannelLogic",
            "CastingTableLogic",
            "FaucetLogic",
            "LavaTankLogic",
            "SmelteryDrainLogic",
            "SmelteryLogic",
            "TankAirLogic",
            "TowerFurnaceLogic");

    private static final List<String> spatialIOToolLogics = Arrays.asList(
            "CraftingStationLogic",
            "FrypanLogic",
            "PartBuilderLogic",
            "PatternChestLogic",
            "StencilTableLogic",
            "ToolForgeLogic",
            "ToolStationLogic");

    @Handler
    public void init(FMLInitializationEvent event) {
        log.info("AE2 detected. Registering for Spatial IO.");

        addtoSpatialWhitelist("tconstruct.blocks.logic.DryingRackLogic");
        addtoSpatialWhitelist("tconstruct.mechworks.logic.TileEntityLandmine");

        for (String s : spatialIOSmelteryLogics) {
            addtoSpatialWhitelist("tconstruct.smeltery.logic." + s);
        }

        for (String s : spatialIOToolLogics) {
            addtoSpatialWhitelist("tconstruct.tools.logic." + s);
        }
    }

    public void addtoSpatialWhitelist(String teClass) {
        FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", teClass);
    }
}
