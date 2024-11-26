package tconstruct.smeltery.itemblocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import mantle.blocks.abstracts.MultiItemBlock;

public class SearedTableItemBlock extends MultiItemBlock {

    public static final String[] blockTypes = { "Table", "Faucet", "Basin" };

    public SearedTableItemBlock(Block b) {
        super(b, "SearedBlock", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List p_77624_3_, boolean p_77624_4_) {
        String tooltip = switch (p_77624_1_.getItemDamage()) {
            case 0 -> "smeltery.castingtable.tooltip";
            case 1 -> "smeltery.castingfaucet.tooltip";
            case 2 -> "smeltery.castingbasin.tooltip";
            default -> "";
        };

        if (StatCollector.canTranslate(tooltip)) p_77624_3_.add(StatCollector.translateToLocal(tooltip));
    }
}
