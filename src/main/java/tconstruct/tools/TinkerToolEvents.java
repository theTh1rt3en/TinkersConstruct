package tconstruct.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;

import com.kuba6000.mobsinfo.api.IChanceModifier;
import com.kuba6000.mobsinfo.api.IMobExtraInfoProvider;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;
import com.kuba6000.mobsinfo.loader.extras.Translations;

import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import io.netty.buffer.ByteBuf;
import tconstruct.TConstruct;
import tconstruct.armor.player.TPlayerStats;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.entity.ProjectileBase;
import tconstruct.library.event.PartBuilderEvent;
import tconstruct.library.event.ToolBuildEvent;
import tconstruct.library.event.ToolCraftEvent;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.BowstringMaterial;
import tconstruct.library.tools.FletchingMaterial;
import tconstruct.library.tools.FletchlingLeafMaterial;
import tconstruct.library.tools.ToolCore;
import tconstruct.util.ItemHelper;
import tconstruct.util.config.PHConstruct;
import tconstruct.util.network.MovementUpdatePacket;

@Interface(iface = "com.kuba6000.mobsinfo.api.IMobExtraInfoProvider", modid = "mobsinfo")
public class TinkerToolEvents implements IMobExtraInfoProvider {

    @SubscribeEvent
    public void onCrafting(ItemCraftedEvent event) {
        if (event.crafting == null || event.player == null
                || event.player.worldObj == null
                || event.craftMatrix == null)
            return;

        Item item = event.crafting.getItem();
        if (!event.player.worldObj.isRemote) {
            if (item == Item.getItemFromBlock(TinkerTools.toolStationWood)) {
                if (!PHConstruct.beginnerBook) {
                    return;
                }

                TPlayerStats stats = TPlayerStats.get(event.player);
                if (!stats.materialManual) {
                    stats.materialManual = true;
                    AbilityHelper.spawnItemAtPlayer(event.player, new ItemStack(TinkerTools.manualBook, 1, 1));
                }
            }

            // slab pattern chest
            if (item == Item.getItemFromBlock(TinkerTools.craftingSlabWood) && event.crafting.getItemDamage() == 4) {
                // copy over NBT
                for (int i = 0; i < event.craftMatrix.getSizeInventory(); i++) {
                    ItemStack stack = event.craftMatrix.getStackInSlot(i);
                    if (stack == null) continue;
                    // regular pattern chest
                    if (stack.getItem() == Item.getItemFromBlock(TinkerTools.toolStationWood)
                            && stack.getItemDamage() == 5) {
                        event.crafting.setTagCompound(stack.getTagCompound());
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void buildTool(ToolBuildEvent event) {
        // check if the handle is a bone
        if (event.handleStack.getItem() == Items.bone) {
            event.handleStack = new ItemStack(TinkerTools.toolRod, 1, 5); // bone tool rod
            return;
        }

        // check if the handle is a stick
        List<ItemStack> sticks = OreDictionary.getOres("stickWood");
        for (ItemStack stick : sticks) if (OreDictionary.itemMatches(stick, event.handleStack, false)) {
            event.handleStack = new ItemStack(TinkerTools.toolRod, 1, 0); // wooden tool rod
            return;
        }
    }

    @SubscribeEvent
    public void craftTool(ToolCraftEvent.NormalTool event) {
        NBTTagCompound toolTag = event.toolTag.getCompoundTag("InfiTool");
        if (PHConstruct.denyMattock && event.tool == TinkerTools.mattock) {
            int head = toolTag.getInteger("Head");
            int handle = toolTag.getInteger("Handle");
            int accessory = toolTag.getInteger("Accessory");

            if (!allowCrafting(head, handle, accessory)) {
                event.setResult(Result.DENY);
                return;
            }
        }

        // bonus modifiers
        handlePaper(toolTag, event.tool);
        handleThaumium(toolTag, event.tool);

        if (event.tool == TinkerTools.battlesign) {
            int modifiers = toolTag.getInteger("Modifiers");
            modifiers += 1;
            toolTag.setInteger("Modifiers", modifiers);
        }
    }

    private void handlePaper(NBTTagCompound toolTag, ToolCore tool) {
        int modifiers = toolTag.getInteger("Modifiers");
        if (toolTag.getInteger("Head") == TinkerTools.MaterialID.Paper) modifiers++;
        if (toolTag.getInteger("Handle") == TinkerTools.MaterialID.Paper) modifiers++;
        if (toolTag.getInteger("Accessory") == TinkerTools.MaterialID.Paper) modifiers++;
        if (toolTag.getInteger("Extra") == TinkerTools.MaterialID.Paper) modifiers++;

        // 2 part tools gain 2 modifiers for the head
        if (tool.getPartAmount() == 2 && toolTag.getInteger("Head") == TinkerTools.MaterialID.Paper) modifiers++;

        toolTag.setInteger("Modifiers", modifiers);
    }

    private void handleThaumium(NBTTagCompound toolTag, ToolCore tool) {
        // count thaumic parts
        int thaum = 0;
        if (toolTag.getInteger("Head") == TinkerTools.MaterialID.Thaumium) thaum++;
        if (toolTag.getInteger("Handle") == TinkerTools.MaterialID.Thaumium) thaum++;
        if (toolTag.getInteger("Accessory") == TinkerTools.MaterialID.Thaumium) thaum++;
        if (toolTag.getInteger("Extra") == TinkerTools.MaterialID.Thaumium) thaum++;

        // each part gives 0.5 modifiers, rounded up
        int bonusModifiers = (int) Math.ceil((double) thaum / 2d);

        // 2-part tools get 1 modifier per part
        if (tool.getPartAmount() == 2) bonusModifiers = thaum;

        int modifiers = toolTag.getInteger("Modifiers");
        modifiers += bonusModifiers;
        toolTag.setInteger("Modifiers", modifiers);
    }

    private boolean allowCrafting(int head, int handle, int accessory) {
        int[] nonMetals = { 0, 1, 3, 4, 5, 6, 7, 8, 9, 17 };
        for (int nonMetal : nonMetals) {
            if (head == nonMetal || handle == nonMetal || accessory == nonMetal) return false;
        }
        return true;
    }

    @SubscribeEvent
    public void craftPart(PartBuilderEvent.NormalPart event) {
        // bowstring
        if (event.pattern.getItem() == TinkerTools.woodPattern && event.pattern.getItemDamage() == 23) {
            ItemStack result = craftBowString(event.material);
            if (result != null) {
                event.overrideResult(new ItemStack[] { result, null });
            }
        }

        // fletching
        if (event.pattern.getItem() == TinkerTools.woodPattern && event.pattern.getItemDamage() == 24) {
            ItemStack result = craftFletching(event.material);
            if (result != null) {
                event.overrideResult(new ItemStack[] { result, null });
            }
        }
    }

    public static ItemStack craftBowString(ItemStack stack) {
        if (stack.stackSize < 3) return null;

        BowstringMaterial mat = (BowstringMaterial) TConstructRegistry
                .getCustomMaterial(stack, BowstringMaterial.class);
        if (mat != null) return mat.craftingItem.copy();
        return null;
    }

    public static ItemStack craftFletching(ItemStack stack) {
        FletchingMaterial mat = (FletchingMaterial) TConstructRegistry
                .getCustomMaterial(stack, FletchingMaterial.class);
        // maybe it's a leaf fletchling
        if (mat == null)
            mat = (FletchingMaterial) TConstructRegistry.getCustomMaterial(stack, FletchlingLeafMaterial.class);
        if (mat != null) return mat.craftingItem.copy();
        return null;
    }

    @SubscribeEvent
    public void onAttack(LivingAttackEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entityLiving;
            // Cutlass
            ItemStack stack = player.getCurrentEquippedItem();
            if (stack != null && stack.getItem() == TinkerTools.battlesign) {
                // broken battlesign?
                if (!stack.hasTagCompound() || stack.getTagCompound().getCompoundTag("InfiTool").getBoolean("Broken"))
                    return;

                DamageSource source = event.source;
                if (!source.isUnblockable() && !source.isMagicDamage() && !source.isExplosion()) {
                    if (source.isProjectile()) {
                        Entity projectile = source.getSourceOfDamage();
                        if (projectile == null) return;
                        Vec3 motion = Vec3
                                .createVectorHelper(projectile.motionX, projectile.motionY, projectile.motionZ);
                        Vec3 look = player.getLookVec();

                        // this gives a factor of how much we're looking at the incoming arrow
                        double strength = -look.dotProduct(motion.normalize());
                        // we're looking away. oh no.
                        if (strength < 0.1) return;

                        // no damage, hooraaay
                        event.setCanceled(true);

                        double speed = projectile.motionX * projectile.motionX + projectile.motionY * projectile.motionY
                                + projectile.motionZ * projectile.motionZ;
                        speed = Math.sqrt(speed);

                        speed = (speed + 2) * strength;

                        // now we simply set the look vector with the speed and get our new vector!
                        projectile.motionX = look.xCoord * speed;
                        projectile.motionY = look.yCoord * speed;
                        projectile.motionZ = look.zCoord * speed;

                        projectile.rotationYaw = (float) (Math.atan2(projectile.motionX, projectile.motionZ) * 180.0D
                                / Math.PI);
                        projectile.rotationPitch = (float) (Math.atan2(projectile.motionY, speed) * 180.0D / Math.PI);

                        // send the current status to the client
                        TConstruct.packetPipeline.sendToAll(new MovementUpdatePacket(projectile));

                        if (projectile instanceof EntityArrow) {
                            ((EntityArrow) projectile).shootingEntity = player;

                            // the inverse is done when the event is cancelled in arrows etc.
                            // we reverse it so it has no effect. yay
                            projectile.motionX /= -0.10000000149011612D;
                            projectile.motionY /= -0.10000000149011612D;
                            projectile.motionZ /= -0.10000000149011612D;
                            // not needed at the client since it gets the absolute values sent

                            // tinker projectiles don't check for stuff hit to prevent weird behaviour.
                            // we have to de-defuse them so the reflected projectiles can hit stuff again
                            if (projectile instanceof ProjectileBase) ((ProjectileBase) projectile).defused = false;
                        }
                    } else {
                        Entity attacker = source.getEntity();
                        if (attacker != null) {
                            attacker.attackEntityFrom(DamageSource.causeThornsDamage(player), event.ammount);
                        }
                    }

                    // durability--
                    AbilityHelper.damageTool(stack, (int) Math.ceil(event.ammount / 2f), player, false);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingDrop(LivingDropsEvent event) {
        // ANY CHANGE MADE IN HERE MUST ALSO BE MADE IN provideDropsInformation!
        if (event.entityLiving == null) return;

        if (event.recentlyHit) {
            if (event.entityLiving instanceof EntitySkeleton enemy) {
                if (event.source.damageType.equals("player")) {
                    EntityPlayer player = (EntityPlayer) event.source.getEntity();
                    ItemStack stack = player.getCurrentEquippedItem();
                    if (stack != null && stack.hasTagCompound() && stack.getItem() instanceof ToolCore) {
                        int beheading = stack.getTagCompound().getCompoundTag("InfiTool").getInteger("Beheading");
                        if (stack.getItem() == TinkerTools.cleaver) beheading += 2;
                        if (beheading > 0 && TConstruct.random.nextInt(100) < beheading * 10) {
                            ItemHelper.addDrops(event, new ItemStack(Items.skull, 1, enemy.getSkeletonType()));
                        }
                    }
                }
                if (enemy.getSkeletonType() == 1
                        && TConstruct.random.nextInt(Math.max(1, 5 - event.lootingLevel)) == 0) {
                    ItemHelper.addDrops(event, new ItemStack(TinkerTools.materials, 1, 8));
                }
            }
            if (event.entityLiving.getClass() == EntityZombie.class) {
                if (event.source.damageType.equals("player")) {
                    EntityPlayer player = (EntityPlayer) event.source.getEntity();
                    ItemStack stack = player.getCurrentEquippedItem();

                    if (stack != null && stack.hasTagCompound() && stack.getItem() instanceof ToolCore) {
                        int beheading = stack.getTagCompound().getCompoundTag("InfiTool").getInteger("Beheading");
                        if (stack.hasTagCompound() && stack.getItem() == TinkerTools.cleaver) beheading += 2;
                        if (beheading > 0 && TConstruct.random.nextInt(100) < beheading * 10) {
                            ItemHelper.addDrops(event, new ItemStack(Items.skull, 1, 2));
                        }
                    }

                    if (stack != null && stack.hasTagCompound()
                            && stack.getItem() == TinkerTools.cleaver
                            && TConstruct.random.nextInt(100) < 10) {
                        ItemHelper.addDrops(event, new ItemStack(Items.skull, 1, 2));
                    }
                }
            }
            if (event.entityLiving.getClass() == EntityCreeper.class) {
                if (event.source.damageType.equals("player")) {
                    EntityPlayer player = (EntityPlayer) event.source.getEntity();
                    ItemStack stack = player.getCurrentEquippedItem();
                    if (stack != null && stack.hasTagCompound() && stack.getItem() instanceof ToolCore) {
                        int beheading = stack.getTagCompound().getCompoundTag("InfiTool").getInteger("Beheading");
                        if (stack.getItem() == TinkerTools.cleaver) beheading += 2;
                        if (beheading > 0 && TConstruct.random.nextInt(100) < beheading * 5) {
                            ItemHelper.addDrops(event, new ItemStack(Items.skull, 1, 4));
                        }
                    }
                }
            }
        }
        if (event.entityLiving instanceof EntityPlayer player) {
            if (PHConstruct.dropPlayerHeads) {
                ItemStack dropStack = new ItemStack(Items.skull, 1, 3);
                NBTTagCompound nametag = new NBTTagCompound();
                nametag.setString("SkullOwner", player.getDisplayName());
                dropStack.setTagCompound(nametag);
                ItemHelper.addDrops(event, dropStack);
            } else if (event.source.damageType.equals("player")) {
                EntityPlayer source = (EntityPlayer) event.source.getEntity();
                ItemStack stack = source.getCurrentEquippedItem();
                if (stack != null && stack.hasTagCompound() && stack.getItem() instanceof ToolCore) {
                    int beheading = stack.getTagCompound().getCompoundTag("InfiTool").getInteger("Beheading");
                    if (stack.getItem() == TinkerTools.cleaver) beheading += 2;
                    if (beheading > 0 && TConstruct.random.nextInt(100) < beheading * 50) {
                        ItemStack dropStack = new ItemStack(Items.skull, 1, 3);
                        NBTTagCompound nametag = new NBTTagCompound();
                        nametag.setString("SkullOwner", player.getDisplayName());
                        dropStack.setTagCompound(nametag);
                        ItemHelper.addDrops(event, dropStack);
                    }
                }
            }
        }
    }

    @Method(modid = "mobsinfo")
    @Override
    public void provideExtraDropsInformation(@Nonnull String entityString, @Nonnull ArrayList<MobDrop> drops,
            @Nonnull MobRecipe recipe) {
        if (recipe.entity instanceof EntitySkeleton) {

            MobDrop drop = new MobDrop(
                    new ItemStack(Items.skull, 1, ((EntitySkeleton) recipe.entity).getSkeletonType()),
                    MobDrop.DropType.Normal,
                    0,
                    null,
                    null,
                    false,
                    false);
            drop.variableChance = true;
            drop.chanceModifiers.add(new BeheadingModifier(10d, 20d));
            drops.add(drop);

            if (((EntitySkeleton) recipe.entity).getSkeletonType() == 1) {
                MobDrop drop2 = new MobDrop(
                        new ItemStack(TinkerTools.materials, 1, 8),
                        MobDrop.DropType.Normal,
                        2000,
                        null,
                        null,
                        true,
                        false);
                drops.add(drop2);
            }

        }

        if (recipe.entity.getClass() == EntityZombie.class) {
            MobDrop drop = new MobDrop(
                    new ItemStack(Items.skull, 1, 2),
                    MobDrop.DropType.Normal,
                    0,
                    null,
                    null,
                    false,
                    false);
            drop.variableChance = true;
            drop.chanceModifiers.add(new BeheadingModifier(10d, 20d));
            drops.add(drop);

            MobDrop drop2 = new MobDrop(
                    new ItemStack(TinkerTools.materials, 1, 2),
                    MobDrop.DropType.Normal,
                    0,
                    null,
                    null,
                    true,
                    false);
            drop2.variableChance = true;
            drop2.chanceModifiers.addAll(
                    Arrays.asList(
                            new IChanceModifier.NormalChance(10d),
                            new IChanceModifier.DropsOnlyUsing(TinkerTools.cleaver)));
            drops.add(drop2);
        }

        if (recipe.entity.getClass() == EntityCreeper.class) {
            MobDrop drop = new MobDrop(
                    new ItemStack(Items.skull, 1, 4),
                    MobDrop.DropType.Normal,
                    0,
                    null,
                    null,
                    false,
                    false);
            drop.variableChance = true;
            drop.chanceModifiers.add(new BeheadingModifier(5d, 10d));
            drops.add(drop);
        }
    }

    private static class BeheadingModifier implements IChanceModifier {

        double m1, m2;

        @SuppressWarnings("unused")
        BeheadingModifier() {
            // Constructor is called via reflection in IChanceModifier.loadFromByteBuf()
        }

        BeheadingModifier(double m1, double m2) {
            this.m1 = m1;
            this.m2 = m2;
        }

        @Override
        public String getDescription() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void applyTooltip(List<String> currentTooltip) {
            currentTooltip.addAll(
                    Arrays.asList(
                            Translations.BASE_CHANCE.get(0d),
                            StatCollector.translateToLocalFormatted(
                                    "tconstruct.mobsinfocompat.tinkers_construct_beheading",
                                    m1),
                            StatCollector.translateToLocalFormatted(
                                    "tconstruct.mobsinfocompat.tinkers_construct_beheading_1",
                                    m2)));
        }

        @Override
        public double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
                EntityLiving victim) {
            if (!(attacker instanceof EntityPlayer)) return 0d;
            ItemStack stack = ((EntityPlayer) attacker).getCurrentEquippedItem();
            if (stack != null && stack.hasTagCompound() && stack.getItem() instanceof ToolCore) {
                int beheading = stack.getTagCompound().getCompoundTag("InfiTool").getInteger("Beheading");
                if (stack.getItem() == TinkerTools.cleaver) beheading += 2;
                return m1 * beheading;
            }
            return 0d;
        }

        @Override
        public void writeToByteBuf(ByteBuf byteBuf) {
            byteBuf.writeDouble(m1);
            byteBuf.writeDouble(m2);
        }

        @Override
        public void readFromByteBuf(ByteBuf byteBuf) {
            m1 = byteBuf.readDouble();
            m2 = byteBuf.readDouble();
        }
    }

    @SubscribeEvent
    public void registerOre(OreRegisterEvent evt) {

        if (evt.Name.equals("crystalQuartz")) {
            TinkerTools.modAttack.addStackToMatchList(evt.Ore, 2);
        } else if (evt.Name.equals("crystalCertusQuartz")) {
            TinkerTools.modAttack.addStackToMatchList(evt.Ore, 24);
        }
    }

    @SubscribeEvent
    public void damageToolsOnDeath(PlayerDropsEvent event) {
        if (!PHConstruct.deathPenality) return;

        EnumDifficulty difficulty = event.entityPlayer.worldObj.difficultySetting;
        // easy and peaceful don't punish
        if (difficulty == EnumDifficulty.PEACEFUL || difficulty == EnumDifficulty.EASY) return;

        int punishment = 20; // normal has 5%
        if (difficulty == EnumDifficulty.HARD) punishment = 10; // hard has 10%

        // check if we have to reduce it
        // did the player live long enough to receive derp-protection?
        // (yes, you receive protection every time you log in. we're that nice.)
        int derp = 1;
        if (event.entityPlayer.ticksExisted < 60 * 5 * 20) {
            derp = TPlayerStats.get(event.entityPlayer).derpLevel;
            if (derp <= 0) derp = 1;
            punishment *= derp;
        }

        boolean damaged = false;
        for (EntityItem drop : event.drops) {
            // we're only interested in tools
            if (!(drop.getEntityItem().getItem() instanceof ToolCore) || !drop.getEntityItem().hasTagCompound())
                continue;

            // damage tools by 10% of their total durability!
            NBTTagCompound tags = drop.getEntityItem().getTagCompound().getCompoundTag("InfiTool");
            int dur = tags.getInteger("TotalDurability");
            dur /= punishment;

            AbilityHelper.damageTool(drop.getEntityItem(), dur, event.entityPlayer, true);
            damaged = true;
        }

        if (damaged) {
            derp++;
        }

        // increase derplevel by 1. Minimal derp level is 1.
        TPlayerStats.get(event.entityPlayer).derpLevel = derp + 1;
    }
}
