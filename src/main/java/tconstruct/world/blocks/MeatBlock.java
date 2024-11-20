package tconstruct.world.blocks;

import static tconstruct.util.Reference.resource;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWood;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tconstruct.library.TConstructRegistry;

public class MeatBlock extends BlockWood {

    private IIcon[] icons;
    private final String[] textureNames = new String[] { "ham_skin", "ham_bone" };

    public MeatBlock() {
        this.setHardness(1.0F);
        this.setStepSound(Block.soundTypeWood);
        this.setCreativeTab(TConstructRegistry.blockTab);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata) {
        int tex = (metadata % 4) * 2;
        int orientation = metadata / 4;

        switch (orientation)
        // Ends of logs
        {
            case 0:
                if (side == 0 || side == 1) return icons[tex + 1];
                break;
            case 1:
                if (side == 4 || side == 5) return icons[tex + 1];
                break;
            case 2:
                if (side == 2 || side == 3) return icons[tex + 1];
                break;
        }

        return icons[tex];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i) {
            this.icons[i] = iconRegister.registerIcon(resource(textureNames[i]));
        }
    }

    @Override
    public Item getItemDropped(int par1, Random par2Random, int par3) {
        return new ItemStack(this).getItem();
    }

    /**
     * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
     */
    @Override
    public int onBlockPlaced(World par1World, int par2, int par3, int par4, int par5, float par6, float par7,
            float par8, int par9) {
        int j1 = par9 & 3;
        byte b0 = switch (par5) {
            case 2, 3 -> 8;
            case 4, 5 -> 4;
            default -> 0;
        };

        return j1 | b0;
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    @Override
    public int damageDropped(int par1) {
        return par1 & 3;
    }

    /**
     * returns a number between 0 and 3
     */
    public static int limitToValidMetadata(int par0) {
        return par0 & 3;
    }

    @Override
    protected ItemStack createStackedBlock(int par1) {
        return new ItemStack(this, 1, limitToValidMetadata(par1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item b, CreativeTabs par2CreativeTabs, List par3List) {
        for (int i = 0; i < icons.length / 2; i++) par3List.add(new ItemStack(b, 1, i));
    }

    @Override
    public boolean isBeaconBase(IBlockAccess worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ) {
        return true;
    }
}
