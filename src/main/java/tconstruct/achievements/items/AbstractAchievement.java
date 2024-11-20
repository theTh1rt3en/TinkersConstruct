package tconstruct.achievements.items;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static tconstruct.util.Reference.prefix;

import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class AbstractAchievement {

    private final String key;
    private final int displayRow;
    private final int displayColumn;
    private final ItemStack itemStack;
    private final boolean isSpecial;
    private final Achievement parent;
    private Achievement achievement;

    public String getKey() {
        return prefix(key);
    }

    public Achievement get() {
        if (nonNull(achievement)) {
            return achievement;
        }

        achievement = new Achievement(
                getKey(),
                getKey(),
                getDisplayRow(),
                getDisplayColumn(),
                getItemStack(),
                getParent());

        if (isNull(this.getParent())) {
            achievement.initIndependentStat();
        }

        if (this.isSpecial()) {
            achievement.setSpecial();
        }

        return achievement;
    }
}
