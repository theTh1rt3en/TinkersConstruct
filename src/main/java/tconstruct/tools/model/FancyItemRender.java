package tconstruct.tools.model;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FancyItemRender extends Render {

    private static final ResourceLocation field_110798_h = new ResourceLocation(
            "textures/misc/enchanted_item_glint.png");
    private final RenderBlocks itemRenderBlocks = new RenderBlocks();

    /** The RNG used in RenderItem (for bobbing itemstacks on the ground) */
    private final Random random = new Random();

    public boolean renderWithColor = true;

    /** Defines the zLevel of rendering of item on GUI. */
    public float zLevel;

    public static boolean renderInFrame;

    public FancyItemRender() {
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
    }

    /**
     * Renders the item
     */
    public void doRenderItem(EntityItem par1EntityItem, double par2, double par4, double par6, float par8, float par9) {
        this.bindEntityTexture(par1EntityItem);
        this.random.setSeed(187L);
        ItemStack itemstack = par1EntityItem.getEntityItem();

        if (itemstack.getItem() != null) {
            GL11.glPushMatrix();
            float f2 = shouldBob()
                    ? MathHelper.sin(((float) par1EntityItem.age + par9) / 10.0F + par1EntityItem.hoverStart) * 0.1F
                            + 0.1F
                    : 0F;
            float f3 = (((float) par1EntityItem.age + par9) / 20.0F + par1EntityItem.hoverStart)
                    * (180F / (float) Math.PI);
            byte b0 = getMiniBlockCount(itemstack);

            GL11.glTranslatef((float) par2, (float) par4 + f2, (float) par6);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            float f4;
            float f5;
            float f6;
            int i;

            Block block = Block.getBlockFromItem(itemstack.getItem());

            if (ForgeHooksClient.renderEntityItem(
                    par1EntityItem,
                    itemstack,
                    f2,
                    f3,
                    random,
                    renderManager.renderEngine,
                    field_147909_c,
                    1)) {} else
                if (itemstack.getItemSpriteNumber() == 0 && block != null
                        && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack.getItem()).getRenderType())) {
                            GL11.glRotatef(f3, 0.0F, 1.0F, 0.0F);

                            if (renderInFrame) {
                                GL11.glScalef(1.25F, 1.25F, 1.25F);
                                GL11.glTranslatef(0.0F, 0.05F, 0.0F);
                                GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                            }

                            float f7 = 0.25F;
                            int j = block.getRenderType();

                            if (j == 1 || j == 19 || j == 12 || j == 2) {
                                f7 = 0.5F;
                            }

                            GL11.glScalef(f7, f7, f7);

                            for (i = 0; i < b0; ++i) {
                                GL11.glPushMatrix();

                                if (i > 0) {
                                    f5 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / f7;
                                    f4 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / f7;
                                    f6 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / f7;
                                    GL11.glTranslatef(f5, f4, f6);
                                }

                                f5 = 1.0F;
                                this.itemRenderBlocks.renderBlockAsItem(block, itemstack.getItemDamage(), f5);
                                GL11.glPopMatrix();
                            }
                        } else {
                            float f8;

                            if (itemstack.getItemSpriteNumber() == 1
                                    && itemstack.getItem().requiresMultipleRenderPasses()) {
                                if (renderInFrame) {
                                    GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
                                    GL11.glTranslatef(0.0F, -0.05F, 0.0F);
                                } else {
                                    GL11.glScalef(0.5F, 0.5F, 0.5F);
                                }

                                for (int k = 0; k
                                        < itemstack.getItem().getRenderPasses(itemstack.getItemDamage()); ++k) {
                                    this.random.setSeed(187L);
                                    IIcon icon = itemstack.getItem().getIcon(itemstack, k);
                                    f8 = 1.0F;

                                    if (this.renderWithColor) {
                                        i = itemstack.getItem().getColorFromItemStack(itemstack, k);
                                        f5 = (i >> 16 & 255) / 255.0F;
                                        f4 = (i >> 8 & 255) / 255.0F;
                                        f6 = (i & 255) / 255.0F;
                                        GL11.glColor4f(f5 * f8, f4 * f8, f6 * f8, 1.0F);
                                        this.renderDroppedItem(
                                                par1EntityItem,
                                                icon,
                                                b0,
                                                par9,
                                                f5 * f8,
                                                f4 * f8,
                                                f6 * f8,
                                                k);
                                    } else {
                                        this.renderDroppedItem(par1EntityItem, icon, b0, par9, 1.0F, 1.0F, 1.0F, k);
                                    }
                                }
                            } else {
                                if (renderInFrame) {
                                    GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
                                    GL11.glTranslatef(0.0F, -0.05F, 0.0F);
                                } else {
                                    GL11.glScalef(0.5F, 0.5F, 0.5F);
                                }

                                IIcon icon1 = itemstack.getIconIndex();

                                if (this.renderWithColor) {
                                    int l = itemstack.getItem().getColorFromItemStack(itemstack, 0);
                                    f8 = (l >> 16 & 255) / 255.0F;
                                    float f9 = (l >> 8 & 255) / 255.0F;
                                    f5 = (l & 255) / 255.0F;
                                    f4 = 1.0F;
                                    this.renderDroppedItem(par1EntityItem, icon1, b0, par9, f8 * f4, f9 * f4, f5 * f4);
                                } else {
                                    this.renderDroppedItem(par1EntityItem, icon1, b0, par9, 1.0F, 1.0F, 1.0F);
                                }
                            }
                        }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
        }
    }

    protected ResourceLocation func_110796_a(EntityItem par1EntityItem) {
        return this.renderManager.renderEngine
                .getResourceLocation(par1EntityItem.getEntityItem().getItemSpriteNumber());
    }

    /**
     * Renders a dropped item
     */
    private void renderDroppedItem(EntityItem par1EntityItem, IIcon par2Icon, int par3, float par4, float par5,
            float par6, float par7) {
        renderDroppedItem(par1EntityItem, par2Icon, par3, par4, par5, par6, par7, 0);
    }

    private void renderDroppedItem(EntityItem par1EntityItem, IIcon par2Icon, int par3, float par4, float par5,
            float par6, float par7, int pass) {
        Tessellator tessellator = Tessellator.instance;

        if (par2Icon == null) {
            TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
            ResourceLocation resourcelocation = texturemanager
                    .getResourceLocation(par1EntityItem.getEntityItem().getItemSpriteNumber());
            par2Icon = ((TextureMap) texturemanager.getTexture(resourcelocation)).getAtlasSprite("missingno");
        }

        float f4 = par2Icon.getMinU();
        float f5 = par2Icon.getMaxU();
        float f6 = par2Icon.getMinV();
        float f7 = par2Icon.getMaxV();
        float f9 = 0.5F;
        float f10 = 0.25F;
        float f11;

        GL11.glPushMatrix();

        if (renderInFrame) {
            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        } else {
            GL11.glRotatef(
                    (((float) par1EntityItem.age + par4) / 20.0F + par1EntityItem.hoverStart)
                            * (180F / (float) Math.PI),
                    0.0F,
                    1.0F,
                    0.0F);
        }

        float f12 = 0.0625F;
        f11 = 0.021875F;
        ItemStack itemstack = par1EntityItem.getEntityItem();
        byte b0 = getMiniItemCount(itemstack);

        GL11.glTranslatef(-f9, -f10, -((f12 + f11) * b0 / 2.0F));

        for (int k = 0; k < b0; ++k) {
            // Makes items offset when in 3D, like when in 2D, looks much better. Considered a vanilla bug...
            if (k > 0 && shouldSpreadItems()) {
                float x = (random.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
                float y = (random.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
                GL11.glTranslatef(x, y, f12 + f11);
            } else {
                GL11.glTranslatef(0f, 0f, f12 + f11);
            }

            if (itemstack.getItemSpriteNumber() == 0) {
                this.bindTexture(TextureMap.locationBlocksTexture);
            } else {
                this.bindTexture(TextureMap.locationItemsTexture);
            }

            GL11.glColor4f(par5, par6, par7, 1.0F);
            ItemRenderer.renderItemIn2D(
                    tessellator,
                    f5,
                    f6,
                    f4,
                    f7,
                    par2Icon.getIconWidth(),
                    par2Icon.getIconHeight(),
                    f12);

            if (itemstack.hasEffect(pass)) {
                GL11.glDepthFunc(GL11.GL_EQUAL);
                GL11.glDisable(GL11.GL_LIGHTING);
                this.renderManager.renderEngine.bindTexture(field_110798_h);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                float f13 = 0.76F;
                GL11.glColor4f(0.5F * f13, 0.25F * f13, 0.8F * f13, 1.0F);
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glPushMatrix();
                float f14 = 0.125F;
                GL11.glScalef(f14, f14, f14);
                float f15 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                GL11.glTranslatef(f15, 0.0F, 0.0F);
                GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, f12);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(f14, f14, f14);
                f15 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                GL11.glTranslatef(-f15, 0.0F, 0.0F);
                GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, f12);
                GL11.glPopMatrix();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDepthFunc(GL11.GL_LEQUAL);
            }
        }

        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return this.func_110796_a((EntityItem) par1Entity);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    @Override
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
        this.doRenderItem((EntityItem) par1Entity, par2, par4, par6, par8, par9);
    }

    /**
     * Items should spread out when rendered in 3d?
     * 
     * @return
     */
    public boolean shouldSpreadItems() {
        return true;
    }

    /**
     * Items should have a bob effect
     * 
     * @return
     */
    public boolean shouldBob() {
        return true;
    }

    public byte getMiniBlockCount(ItemStack stack) {
        byte ret = 1;
        if (stack.stackSize > 1) ret = 2;
        if (stack.stackSize > 5) ret = 3;
        if (stack.stackSize > 20) ret = 4;
        if (stack.stackSize > 40) ret = 5;
        return ret;
    }

    /**
     * Allows for a subclass to override how many rendered items appear in a "mini item 3d stack"
     * 
     * @param stack
     * @return
     */
    public byte getMiniItemCount(ItemStack stack) {
        byte ret = 1;
        if (stack.stackSize > 1) ret = 2;
        if (stack.stackSize > 15) ret = 3;
        if (stack.stackSize > 31) ret = 4;
        return ret;
    }
}
