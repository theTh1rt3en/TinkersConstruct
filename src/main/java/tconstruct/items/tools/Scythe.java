package tconstruct.items.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

import cpw.mods.fml.client.FMLClientHandler;
import tconstruct.library.ActiveToolMod;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.Weapon;
import tconstruct.tools.TinkerTools;

public class Scythe extends Weapon {

    static Material[] materials = new Material[] { Material.web, Material.cactus, Material.plants, Material.leaves,
            Material.vine, Material.gourd };

    public Scythe() {
        super(4);
        this.setUnlocalizedName("InfiTool.Scythe");
    }

    @Override
    protected Material[] getEffectiveMaterials() {
        return materials;
    }

    @Override
    public Item getHeadItem() {
        return TinkerTools.scytheBlade;
    }

    @Override
    public Item getHandleItem() {
        return TinkerTools.toughRod;
    }

    @Override
    public Item getAccessoryItem() {
        return TinkerTools.toughBinding;
    }

    @Override
    public Item getExtraItem() {
        return TinkerTools.toughRod;
    }

    @Override
    public int getPartAmount() {
        return 4;
    }

    @Override
    public String getIconSuffix(int partType) {
        return switch (partType) {
            case 0 -> "_scythe_head";
            case 1 -> "_scythe_head_broken";
            case 2 -> "_scythe_handle";
            case 3 -> "_scythe_binding";
            case 4 -> "_scythe_accessory";
            default -> "";
        };
    }

    @Override
    public float getDurabilityModifier() {
        return 3.0f;
    }

    @Override
    public float getRepairCost() {
        return 4.0f;
    }

    @Override
    public String getEffectSuffix() {
        return "_scythe_effect";
    }

    @Override
    public String getDefaultFolder() {
        return "scythe";
    }

    @Override
    public int durabilityTypeAccessory() {
        return 1;
    }

    @Override
    public int durabilityTypeExtra() {
        return 1;
    }

    @Override
    public float getDamageModifier() {
        return 0.75f;
    }

    @Override
    public String[] getTraits() {
        return new String[] { "weapon", "melee", "harvest" };
    }

    /* Scythe Specific */

    @Override
    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        if (!stack.hasTagCompound()) return false;

        World world = player.worldObj;
        final Block blockB = world.getBlock(x, y, z);
        final int meta = world.getBlockMetadata(x, y, z);
        if (!stack.hasTagCompound()) return false;
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        boolean butter = EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, stack) > 0;
        int fortune = EnchantmentHelper.getFortuneModifier(player);
        for (int xPos = x - 1; xPos <= x + 1; xPos++) {
            for (int yPos = y - 1; yPos <= y + 1; yPos++) {
                for (int zPos = z - 1; zPos <= z + 1; zPos++) {
                    if (!(tags.getBoolean("Broken"))) {
                        boolean cancelHarvest = false;
                        for (ActiveToolMod mod : TConstructRegistry.activeModifiers) {
                            if (mod.beforeBlockBreak(this, stack, xPos, yPos, zPos, player)) cancelHarvest = true;
                        }

                        if (!cancelHarvest) {
                            Block localBlock = world.getBlock(xPos, yPos, zPos);
                            int localMeta = world.getBlockMetadata(xPos, yPos, zPos);
                            float localHardness = localBlock == null ? Float.MAX_VALUE
                                    : localBlock.getBlockHardness(world, xPos, yPos, zPos);
                            if (localBlock != null) {
                                for (Material material : materials) {
                                    if (material == localBlock.getMaterial()) {
                                        if (!player.capabilities.isCreativeMode) {
                                            if (butter && localBlock instanceof IShearable
                                                    && ((IShearable) localBlock)
                                                            .isShearable(stack, player.worldObj, xPos, yPos, zPos)) {
                                                ArrayList<ItemStack> drops = ((IShearable) localBlock).onSheared(
                                                        stack,
                                                        player.worldObj,
                                                        xPos,
                                                        yPos,
                                                        zPos,
                                                        EnchantmentHelper.getEnchantmentLevel(
                                                                Enchantment.fortune.effectId,
                                                                stack));
                                                Random rand = new Random();

                                                if (!world.isRemote) for (ItemStack dropStack : drops) {
                                                    float f = 0.7F;
                                                    double d = (rand.nextFloat() * f) + (1.0F - f) * 0.5D;
                                                    double d1 = (rand.nextFloat() * f) + (1.0F - f) * 0.5D;
                                                    double d2 = (rand.nextFloat() * f) + (1.0F - f) * 0.5D;
                                                    EntityItem entityitem = new EntityItem(
                                                            player.worldObj,
                                                            xPos + d,
                                                            yPos + d1,
                                                            zPos + d2,
                                                            dropStack);
                                                    entityitem.delayBeforeCanPickup = 10;
                                                    player.worldObj.spawnEntityInWorld(entityitem);
                                                }

                                                if (localHardness > 0f) onBlockDestroyed(
                                                        stack,
                                                        world,
                                                        localBlock,
                                                        xPos,
                                                        yPos,
                                                        zPos,
                                                        player);
                                                player.addStat(
                                                        StatList.mineBlockStatArray[Block.getIdFromBlock(localBlock)],
                                                        1);
                                                world.setBlockToAir(xPos, yPos, zPos);
                                            } else {

                                                // Workaround for dropping experience
                                                int exp = localBlock.getExpDrop(world, localMeta, fortune);

                                                localBlock.onBlockHarvested(world, xPos, yPos, zPos, localMeta, player);
                                                if (localBlock.removedByPlayer(world, player, xPos, yPos, zPos, true)) {
                                                    localBlock.onBlockDestroyedByPlayer(
                                                            world,
                                                            xPos,
                                                            yPos,
                                                            zPos,
                                                            localMeta);
                                                    localBlock.harvestBlock(world, player, xPos, yPos, zPos, localMeta);
                                                    // Workaround for dropping experience
                                                    if (!butter)
                                                        localBlock.dropXpOnBlockBreak(world, xPos, yPos, zPos, exp);
                                                }

                                                if (world.isRemote) {
                                                    INetHandler handler = FMLClientHandler.instance()
                                                            .getClientPlayHandler();
                                                    if (handler instanceof NetHandlerPlayClient handlerClient) {
                                                        handlerClient.addToSendQueue(
                                                                new C07PacketPlayerDigging(
                                                                        0,
                                                                        x,
                                                                        y,
                                                                        z,
                                                                        Minecraft
                                                                                .getMinecraft().objectMouseOver.sideHit));
                                                        handlerClient.addToSendQueue(
                                                                new C07PacketPlayerDigging(
                                                                        2,
                                                                        x,
                                                                        y,
                                                                        z,
                                                                        Minecraft
                                                                                .getMinecraft().objectMouseOver.sideHit));
                                                    }
                                                }

                                                if (localHardness > 0f) onBlockDestroyed(
                                                        stack,
                                                        world,
                                                        localBlock,
                                                        xPos,
                                                        yPos,
                                                        zPos,
                                                        player);
                                            }
                                        } else {
                                            world.setBlockToAir(xPos, yPos, zPos);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!world.isRemote) world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(blockB) + (meta << 12));
        return super.onBlockStartBreak(stack, x, y, z, player);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
                entity.posX,
                entity.posY,
                entity.posZ,
                entity.posX + 1.0D,
                entity.posY + 1.0D,
                entity.posZ + 1.0D).expand(1.0D, 1.0D, 1.0D);
        List<Entity> list = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, box);
        for (Entity e : list) {
            AbilityHelper.onLeftClickEntity(stack, player, e, this);
        }
        return true;
    }
}
