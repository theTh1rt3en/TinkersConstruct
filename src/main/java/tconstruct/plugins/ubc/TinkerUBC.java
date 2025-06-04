package tconstruct.plugins.ubc;

import static tconstruct.util.Reference.MOD_ID;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import exterminatorJeff.undergroundBiomes.api.UBAPIHook;
import exterminatorJeff.undergroundBiomes.api.UBOreTexturizer.BlocksAreAlreadySet;
import lombok.extern.log4j.Log4j2;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import tconstruct.world.TinkerWorld;

@Log4j2(topic = MOD_ID)
@Pulse(
        id = "Tinkers' Underground Biomes Compatiblity",
        description = "Tinkers' Construct compatibilty for Underground Biomes Construct",
        modsRequired = "UndergroundBiomes",
        pulsesRequired = "Tinkers' World",
        forced = true)
public class TinkerUBC {

    @Handler
    public void preInit(FMLPreInitializationEvent event) {
        registerBlock(3, "Copper");
        registerBlock(4, "Tin");
        registerBlock(5, "Aluminum");
    }

    private void registerBlock(int meta, String blockName) {
        String overlayTexture = blockName.toLowerCase();
        try {
            UBAPIHook.ubAPIHook.ubOreTexturizer.requestUBOreSetup(
                    TinkerWorld.oreSlag,
                    meta,
                    "tinker:ore_" + overlayTexture + "_overlay",
                    "MetalOre." + blockName);
        } catch (RuntimeException exception) {
            // we have to work around this because otherwise FML would access this for some reason
            // and crash because BlocksAreAlreadySet Exception is not present without UBC
            if (exception instanceof BlocksAreAlreadySet) log.error("{} is already registered in UBC", blockName);
            else
                // rethrow if it's another exception
                throw exception;
        }
    }
}
