package tconstruct.smeltery.logic;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import mantle.blocks.abstracts.MultiServantLogic;
import mantle.blocks.iface.IFacingLogic;
import mantle.world.CoordTuple;

public class SmelteryDrainLogic extends MultiServantLogic implements IFluidHandler, IFacingLogic {

    byte direction;

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (hasValidMaster() && resource != null && canFill(from, resource.getFluid())) {
            SmelteryLogic smeltery = (SmelteryLogic) worldObj
                    .getTileEntity(getMasterPosition().x, getMasterPosition().y, getMasterPosition().z);
            return smeltery.fill(resource, doFill);
        } else {
            return 0;
        }
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        if (hasValidMaster() && canDrain(from, null)) {
            SmelteryLogic smeltery = (SmelteryLogic) worldObj
                    .getTileEntity(getMasterPosition().x, getMasterPosition().y, getMasterPosition().z);
            return smeltery.drain(maxDrain, doDrain);
        }
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (hasValidMaster() && canDrain(from, resource.getFluid())) {
            SmelteryLogic smeltery = (SmelteryLogic) worldObj
                    .getTileEntity(getMasterPosition().x, getMasterPosition().y, getMasterPosition().z);
            if (resource.getFluid() == smeltery.getFluid().getFluid()) {
                return smeltery.drain(resource.amount, doDrain);
            }
        }
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return true;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        // Check that the drain is coming from the front of the block
        // and that the fluid to be drained is in the smeltery.
        if (!hasValidMaster()) return false;

        boolean containsFluid = fluid == null;
        if (fluid != null) {
            SmelteryLogic smeltery = (SmelteryLogic) worldObj
                    .getTileEntity(getMasterPosition().x, getMasterPosition().y, getMasterPosition().z);
            for (FluidStack fstack : smeltery.moltenMetal) {
                if (fstack.getFluidID() == fluid.getID()) {
                    containsFluid = true;
                    break;
                }
            }
        }
        return containsFluid;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        if (hasValidMaster() && (from == getForgeDirection() || from == getForgeDirection().getOpposite()
                || from == ForgeDirection.UNKNOWN)) {
            SmelteryLogic smeltery = (SmelteryLogic) worldObj
                    .getTileEntity(getMasterPosition().x, getMasterPosition().y, getMasterPosition().z);
            return smeltery.getMultiTankInfo();
        }
        return null;
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
    public void setDirection(int side) {}

    @Override
    public void setDirection(float yaw, float pitch, EntityLivingBase player) {
        if (pitch > 45) direction = 1;
        else if (pitch < -45) direction = 0;
        else {
            int facing = MathHelper.floor_double((yaw / 360) + 0.5D) & 3;
            switch (facing) {
                case 0 -> direction = 2;
                case 1 -> direction = 5;
                case 2 -> direction = 3;
                case 3 -> direction = 4;
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tags) {
        super.readFromNBT(tags);
        direction = tags.getByte("Direction");
    }

    @Override
    public void writeToNBT(NBTTagCompound tags) {
        super.writeToNBT(tags);
        tags.setByte("Direction", direction);
    }

    /* Packets */
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readFromNBT(packet.func_148857_g());
        worldObj.func_147479_m(xCoord, yCoord, zCoord);
    }

    public int comparatorStrength() {
        CoordTuple master = this.getMasterPosition();
        // invalid smeltery
        if (master == null) return 0;
        SmelteryLogic smeltery = (SmelteryLogic) worldObj.getTileEntity(master.x, master.y, master.z);

        // this can happen when the comparator checks its strength before the drain is getting updated on a broken
        // master (smeltery controller)
        if (smeltery == null) return 0;

        if (smeltery.maxLiquid == 0) return 0;

        return MathHelper.ceiling_float_int(15f * smeltery.currentLiquid / smeltery.maxLiquid);
    }
}
