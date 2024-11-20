package tconstruct.achievements.items;

import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

import tconstruct.tools.TinkerTools;

public class TinkererAchievement extends AbstractAchievement {

    private static final String KEY = "tinkerer";
    private static final int DISPLAY_ROW = 2;
    private static final int DISPLAY_COLUMN = 2;
    private static final ItemStack ITEM_STACK = new ItemStack(TinkerTools.titleIcon, 1, 4096);
    private static final boolean IS_SPECIAL = false;

    public TinkererAchievement(Achievement parent) {
        super(KEY, DISPLAY_ROW, DISPLAY_COLUMN, ITEM_STACK, IS_SPECIAL, parent);
    }
}
