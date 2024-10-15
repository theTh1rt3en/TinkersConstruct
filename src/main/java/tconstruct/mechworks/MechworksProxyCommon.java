package tconstruct.mechworks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.IGuiHandler;
import tconstruct.mechworks.inventory.ContainerLandmine;
import tconstruct.mechworks.logic.TileEntityLandmine;
import tconstruct.proxy.TProxyServer;

public class MechworksProxyCommon implements IGuiHandler {

    public void initialize() {
        registerGuiHandler();
    }

    public static int drawbridgeID = 9;
    public static final int landmineID = 10;

    protected void registerGuiHandler() {
        TProxyServer.registerServerGuiHandler(landmineID, this);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == MechworksProxyCommon.landmineID) {
            return new ContainerLandmine(player, (TileEntityLandmine) world.getTileEntity(x, y, z));
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        // TODO Auto-generated method stub
        return null;
    }
}
