package tconstruct.plugins.natura;

import static tconstruct.util.Reference.MOD_ID;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import tconstruct.api.harvesting.CropHarvestHandlers;

@ObjectHolder(MOD_ID)
@Pulse(
        id = "Tinkers Natura Compatibility",
        description = "Tinkers Construct compatibility for Natura",
        modsRequired = "Natura",
        forced = true)
public class TinkerNatura {

    @Handler
    public void init(FMLInitializationEvent event) {
        CropHarvestHandlers.registerCropHarvestHandler(new NaturaCropHarvestHandler());
    }
}
