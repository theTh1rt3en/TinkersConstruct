package tconstruct.achievements.items;

import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

import tconstruct.tools.TinkerTools;

public class DualConvenienceAchievement extends AbstractAchievement {

    private static final String KEY = "dualConvenience";
    private static final int DISPLAY_ROW = 0;
    private static final int DISPLAY_COLUMN = 7;
    private static final ItemStack ITEM_STACK = new ItemStack(TinkerTools.titleIcon, 1, 4100);
    private static final boolean IS_SPECIAL = true;

    public DualConvenienceAchievement(Achievement parent) {
        super(KEY, DISPLAY_ROW, DISPLAY_COLUMN, ITEM_STACK, IS_SPECIAL, parent);
    }
}
