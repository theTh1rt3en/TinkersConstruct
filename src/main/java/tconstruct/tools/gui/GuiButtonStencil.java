package tconstruct.tools.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import tconstruct.library.client.StencilGuiElement;

public class GuiButtonStencil extends GuiButton {

    /**
     * True for pointing right (next page), false for pointing left (previous page).
     */
    int textureX;

    int textureY;
    public String texture;
    public StencilGuiElement element;
    private static ResourceLocation background;

    public GuiButtonStencil(int id, int posX, int posY, int texX, int texY, String domain, String tex,
            StencilGuiElement e) {
        super(id, posX, posY, 18, 18, "");
        textureX = texX;
        textureY = texY;
        texture = tex;
        element = e;
        background = new ResourceLocation(domain, tex);
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(background);

            this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition
                    && mouseX < this.xPosition + this.width
                    && mouseY < this.yPosition + this.height;
            int index = 18 * getHoverState(field_146123_n);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 144 + index * 2, 234, 18, 18);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, textureX * 18, textureY * 18, 18, 18);
        }
    }
}
