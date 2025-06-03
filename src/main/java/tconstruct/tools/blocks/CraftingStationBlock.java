package tconstruct.tools.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mantle.blocks.abstracts.InventoryBlock;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.tools.ToolProxyCommon;
import tconstruct.tools.logic.CraftingStationLogic;
import tconstruct.tools.model.TableRender;

public class CraftingStationBlock extends InventoryBlock {

    public CraftingStationBlock(Material material) {
        super(material);
        this.setCreativeTab(TConstructRegistry.blockTab);
        this.setHardness(2f);
        this.setStepSound(Block.soundTypeWood);
    }

    /* Rendering */
    @Override
    public String[] getTextureNames() {
        return new String[] { "craftingstation_top", "craftingstation_side", "craftingstation_bottom" };
    }

    @Override
    public String getTextureDomain(int textureNameIndex) {
        return "tinker";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return icons[getTextureIndex(side)];
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
    public int getRenderType() {
        return TableRender.model;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        return true;
    }

    @Override
    public Integer getGui(World world, int x, int y, int z, EntityPlayer entityplayer) {
        return ToolProxyCommon.craftingStationID;
    }

    @Override
    public Object getModInstance() {
        return TConstruct.instance;
    }

    @Override
    public void getSubBlocks(Item id, CreativeTabs tab, List<ItemStack> list) {
        for (int iter = 0; iter < 1; iter++) {
            list.add(new ItemStack(id, 1, iter));
        }
    }

    @Override
    public TileEntity createNewTileEntity(World arg0, int arg1) {
        return new CraftingStationLogic();
    }
}
