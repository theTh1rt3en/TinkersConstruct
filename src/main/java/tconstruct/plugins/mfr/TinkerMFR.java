package tconstruct.plugins.mfr;

import static tconstruct.util.Reference.MOD_ID;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import lombok.extern.log4j.Log4j2;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;

@Log4j2(topic = MOD_ID)
@ObjectHolder(MOD_ID)
@Pulse(
        id = "Tinkers MFR Compatibility",
        description = "Tinkers Construct compatibility for MineFactory Reloaded",
        modsRequired = "MineFactoryReloaded",
        forced = true)
public class TinkerMFR {

    @Handler
    public void init(FMLInitializationEvent event) {
        log.info(
                "MineFactoryReloaded detected. Registering TConstruct farmables/grindables with MFR's Farming Registry.");
        MFRRegister.registerWithMFR();
        /*
         * Perhaps TC ores should be registered as drops from the MFR Laser Drill here, but I don't know which things
         * would be suitable for that. Syntax: FarmingRegistry.registerLaserOre(int weight, ItemStack droppedStack));
         * Currently used weights are from about 50 (emerald) to 175 (coal).
         */
    }
}
