package tconstruct.items.tools;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tconstruct.library.tools.Weapon;
import tconstruct.tools.TinkerTools;

public class Longsword extends Weapon {

    public Longsword() {
        super(4);
        this.setUnlocalizedName("InfiTool.Longsword");
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.bow;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.onGround) {
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        }
        return stack;
    }

    @Override
    public float chargeAttack() {
        return 1.5f;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int useCount) {
        int time = this.getMaxItemUseDuration(stack) - useCount;
        if (time > 5) {
            player.addExhaustion(0.2F);
            player.setSprinting(true);

            float increase = (float) (0.02 * time + 0.2);
            if (increase > 0.56f) increase = 0.56f;
            player.motionY += increase;

            float speed = 0.05F * time;
            if (speed > 0.925f) speed = 0.925f;
            player.motionX = -MathHelper.sin(player.rotationYaw / 180.0F * (float) Math.PI)
                    * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI)
                    * speed;
            player.motionZ = MathHelper.cos(player.rotationYaw / 180.0F * (float) Math.PI)
                    * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI)
                    * speed;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
        super.onUpdate(stack, world, entity, par4, par5);
        if (entity instanceof EntityPlayerSP player) {
            ItemStack usingItem = player.getItemInUse();
            if (usingItem != null && usingItem.getItem() == this) {
                player.movementInput.moveForward *= 5.0F;
                player.movementInput.moveStrafe *= 5.0F;
            }
        }
    }

    @Override
    public Item getHeadItem() {
        return TinkerTools.swordBlade;
    }

    @Override
    public Item getAccessoryItem() {
        return TinkerTools.handGuard;
    }

    @Override
    public String getIconSuffix(int partType) {
        return switch (partType) {
            case 0 -> "_longsword_blade";
            case 1 -> "_longsword_blade_broken";
            case 2 -> "_longsword_handle";
            case 3 -> "_longsword_accessory";
            default -> "";
        };
    }

    @Override
    public String getEffectSuffix() {
        return "_longsword_effect";
    }

    @Override
    public String getDefaultFolder() {
        return "longsword";
    }
}
