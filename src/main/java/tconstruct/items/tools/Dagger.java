package tconstruct.items.tools;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import tconstruct.library.tools.Weapon;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.entity.DaggerEntity;

public class Dagger extends Weapon {

    public Dagger() {
        super(1);
        this.setUnlocalizedName("InfiTool.Dagger");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
        ItemStack stack = itemstack.copy();
        if (!world.isRemote) {
            DaggerEntity dagger = new DaggerEntity(world, player, 1.5f, 0, stack);
            if (player.capabilities.isCreativeMode) dagger.canBePickedUp = 2;
            world.spawnEntityInWorld(dagger);
        }
        itemstack.stackSize--;
        return itemstack;
    }

    @Override
    public ItemStack onEaten(ItemStack itemstack, World world, EntityPlayer player) {
        ItemStack stack = itemstack.copy();
        if (!world.isRemote) {
            DaggerEntity dagger = new DaggerEntity(world, player, 1.5f, 0, stack);
            world.spawnEntityInWorld(dagger);
        }
        itemstack.stackSize--;
        return itemstack;
    }

    @Override
    public String[] getTraits() {
        return new String[] { "weapon", "melee", "throwing" };
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.bow;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 10;
    }

    @Override
    public String getIconSuffix(int partType) {
        return switch (partType) {
            case 0 -> "_dagger_blade";
            case 1 -> "_dagger_blade_broken";
            case 2 -> "_dagger_handle";
            case 3 -> "_dagger_accessory";
            default -> "";
        };
    }

    @Override
    public String getEffectSuffix() {
        return "_dagger_effect";
    }

    @Override
    public String getDefaultFolder() {
        return "dagger";
    }

    @Override
    public Item getHeadItem() {
        return TinkerTools.knifeBlade;
    }

    @Override
    public Item getAccessoryItem() {
        return TinkerTools.crossbar;
    }
}
