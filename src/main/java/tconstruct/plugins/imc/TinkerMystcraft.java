package tconstruct.plugins.imc;

import static tconstruct.smeltery.TinkerSmeltery.moltenAlubrassFluid;
import static tconstruct.smeltery.TinkerSmeltery.moltenAlumiteFluid;
import static tconstruct.smeltery.TinkerSmeltery.moltenArditeFluid;
import static tconstruct.smeltery.TinkerSmeltery.moltenBronzeFluid;
import static tconstruct.smeltery.TinkerSmeltery.moltenCobaltFluid;
import static tconstruct.smeltery.TinkerSmeltery.moltenElectrumFluid;
import static tconstruct.smeltery.TinkerSmeltery.moltenEmeraldFluid;
import static tconstruct.smeltery.TinkerSmeltery.moltenEnderFluid;
import static tconstruct.smeltery.TinkerSmeltery.moltenEnderiumFluid;
import static tconstruct.smeltery.TinkerSmeltery.moltenGoldFluid;
import static tconstruct.smeltery.TinkerSmeltery.moltenInvarFluid;
import static tconstruct.smeltery.TinkerSmeltery.moltenLumiumFluid;
import static tconstruct.smeltery.TinkerSmeltery.moltenManyullynFluid;
import static tconstruct.smeltery.TinkerSmeltery.moltenMithrilFluid;
import static tconstruct.smeltery.TinkerSmeltery.moltenQuartzFluid;
import static tconstruct.smeltery.TinkerSmeltery.moltenShinyFluid;
import static tconstruct.smeltery.TinkerSmeltery.moltenSignalumFluid;
import static tconstruct.smeltery.TinkerSmeltery.moltenSilverFluid;
import static tconstruct.smeltery.TinkerSmeltery.moltenSteelFluid;
import static tconstruct.smeltery.TinkerSmeltery.pigIronFluid;
import static tconstruct.util.Reference.MOD_ID;

import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import lombok.extern.log4j.Log4j2;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;

@Log4j2(topic = MOD_ID)
@Pulse(
        id = "Tinkers Mystcraft Compatibility",
        forced = true,
        modsRequired = "Mystcraft",
        pulsesRequired = "Tinkers' Smeltery")
public class TinkerMystcraft {

    @Handler
    public void init(FMLInitializationEvent event) {
        final Fluid[] fluids = new Fluid[] {
                // precious tinker fluids
                moltenGoldFluid, moltenSteelFluid, moltenEmeraldFluid, moltenQuartzFluid, moltenArditeFluid,
                moltenCobaltFluid,
                // all alloys
                pigIronFluid, moltenBronzeFluid, moltenAlumiteFluid, moltenAlubrassFluid, moltenManyullynFluid,
                // precious TE fluids
                moltenEnderFluid, moltenSilverFluid, moltenInvarFluid, moltenElectrumFluid, moltenShinyFluid,
                moltenSignalumFluid, moltenLumiumFluid, moltenMithrilFluid, moltenEnderiumFluid };

        log.info("Mystcraft detected. Blacklisting Mystcraft fluid symbols.");
        for (Fluid fluid : fluids) {
            if (fluid == null) continue;
            FMLInterModComms.sendMessage("Mystcraft", "blacklistfluid", fluid.getName());
        }
    }
}
