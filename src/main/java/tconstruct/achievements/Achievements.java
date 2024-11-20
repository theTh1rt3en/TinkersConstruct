package tconstruct.achievements;

import static tconstruct.util.Reference.prefix;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.Achievement;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.AchievementPage;

import lombok.experimental.UtilityClass;
import tconstruct.achievements.items.AbstractAchievement;
import tconstruct.achievements.items.BeginnerAchievement;
import tconstruct.achievements.items.DualConvenienceAchievement;
import tconstruct.achievements.items.EnemySlayerAchievement;
import tconstruct.achievements.items.PatternAchievement;
import tconstruct.achievements.items.PreparedFightAchievement;
import tconstruct.achievements.items.ProTinkererAchievement;
import tconstruct.achievements.items.SmelteryMakerAchievement;
import tconstruct.achievements.items.TinkererAchievement;
import tconstruct.util.config.PHConstruct;

@UtilityClass
public class Achievements {

    private static final HashMap<String, Achievement> ACHIEVEMENT_MAP = new HashMap<>();

    /**
     * Adds an achievement and registers it, so there is no need to call .registerStat
     *
     * @param name        The name of the achievement
     * @param achievement The achievement
     */
    public static void addAchievement(String name, Achievement achievement) {
        if (!PHConstruct.achievementsEnabled) {
            return;
        }

        ACHIEVEMENT_MAP.put(name, achievement.registerStat());
    }

    /**
     * Adds an achievements and registers them, so there is no need to call .registerStat for each item
     *
     * @param achievements The achievements
     */
    public static void addAchievements(AbstractAchievement... achievements) {
        for (var achievement : achievements) {
            addAchievement(achievement.getKey(), achievement.get());
        }
    }

    /**
     * Returns a registered achievement
     *
     * @param name The name of the achievement
     * @return The achievement
     */
    public static Achievement getAchievement(String name) {
        return ACHIEVEMENT_MAP.get(name);
    }

    /**
     * Grants the achievement
     *
     * @param player The player that earned the achievement
     * @param name   The name of the achievement
     */
    public static void triggerAchievement(EntityPlayer player, String name) {
        if (!PHConstruct.achievementsEnabled) {
            return;
        }

        Achievement ach = getAchievement(name);

        if (ach != null) {
            player.triggerAchievement(ach);
        }
    }

    /**
     * Adds all the achievements included in TConstruct, call before registerAchievementPane is called
     */
    public static void addDefaultAchievements() {
        if (!PHConstruct.achievementsEnabled) {
            return;
        }

        var beginnerAchievement = new BeginnerAchievement(null);
        var patternAchievement = new PatternAchievement(beginnerAchievement.get());
        var tinkererAchievement = new TinkererAchievement(patternAchievement.get());
        var preparedFightAchievement = new PreparedFightAchievement(tinkererAchievement.get());
        var proTinkererAchievement = new ProTinkererAchievement(tinkererAchievement.get());
        var smelteryMakerAchievement = new SmelteryMakerAchievement(beginnerAchievement.get());
        var enemySlayerAchievement = new EnemySlayerAchievement(preparedFightAchievement.get());
        var dualConvenienceAchievement = new DualConvenienceAchievement(enemySlayerAchievement.get());

        addAchievements(
                beginnerAchievement,
                patternAchievement,
                tinkererAchievement,
                preparedFightAchievement,
                proTinkererAchievement,
                smelteryMakerAchievement,
                enemySlayerAchievement,
                dualConvenienceAchievement);
    }

    /**
     * Should be called after all the achievements are loaded (PostInit would be good)
     */
    public static void registerAchievementPane() {
        if (!PHConstruct.achievementsEnabled) {
            return;
        }

        var achievements = ACHIEVEMENT_MAP.values().toArray(new Achievement[0]);
        var achievementsPage = new AchievementPage(
                StatCollector.translateToLocal(prefix("achievementPage.name")),
                achievements);
        AchievementPage.registerAchievementPage(achievementsPage);
    }
}
