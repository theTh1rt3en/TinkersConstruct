package tconstruct.plugins.wdmla;

import com.gtnewhorizons.wdmla.plugin.universal.ItemStorageProvider;
import mantle.blocks.abstracts.InventorySlab;
import net.minecraft.util.ResourceLocation;

import com.gtnewhorizons.wdmla.api.IWDMlaClientRegistration;
import com.gtnewhorizons.wdmla.api.IWDMlaCommonRegistration;
import com.gtnewhorizons.wdmla.api.IWDMlaPlugin;
import com.gtnewhorizons.wdmla.api.WDMlaPlugin;
import com.gtnewhorizons.wdmla.plugin.vanilla.TECustomNameHeaderProvider;

import mantle.pulsar.pulse.Pulse;
import tconstruct.TConstruct;
import tconstruct.armor.blocks.DryingRack;
import tconstruct.mechworks.blocks.BlockLandmine;
import tconstruct.tools.blocks.CraftingStationBlock;
import tconstruct.tools.blocks.FurnaceSlab;
import tconstruct.tools.blocks.ToolStationBlock;

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
        registration.registerBlockDataProvider(TECustomNameHeaderProvider.INSTANCE, FurnaceSlab.class);
        registration.registerBlockDataProvider(FurnaceSlabProvider.INSTANCE, FurnaceSlab.class);
        registration.registerItemStorage(ItemStorageProvider.Extension.INSTANCE, CraftingStationBlock.class);
        registration.registerItemStorage(ItemStorageProvider.Extension.INSTANCE, ToolStationBlock.class);
        registration.registerItemStorage(ItemStorageProvider.Extension.INSTANCE, InventorySlab.class);
    }

    @Override
    public void registerClient(IWDMlaClientRegistration registration) {
        registration.registerBlockComponent(LandmineHeaderProvider.INSTANCE, BlockLandmine.class);
        registration.registerBlockComponent(DryingRackProvider.INSTANCE, DryingRack.class);
        registration.registerBlockComponent(FurnaceSlabProvider.INSTANCE, FurnaceSlab.class);
    }

    public static ResourceLocation TiC(String uid) {
        return new ResourceLocation(TConstruct.modID.toLowerCase(), uid);
    }
}
