package teamforesight.arcpara.Client.CastingOverlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.resources.ResourceLocation;
import teamforesight.arcpara.ArcPara;
import teamforesight.arcpara.Capability.ISpellCaster;
import teamforesight.arcpara.Registry.CapabilityRegistry;

public class CastingOverlayRenderer extends Overlay {

    private static final ResourceLocation OVERLAY = new ResourceLocation(ArcPara.MODID, "textures/gui/casting_overlay.png");
    private final Minecraft minecraft;
    public int selectedSpellIndex = 0;

    public CastingOverlayRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.minecraft.screen != null) {
            this.minecraft.screen.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        ISpellCaster cap = CapabilityRegistry.getSpellCaster(minecraft.player).orElse(null);
        if (cap == null) {
            ArcPara.LOGGER.error("Player is missing SpellCaster capability in CastingOverlayRenderer!");
            return;
        }

        RenderSystem.enableBlend();
        renderSpellBar(pGuiGraphics, pPartialTick, cap);
        renderManaBar(pGuiGraphics, pPartialTick, cap);
        RenderSystem.disableBlend();
    }

    private void renderSpellBar(GuiGraphics pGuiGraphics, float pPartialTick, ISpellCaster cap) {
        pGuiGraphics.blit(OVERLAY, pGuiGraphics.guiWidth() - 22, (int) (pGuiGraphics.guiHeight() * 0.5f - 61), 0, 0, 22, 122);
        pGuiGraphics.blit(OVERLAY, pGuiGraphics.guiWidth() - 23, (int) (pGuiGraphics.guiHeight() * 0.5f - 62 + (20 * selectedSpellIndex)), 22, 0, 22, 24);

        //Render spells
        for (int i = 0; i < 6; i++) {
            String spell = cap.getEquippedSpells()[i];
            if (!spell.isEmpty() && !spell.equals("empty")) {
                ResourceLocation spell_id = ResourceLocation.tryParse(spell);
                pGuiGraphics.blit(new ResourceLocation(spell_id.getNamespace(), "textures/gui/spells/%s.png".formatted(spell_id.getPath())), pGuiGraphics.guiWidth() - 19, (int) (pGuiGraphics.guiHeight() * 0.5f - 58 + (20 * i)), 0, 0, 16, 16, 16, 16);
            }
        }
    }

    private void renderManaBar(GuiGraphics pGuiGraphics, float pPartialTick, ISpellCaster cap) {
        pGuiGraphics.blit(OVERLAY, pGuiGraphics.guiWidth() - 31, (int) (pGuiGraphics.guiHeight() * 0.5f - 61), 46, 0, 8, 122);
        RenderSystem.setShaderColor(1, 1, 1, 0.5f);
        double guiScale = minecraft.getWindow().getGuiScale();
        RenderSystem.enableScissor((int) ((pGuiGraphics.guiWidth() - 31) * guiScale), (int) ((pGuiGraphics.guiHeight() * 0.5f - 61) * guiScale), (int) (8 * guiScale), (int) (122 * guiScale));
        float missing_mana_percentage = 1f - cap.getMana() / cap.getMaxMana();
        pGuiGraphics.blit(OVERLAY, pGuiGraphics.guiWidth() - 31, (int) ((pGuiGraphics.guiHeight() * 0.5f - 61) + missing_mana_percentage * 122), 54, 0, 8, 122);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableScissor();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}
