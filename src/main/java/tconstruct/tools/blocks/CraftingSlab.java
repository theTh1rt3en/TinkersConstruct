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
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mantle.blocks.abstracts.InventorySlab;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.ToolProxyCommon;
import tconstruct.tools.logic.CraftingStationLogic;
import tconstruct.tools.logic.PartBuilderLogic;
import tconstruct.tools.logic.PartChestLogic;
import tconstruct.tools.logic.PatternChestLogic;
import tconstruct.tools.logic.StencilTableLogic;
import tconstruct.tools.logic.ToolForgeLogic;
import tconstruct.tools.logic.ToolStationLogic;
import tconstruct.util.config.PHConstruct;

public class CraftingSlab extends InventorySlab {

    public CraftingSlab(Material material) {
        super(material);
        this.setCreativeTab(TConstructRegistry.blockTab);
        this.setHardness(2f);
        this.stepSound = Block.soundTypeWood;
    }

    /* Rendering */
    @Override
    public String[] getTextureNames() {
        return new String[] { "craftingstation_top", "craftingstation_slab_side", "craftingstation_bottom",
                "toolstation_top", "toolstation_slab_side", "toolstation_bottom", "partbuilder_oak_top",
                "partbuilder_slab_side", "partbuilder_oak_bottom", "stenciltable_oak_top", "stenciltable_slab_side",
                "stenciltable_oak_bottom", "patternchest", "patternchest_slab_side", "patternchest", "toolforge_top",
                "toolforge_slab_side", "toolforge_top", "partchest", "partchest_slab_side", "partchest" };
    }

    @Override
    public String getTextureDomain(int textureNameIndex) {
        return "tinker";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return icons[(meta % 8) * 3 + getTextureIndex(side)];
    }

    public int getTextureIndex(int side) {
        if (side == 0) return 2;
        if (side == 1) return 0;

        return 1;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x, y, z);
        if (metadata == 5) return AxisAlignedBB.getBoundingBox(
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
    public TileEntity createTileEntity(World world, int metadata) {
        return switch (metadata % 8) {
            case 0 -> new CraftingStationLogic();
            case 1 -> new ToolStationLogic();
            case 2 -> new PartBuilderLogic();
            case 3 -> new StencilTableLogic();
            case 4 -> new PatternChestLogic();
            case 5 -> new ToolForgeLogic();
            case 6 -> new PartChestLogic();
            default -> null;
        };
    }

    @Override
    public Integer getGui(World world, int x, int y, int z, EntityPlayer entityplayer) {
        int meta = world.getBlockMetadata(x, y, z) % 8;
        return switch (meta) {
            case 0 -> ToolProxyCommon.craftingStationID;
            case 1 -> ToolProxyCommon.toolStationID;
            case 2 -> ToolProxyCommon.partBuilderID;
            case 3 -> ToolProxyCommon.stencilTableID;
            case 4 -> ToolProxyCommon.patternChestID;
            case 5 -> ToolProxyCommon.toolForgeID;
            case 6 -> ToolProxyCommon.partChestID;
            default -> -1;
        };
    }

    @Override
    public Object getModInstance() {
        return TConstruct.instance;
    }

    @Override
    public void getSubBlocks(Item b, CreativeTabs tab, List<ItemStack> list) {
        for (int iter = 0; iter < 7; iter++) {
            list.add(new ItemStack(b, 1, iter));
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack stack) {
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
            if (meta == 4) {
                PatternChestLogic logic = (PatternChestLogic) world.getTileEntity(x, y, z);
                for (int i = 1; i <= 13; i++) {
                    logic.setInventorySlotContents(i - 1, new ItemStack(TinkerTools.woodPattern, 1, i));
                }
                logic.setInventorySlotContents(13, new ItemStack(TinkerTools.woodPattern, 1, 22));
            }
        }
        super.onBlockPlacedBy(world, x, y, z, entityliving, stack);
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int metadata) {
        return switch (metadata % 8) {
            case 0 -> new CraftingStationLogic();
            case 1 -> new ToolStationLogic();
            case 2 -> new PartBuilderLogic();
            case 3 -> new StencilTableLogic();
            case 4 -> new PatternChestLogic();
            case 5 -> new ToolForgeLogic();
            case 6 -> new PartChestLogic();
            default -> null;
        };
    }

    /* Keep pattern chest inventory */
    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        player.addExhaustion(0.025F);

        if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
            int meta = world.getBlockMetadata(x, y, z);
            if (meta == 4) {
                ItemStack chest = new ItemStack(this, 1, 4);
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
            } else if (meta == 6) {
                ItemStack chest = new ItemStack(this, 1, 6);
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
        if (meta != 4 && meta != 6) super.harvestBlock(world, player, x, y, z, meta);
        // Do nothing
    }
}
