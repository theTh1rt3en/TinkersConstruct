package tconstruct.tools;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.IGuiHandler;
import mantle.blocks.abstracts.InventoryLogic;
import tconstruct.proxy.TProxyServer;

public class ToolProxyCommon implements IGuiHandler {

    public static final int toolStationID = 0;
    public static final int partBuilderID = 1;
    public static final int patternChestID = 2;
    public static final int stencilTableID = 3;
    public static final int frypanGuiID = 4;
    public static final int toolForgeID = 5;
    public static final int furnaceID = 8;
    public static final int craftingStationID = 11;
    public static final int battlesignTextID = 12;

    public ToolProxyCommon() {}

    public void initialize() {
        registerGuiHandler();
    }

    protected void registerGuiHandler() {
        TProxyServer.registerServerGuiHandler(toolStationID, this);
        TProxyServer.registerServerGuiHandler(partBuilderID, this);
        TProxyServer.registerServerGuiHandler(patternChestID, this);
        TProxyServer.registerServerGuiHandler(stencilTableID, this);
        TProxyServer.registerServerGuiHandler(frypanGuiID, this);
        TProxyServer.registerServerGuiHandler(toolForgeID, this);
        TProxyServer.registerServerGuiHandler(furnaceID, this);
        TProxyServer.registerServerGuiHandler(craftingStationID, this);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof InventoryLogic) {
            return ((InventoryLogic) tile).getGuiContainer(player.inventory, world, x, y, z);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }
}
