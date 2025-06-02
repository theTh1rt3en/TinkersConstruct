package tconstruct.plugins;

import static tconstruct.util.Reference.MOD_ID;

import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import lombok.extern.log4j.Log4j2;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.modifiers.armor.AModThaumicVision;
import tconstruct.world.TinkerWorld;
import thaumcraft.api.ItemApi;

@Log4j2(topic = MOD_ID)
@ObjectHolder(MOD_ID)
@Pulse(
        id = "Tinkers Thaumcraft Compatibility",
        description = "Tinkers Construct compatibility for Thaumcraft",
        modsRequired = "Thaumcraft",
        pulsesRequired = "Tinkers' World",
        forced = true)
public class TinkerThaumcraft {

    @Handler
    public void init(FMLInitializationEvent event) {
        log.info("Thaumcraft detected. Registering harvestables.");
        sendIMC();
        registerModifiers();
    }

    private void sendIMC() {
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TinkerWorld.oreBerry, 1, 12));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TinkerWorld.oreBerry, 1, 13));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TinkerWorld.oreBerry, 1, 14));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TinkerWorld.oreBerry, 1, 15));
        FMLInterModComms
                .sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TinkerWorld.oreBerrySecond, 1, 12));
        FMLInterModComms
                .sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TinkerWorld.oreBerrySecond, 1, 13));
    }

    private void registerModifiers() {
        ItemStack thaumometer = ItemApi.getItem("itemThaumometer", 0);
        if (thaumometer != null) {
            // Thaumometer Vision!
            ModifyBuilder.registerModifier(new AModThaumicVision(thaumometer));
        }
    }
}
