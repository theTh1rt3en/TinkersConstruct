package tconstruct.armor;

import net.minecraft.client.Minecraft;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ArmorTickHandler {

    private final Minecraft mc = Minecraft.getMinecraft();

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void tickEnd(ClientTickEvent event) {
        if (mc.thePlayer != null && mc.thePlayer.onGround) ArmorProxyClient.controlInstance.landOnGround();
    }
}
