package tconstruct.library.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import cofh.api.energy.IEnergyContainerItem;
import cofh.core.item.IEqualityOverrideItem;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tconstruct.library.ActiveToolMod;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.modifier.IModifyable;
import tconstruct.library.modifier.ItemModifier;
import tconstruct.library.util.TextureHelper;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.entity.FancyEntityItem;
import tconstruct.util.config.PHConstruct;
import tconstruct.weaponry.TinkerWeaponry;

/**
 * NBTTags Main tag - InfiTool
 *
 * @see ToolBuilder
 *
 *      Required: Head: Base and render tag, above the handle Handle: Base and render tag, bottom layer
 *
 *      Damage: Replacement for metadata MaxDamage: ItemStacks only read setMaxDamage() Broken: Represents whether the
 *      tool is broken (boolean) Attack: How much damage a mob will take MiningSpeed: The speed at which a tool mines
 *
 *      Others: Accessory: Base and tag, above head. Sword guards, binding, etc Effects: Render tag, top layer. Fancy
 *      effects like moss or diamond edge. Render order: Handle > Head > Accessory > Effect1 > Effect2 > Effect3 > etc
 *      Unbreaking: Reinforced in-game, 10% chance to not use durability per level Stonebound: Mines faster as the tool
 *      takes damage, but has less attack Spiny: Opposite of stonebound
 *
 *      Modifiers have their own tags.
 * @see ItemModifier
 */
@Optional.InterfaceList({ @Optional.Interface(modid = "CoFHAPI|energy", iface = "cofh.api.energy.IEnergyContainerItem"),
        @Optional.Interface(modid = "CoFHCore", iface = "cofh.core.item.IEqualityOverrideItem") })
public abstract class ToolCore extends Item implements IEnergyContainerItem, IEqualityOverrideItem, IModifyable {

    protected Random random = new Random();
    protected int damageVsEntity;
    public static IIcon blankSprite;
    public static IIcon emptyIcon;

    public ToolCore(int baseDamage) {
        super();
        this.maxStackSize = 1;
        this.setMaxDamage(100);
        this.setUnlocalizedName("InfiTool");
        this.setCreativeTab(TConstructRegistry.toolTab);
        damageVsEntity = baseDamage;
        TConstructRegistry.addToolMapping(this);
        setNoRepair();
        canRepair = false;
    }

    @Override
    public String getBaseTagName() {
        return "InfiTool";
    }

    @Override
    public String getModifyType() {
        return "Tool";
    }

    /**
     * Determines crafting behavior with regards to durability 0: None 1: Adds handle modifier 2: Averages part with the
     * rest of the tool (head)
     *
     * @return type
     */
    public int durabilityTypeHandle() {
        return 1;
    }

    public int durabilityTypeAccessory() {
        return 0;
    }

    public int durabilityTypeExtra() {
        return 0;
    }

    public int getModifierAmount() {
        return 3;
    }

    public String getToolName() {
        return this.getClass().getSimpleName();
    }

    public String getLocalizedToolName() {
        return StatCollector.translateToLocal("tool." + getToolName().toLowerCase());
    }

    /* Rendering */

    public HashMap<Integer, IIcon> headIcons = new HashMap<>();
    public HashMap<Integer, IIcon> brokenIcons = new HashMap<>();
    public HashMap<Integer, IIcon> handleIcons = new HashMap<>();
    public HashMap<Integer, IIcon> accessoryIcons = new HashMap<>();
    public HashMap<Integer, IIcon> effectIcons = new HashMap<>();
    public HashMap<Integer, IIcon> extraIcons = new HashMap<>();

    // Not liking this
    public HashMap<Integer, String> headStrings = new HashMap<>();
    public HashMap<Integer, String> brokenPartStrings = new HashMap<>();
    public HashMap<Integer, String> handleStrings = new HashMap<>();
    public HashMap<Integer, String> accessoryStrings = new HashMap<>();
    public HashMap<Integer, String> effectStrings = new HashMap<>();
    public HashMap<Integer, String> extraStrings = new HashMap<>();

    @SideOnly(Side.CLIENT)
    @Override
    public boolean requiresMultipleRenderPasses() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public final int getRenderPasses(int metadata) {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack par1ItemStack) {
        return false;
    }

    // Override me please!
    public int getPartAmount() {
        return 3;
    }

    public abstract String getIconSuffix(int partType);

    public abstract String getEffectSuffix();

    public abstract String getDefaultFolder();

    /**
     * Returns the COMPLETE resource path. Example: tinker:broadsword
     *
     * @return
     */
    public String getDefaultTexturePath() {
        return "tinker:" + getDefaultFolder();
    }

    public void registerPartPaths(int index, String[] location) {
        headStrings.put(index, location[0]);
        brokenPartStrings.put(index, location[1]);
        handleStrings.put(index, location[2]);
        if (location.length > 3) accessoryStrings.put(index, location[3]);
        if (location.length > 4) extraStrings.put(index, location[4]);
    }

    public void registerAlternatePartPaths(int index, String[] location) {}

    public void registerEffectPath(int index, String location) {
        effectStrings.put(index, location);
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        boolean minimalTextures = PHConstruct.minimalTextures;
        addIcons(headStrings, headIcons, iconRegister, getIconSuffix(0), minimalTextures);
        addIcons(brokenPartStrings, brokenIcons, iconRegister, getIconSuffix(1), minimalTextures);
        addIcons(handleStrings, handleIcons, iconRegister, getIconSuffix(2), minimalTextures);
        addIcons(accessoryStrings, accessoryIcons, iconRegister, getIconSuffix(3), minimalTextures);
        addIcons(extraStrings, extraIcons, iconRegister, getIconSuffix(4), minimalTextures);

        addIcons(effectStrings, effectIcons, iconRegister, null, false);

        emptyIcon = iconRegister.registerIcon("tinker:blankface");
    }

    protected void addIcons(HashMap<Integer, String> textures, HashMap<Integer, IIcon> icons,
            IIconRegister iconRegister, String standard, boolean defaultOnly) {
        icons.clear();

        if (!defaultOnly) // compatibility mode: no specific textures
            for (Map.Entry<Integer, String> entry : textures.entrySet()) {
                if (TextureHelper.itemTextureExists(entry.getValue()))
                    icons.put(entry.getKey(), iconRegister.registerIcon(entry.getValue()));
            }

        if (standard != null && !standard.isEmpty()) {
            standard = getDefaultTexturePath() + "/" + standard;
            icons.put(-1, iconRegister.registerIcon(standard));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return blankSprite;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int renderPass) {
        NBTTagCompound tags = stack.getTagCompound();

        if (tags != null) {
            tags = stack.getTagCompound().getCompoundTag("InfiTool");
            if (renderPass < getPartAmount()) {
                // Handle
                if (renderPass == 0) return getCorrectIcon(handleIcons, tags.getInteger("RenderHandle"));
                // Head
                else if (renderPass == 1) {
                    if (tags.getBoolean("Broken")) return getCorrectIcon(brokenIcons, tags.getInteger("RenderHead"));
                    else return getCorrectIcon(headIcons, tags.getInteger("RenderHead"));
                }
                // Accessory
                else if (renderPass == 2) return getCorrectIcon(accessoryIcons, tags.getInteger("RenderAccessory"));
                // Extra
                else if (renderPass == 3) return getCorrectIcon(extraIcons, tags.getInteger("RenderExtra"));
            }
            // Effects
            else if (renderPass <= 10) {
                String effect = "Effect" + (1 + renderPass - getPartAmount());
                if (tags.hasKey(effect)) return effectIcons.get(tags.getInteger(effect));
            }
            return blankSprite;
        }
        return emptyIcon;
    }

    protected IIcon getCorrectIcon(Map<Integer, IIcon> icons, int id) {
        if (icons.containsKey(id)) return icons.get(id);

        // default icon
        return icons.get(-1);
    }

    /* Tags and information about the tool */
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound tags = stack.getTagCompound();
        if (tags.hasKey("Energy")) {
            String color = "";
            int RF = tags.getInteger("Energy");

            if (RF != 0) {
                if (RF <= this.getMaxEnergyStored(stack) / 3) color = "\u00a74";
                else if (RF > this.getMaxEnergyStored(stack) * 2 / 3) color = "\u00a72";
                else color = "\u00a76";
            }

            String energy = color + tags.getInteger("Energy") + "/" + getMaxEnergyStored(stack) + " RF";
            list.add(energy);
        }
        if (tags.hasKey("InfiTool")) {
            boolean broken = tags.getCompoundTag("InfiTool").getBoolean("Broken");
            if (broken) list.add("\u00A7o" + StatCollector.translateToLocal("tool.core.broken"));
            else {
                int head = tags.getCompoundTag("InfiTool").getInteger("Head");
                int handle = tags.getCompoundTag("InfiTool").getInteger("Handle");
                int binding = tags.getCompoundTag("InfiTool").getInteger("Accessory");
                int extra = tags.getCompoundTag("InfiTool").getInteger("Extra");

                String headName = getAbilityNameForType(head, 0);
                if (!headName.equals("")) list.add(getStyleForType(head) + headName);

                String handleName = getAbilityNameForType(handle, 1);
                if (!handleName.equals("") && handle != head) list.add(getStyleForType(handle) + handleName);

                if (getPartAmount() >= 3) {
                    String bindingName = getAbilityNameForType(binding, 2);
                    if (!bindingName.equals("") && binding != head && binding != handle)
                        list.add(getStyleForType(binding) + bindingName);
                }

                if (getPartAmount() >= 4) {
                    String extraName = getAbilityNameForType(extra, 3);
                    if (!extraName.equals("") && extra != head && extra != handle && extra != binding)
                        list.add(getStyleForType(extra) + extraName);
                }

                int unbreaking = tags.getCompoundTag("InfiTool").getInteger("Unbreaking");
                String reinforced = getReinforcedName(head, handle, binding, extra, unbreaking);
                if (!reinforced.equals("")) list.add(reinforced);

                boolean displayToolTips = true;
                int tipNum = 0;
                while (displayToolTips) {
                    tipNum++;
                    String tooltip = "Tooltip" + tipNum;
                    if (tags.getCompoundTag("InfiTool").hasKey(tooltip)) {
                        String tipName = tags.getCompoundTag("InfiTool").getString(tooltip);
                        if (!tipName.equals("")) {
                            // let's see if we can translate it somehow
                            // strip color information
                            String locString = "modifier.tooltip."
                                    + EnumChatFormatting.getTextWithoutFormattingCodes(tipName);
                            locString = locString.replace(" ", "");
                            if (StatCollector.canTranslate(locString)) tipName = tipName.replace(
                                    EnumChatFormatting.getTextWithoutFormattingCodes(tipName),
                                    StatCollector.translateToLocal(locString));

                            list.add(tipName);
                        }
                    } else displayToolTips = false;
                }
            }
        }
        list.add("");
        int attack = (int) (tags.getCompoundTag("InfiTool").getInteger("Attack") * this.getDamageModifier());
        list.add(
                "\u00A79+" + attack
                        + " "
                        + StatCollector.translateToLocalFormatted("attribute.name.generic.attackDamage"));
    }

    public static String getStyleForType(int type) {
        return TConstructRegistry.getMaterial(type).style();
    }

    /**
     * Returns the localized name of the materials ability. Only use this for display purposes, not for logic.
     */
    public String getAbilityNameForType(int type, int part) {
        return TConstructRegistry.getMaterial(type).ability();
    }

    public String getReinforcedName(int head, int handle, int accessory, int extra, int unbreaking) {
        tconstruct.library.tools.ToolMaterial headMat = TConstructRegistry.getMaterial(head);
        tconstruct.library.tools.ToolMaterial handleMat = TConstructRegistry.getMaterial(handle);
        tconstruct.library.tools.ToolMaterial accessoryMat = TConstructRegistry.getMaterial(accessory);
        tconstruct.library.tools.ToolMaterial extraMat = TConstructRegistry.getMaterial(extra);

        int reinforced = 0;
        String style = "";
        int current = headMat.reinforced();
        if (current > 0) {
            style = headMat.style();
            reinforced = current;
        }
        current = handleMat.reinforced();
        if (current > 0 && current > reinforced) {
            style = handleMat.style();
            reinforced = current;
        }
        if (getPartAmount() >= 3) {
            current = accessoryMat.reinforced();
            if (current > 0 && current > reinforced) {
                style = accessoryMat.style();
                reinforced = current;
            }
        }
        if (getPartAmount() >= 4) {
            current = extraMat.reinforced();
            if (current > 0 && current > reinforced) {
                style = extraMat.style();
                reinforced = current;
            }
        }

        reinforced += unbreaking - reinforced;

        if (reinforced > 0) {
            return style + getReinforcedString(reinforced);
        }
        return "";
    }

    String getReinforcedString(int reinforced) {
        if (reinforced > 9) return StatCollector.translateToLocal("tool.unbreakable");
        String ret = StatCollector.translateToLocal("tool.reinforced") + " ";
        switch (reinforced) {
            case 1:
                ret += "I";
                break;
            case 2:
                ret += "II";
                break;
            case 3:
                ret += "III";
                break;
            case 4:
                ret += "IV";
                break;
            case 5:
                ret += "V";
                break;
            case 6:
                ret += "VI";
                break;
            case 7:
                ret += "VII";
                break;
            case 8:
                ret += "VIII";
                break;
            case 9:
                ret += "IX";
                break;
            default:
                ret += "X";
                break;
        }
        return ret;
    }

    // Used for sounds and the like
    public void onEntityDamaged(World world, EntityLivingBase player, Entity entity) {}

    /* Creative mode tools */

    @Override
    public void getSubItems(Item id, CreativeTabs tab, List list) {
        for (Map.Entry<Integer, tconstruct.library.tools.ToolMaterial> integerToolMaterialEntry : TConstructRegistry.toolMaterials
                .entrySet()) {
            tconstruct.library.tools.ToolMaterial material = integerToolMaterialEntry.getValue();
            buildTool(integerToolMaterialEntry.getKey(), null, list);
        }
    }

    public void buildTool(int id, String name, List list) {
        Item accessory = getAccessoryItem();
        ItemStack accessoryStack = accessory != null ? new ItemStack(getAccessoryItem(), 1, id) : null;
        Item extra = getExtraItem();
        ItemStack extraStack = extra != null ? new ItemStack(extra, 1, id) : null;
        ItemStack tool = ToolBuilder.instance.buildTool(
                new ItemStack(getHeadItem(), 1, id),
                new ItemStack(getHandleItem(), 1, id),
                accessoryStack,
                extraStack,
                name);
        if (tool != null) {
            tool.getTagCompound().getCompoundTag("InfiTool").setBoolean("Built", true);
            list.add(tool);
        }
    }

    public abstract Item getHeadItem();

    public abstract Item getAccessoryItem();

    public Item getExtraItem() {
        return null;
    }

    public Item getHandleItem() {
        return TinkerTools.toolRod;
    }

    /* Updating */

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
        for (ActiveToolMod mod : TConstructRegistry.activeModifiers) {
            mod.updateTool(this, stack, world, entity);
        }
    }

    /* Tool uses */

    // Types
    public abstract String[] getTraits();

    // Mining
    @Override
    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        if (!stack.hasTagCompound()) return false;

        boolean cancelHarvest = false;
        for (ActiveToolMod mod : TConstructRegistry.activeModifiers) {
            if (mod.beforeBlockBreak(this, stack, x, y, z, player)) cancelHarvest = true;
        }

        return cancelHarvest;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack itemstack, World world, Block block, int x, int y, int z,
            EntityLivingBase player) {
        if (!itemstack.hasTagCompound()) return false;

        // callbacks!
        for (ActiveToolMod mod : TConstructRegistry.activeModifiers)
            mod.afterBlockBreak(this, itemstack, block, x, y, z, player);

        if (block != null && (double) block.getBlockHardness(world, x, y, z) != 0.0D) {
            return AbilityHelper.onBlockChanged(itemstack, world, block, x, y, z, player, random);
        }
        return true;
    }

    @Override
    public float getDigSpeed(ItemStack stack, Block block, int meta) {
        if (!stack.hasTagCompound()) return 0f;

        NBTTagCompound tags = stack.getTagCompound();
        if (tags.getCompoundTag("InfiTool").getBoolean("Broken")) return 0.1f;
        return 1f;
    }

    // Attacking
    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        return AbilityHelper.onLeftClickEntity(stack, player, entity, this);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase mob, EntityLivingBase player) {
        return true;
    }

    public boolean pierceArmor() {
        return false;
    }

    public float chargeAttack() {
        return 1f;
    }

    public int getDamageVsEntity(Entity par1Entity) {
        return this.damageVsEntity;
    }

    // Changes how much durability the base tool has
    public float getDurabilityModifier() {
        return 1f;
    }

    public float getRepairCost() {
        return getDurabilityModifier();
    }

    public float getDamageModifier() {
        return 1.0f;
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        NBTTagCompound tags = stack.getTagCompound();

        if (tags != null) {
            tags = stack.getTagCompound().getCompoundTag("InfiTool");
            if (renderPass < getPartAmount()) {
                switch (renderPass) {
                    case 0:
                        return getCorrectColor(stack, renderPass, tags, "Handle", handleIcons);
                    case 1:
                        return tags.getBoolean("Broken") ? getCorrectColor(stack, renderPass, tags, "Head", brokenIcons)
                                : getCorrectColor(stack, renderPass, tags, "Head", headIcons);
                    case 2:
                        return getCorrectColor(stack, renderPass, tags, "Accessory", accessoryIcons);
                    case 3:
                        return getCorrectColor(stack, renderPass, tags, "Extra", extraIcons);
                }
            }
        }
        return super.getColorFromItemStack(stack, renderPass);
    }

    protected int getCorrectColor(ItemStack stack, int renderPass, NBTTagCompound tags, String key,
            Map<Integer, IIcon> map) {
        // custom coloring
        if (tags.hasKey(key + "Color")) return tags.getInteger(key + "Color");

        // custom texture?
        int matId = tags.getInteger("Render" + key);
        if (map.containsKey(matId)) return super.getColorFromItemStack(stack, renderPass);

        // color default texture with material color
        return getDefaultColor(renderPass, matId);
    }

    protected int getDefaultColor(int renderPass, int materialID) {
        if (TConstructRegistry.getMaterial(materialID) != null)
            return TConstructRegistry.getMaterial(materialID).primaryColor();

        return 0xffffffff;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        boolean used = false;
        int hotbarSlot = player.inventory.currentItem;
        int itemSlot = hotbarSlot == 0 ? 8 : hotbarSlot + 1;
        ItemStack nearbyStack;

        if (hotbarSlot < 8) {
            nearbyStack = player.inventory.getStackInSlot(itemSlot);
            if (nearbyStack != null) {
                Item item = nearbyStack.getItem();
                if (item instanceof ItemPotion && ItemPotion.isSplash(nearbyStack.getItemDamage())) {
                    nearbyStack = item.onItemRightClick(nearbyStack, world, player);
                    if (nearbyStack.stackSize < 1) {
                        nearbyStack = null;
                        player.inventory.setInventorySlotContents(itemSlot, null);
                    }
                }

                // throw shurikens!
                if (item != null && item == TinkerWeaponry.shuriken) {
                    item.onItemRightClick(nearbyStack, world, player);
                }
            }
        }
        return stack;
    }

    /* Vanilla overrides */
    @Override
    public boolean isItemTool(ItemStack par1ItemStack) {
        return false;
    }

    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return false;
    }

    @Override
    public boolean isRepairable() {
        return false;
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }

    @Override
    public boolean isFull3D() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack par1ItemStack, int pass) {
        return false;
    }

    /* Proper stack damage */
    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        if (!stack.hasTagCompound()) return false;

        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        return !tags.getBoolean("Broken") && getDamage(stack) > 0;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return 100;
    }

    @Override
    public int getDamage(ItemStack stack) {
        NBTTagCompound tags = stack.getTagCompound();
        if (tags == null) {
            return 0;
        }
        if (tags.hasKey("Energy")) {
            int energy = tags.getInteger("Energy");
            int max = getMaxEnergyStored(stack);
            if (energy > 0) {
                int damage = ((max - energy) * 100) / max;
                if (damage == 0 && max - energy > 0) damage = 1;
                super.setDamage(stack, damage);
                return damage;
            }
        }
        int dur = tags.getCompoundTag("InfiTool").getInteger("Damage");
        int max = tags.getCompoundTag("InfiTool").getInteger("TotalDurability");
        int damage = 0;
        if (max > 0) damage = (dur * 100) / max;

        // rounding.
        if (damage == 0 && dur > 0) damage = 1;

        // synchronize values with stack..
        super.setDamage(stack, damage);
        return damage;
    }

    @Override
    public int getDisplayDamage(ItemStack stack) {
        return getDamage(stack);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        int change = damage - stack.getItemDamage();
        if (change == 0) return;

        AbilityHelper.damageTool(stack, change, null, false);
        getDamage(stack); // called to synchronize with itemstack value
    }

    /* Prevent tools from dying */
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new FancyEntityItem(world, location, itemstack);
    }

    // TE support section -- from COFH core API reference section

    // TE power constants. These are only for backup if the lookup of the real value somehow fails!
    protected int capacity = 400000;
    protected int maxReceive = 400000;
    protected int maxExtract = 80;

    /* IEnergyContainerItem */
    @Override
    @Optional.Method(modid = "CoFHAPI|energy")
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        NBTTagCompound tags = container.getTagCompound();
        if (tags == null || !tags.hasKey("Energy")) return 0;
        int energy = tags.getInteger("Energy");
        int energyReceived = tags.hasKey("EnergyReceiveRate") ? tags.getInteger("EnergyReceiveRate") : this.maxReceive; // backup
                                                                                                                        // value
        int maxEnergy = tags.hasKey("EnergyMax") ? tags.getInteger("EnergyMax") : this.capacity; // backup value

        // calculate how much we can receive
        energyReceived = Math.min(maxEnergy - energy, Math.min(energyReceived, maxReceive));
        if (!simulate) {
            energy += energyReceived;
            tags.setInteger("Energy", energy);
            // container.setItemDamage(1 + (getMaxEnergyStored(container) - energy) * (container.getMaxDamage() - 2) /
            // getMaxEnergyStored(container));
        }
        return energyReceived;
    }

    @Override
    @Optional.Method(modid = "CoFHAPI|energy")
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        NBTTagCompound tags = container.getTagCompound();
        if (tags == null || !tags.hasKey("Energy")) {
            return 0;
        }
        int energy = tags.getInteger("Energy");
        int energyExtracted = tags.hasKey("EnergyExtractionRate") ? tags.getInteger("EnergyExtractionRate")
                : this.maxExtract; // backup value

        // calculate how much we can extract
        energyExtracted = Math.min(energy, Math.min(energyExtracted, maxExtract));
        if (!simulate) {
            energy -= energyExtracted;
            tags.setInteger("Energy", energy);
            // container.setItemDamage(1 + (getMaxEnergyStored(container) - energy) * (container.getMaxDamage() - 1) /
            // getMaxEnergyStored(container));
        }
        return energyExtracted;
    }

    @Override
    @Optional.Method(modid = "CoFHAPI|energy")
    public int getEnergyStored(ItemStack container) {
        NBTTagCompound tags = container.getTagCompound();
        if (tags == null || !tags.hasKey("Energy")) {
            return 0;
        }
        return tags.getInteger("Energy");
    }

    @Override
    @Optional.Method(modid = "CoFHAPI|energy")
    public int getMaxEnergyStored(ItemStack container) {
        NBTTagCompound tags = container.getTagCompound();
        if (tags == null || !tags.hasKey("Energy")) return 0;

        if (tags.hasKey("EnergyMax")) return tags.getInteger("EnergyMax");
        // backup
        return capacity;
    }

    @Override
    @Optional.Method(modid = "CoFHCore")
    public boolean isLastHeldItemEqual(ItemStack current, ItemStack previous) {
        if (!current.hasTagCompound() || !previous.hasTagCompound()) return false;

        NBTTagCompound curTags = current.getTagCompound();
        NBTTagCompound prevTags = previous.getTagCompound();
        if (curTags == prevTags) return true;
        if (!curTags.hasKey("InfiTool") || !prevTags.hasKey("InfiTool")) return false;

        // create copies so we don't modify the original
        curTags = (NBTTagCompound) curTags.copy();
        prevTags = (NBTTagCompound) prevTags.copy();

        curTags.removeTag("Energy");
        prevTags.removeTag("Energy");
        curTags.getCompoundTag("InfiTool").removeTag("Damage");
        prevTags.getCompoundTag("InfiTool").removeTag("Damage");

        return curTags.equals(prevTags);
    }
    // end of TE support section

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String name = ToolBuilder.defaultToolName(stack);
        return name != null ? name : super.getItemStackDisplayName(stack);
    }
}
