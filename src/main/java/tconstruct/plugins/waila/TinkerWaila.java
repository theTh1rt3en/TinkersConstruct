package tconstruct.plugins.waila;

import static tconstruct.util.Reference.MOD_ID;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import lombok.extern.log4j.Log4j2;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;

@Log4j2(topic = MOD_ID)
@ObjectHolder(MOD_ID)
@Pulse(
        id = "Tinkers Waila Compatibility",
        description = "Tinkers Construct compatibility for Waila",
        modsRequired = "Waila",
        forced = true)
public class TinkerWaila {

    @Handler
    public void init(FMLInitializationEvent event) {
        log.info("Waila detected. Registering TConstruct tank blocks with Waila registry.");
        FMLInterModComms.sendMessage("Waila", "register", "tconstruct.plugins.waila.WailaRegistrar.wailaCallback");
    }
}
