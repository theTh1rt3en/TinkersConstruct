package tconstruct.world.blocks;

import static tconstruct.util.Reference.resource;

import net.minecraft.block.BlockLadder;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class StoneLadder extends BlockLadder {

    private IIcon icon;

    // Use the normally protected constructor
    public StoneLadder() {
        super();
        this.setBlockName("decoration.stoneladder");
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public void registerBlockIcons(IIconRegister register) {
        icon = register.registerIcon(resource("ladder_stone"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int meta, int side) {
        return icon;
    }
}
