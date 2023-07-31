package teamforesight.arcpara.Client.CastingOverlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.TickEvent;
import teamforesight.arcpara.ArcPara;
import teamforesight.arcpara.Capability.ISpellCaster;
import teamforesight.arcpara.ModUtil;
import teamforesight.arcpara.Registry.CapabilityRegistry;
import teamforesight.arcpara.Registry.SpellRegistry;
import teamforesight.arcpara.Spell.Spell;

public class CastingOverlayRenderer extends Overlay {

	private static final ResourceLocation OVERLAY = new ResourceLocation(ArcPara.MODID, "textures/gui/casting_overlay.png");
	private final Minecraft minecraft;
	public int selectedSpellIndex = 0;

	/**
	 * Opened on inventory button when in casting mode
	 * @see CastingOverlayInputHandler#onClientTickEnd(TickEvent.ClientTickEvent) 
	 */
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
		for (int i = 0; i < 6; i++) {
			String spell = cap.getEquippedSpells()[i];
			if (!spell.isEmpty() && !spell.equals("empty")) {
				ResourceLocation spell_id = ResourceLocation.tryParse(spell);
				if (minecraft.options.renderDebug) {
					//Debug info
					pGuiGraphics.drawCenteredString(minecraft.font, spell_id.toString(), pGuiGraphics.guiWidth() - 100, (int) (pGuiGraphics.guiHeight() * 0.5f - 55 + (20 * i)), 13158655);
				}
				pGuiGraphics.blit(new ResourceLocation(spell_id.getNamespace(), "textures/gui/spells/%s.png".formatted(spell_id.getPath())), pGuiGraphics.guiWidth() - 19, (int) (pGuiGraphics.guiHeight() * 0.5f - 58 + (20 * i)), 0, 0, 16, 16, 16, 16);
			}
		}
	}

	private void renderManaBar(GuiGraphics pGuiGraphics, float pPartialTick, ISpellCaster cap) {
		if (minecraft.options.renderDebug) {
			//Debug info
			pGuiGraphics.drawCenteredString(minecraft.font, "%d/%d".formatted((int) cap.getMana(), (int) cap.getMaxMana()), pGuiGraphics.guiWidth() - 27, (int) (pGuiGraphics.guiHeight() * 0.5f - 70), 13158655);
		}
		pGuiGraphics.blit(OVERLAY, pGuiGraphics.guiWidth() - 31, (int) (pGuiGraphics.guiHeight() * 0.5f - 61), 46, 0, 8, 122);

		Spell spell = SpellRegistry.getSpell(ResourceLocation.tryParse(cap.getEquippedSpells()[selectedSpellIndex]));
		float transparency = ModUtil.waveFunc(0.3f) * 0.25f + 0.5f;
		RenderSystem.setShaderColor(0.25f, 0.6f, 1, transparency);
		if (spell != null) {
			if (spell.manaCost > cap.getMana()) {
				RenderSystem.setShaderColor(1, 0f, 0f, transparency);
			}
		}
		float perc = 1f - cap.getMana() / cap.getMaxMana();
		float pixel_offset = perc * 122;
		pGuiGraphics.blit(OVERLAY, pGuiGraphics.guiWidth() - 29, (int) (pGuiGraphics.guiHeight() * 0.5f - 59 + pixel_offset), 56, 2, 6, (int) (119 - pixel_offset));
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

}
