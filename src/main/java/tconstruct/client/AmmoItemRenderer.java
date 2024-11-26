package tconstruct.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import tconstruct.library.weaponry.IAmmo;

public class AmmoItemRenderer extends FlexibleToolRenderer {

    public AmmoItemRenderer() {}

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        if (type == ItemRenderType.INVENTORY) return true;

        return super.handleRenderType(item, type);
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        // render the item regularly
        super.renderItem(type, item, data);
        if (item.getTagCompound() == null || type != ItemRenderType.INVENTORY) return;

        // render custom stacksize
        renderAmmoCount(item);
    }

    public void renderAmmoCount(ItemStack item) {
        if (!(item.getItem() instanceof IAmmo)) return;
        int amount = ((IAmmo) item.getItem()).getAmmoCount(item);
        String str = String.valueOf(amount);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glScalef(0.7f, 0.7f, 0.7f);
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        fontRenderer.drawStringWithShadow(str, 7 + 19 - 2 - fontRenderer.getStringWidth(str), 7 + 6 + 3, 16777215);

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
}
