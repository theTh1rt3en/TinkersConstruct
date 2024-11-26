package tconstruct.plugins.te4;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.event.FMLInterModComms;

/**
 * Taken from CoFHLib
 */
public class TE4Helper {

    public static void addPulverizerRecipe(int energy, ItemStack input, ItemStack primaryOutput,
            ItemStack secondaryOutput, int secondaryChance) {

        if (input == null || primaryOutput == null || secondaryOutput == null) {
            return;
        }
        NBTTagCompound toSend = new NBTTagCompound();

        toSend.setInteger("energy", energy);
        toSend.setTag("input", new NBTTagCompound());
        toSend.setTag("primaryOutput", new NBTTagCompound());
        toSend.setTag("secondaryOutput", new NBTTagCompound());

        input.writeToNBT(toSend.getCompoundTag("input"));
        primaryOutput.writeToNBT(toSend.getCompoundTag("primaryOutput"));
        secondaryOutput.writeToNBT(toSend.getCompoundTag("secondaryOutput"));
        toSend.setInteger("secondaryChance", secondaryChance);

        FMLInterModComms.sendMessage("ThermalExpansion", "PulverizerRecipe", toSend);
    }

    public static void addSmelterRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput,
            ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

        if (primaryInput == null || secondaryInput == null || primaryOutput == null || secondaryOutput == null) {
            return;
        }
        NBTTagCompound toSend = new NBTTagCompound();

        toSend.setInteger("energy", energy);
        toSend.setTag("primaryInput", new NBTTagCompound());
        toSend.setTag("secondaryInput", new NBTTagCompound());
        toSend.setTag("primaryOutput", new NBTTagCompound());
        toSend.setTag("secondaryOutput", new NBTTagCompound());

        primaryInput.writeToNBT(toSend.getCompoundTag("primaryInput"));
        secondaryInput.writeToNBT(toSend.getCompoundTag("secondaryInput"));
        primaryOutput.writeToNBT(toSend.getCompoundTag("primaryOutput"));
        secondaryOutput.writeToNBT(toSend.getCompoundTag("secondaryOutput"));
        toSend.setInteger("secondaryChance", secondaryChance);

        FMLInterModComms.sendMessage("ThermalExpansion", "SmelterRecipe", toSend);
    }

}
