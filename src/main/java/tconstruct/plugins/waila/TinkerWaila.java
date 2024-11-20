package tconstruct.plugins.waila;

import static tconstruct.util.Reference.MOD_ID;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import tconstruct.TConstruct;

@ObjectHolder(MOD_ID)
@Pulse(
        id = "Tinkers Waila Compatibility",
        description = "Tinkers Construct compatibility for Waila",
        modsRequired = "Waila",
        forced = true)
public class TinkerWaila {

    @Handler
    public void init(FMLInitializationEvent event) {
        TConstruct.logger.info("Waila detected. Registering TConstruct tank blocks with Waila registry.");
        FMLInterModComms.sendMessage("Waila", "register", "tconstruct.plugins.waila.WailaRegistrar.wailaCallback");
    }
}
