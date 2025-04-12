package tconstruct.plugins.wdmla;

import com.gtnewhorizons.wdmla.api.IWDMlaClientRegistration;
import com.gtnewhorizons.wdmla.api.IWDMlaCommonRegistration;
import com.gtnewhorizons.wdmla.api.IWDMlaPlugin;
import com.gtnewhorizons.wdmla.api.WDMlaPlugin;
import mantle.pulsar.pulse.Pulse;
import tconstruct.armor.blocks.DryingRack;
import tconstruct.mechworks.blocks.BlockLandmine;

@Pulse(
        id = "Tinkers WDMla Compatibility",
        description = "Tinkers Construct compatibility for WDMla",
        modsRequired = "wdmla",
        forced = true)
@WDMlaPlugin
public class TinkerWDMla implements IWDMlaPlugin {

    @Override
    public void register(IWDMlaCommonRegistration registration) {
        registration.registerBlockDataProvider(DryingRackProvider.INSTANCE, DryingRack.class);
    }

    @Override
    public void registerClient(IWDMlaClientRegistration registration) {
        registration.registerBlockComponent(LandmineHeaderProvider.INSTANCE, BlockLandmine.class);
        registration.registerBlockComponent(DryingRackProvider.INSTANCE, DryingRack.class);
    }
}
