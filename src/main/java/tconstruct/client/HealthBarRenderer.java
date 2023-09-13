package tconstruct.client;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.HEALTH;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class HealthBarRenderer extends Gui {

    private static final boolean isRpghudLoaded = Loader.isModLoaded("rpghud");
    private static final boolean isTukmc_vzLoaded = Loader.isModLoaded("tukmc_Vz");
    private static final boolean isBorderlandsModLoaded = Loader.isModLoaded("borderlands");
    private static final ResourceLocation TINKER_HEARTS = new ResourceLocation("tinker", "textures/gui/newhearts.png");
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final Random rand = new Random();
    private int updateCounter = 0;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START && !mc.isGamePaused()) {
            this.updateCounter++;
        }
    }

    /* HUD */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderHealthbar(RenderGameOverlayEvent.Pre event) {

        if (event.type != RenderGameOverlayEvent.ElementType.HEALTH) {
            return;
        }

        // uses different display, displays health correctly by itself.
        if (isRpghudLoaded) {
            return;
        }

        if (isTukmc_vzLoaded && !isBorderlandsModLoaded) {
            // Loader check to avoid conflicting
            // with a GUI mod (thanks Vazkii!)
            return;
        }

        mc.mcProfiler.startSection("health");
        GL11.glEnable(GL11.GL_BLEND);

        int scaledWidth = event.resolution.getScaledWidth();
        int scaledHeight = event.resolution.getScaledHeight();
        int xBasePos = scaledWidth / 2 - 91;
        int yBasePos = scaledHeight - 39;

        boolean highlight = mc.thePlayer.hurtResistantTime / 3 % 2 == 1;

        if (mc.thePlayer.hurtResistantTime < 10) {
            highlight = false;
        }

        final IAttributeInstance attrMaxHealth = mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        final int health = MathHelper.ceiling_float_int(mc.thePlayer.getHealth());
        final int healthLast = MathHelper.ceiling_float_int(mc.thePlayer.prevHealth);
        float healthMax = (float) attrMaxHealth.getAttributeValue();
        if (healthMax > 20F) healthMax = 20F;
        float absorb = mc.thePlayer.getAbsorptionAmount();

        final int healthRows = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F / 10.0F);
        final int rowHeight = Math.max(10 - (healthRows - 2), 3);

        this.rand.setSeed(updateCounter * 312871L);

        int left = scaledWidth / 2 - 91;
        int top = scaledHeight - GuiIngameForge.left_height;

        if (!GuiIngameForge.renderExperiance) {
            top += 7;
            yBasePos += 7;
        }

        int regen = -1;
        if (mc.thePlayer.isPotionActive(Potion.regeneration)) {
            regen = updateCounter % 25;
        }

        final int TOP;
        int tinkerPotionOffset = 0;
        if (mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
            TOP = 9 * 5;
            tinkerPotionOffset += 27;
        } else {
            TOP = 0;
        }
        final int BACKGROUND = (highlight ? 25 : 16);
        int MARGIN = 16;
        if (mc.thePlayer.isPotionActive(Potion.poison)) {
            MARGIN += 36;
            tinkerPotionOffset = 9;
        } else if (mc.thePlayer.isPotionActive(Potion.wither)) {
            MARGIN += 72;
            tinkerPotionOffset = 18;
        }
        float absorbRemaining = absorb;

        for (int i = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F) - 1; i >= 0; --i) {
            final int row = MathHelper.ceiling_float_int((float) (i + 1) / 10.0F) - 1;
            int x = left + i % 10 * 8;
            int y = top - row * rowHeight;

            if (health <= 4) y += rand.nextInt(2);
            if (i == regen) y -= 2;

            this.drawTexturedModalRect(x, y, BACKGROUND, TOP, 9, 9);

            if (highlight) {
                if (i * 2 + 1 < healthLast) {
                    this.drawTexturedModalRect(x, y, MARGIN + 54, TOP, 9, 9); // 6
                } else if (i * 2 + 1 == healthLast) {
                    this.drawTexturedModalRect(x, y, MARGIN + 63, TOP, 9, 9); // 7
                }
            }

            if (absorbRemaining > 0.0F) {
                if (absorbRemaining == absorb && absorb % 2.0F == 1.0F) {
                    this.drawTexturedModalRect(x, y, MARGIN + 153, TOP, 9, 9); // 17
                } else {
                    this.drawTexturedModalRect(x, y, MARGIN + 144, TOP, 9, 9); // 16
                }
                absorbRemaining -= 2.0F;
            } else {
                if (i * 2 + 1 < health) {
                    this.drawTexturedModalRect(x, y, MARGIN + 36, TOP, 9, 9); // 4
                } else if (i * 2 + 1 == health) {
                    this.drawTexturedModalRect(x, y, MARGIN + 45, TOP, 9, 9); // 5
                }
            }
        }

        if (health > 20) {
            // Render tinkers' hearts
            mc.getTextureManager().bindTexture(TINKER_HEARTS);
            for (int i = 0; i < health / 20; i++) {
                final int renderHearts = Math.min(10, (health - 20 * (i + 1)) / 2);
                for (int j = 0; j < renderHearts; j++) {
                    int y = 0;
                    if (j == regen) y -= 2;
                    this.drawTexturedModalRect(xBasePos + 8 * j, yBasePos + y, 18 * i, tinkerTextureY, 9, 9);
                }
                if (health % 2 == 1 && renderHearts < 10) {
                    this.drawTexturedModalRect(xBasePos + 8 * renderHearts, yBasePos, 9 + 18 * i, tinkerTextureY, 9, 9);
                }
            }
            mc.getTextureManager().bindTexture(icons);
        }

        GuiIngameForge.left_height += 10;
        if (absorb > 0) GuiIngameForge.left_height += 10;
        GL11.glDisable(GL11.GL_BLEND);
        mc.mcProfiler.endSection();
        event.setCanceled(true);
        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(event, HEALTH));

    }

}
