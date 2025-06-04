package tconstruct.armor;

import static tconstruct.util.Reference.MOD_ID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.IGuiHandler;
import lombok.extern.log4j.Log4j2;
import tconstruct.armor.inventory.ArmorExtendedContainer;
import tconstruct.armor.inventory.KnapsackContainer;
import tconstruct.armor.player.TPlayerStats;
import tconstruct.common.TProxyCommon;

@Log4j2(topic = MOD_ID)
public class ArmorProxyCommon implements IGuiHandler {

    public static final int inventoryGui = 100;
    public static final int armorGuiID = 101;
    public static final int knapsackGuiID = 102;

    public void preInit() {}

    public void initialize() {
        registerGuiHandler();
    }

    protected void registerGuiHandler() {
        TProxyCommon.registerServerGuiHandler(inventoryGui, this);
        TProxyCommon.registerServerGuiHandler(armorGuiID, this);
        TProxyCommon.registerServerGuiHandler(knapsackGuiID, this);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == ArmorProxyCommon.inventoryGui) {
            return player.inventoryContainer;
        }
        if (ID == ArmorProxyCommon.armorGuiID) {
            TPlayerStats stats = TPlayerStats.get(player);
            return new ArmorExtendedContainer(player.inventory, stats.armor);
        }
        if (ID == ArmorProxyCommon.knapsackGuiID) {
            TPlayerStats stats = TPlayerStats.get(player);
            return new KnapsackContainer(player.inventory, stats.knapsack);
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        // TODO Auto-generated method stub
        return null;
    }

    public void registerTickHandler() {}

    public void registerKeys() {}

    public void updatePlayerStats(TPlayerStats stats) {}

    public void dumpTPlayerStats(TPlayerStats stats) {
        log.debug("~~~~~~~~~~~~~~~~~ STATS ~~~~~~~~~~~~~~~~~");
        log.debug("Player: {}", stats.player.get().getCommandSenderName());
        log.debug("Level: {}", stats.level);
        log.debug("BonusHealth: {}", stats.bonusHealth);
        log.debug("Damage: {}", stats.damage);
        log.debug("Hunger: {}", stats.hunger);
        log.debug("Prev Dim: {}", stats.previousDimension);
        log.debug("Climb Walls: {}", stats.climbWalls);
        log.debug("Activate Goggles: {}", stats.activeGoggles);
        log.debug("Beginner Manual: {}", stats.beginnerManual);
        log.debug("Material Manual: {}", stats.materialManual);
        log.debug("Smeltery Manual: {}", stats.smelteryManual);
        log.debug("Weaponry Manual: {}", stats.weaponryManual);
        log.debug("BattleSign Bonus: {}", stats.battlesignBonus);
        log.debug("Derp Level: {}", stats.derpLevel);
        int i = 0;
        for (ItemStack stack : stats.armor.inventory) {
            if (stack != null) {
                log.debug("Armor Slot: {} Contains: {}", i, stack.getDisplayName());
            }
            i++;
        }
        i = 0;
        for (ItemStack stack : stats.knapsack.inventory) {
            if (stack != null) {
                log.debug("Knapsack Slot: {} Contains: {}", i, stack.getDisplayName());
            }
            i++;
        }
        log.debug("~~~~~~~~~~~~~~~~~ STATS ~~~~~~~~~~~~~~~~~");
    }
}
