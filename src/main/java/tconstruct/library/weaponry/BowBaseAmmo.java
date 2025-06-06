package tconstruct.library.weaponry;

import java.util.List;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.common.Loader;
import mods.battlegear2.api.core.IBattlePlayer;
import tconstruct.compat.Battlegear2Compat;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.tools.BowstringMaterial;
import tconstruct.library.tools.CustomMaterial;
import tconstruct.weaponry.ammo.ArrowAmmo;
import tconstruct.weaponry.entity.ArrowEntity;

public abstract class BowBaseAmmo extends ProjectileWeapon {

    private static final boolean isBattlegear2Loaded = Loader.isModLoaded("battlegear2");

    public BowBaseAmmo(int baseDamage, String name) {
        super(baseDamage, name);
    }

    @Override
    public int durabilityTypeAccessory() {
        return 2; // head-type
    }

    @Override
    public int durabilityTypeExtra() {
        return 1; // handle-type
    }

    @Override
    public boolean zoomOnWindup(ItemStack itemStack) {
        return true;
    }

    @Override
    public float getZoom(ItemStack itemStack) {
        return 1.2f;
    }

    @Override
    public float getMinWindupProgress(ItemStack itemStack) {
        return 0.5f;
    }

    @Override
    public float getProjectileSpeed(ItemStack itemStack) {
        return super.getProjectileSpeed(itemStack) * 0.9f;
    }

    @Override
    public ItemStack searchForAmmo(EntityPlayer player, ItemStack weapon) {
        // arrow priority: hotbar > inventory, tinker arrows > regular arrows
        if (isBattlegear2Loaded && player instanceof IBattlePlayer battlePlayer
                && battlePlayer.battlegear2$isBattlemode()) {
            ItemStack offhand = Battlegear2Compat.getBattlegear2Offhand(player);
            if (checkTinkerArrow(offhand) || checkVanillaArrow(offhand)) {
                return offhand;
            }
        }

        ItemStack[] inventory = player.inventory.mainInventory;

        // check hotbar for tinker arrows
        for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++)
            if (checkTinkerArrow(inventory[i])) return inventory[i];

        // check hotbar for vanilla arrows
        for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++)
            if (checkVanillaArrow(inventory[i])) return inventory[i];

        // check the rest of the inventory for tinker arrows
        for (int i = InventoryPlayer.getHotbarSize(); i < inventory.length; i++)
            if (checkTinkerArrow(inventory[i])) return inventory[i];

        // check the rest of the inventory for vanilla arrows
        for (int i = InventoryPlayer.getHotbarSize(); i < inventory.length; i++)
            if (checkVanillaArrow(inventory[i])) return inventory[i];

        // creative mode
        if (player.capabilities.isCreativeMode
                || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, weapon) > 0)
            return new ItemStack(Items.arrow);

        return null;
    }

    private boolean checkTinkerArrow(ItemStack stack) {
        // null
        if (stack == null) return false;
        // no tinker arrow
        if (!(stack.getItem() instanceof ArrowAmmo)) return false;
        // no ammo left
        return ((IAmmo) stack.getItem()).getAmmoCount(stack) > 0;
    }

    private boolean checkVanillaArrow(ItemStack stack) {
        // null
        if (stack == null) return false;
        // no arrow
        return stack.getItem() == Items.arrow;
        // inventory shouldn't contain stacksize 0 items so we don't have to check that.
    }

    @Override
    protected Entity createProjectile(ItemStack arrows, World world, EntityPlayer player, float speed, float accuracy,
            float windup) {
        EntityArrow arrow;

        if (arrows.getItem() == Items.arrow) {
            arrow = new EntityArrow(world, player, speed / 1.5f); // vanilla arrows internally do x1.5
        } else {
            ItemStack reference = arrows.copy();
            reference.stackSize = 1;
            reference.getTagCompound().getCompoundTag("InfiTool").setInteger("Ammo", 1);
            arrow = new ArrowEntity(world, player, speed, accuracy, reference);
        }

        if (player.capabilities.isCreativeMode) arrow.canBePickedUp = 2;

        if (windup >= 1f) arrow.setIsCritical(true);

        return arrow;
    }

    @Override
    public void playFiringSound(World world, EntityPlayer player, ItemStack weapon, ItemStack ammo, float speed,
            float accuracy) {
        world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + speed * 0.5F);
    }

    @Override
    public void buildTool(int id, String name, List list) {
        // does the material have a bow material?
        if (TConstructRegistry.getBowMaterial(id) == null) return;

        // all creative bows use regular bowstring
        ItemStack handleStack = new ItemStack(getHandleItem(), 1, 0); // regular bowstring
        ItemStack accessoryStack = getPartAmount() > 2 ? new ItemStack(getAccessoryItem(), 1, id) : null;
        ItemStack extraStack = getPartAmount() > 3 ? new ItemStack(getExtraItem(), 1, id) : null;

        ItemStack tool = ToolBuilder.instance
                .buildTool(new ItemStack(getHeadItem(), 1, id), handleStack, accessoryStack, extraStack, "");
        if (tool != null) {
            tool.getTagCompound().getCompoundTag("InfiTool").setBoolean("Built", true);
            list.add(tool);
        }
    }

    @Override
    protected int getDefaultColor(int renderPass, int materialID) {
        // bowstring uses custom material
        if (renderPass == 0) {
            CustomMaterial material = TConstructRegistry.getCustomMaterial(materialID, BowstringMaterial.class);
            if (material != null) {
                return material.color;
            }
        }

        return super.getDefaultColor(renderPass, materialID);
    }

    @Override
    public String[] getTraits() {
        return new String[] { "weapon", "bow", "windup" };
    }
}
