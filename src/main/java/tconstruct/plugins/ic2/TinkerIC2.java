package tconstruct.plugins.ic2;

import static tconstruct.util.Reference.MOD_ID;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import lombok.extern.log4j.Log4j2;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import tconstruct.api.harvesting.CropHarvestHandlers;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.util.config.PHConstruct;

@Log4j2(topic = MOD_ID)
@ObjectHolder(MOD_ID)
@Pulse(
        id = "Tinkers IC2 Compatibility",
        description = "Tinkers Construct compatibility for IndustrialCraft 2",
        modsRequired = "IC2",
        forced = true)
public class TinkerIC2 {

    private static final String IC2_UUM_FLUIDNAME = "ic2fluiduumatter";

    @Handler
    public void init(FMLInitializationEvent event) {
        log.info("IC2 detected. Preparing for shenanigans.");

        CropHarvestHandlers.registerCropHarvestHandler(new Ic2CropHarvestHandler());

        Fluid fluidUUM = FluidRegistry.getFluid(IC2_UUM_FLUIDNAME);
        if (fluidUUM == null) return;

        FluidStack fluidStackBlock = new FluidStack(fluidUUM, 4500);
        LiquidCasting basinCasting = TConstructRegistry.getBasinCasting();

        // Block casting
        if (!PHConstruct.disableAllRecipes) {
            basinCasting.addCastingRecipe(
                    new ItemStack(Blocks.diamond_block),
                    fluidStackBlock,
                    new ItemStack(Blocks.dirt),
                    true,
                    50);
        }
    }
}
