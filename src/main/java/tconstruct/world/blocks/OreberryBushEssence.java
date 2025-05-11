package tconstruct.world.blocks;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class OreberryBushEssence extends OreberryBush implements IPlantable {

    public OreberryBushEssence(String[] textureNames, int meta, int sub, String[] oreTypes) {
        super(textureNames, meta, sub, oreTypes);
    }

    /* Bush growth */

    @Override
    public void updateTick(World world, int x, int y, int z, Random random1) {
        if (world.isRemote) {
            return;
        }

        if (random1.nextInt(20) == 0) {
            int meta = world.getBlockMetadata(x, y, z);
            if (world.getFullBlockLightValue(x, y, z) < 10 || meta % 4 == 1) {
                if (meta < 12) {
                    world.setBlock(x, y, z, this, meta + 4, 3);
                }
            }
        }
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return true;
    }
}
