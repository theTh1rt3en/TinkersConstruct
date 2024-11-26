package tconstruct.modifiers.armor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import tconstruct.TConstruct;
import tconstruct.armor.player.TPlayerStats;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.armor.ArmorPart;
import tconstruct.library.modifier.ActiveArmorMod;
import tconstruct.library.modifier.IModifyable;

public class ActiveTinkerArmor extends ActiveArmorMod {

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack, ArmorCore armor, ArmorPart type) {
        if (!itemStack.hasTagCompound()) return;
        NBTTagCompound tag = itemStack.getTagCompound()
                .getCompoundTag(((IModifyable) itemStack.getItem()).getBaseTagName());
        if (tag == null) return;
        if (tag.hasKey("Moss")) {
            int chance = tag.getInteger("Moss");
            int check = world.canBlockSeeTheSky((int) player.posX, (int) player.posY, (int) player.posZ) ? 350 : 1150;
            if (TConstruct.random.nextInt(check) < chance) {
                int current = tag.getInteger("Damage");
                if (current > 0) {
                    current--;
                    tag.setInteger("Damage", current);
                    itemStack.setItemDamage(current);
                }
            }
        }
        if (type == ArmorPart.Head) {
            TPlayerStats stats = TPlayerStats.get(player);
            if (stats.activeGoggles && tag.getBoolean("Night Vision"))
                player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 15 * 20, 0, true));

        }
        if (type == ArmorPart.Chest && player.isSneaking() && tag.getBoolean("Stealth"))
            player.addPotionEffect(new PotionEffect(Potion.invisibility.id, 2, 0, true));

        if (type == ArmorPart.Feet && player.isInWater()) {
            if (!player.isSneaking() && tag.getBoolean("WaterWalk") && player.motionY <= 0) {
                player.motionY = 0;
            }
            if (tag.getBoolean("LeadBoots")) {
                if (player.motionY > 0) player.motionY *= 0.5f;
                else if (player.motionY < 0) player.motionY *= 1.5f;
            }
        }

    }
}
