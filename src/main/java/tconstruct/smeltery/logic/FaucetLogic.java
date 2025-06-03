package tconstruct.smeltery.logic;

import static tconstruct.util.Constants.LIQUID_UPDATE_AMOUNT;
import static tconstruct.util.Constants.LIQUID_VALUE_INGOT;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import mantle.blocks.iface.IActiveLogic;
import mantle.blocks.iface.IFacingLogic;

public class FaucetLogic extends TileEntity implements IFacingLogic, IActiveLogic, IFluidHandler {

    byte direction = 0;
    boolean active;
    public FluidStack liquid;
    public boolean hasRedstonePower = false;

    public boolean activateFaucet() {
        if (liquid == null && active) {
            int x = xCoord, z = zCoord;
            switch (getRenderDirection()) {
                case 2:
                    z++;
                    break;
                case 3:
                    z--;
                    break;
                case 4:
                    x++;
                    break;
                case 5:
                    x--;
                    break;
            }

            TileEntity drainte = worldObj.getTileEntity(x, yCoord, z);
            TileEntity tankte = worldObj.getTileEntity(xCoord, yCoord - 1, zCoord);

            if (drainte instanceof IFluidHandler && tankte instanceof IFluidHandler) {
                FluidStack templiquid = ((IFluidHandler) drainte).drain(getForgeDirection(), LIQUID_VALUE_INGOT, false);
                if (templiquid != null) {
                    int drained = ((IFluidHandler) tankte).fill(ForgeDirection.UP, templiquid, false);
                    if (drained > 0) {
                        liquid = ((IFluidHandler) drainte).drain(getForgeDirection(), drained, true);
                        ((IFluidHandler) tankte).fill(ForgeDirection.UP, liquid, true);
                        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void updateEntity() {
        if (liquid != null) {
            liquid.amount -= LIQUID_UPDATE_AMOUNT;
            if (liquid.amount <= 0) {
                liquid = null;
                if (!activateFaucet()) {
                    active = false;
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                }
            }
        }
    }

    @Override
    public byte getRenderDirection() {
        return direction;
    }

    @Override
    public ForgeDirection getForgeDirection() {
        return ForgeDirection.VALID_DIRECTIONS[direction];
    }

    @Override
    public void setDirection(int side) {
        if (side != 0 && side != 1) {
            direction = (byte) side;
        }
    }

    @Override
    public void setDirection(float yaw, float pitch, EntityLivingBase player) {
        if (direction > 1) return;

        int facing = MathHelper.floor_double((double) (yaw / 360) + 0.5D) & 3;
        switch (facing) {
            case 1 -> direction = 5;
            case 2 -> direction = 3;
            case 3 -> direction = 4;
            default -> direction = 2;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tags) {
        super.readFromNBT(tags);
        readCustomNBT(tags);
    }

    public void readCustomNBT(NBTTagCompound tags) {
        direction = tags.getByte("Direction");
        if (tags.getBoolean("hasLiquid")) {
            this.liquid = FluidStack.loadFluidStackFromNBT(tags.getCompoundTag("Fluid"));
        } else this.liquid = null;
    }

    @Override
    public void writeToNBT(NBTTagCompound tags) {
        super.writeToNBT(tags);
        writeCustomNBT(tags);
    }

    public void writeCustomNBT(NBTTagCompound tags) {
        tags.setByte("Direction", direction);
        tags.setBoolean("hasLiquid", liquid != null);
        if (liquid != null) {
            NBTTagCompound nbt = new NBTTagCompound();
            liquid.writeToNBT(nbt);
            tags.setTag("Fluid", nbt);
        }
    }

    /* Packets */
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeCustomNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readCustomNBT(packet.func_148857_g());
        worldObj.func_147479_m(xCoord, yCoord, zCoord);
    }

    @Override
    public boolean getActive() {
        return active;
    }

    @Override
    public void setActive(boolean flag) {
        if (!active) {
            active = true;
            active = activateFaucet();
        } else {
            active = false;
        }
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        // TODO Auto-generated method stub
        return null;
    }
}
