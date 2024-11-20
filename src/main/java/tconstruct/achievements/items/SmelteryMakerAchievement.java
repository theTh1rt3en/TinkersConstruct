package tconstruct.achievements.items;

import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

import tconstruct.smeltery.TinkerSmeltery;

public class SmelteryMakerAchievement extends AbstractAchievement {

    private static final String KEY = "smelteryMaker";
    private static final int DISPLAY_ROW = -2;
    private static final int DISPLAY_COLUMN = -1;
    private static final ItemStack ITEM_STACK = new ItemStack(TinkerSmeltery.smeltery);
    private static final boolean IS_SPECIAL = false;

    public SmelteryMakerAchievement(Achievement parent) {
        super(KEY, DISPLAY_ROW, DISPLAY_COLUMN, ITEM_STACK, IS_SPECIAL, parent);
    }
}
