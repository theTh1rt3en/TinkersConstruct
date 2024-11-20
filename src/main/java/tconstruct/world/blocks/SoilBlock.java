package tconstruct.world.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import tconstruct.blocks.TConstructBlock;

public class SoilBlock extends TConstructBlock {

    static String[] soilTypes = new String[] { "slimesand", "grout", "slimesandblue", "graveyardsoil",
            "consecratedsoil", "slimedirt_blue", "nether_grout" };

    public SoilBlock() {
        super(Material.ground, 3.0F, soilTypes);
        this.setHarvestLevel("shovel", -1);
    }

    @Override
    public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
        if (entity instanceof EntityLivingBase
                && ((EntityLivingBase) entity).getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
            int metadata = world.getBlockMetadata(x, y, z);
            if (metadata == 3) {
                ((EntityLivingBase) entity).heal(1);
            } else if (metadata == 4) {
                entity.attackEntityFrom(DamageSource.magic, 1);
                entity.setFire(1);
            }
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta < 3) {
            entity.motionX *= 0.4;
            entity.motionZ *= 0.4;
            if (meta != 1 && entity instanceof EntityLivingBase) {
                ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.weakness.id, 1));
                ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.jump.id, 1, 1));
            }
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return Blocks.soul_sand.getCollisionBoundingBoxFromPool(world, x, y, z);
    }
}
