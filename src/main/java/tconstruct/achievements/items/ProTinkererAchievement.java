package tconstruct.achievements.items;

import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

import tconstruct.tools.TinkerTools;

public class ProTinkererAchievement extends AbstractAchievement {

    private static final String KEY = "proTinkerer";
    private static final int DISPLAY_ROW = 4;
    private static final int DISPLAY_COLUMN = 3;
    private static final ItemStack ITEM_STACK = new ItemStack(TinkerTools.titleIcon, 1, 4098);
    private static final boolean IS_SPECIAL = false;

    public ProTinkererAchievement(Achievement parent) {
        super(KEY, DISPLAY_ROW, DISPLAY_COLUMN, ITEM_STACK, IS_SPECIAL, parent);
    }
}
