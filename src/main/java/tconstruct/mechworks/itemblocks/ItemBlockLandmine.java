package tconstruct.mechworks.itemblocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import tconstruct.mechworks.TinkerMechworks;
import tconstruct.mechworks.logic.TileEntityLandmine;

/**
 *
 * @author fuj1n
 *
 */
public class ItemBlockLandmine extends ItemBlock {

    public ItemBlockLandmine(Block b) {
        super(b);
        this.setHasSubtypes(true);
    }

    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        String interaction = switch (par1ItemStack.getItemDamage()) {
            case 0 -> StatCollector.translateToLocal("landmine1.tooltip");
            case 1 -> StatCollector.translateToLocal("landmine2.tooltip");
            case 2 -> StatCollector.translateToLocal("landmine3.tooltip");
            default -> StatCollector.translateToLocal("landmine4.tooltip");
        };

        par3List.add(StatCollector.translateToLocal("landmine5.tooltip") + interaction);
    }

    @Override
    public int getMetadata(int par1) {
        return 0;
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
            float hitX, float hitY, float hitZ, int metadata) {
        if (!world.setBlock(x, y, z, TinkerMechworks.landmine, metadata, 3)) {
            return false;
        }

        if (world.getBlock(x, y, z) == TinkerMechworks.landmine) {
            TinkerMechworks.landmine.onBlockPlacedBy(world, x, y, z, player, stack);

            TileEntityLandmine te = (TileEntityLandmine) world.getTileEntity(x, y, z);
            if (te == null) {
                te = (TileEntityLandmine) TinkerMechworks.landmine.createTileEntity(world, metadata);
            }

            te.triggerType = stack.getItemDamage();
            world.setTileEntity(x, y, z, te);

            TinkerMechworks.landmine.onPostBlockPlaced(world, x, y, z, metadata);
        }

        return true;
    }

    public static Random getRandom() {
        return itemRand;
    }
}
