package tconstruct.plugins.nei;

import static tconstruct.util.Reference.MOD_ID;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import codechicken.nei.PositionedStack;
import codechicken.nei.api.IStackPositioner;
import codechicken.nei.recipe.GuiRecipe;
import lombok.extern.log4j.Log4j2;
import tconstruct.tools.gui.CraftingStationGui;

@Log4j2(topic = MOD_ID)
public class CraftingStationStackPositioner implements IStackPositioner {

    @Override
    public ArrayList<PositionedStack> positionStacks(ArrayList<PositionedStack> stacks) {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;

        if (screen instanceof GuiRecipe) {
            screen = ((GuiRecipe<?>) screen).firstGui;
        }

        if (screen instanceof CraftingStationGui) {
            CraftingStationGui gui = (CraftingStationGui) screen;

            int offsetX = gui.hasChest() ? 5 + gui.getChestWidth() : 5;
            int offsetY = 11;

            for (PositionedStack stack : stacks) {
                stack.relx += offsetX;
                stack.rely += offsetY;
            }
        } else {
            log.warn("No CraftingStationGui found!");
        }

        return stacks;
    }
}
