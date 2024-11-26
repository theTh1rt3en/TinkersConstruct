package tconstruct.armor.gui;

import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import tconstruct.armor.inventory.KnapsackContainer;
import tconstruct.armor.player.KnapsackInventory;
import tconstruct.client.tabs.InventoryTabKnapsack;
import tconstruct.client.tabs.TabRegistry;

public class KnapsackGui extends InventoryEffectRenderer {

    public InventoryPlayer inv;
    public KnapsackInventory stats;

    public KnapsackGui(InventoryPlayer inventoryplayer, KnapsackInventory holder) {
        super(new KnapsackContainer(inventoryplayer, holder));
        inv = inventoryplayer;
        stats = holder;
    }

    @Override
    public void initGui() {
        super.initGui();

        int cornerX = guiLeft;
        int cornerY = guiTop;
        this.buttonList.clear();

        TabRegistry.updateTabValues(cornerX, cornerY, InventoryTabKnapsack.class);
        TabRegistry.addTabsToList(this.buttonList);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        fontRendererObj.drawString(StatCollector.translateToLocal("inventory.knapsack"), 8, 6, 0x404040);
        fontRendererObj
                .drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 4, 0x404040);
    }

    private static final ResourceLocation background = new ResourceLocation("tinker", "textures/gui/knapsack.png");

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);
        int cornerX = guiLeft;
        int cornerY = guiTop;
        drawTexturedModalRect(cornerX, cornerY, 0, 0, xSize, ySize);
    }
}
