package tconstruct.plugins.imc;

import static tconstruct.util.Reference.MOD_ID;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import lombok.extern.log4j.Log4j2;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import tconstruct.smeltery.TinkerSmeltery;

@Log4j2(topic = MOD_ID)
@Pulse(
        id = "Tinkers BuildCraft Compatibility",
        description = "Tinkers Construct compatibility for BC Transport",
        modsRequired = "BuildCraft|Transport",
        forced = true)
public class TinkerBuildCraft {

    @Handler
    public void init(FMLInitializationEvent event) {
        log.info("BuildCraft detected. Registering facades.");
        // Smeltery Blocks
        addFacade(TinkerSmeltery.smeltery, 2);
        addFacade(TinkerSmeltery.smelteryNether, 2);
        for (int sc = 4; sc < 11; sc++) {
            addFacade(TinkerSmeltery.smeltery, sc);
            addFacade(TinkerSmeltery.smelteryNether, sc);
        }

        addFacade(TinkerSmeltery.searedBlock, 0);
        addFacade(TinkerSmeltery.searedBlockNether, 0);
    }

    private void addFacade(Block b, int meta) {
        FMLInterModComms.sendMessage("BuildCraft|Transport", "add-facade", new ItemStack(b, 1, meta));
    }
}
