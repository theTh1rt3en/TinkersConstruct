package tconstruct.proxy;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.IGuiHandler;

public abstract class TProxyCommon implements TProxy {

    private static final HashMap<Integer, IGuiHandler> serverGuiHandlers = new HashMap<>();
    private static final HashMap<Integer, IGuiHandler> clientGuiHandlers = new HashMap<>();

    public static void registerServerGuiHandler(int gui, IGuiHandler handler) {
        serverGuiHandlers.put(gui, handler);
    }

    public static void registerClientGuiHandler(int gui, IGuiHandler handler) {
        clientGuiHandlers.put(gui, handler);
    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        IGuiHandler handler = serverGuiHandlers.get(id);
        if (handler != null) return handler.getServerGuiElement(id, player, world, x, y, z);
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        IGuiHandler handler = clientGuiHandlers.get(id);
        if (handler != null) return handler.getClientGuiElement(id, player, world, x, y, z);
        return null;
    }
}
