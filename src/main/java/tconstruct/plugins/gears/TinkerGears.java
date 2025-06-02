package tconstruct.plugins.gears;

import static tconstruct.util.Constants.LIQUID_VALUE_INGOT;
import static tconstruct.util.Reference.MOD_ID;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import lombok.extern.log4j.Log4j2;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.FluidType;
import tconstruct.library.crafting.Smeltery;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.util.config.PHConstruct;

@Log4j2(topic = MOD_ID)
@Pulse(
        id = "Tinkers Gears",
        description = "Adds a gear cast if other mods provide gears",
        pulsesRequired = "Tinkers' Smeltery")
public class TinkerGears {

    public static Item gearCast;

    @Handler
    public void preInit(FMLPreInitializationEvent event) {
        log.info("Gear module active. Adding gear cast.");
        gearCast = new GearCast();

        GameRegistry.registerItem(gearCast, "gearCast");
    }

    @Handler
    public void postInit(FMLPostInitializationEvent event) {
        ItemStack cast = new ItemStack(gearCast);
        FluidStack aluCastLiquid = new FluidStack(TinkerSmeltery.moltenAlubrassFluid, LIQUID_VALUE_INGOT);
        FluidStack goldCastLiquid = null;
        if (!PHConstruct.removeGoldCastRecipes) {
            goldCastLiquid = new FluidStack(TinkerSmeltery.moltenGoldFluid, LIQUID_VALUE_INGOT * 2);
        }

        // find all gears in the registry
        if (!PHConstruct.disableAllRecipes) {
            for (String oreName : OreDictionary.getOreNames()) {
                if (!oreName.startsWith("gear")) continue;

                List<ItemStack> gears = OreDictionary.getOres(oreName);

                // register every gear besides wooden gear for creating a gear cast
                if (!oreName.equals("gearWood")) {
                    for (ItemStack g : gears) {
                        TConstructRegistry.getTableCasting().addCastingRecipe(cast, aluCastLiquid, g, false, 50);
                        if (!PHConstruct.removeGoldCastRecipes)
                            TConstructRegistry.getTableCasting().addCastingRecipe(cast, goldCastLiquid, g, false, 50);
                    }
                }

                // find a fluid that fits the gear
                String material = oreName.substring(4);
                // try the oredict name directly
                Fluid fluid = FluidRegistry.getFluid(material);
                // or lowercased
                if (fluid == null) fluid = FluidRegistry.getFluid(material.toLowerCase());
                // or in the tinkers liquid format
                if (fluid == null) fluid = FluidRegistry.getFluid(material.toLowerCase() + ".molten");

                // found one?
                if (fluid != null) {
                    ItemStack gear = gears.get(0);
                    FluidStack liquid = new FluidStack(fluid.getID(), LIQUID_VALUE_INGOT * 4);
                    // gear casting
                    TConstructRegistry.getTableCasting().addCastingRecipe(gear, liquid, cast, 55);
                    // and melting it back
                    FluidType ft = FluidType.getFluidType(fluid);
                    if (ft != null) Smeltery.addMelting(ft, gear, 100, LIQUID_VALUE_INGOT * 4);
                    else Smeltery.addMelting(gear, TinkerSmeltery.glueBlock, 0, 100, liquid);
                }
            }
        }
    }
}
