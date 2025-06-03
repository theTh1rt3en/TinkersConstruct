package tconstruct.tools.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mantle.blocks.abstracts.InventoryBlock;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.logic.PartBuilderLogic;
import tconstruct.tools.logic.PartChestLogic;
import tconstruct.tools.logic.PatternChestLogic;
import tconstruct.tools.logic.StencilTableLogic;
import tconstruct.tools.logic.ToolStationLogic;
import tconstruct.tools.model.TableRender;
import tconstruct.util.config.PHConstruct;

public class ToolStationBlock extends InventoryBlock {

    public ToolStationBlock(Material material) {
        super(material);
        this.setCreativeTab(TConstructRegistry.blockTab);
        this.setHardness(2f);
        this.setStepSound(Block.soundTypeWood);
    }

    /* Rendering */
    @Override
    public String[] getTextureNames() {
        return new String[] { "toolstation_top", "toolstation_side", "toolstation_bottom", "partbuilder_oak_top",
                "partbuilder_oak_side", "partbuilder_oak_bottom", "partbuilder_spruce_top", "partbuilder_spruce_side",
                "partbuilder_spruce_bottom", "partbuilder_birch_top", "partbuilder_birch_side",
                "partbuilder_birch_bottom", "partbuilder_jungle_top", "partbuilder_jungle_side",
                "partbuilder_jungle_bottom", "patternchest", "patternchest_side", "patternchest",
                "stenciltable_oak_top", "stenciltable_oak_side", "stenciltable_oak_bottom", "stenciltable_spruce_top",
                "stenciltable_spruce_side", "stenciltable_spruce_bottom", "stenciltable_birch_top",
                "stenciltable_birch_side", "stenciltable_birch_bottom", "stenciltable_jungle_top",
                "stenciltable_jungle_side", "stenciltable_jungle_bottom", "partchest", "partchest_side", "partchest" };
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (meta <= 4) {
            return icons[meta * 3 + getTextureIndex(side)];
        } else if (meta <= 9) {
            return icons[15 + getTextureIndex(side)];
        } else {
            return icons[meta * 3 + getTextureIndex(side) - 12];
        }
    }

    public int getTextureIndex(int side) {
        if (side == 0) return 2;
        if (side == 1) return 0;

        return 1;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return side == ForgeDirection.UP;
    }

    @Override
    public int getRenderType() {
        return TableRender.model;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        return true;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x, y, z);
        if (metadata == 5 || metadata == 14) return AxisAlignedBB.getBoundingBox(
                (double) x + this.minX,
                (double) y + this.minY,
                (double) z + this.minZ,
                (double) x + this.maxX,
                (double) y + this.maxY - 0.125,
                (double) z + this.maxZ);
        return AxisAlignedBB.getBoundingBox(
                (double) x + this.minX,
                (double) y + this.minY,
                (double) z + this.minZ,
                (double) x + this.maxX,
                (double) y + this.maxY,
                (double) z + this.maxZ);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return switch (metadata) {
            case 0 -> new ToolStationLogic();
            case 1, 3, 2, 4 -> new PartBuilderLogic();
            case 5, 9, 8, 7, 6 -> new PatternChestLogic();
            case 10, 13, 12, 11 -> new StencilTableLogic();
            case 14 -> new PartChestLogic();
            default -> null;
        };
    }

    @Override
    public Integer getGui(World world, int x, int y, int z, EntityPlayer entityplayer) {
        int md = world.getBlockMetadata(x, y, z);
        if (md == 0) return 0;
        else if (md < 5) return 1;
        else if (md < 10) return 2;
        else if (md == 14) return 6;
        else return 3;

        // return -1;
    }

    @Override
    public Object getModInstance() {
        return TConstruct.instance;
    }

    @Override
    public void getSubBlocks(Item id, CreativeTabs tab, List<ItemStack> list) {
        for (int iter = 0; iter < 6; iter++) {
            list.add(new ItemStack(id, 1, iter));
        }

        for (int iter = 10; iter < 14; iter++) {
            list.add(new ItemStack(id, 1, iter));
        }

        list.add(new ItemStack(id, 1, 14));
    }

    @Override
    public String getTextureDomain(int textureNameIndex) {
        return "tinker";
    }

    /* Keep pattern chest and part chest inventory */
    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        player.addExhaustion(0.025F);

        if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
            int meta = world.getBlockMetadata(x, y, z);
            if (meta >= 5 && meta <= 9) {
                ItemStack chest = new ItemStack(this, 1, 5);
                NBTTagCompound inventory = new NBTTagCompound();
                PatternChestLogic logic = (PatternChestLogic) world.getTileEntity(x, y, z);
                logic.writeInventoryToNBT(inventory);
                NBTTagCompound baseTag = new NBTTagCompound();
                baseTag.setTag("Inventory", inventory);
                chest.setTagCompound(baseTag);

                // remove content. This is necessary because otherwise the patterns would also spill into the world
                // we don't want to prevent that since that's the intended behaviour for explosions.
                for (int i = 0; i < logic.getSizeInventory(); i++) logic.setInventorySlotContents(i, null);

                // Spawn item
                if (!player.capabilities.isCreativeMode || player.isSneaking()) {
                    float f = 0.7F;
                    double d0 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                    double d1 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                    double d2 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                    EntityItem entityitem = new EntityItem(
                            world,
                            (double) x + d0,
                            (double) y + d1,
                            (double) z + d2,
                            chest);
                    entityitem.delayBeforeCanPickup = 10;
                    world.spawnEntityInWorld(entityitem);
                }
            } else if (meta == 14) {
                ItemStack chest = new ItemStack(this, 1, 14);
                NBTTagCompound inventory = new NBTTagCompound();
                PartChestLogic logic = (PartChestLogic) world.getTileEntity(x, y, z);
                logic.writeInventoryToNBT(inventory);
                NBTTagCompound baseTag = new NBTTagCompound();
                baseTag.setTag("Inventory", inventory);
                chest.setTagCompound(baseTag);

                // remove content. This is necessary because otherwise the parts would also spill into the world
                // we don't want to prevent that since that's the intended behaviour for explosions.
                for (int i = 0; i < logic.getSizeInventory(); i++) logic.setInventorySlotContents(i, null);

                // Spawn item
                if (!player.capabilities.isCreativeMode || player.isSneaking()) {
                    float f = 0.7F;
                    double d0 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                    double d1 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                    double d2 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                    EntityItem entityitem = new EntityItem(
                            world,
                            (double) x + d0,
                            (double) y + d1,
                            (double) z + d2,
                            chest);
                    entityitem.delayBeforeCanPickup = 10;
                    world.spawnEntityInWorld(entityitem);
                }
            }
        }
        return world.setBlockToAir(x, y, z);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta) {
        if ((meta < 5 || meta > 9) && meta != 14) super.harvestBlock(world, player, x, y, z, meta);
        // Do nothing
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack) {
        boolean keptInventory = false;
        if (stack.hasTagCompound()) {
            NBTTagCompound inventory = stack.getTagCompound().getCompoundTag("Inventory");
            TileEntity te = world.getTileEntity(x, y, z);
            if (inventory != null && te instanceof PatternChestLogic logic) {
                logic.readInventoryFromNBT(inventory);
                logic.xCoord = x;
                logic.yCoord = y;
                logic.zCoord = z;
                keptInventory = true;
            } else if (inventory != null && te instanceof PartChestLogic logic) {
                logic.readInventoryFromNBT(inventory);
                logic.xCoord = x;
                logic.yCoord = y;
                logic.zCoord = z;
                keptInventory = true;
            }
        }
        if (!keptInventory && PHConstruct.freePatterns) {
            int meta = world.getBlockMetadata(x, y, z);
            if (meta == 5) {
                PatternChestLogic logic = (PatternChestLogic) world.getTileEntity(x, y, z);
                for (int i = 1; i <= 13; i++) {
                    logic.setInventorySlotContents(i - 1, new ItemStack(TinkerTools.woodPattern, 1, i));
                }
                logic.setInventorySlotContents(13, new ItemStack(TinkerTools.woodPattern, 1, 22));
            }
        }
        super.onBlockPlacedBy(world, x, y, z, living, stack);
    }
}
