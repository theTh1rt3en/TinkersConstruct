package tconstruct.items.tools;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.Weapon;
import tconstruct.tools.TinkerTools;

public class Cleaver extends Weapon {

    public Cleaver() {
        super(5);
        this.setUnlocalizedName("InfiTool.Cleaver");
    }

    @Override
    public Item getHeadItem() {
        return TinkerTools.largeSwordBlade;
    }

    @Override
    public Item getHandleItem() {
        return TinkerTools.toughRod;
    }

    @Override
    public Item getAccessoryItem() {
        return TinkerTools.largePlate;
    }

    @Override
    public Item getExtraItem() {
        return TinkerTools.toughRod;
    }

    @Override
    public int durabilityTypeAccessory() {
        return 2;
    }

    @Override
    public int durabilityTypeExtra() {
        return 1;
    }

    @Override
    public float getRepairCost() {
        return 4.0f;
    }

    @Override
    public float getDurabilityModifier() {
        return 2.5f;
    }

    @Override
    public float getDamageModifier() {
        return 1.4f;
    }

    @Override
    public int getPartAmount() {
        return 4;
    }

    @Override
    public String getIconSuffix(int partType) {
        return switch (partType) {
            case 0 -> "_cleaver_head";
            case 1 -> "_cleaver_head_broken";
            case 2 -> "_cleaver_handle";
            case 3 -> "_cleaver_shield";
            case 4 -> "_cleaver_guard";
            default -> "";
        };
    }

    @Override
    public String getEffectSuffix() {
        return "_cleaver_effect";
    }

    @Override
    public String getDefaultFolder() {
        return "cleaver";
    }

    /* Cleaver specific */
    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (AbilityHelper.onLeftClickEntity(stack, player, entity, this)) {
            entity.hurtResistantTime += 7;
        }
        return true;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
        super.onUpdate(stack, world, entity, par4, par5);
        if (entity instanceof EntityPlayer player) {
            ItemStack equipped = player.getCurrentEquippedItem();
            if (equipped == stack) {
                player.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 2, 2, true));
            }
        }
    }
}
