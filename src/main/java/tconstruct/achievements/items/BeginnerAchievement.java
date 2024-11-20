package tconstruct.achievements.items;

import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

import tconstruct.tools.TinkerTools;

public class BeginnerAchievement extends AbstractAchievement {

    private static final String KEY = "beginner";
    private static final int DISPLAY_ROW = 0;
    private static final int DISPLAY_COLUMN = 0;
    private static final ItemStack ITEM_STACK = new ItemStack(TinkerTools.manualBook);
    private static final boolean IS_SPECIAL = false;

    public BeginnerAchievement(Achievement parent) {
        super(KEY, DISPLAY_ROW, DISPLAY_COLUMN, ITEM_STACK, IS_SPECIAL, parent);
    }
}
