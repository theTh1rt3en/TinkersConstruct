package tconstruct.achievements;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import mantle.items.abstracts.CraftingItem;

public class CraftAchievementItem extends CraftingItem {

    public String grantedAchievement;

    public CraftAchievementItem(String[] names, String[] tex, String folder, String modTexturePrefix, CreativeTabs tab,
            String achievement) {
        super(names, tex, folder, modTexturePrefix, tab);
        grantedAchievement = achievement;
    }

    @Override
    public void onCreated(ItemStack item, World world, EntityPlayer player) {
        if (grantedAchievement != null && !grantedAchievement.isEmpty()) {
            Achievements.triggerAchievement(player, grantedAchievement);
        }
    }
}
