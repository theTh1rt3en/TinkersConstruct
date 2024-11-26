package tconstruct.armor.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import tconstruct.armor.inventory.ArmorExtendedContainer;
import tconstruct.armor.player.ArmorExtended;
import tconstruct.client.tabs.InventoryTabArmorExtended;
import tconstruct.client.tabs.TabRegistry;

public class ArmorExtendedGui extends InventoryEffectRenderer {

    public InventoryPlayer inv;
    public ArmorExtended stats;

    private float xSize_lo;
    private float ySize_lo;

    public ArmorExtendedGui(InventoryPlayer inventoryplayer, ArmorExtended holder) {
        super(new ArmorExtendedContainer(inventoryplayer, holder));
        inv = inventoryplayer;
        stats = holder;
    }

    @Override
    public void initGui() {
        super.initGui();

        int cornerX = guiLeft;
        int cornerY = guiTop;
        this.buttonList.clear();

        TabRegistry.updateTabValues(cornerX, cornerY, InventoryTabArmorExtended.class);
        TabRegistry.addTabsToList(this.buttonList);
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        super.drawScreen(par1, par2, par3);
        this.xSize_lo = par1;
        this.ySize_lo = par2;
    }

    private static final ResourceLocation background = new ResourceLocation("tinker", "textures/gui/armorextended.png");

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);
        int cornerX = guiLeft;
        int cornerY = guiTop;
        drawTexturedModalRect(cornerX, guiTop, 0, 0, xSize, ySize);

        if (!stats.isStackInSlot(0)) drawTexturedModalRect(cornerX + 79, cornerY + 16, 176, 9, 18, 18);
        if (!stats.isStackInSlot(1)) drawTexturedModalRect(cornerX + 79, cornerY + 34, 176, 27, 18, 18);
        if (!stats.isStackInSlot(2)) drawTexturedModalRect(cornerX + 115, cornerY + 16, 212, 9, 18, 18);
        if (!stats.isStackInSlot(3)) drawTexturedModalRect(cornerX + 115, cornerY + 34, 212, 27, 18, 18);
        if (!stats.isStackInSlot(4)) drawTexturedModalRect(cornerX + 151, cornerY + 52, 230, 0, 18, 18);
        if (!stats.isStackInSlot(5)) drawTexturedModalRect(cornerX + 151, cornerY + 34, 230, 18, 18, 18);
        if (!stats.isStackInSlot(6)) drawTexturedModalRect(cornerX + 151, cornerY + 16, 230, 36, 18, 18);

        cornerX = this.guiLeft;
        cornerY = this.guiTop;
        drawPlayerOnGui(
                this.mc,
                cornerX + 33,
                cornerY + 75,
                30,
                (cornerX + 51) - this.xSize_lo,
                (cornerY + 75 - 50) - this.ySize_lo);
    }

    public static void drawPlayerOnGui(Minecraft par0Minecraft, int par1, int par2, int par3, float par4, float par5) {
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef(par1, par2, 50.0F);
        GL11.glScalef(-par3, par3, par3);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        float f2 = par0Minecraft.thePlayer.renderYawOffset;
        float f3 = par0Minecraft.thePlayer.rotationYaw;
        float f4 = par0Minecraft.thePlayer.rotationPitch;
        par4 -= 19;
        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-((float) Math.atan(par5 / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
        par0Minecraft.thePlayer.renderYawOffset = (float) Math.atan(par4 / 40.0F) * 20.0F;
        par0Minecraft.thePlayer.rotationYaw = (float) Math.atan(par4 / 40.0F) * 40.0F;
        par0Minecraft.thePlayer.rotationPitch = -((float) Math.atan(par5 / 40.0F)) * 20.0F;
        par0Minecraft.thePlayer.rotationYawHead = par0Minecraft.thePlayer.rotationYaw;
        GL11.glTranslatef(0.0F, par0Minecraft.thePlayer.yOffset, 0.0F);
        RenderManager.instance.playerViewY = 180.0F;
        RenderManager.instance.renderEntityWithPosYaw(par0Minecraft.thePlayer, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        par0Minecraft.thePlayer.renderYawOffset = f2;
        par0Minecraft.thePlayer.rotationYaw = f3;
        par0Minecraft.thePlayer.rotationPitch = f4;
        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
}
