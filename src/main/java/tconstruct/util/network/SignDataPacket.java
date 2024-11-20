package tconstruct.util.network;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import mantle.common.network.AbstractPacket;
import tconstruct.TConstruct;
import tconstruct.tools.logic.BattlesignLogic;

public class SignDataPacket extends AbstractPacket {

    private int dimension, x, y, z, length;
    private String[] text;

    public SignDataPacket(int dimension, int x, int y, int z, String[] text) {
        this.text = text;
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
        this.length = text.length;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        buffer.writeInt(dimension);
        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
        buffer.writeInt(length);
        for (int i = 0; i < length; i++) {
            ByteBufUtils.writeUTF8String(buffer, text[i]);
        }
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        dimension = buffer.readInt();
        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
        length = buffer.readInt();
        text = new String[length];
        for (int i = 0; i < length; i++) {
            text[i] = ByteBufUtils.readUTF8String(buffer);
        }
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        if (player.worldObj.getTileEntity(x, y, z) instanceof BattlesignLogic logic) {
            logic.setText(text);
        }
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
        if (player.worldObj.blockExists(x, y, z)) {
            return;
        }

        if (player.worldObj.getTileEntity(x, y, z) instanceof BattlesignLogic logic) {
            logic.setText(text);
        }

        TConstruct.packetPipeline.sendToDimension(new SignDataPacket(dimension, x, y, z, text), dimension);
    }
}
