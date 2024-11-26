package tconstruct.tools.entity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import tconstruct.library.tools.AbilityHelper;

@Deprecated
public class RotatingBase extends Entity implements IEntityAdditionalSpawnData {

    public RotatingBase(World world) {
        super(world);
        texID = 0;
        tex2ID = 16 * 3;
        returnStackSlot = -1;
        hasHitGround = false;
        hasHitMob = false;
        damageDealt = 4;
        maxGroundTicks = 2400;
        xTile = -1;
        yTile = -1;
        zTile = -1;
        inTile = Blocks.air;
        onGround = false;
        arrowShake = 0;
        ticksInAir = 0;
        setSize(0.5F, 0.5F);
    }

    @Override
    protected void entityInit() {}

    public void damageItem(int i, boolean flag) {
        if (!flag) {
            return;
        }
        if (returnStack.stackSize < 1) {
            for (int j = 0; j < 8; j++) {
                worldObj.spawnParticle("snowballpoof", posX, posY, posZ, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    public void onHit(MovingObjectPosition movingobjectposition) {}

    @Override
    public void setVelocity(double d, double d1, double d2) {
        motionX = d;
        motionY = d1;
        motionZ = d2;
        if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt_double(d * d + d2 * d2);
            prevRotationYaw = rotationYaw = (float) ((Math.atan2(d, d2) * 180D) / 3.1415927410125732D);
            prevRotationPitch = rotationPitch = (float) ((Math.atan2(d1, f) * 180D) / 3.1415927410125732D);
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
            prevRotationYaw = rotationYaw = (float) ((Math.atan2(motionX, motionZ) * 180D) / Math.PI);
            prevRotationPitch = rotationPitch = (float) ((Math.atan2(motionY, f) * 180D) / Math.PI);
        }
        if (arrowShake > 0) {
            arrowShake--;
        }
        if (onGround) {
            Block i = worldObj.getBlock(xTile, yTile, zTile);
            if (i != inTile) {
                onGround = false;
                motionX *= rand.nextFloat() * 0.2F;
                motionY *= rand.nextFloat() * 0.2F;
                motionZ *= rand.nextFloat() * 0.2F;
                ticksInGround = 0;
                ticksInAir = 0;
            } else {
                if (!hasHitGround) {
                    hasHitGround = true;
                    damageItem(1, true);
                }
            }
            return;
        } else {
            ticksInAir++;
        }
        Vec3 vec3d = Vec3.createVectorHelper(posX, posY, posZ);
        Vec3 vec3d1 = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ);
        MovingObjectPosition movingobjectposition = worldObj.rayTraceBlocks(vec3d, vec3d1);
        vec3d = Vec3.createVectorHelper(posX, posY, posZ);
        vec3d1 = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ);
        if (movingobjectposition != null) {
            vec3d1 = Vec3.createVectorHelper(
                    movingobjectposition.hitVec.xCoord,
                    movingobjectposition.hitVec.yCoord,
                    movingobjectposition.hitVec.zCoord);
        }
        Entity entity = null;
        List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(
                this,
                boundingBox.addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));
        double d = 0.0D;
        for (Entity entity1 : list) {
            if (!entity1.canBeCollidedWith() || entity1 == owner && ticksInAir < 5) {
                continue;
            }
            float f3 = 0.3F;
            AxisAlignedBB axisalignedbb = entity1.boundingBox.expand(f3, f3, f3);
            MovingObjectPosition movingobjectposition2 = axisalignedbb.calculateIntercept(vec3d, vec3d1);
            if (movingobjectposition2 == null) {
                continue;
            }
            double d1 = vec3d.distanceTo(movingobjectposition2.hitVec);
            if (d1 < d || d == 0.0D) {
                entity = entity1;
                d = d1;
            }
        }

        if (entity != null) {
            MovingObjectPosition movingobjectposition1 = new MovingObjectPosition(entity);
            onHit(movingobjectposition1);
        }
        if (movingobjectposition != null) {
            onHit(movingobjectposition);
        }
        posX += motionX;
        posY += motionY;
        posZ += motionZ;
        float f1 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
        rotationYaw = (float) ((Math.atan2(motionX, motionZ) * 180D) / 3.1415927410125732D);
        rotationPitch = (float) ((Math.atan2(motionY, f1) * 180D) / 3.1415927410125732D);
        while (rotationPitch - prevRotationPitch < -180F) {
            prevRotationPitch -= 360F;
        }
        while (rotationPitch - prevRotationPitch >= 180F) {
            prevRotationPitch += 360F;
        }
        while (rotationYaw - prevRotationYaw < -180F) {
            prevRotationYaw -= 360F;
        }
        while (rotationYaw - prevRotationYaw >= 180F) {
            prevRotationYaw += 360F;
        }
        rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F;
        rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F;
        float f2 = 0.99F;
        float f4 = 0.03F;
        if (isInWater()) {
            for (int k = 0; k < 4; k++) {
                float f5 = 0.25F;
                worldObj.spawnParticle(
                        "bubble",
                        posX - motionX * (double) f5,
                        posY - motionY * (double) f5,
                        posZ - motionZ * (double) f5,
                        motionX,
                        motionY,
                        motionZ);
            }

            f2 = 0.8F;
        }
        motionX *= f2;
        motionY *= f2;
        motionZ *= f2;
        motionY -= f4;
        setPosition(posX, posY, posZ);
        if (!onGround) {
            prevBoomerangRotation = boomerangRotation;
            boomerangRotation += 36F;
            while (boomerangRotation > 360F) {
                boomerangRotation -= 360F;
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tags) {
        tags.setTag("Throwable", this.returnStack.writeToNBT(new NBTTagCompound()));
        tags.setShort("xTile", (short) xTile);
        tags.setShort("yTile", (short) yTile);
        tags.setShort("zTile", (short) zTile);
        tags.setString("inTile", inTile.getUnlocalizedName());
        tags.setByte("shake", (byte) arrowShake);
        tags.setByte("onGround", (byte) (onGround ? 1 : 0));
        tags.setBoolean("Retrieval", doNotRetrieve);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tags) {
        this.returnStack = ItemStack.loadItemStackFromNBT(tags.getCompoundTag("Throwable"));
        xTile = tags.getShort("xTile");
        yTile = tags.getShort("yTile");
        zTile = tags.getShort("zTile");
        arrowShake = tags.getByte("shake") & 0xff;
        onGround = tags.getByte("onGround") == 1;
        doNotRetrieve = tags.getBoolean("Retrieval");
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer entityplayer) {
        onCollideWithPlayer(entityplayer, true);
    }

    public void onCollideWithPlayer(EntityPlayer entityplayer, boolean flag) {
        if (returnStack == null || returnStack.stackSize == 0 || worldObj.isRemote) {
            return;
        }
        if (!flag || onGround && arrowShake <= 0) {
            if (!flag || returnsTo != null && !returnsTo.isDead && returnsTo != entityplayer) {
                return;
            }
            if (!doNotRetrieve) AbilityHelper.addToInv(entityplayer, returnStack, true);
            worldObj.playSoundAtEntity(
                    this,
                    "random.pop",
                    0.2F,
                    ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            entityplayer.onItemPickup(this, 1);
            kill();
        }
    }

    @Override
    public float getShadowSize() {
        return 0.0F;
    }

    public ItemStack getEntityItem() {
        return returnStack;
    }

    public int age = 0;
    public int texID;
    public int tex2ID;
    public ItemStack returnStack;
    public int returnStackSlot;
    public boolean hasHitGround;
    public boolean hasHitMob;
    public float prevBoomerangRotation;
    public float boomerangRotation;
    public Entity returnsTo;
    public int damageDealt;
    public int maxGroundTicks;
    protected int xTile;
    protected int yTile;
    protected int zTile;
    protected Block inTile;
    public int arrowShake;
    public EntityPlayer owner;
    protected int ticksInGround;
    protected int ticksInAir;
    public boolean doNotRetrieve;

    @Override
    public void writeSpawnData(ByteBuf data) {
        NBTTagCompound tags = returnStack.getTagCompound().getCompoundTag("InfiTool");
        ByteBufUtils.writeItemStack(data, returnStack);
        data.writeFloat(rotationYaw);
        data.writeInt(tags.getInteger("RenderHandle"));
        data.writeInt(tags.getInteger("RenderHead"));
        data.writeInt(tags.getInteger("RenderAccessory"));
        data.writeInt(tags.getInteger("RenderExtra"));

        int effects = 0;
        if (tags.hasKey("Effect1")) effects++;
        if (tags.hasKey("Effect2")) effects++;
        if (tags.hasKey("Effect3")) effects++;
        if (tags.hasKey("Effect4")) effects++;
        if (tags.hasKey("Effect5")) effects++;
        if (tags.hasKey("Effect6")) effects++;
        data.writeInt(effects);

        switch (effects) {
            case 6:
                data.writeInt(tags.getInteger("Effect6"));
            case 5:
                data.writeInt(tags.getInteger("Effect5"));
            case 4:
                data.writeInt(tags.getInteger("Effect4"));
            case 3:
                data.writeInt(tags.getInteger("Effect3"));
            case 2:
                data.writeInt(tags.getInteger("Effect2"));
            case 1:
                data.writeInt(tags.getInteger("Effect1"));
        }
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        returnStack = ByteBufUtils.readItemStack(data);
        rotationYaw = data.readFloat();
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagCompound toolTag = new NBTTagCompound();
        toolTag.setInteger("RenderHandle", data.readInt());
        toolTag.setInteger("RenderHead", data.readInt());
        toolTag.setInteger("RenderAccessory", data.readInt());
        toolTag.setInteger("RenderExtra", data.readInt());

        switch (data.readInt()) {
            case 6:
                toolTag.setInteger("Effect6", data.readInt());
            case 5:
                toolTag.setInteger("Effect5", data.readInt());
            case 4:
                toolTag.setInteger("Effect4", data.readInt());
            case 3:
                toolTag.setInteger("Effect3", data.readInt());
            case 2:
                toolTag.setInteger("Effect2", data.readInt());
            case 1:
                toolTag.setInteger("Effect1", data.readInt());
        }
        compound.setTag("InfiTool", toolTag);
        returnStack.setTagCompound(compound);
    }
}
