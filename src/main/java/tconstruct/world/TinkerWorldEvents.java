package tconstruct.world;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;

import com.kuba6000.mobsinfo.api.ConstructableItemStack;
import com.kuba6000.mobsinfo.api.IMobExtraInfoProvider;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import tconstruct.TConstruct;
import tconstruct.tools.TinkerTools;
import tconstruct.util.ItemHelper;
import tconstruct.util.config.PHConstruct;

@Interface(iface = "com.kuba6000.mobsinfo.api.IMobExtraInfoProvider", modid = "mobsinfo")
public class TinkerWorldEvents implements IMobExtraInfoProvider {

    @SubscribeEvent
    public void onLivingSpawn(LivingSpawnEvent.SpecialSpawn event) {
        EntityLivingBase living = event.entityLiving;
        if (living.getClass() == EntitySpider.class && TConstruct.random.nextInt(100) == 0) {
            EntityCreeper creeper = new EntityCreeper(living.worldObj);
            spawnEntityLiving(living.posX, living.posY + 1, living.posZ, creeper, living.worldObj);
            if (living.riddenByEntity != null) creeper.mountEntity(living.riddenByEntity);
            else creeper.mountEntity(living);

            EntityXPOrb orb = new EntityXPOrb(
                    living.worldObj,
                    living.posX,
                    living.posY,
                    living.posZ,
                    TConstruct.random.nextInt(20) + 20);
            orb.mountEntity(creeper);
        }
    }

    public static void spawnEntityLiving(double x, double y, double z, EntityLiving entity, World world) {
        if (!world.isRemote) {
            entity.setPosition(x, y, z);
            entity.onSpawnWithEgg(null);
            world.spawnEntityInWorld(entity);
        }
    }

    /* Bonemeal */
    @SubscribeEvent
    public void bonemealEvent(BonemealEvent event) {
        if (!event.world.isRemote) {
            if (event.block == TinkerWorld.slimeSapling) {
                if (TinkerWorld.slimeSapling
                        .boneFertilize(event.world, event.x, event.y, event.z, event.world.rand, event.entityPlayer))
                    event.setResult(Event.Result.ALLOW);
                else event.setCanceled(true);
            }
        }
    }

    /* Damage */
    @SubscribeEvent
    public void onHurt(LivingHurtEvent event) {
        EntityLivingBase reciever = event.entityLiving;
        if (reciever instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entityLiving;
            // Cutlass
            ItemStack stack = player.getCurrentEquippedItem();
            if (stack != null && player.isUsingItem()) {
                Item item = stack.getItem();
                if (item == TinkerTools.cutlass) {
                    player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 3 * 20, 1));
                } else if (item == TinkerTools.battlesign) {
                    event.ammount *= 1.5; // Puts battlesign blocking at 3/4 instead of 1/2
                }
            }
        } else if (reciever instanceof EntityCreeper) {
            Entity attacker = event.source.getEntity();
            if (attacker instanceof EntityLivingBase) {
                Entity target = ((EntityCreeper) reciever).getAttackTarget();
                if (target != null) {
                    float d1 = reciever.getDistanceToEntity(((EntityCreeper) reciever).getAttackTarget());
                    float d2 = reciever.getDistanceToEntity(attacker);
                    if (d2 < d1) {
                        ((EntityCreeper) event.entityLiving)
                                .setAttackTarget((EntityLivingBase) event.source.getEntity());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingDrop(LivingDropsEvent event) {
        // ANY CHANGE MADE IN HERE MUST ALSO BE MADE IN provideExtraDropsInformation!
        if (event.entityLiving == null) return;

        if (event.entityLiving.getClass() == EntityGhast.class) {
            if (PHConstruct.uhcGhastDrops) {
                for (EntityItem o : event.drops) {
                    if (o.getEntityItem().getItem() == Items.ghast_tear) {
                        o.setEntityItemStack(new ItemStack(Items.gold_ingot, 1));
                    }
                }
            } else {
                ItemHelper.addDrops(event, new ItemStack(Items.ghast_tear, 1));
            }
        }
    }

    @Method(modid = "mobsinfo")
    @Override
    public void provideExtraDropsInformation(@Nonnull String entityString, @Nonnull ArrayList<MobDrop> drops,
            @Nonnull MobRecipe recipe) {
        if (recipe.entity.getClass() == EntityGhast.class) {
            if (PHConstruct.uhcGhastDrops) {
                for (MobDrop drop : drops) {
                    if (drop.stack.getItem() == Items.ghast_tear) {
                        drop.stack = new ItemStack(Items.gold_ingot);
                        drop.reconstructableStack = new ConstructableItemStack(drop.stack);
                    }
                }
            } else {
                for (MobDrop drop : drops) {
                    if (drop.stack.getItem() == Items.ghast_tear) {
                        drop.chance += 10000;
                        drop.clampChance();
                    }
                }
            }
        }
    }
}
