package tconstruct.achievements.items;

import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

import tconstruct.tools.TinkerTools;

public class PatternAchievement extends AbstractAchievement {

    private static final String KEY = "pattern";
    private static final int DISPLAY_ROW = 2;
    private static final int DISPLAY_COLUMN = 1;
    private static final ItemStack ITEM_STACK = new ItemStack(TinkerTools.blankPattern);
    private static final boolean IS_SPECIAL = false;

    public PatternAchievement(Achievement parent) {
        super(KEY, DISPLAY_ROW, DISPLAY_COLUMN, ITEM_STACK, IS_SPECIAL, parent);
    }
}
