package teamforesight.arcpara.Client.CastingOverlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.resources.ResourceLocation;
import teamforesight.arcpara.ArcPara;

public class CastingOverlayRenderer extends Overlay {

    private static final ResourceLocation OVERLAY = new ResourceLocation(ArcPara.MODID, "textures/gui/casting_overlay.png");
    private final Minecraft minecraft;

    public int selectedSpell = 0;

    public CastingOverlayRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.minecraft.screen != null) {
            this.minecraft.screen.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }
        renderSpellBar(pGuiGraphics, pPartialTick);
    }

    private void renderSpellBar(GuiGraphics pGuiGraphics, float pPartialTick){
        RenderSystem.enableBlend();
        pGuiGraphics.blit(OVERLAY, pGuiGraphics.guiWidth() - 22, (int) (pGuiGraphics.guiHeight() * 0.5f - 61), 0, 0, 22, 122);
        pGuiGraphics.blit(OVERLAY, pGuiGraphics.guiWidth() - 23, (int) (pGuiGraphics.guiHeight() * 0.5f - 62 + (20 * selectedSpell)), 22, 0, 22, 24);
        RenderSystem.disableBlend();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}
