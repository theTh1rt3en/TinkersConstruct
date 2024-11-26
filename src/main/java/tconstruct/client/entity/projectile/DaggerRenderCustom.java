package tconstruct.client.entity.projectile;

import java.util.Random;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tconstruct.client.ToolCoreRenderer;
import tconstruct.tools.entity.DaggerEntity;

@SideOnly(Side.CLIENT)
@Deprecated
public class DaggerRenderCustom extends Render {

    private static final RenderItem renderer = new RenderItem();
    private static final ToolCoreRenderer toolCoreRenderer = new ToolCoreRenderer(true, true);
    private final Random random = new Random();

    public DaggerRenderCustom() {
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
    }

    /**
     * Renders the item
     */
    public void doRenderItem(DaggerEntity dagger, double par2, double par4, double par6, float par8, float par9) {
        random.setSeed(187L);
        ItemStack item = dagger.getEntityItem();
        GL11.glPushMatrix();
        GL11.glTranslatef((float) par2, (float) par4, (float) par6);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        GL11.glRotatef(
                dagger.prevRotationYaw + (dagger.rotationYaw - dagger.prevRotationYaw) * par9 - 90.0F,
                0.0F,
                1.0F,
                0.0F);
        GL11.glRotatef(
                dagger.prevRotationPitch + (dagger.rotationPitch - dagger.prevRotationPitch) * par9 - 45.0F,
                0.0F,
                0.0F,
                1.0F);

        float rotation = dagger.prevRotationPitch + (dagger.rotationPitch - dagger.prevRotationPitch) * par9;
        GL11.glRotatef(dagger.rotationYaw + 90, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(rotation * 15, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(-0.25f, -0.25f, 0f); // translate to the middle. This makes it so that the dagger rotates
                                               // around its center
        float shake = dagger.arrowShake - par9;
        if (shake > 0.0F) GL11.glRotatef(-MathHelper.sin(shake * 3) * shake, 0, 0, 1);
        float scale = 1.35f;
        GL11.glScalef(scale, scale, scale);

        /* begin hardcoded regular item rendering */
        // see ForgeHooksClient.renderEntityItem
        renderManager.renderEngine.bindTexture(TextureMap.locationItemsTexture);
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        toolCoreRenderer.renderItem(IItemRenderer.ItemRenderType.ENTITY, item);
        /* end hardcoded regular item rendering */

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    @Override
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
        this.doRenderItem((DaggerEntity) par1Entity, par2, par4, par6, par8, par9);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return this.func_110796_a((DaggerEntity) par1Entity);
    }

    protected ResourceLocation func_110796_a(DaggerEntity par1ArrowEntity) {
        return this.renderManager.renderEngine
                .getResourceLocation(par1ArrowEntity.getEntityItem().getItemSpriteNumber());
    }
}
