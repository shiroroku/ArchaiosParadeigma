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
import teamforesight.arcpara.Spell.Spell;

public class CastingOverlayRenderer extends Overlay {

	private static final ResourceLocation OVERLAY = new ResourceLocation(ArcPara.MODID, "textures/gui/casting_overlay.png");
	private final Minecraft Minecraft;
	public int SelectedSpellIndex = 0;

	/**
	 * Opened on inventory button when in casting mode
	 *
	 * @see CastingOverlayInputHandler#onClientTickEnd(TickEvent.ClientTickEvent)
	 */
	public CastingOverlayRenderer (Minecraft pMinecraft) {
		this.Minecraft = pMinecraft;
	}

	@Override
	public void render (GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		if (this.Minecraft.screen != null) {
			this.Minecraft.screen.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		}
		CapabilityRegistry.getSpellCaster(Minecraft.player).ifPresent(caster -> {
			RenderSystem.enableBlend();
			renderSpellBar(pGuiGraphics, caster);
			renderManaBar(pGuiGraphics, caster);
			RenderSystem.disableBlend();
		});
	}

	private void renderSpellBar (GuiGraphics pGuiGraphics, ISpellCaster pSpellCaster) {
		pGuiGraphics.blit(OVERLAY, pGuiGraphics.guiWidth() - 22, (int) (pGuiGraphics.guiHeight() * 0.5f - 61), 0, 0, 22, 122);
		pGuiGraphics.blit(OVERLAY, pGuiGraphics.guiWidth() - 23, (int) (pGuiGraphics.guiHeight() * 0.5f - 62 + (20 * SelectedSpellIndex)), 22, 0, 22, 24);
		for (int i = 0; i < 6; i++) {
			String spell = pSpellCaster.getEquippedSpells()[i];
			if (!spell.isEmpty() && !spell.equals("empty")) {
				ResourceLocation spell_id = ResourceLocation.tryParse(spell);
				if (Minecraft.options.renderDebug) {
					//Debug info
					pGuiGraphics.drawCenteredString(Minecraft.font, spell_id.toString(), pGuiGraphics.guiWidth() - 100, (int) (pGuiGraphics.guiHeight() * 0.5f - 55 + (20 * i)), 13158655);
				}
				pGuiGraphics.blit(new ResourceLocation(spell_id.getNamespace(), "textures/gui/spells/%s.png".formatted(spell_id.getPath())), pGuiGraphics.guiWidth() - 19, (int) (pGuiGraphics.guiHeight() * 0.5f - 58 + (20 * i)), 0, 0, 16, 16, 16, 16);
			}
		}
	}

	private void renderManaBar (GuiGraphics pGuiGraphics, ISpellCaster pSpellCaster) {
		if (Minecraft.options.renderDebug) {
			//Debug info
			pGuiGraphics.drawCenteredString(Minecraft.font, "%d/%d".formatted((int) pSpellCaster.getMana(), (int) pSpellCaster.getMaxMana()), pGuiGraphics.guiWidth() - 27, (int) (pGuiGraphics.guiHeight() * 0.5f - 70), 13158655);
		}
		pGuiGraphics.blit(OVERLAY, pGuiGraphics.guiWidth() - 31, (int) (pGuiGraphics.guiHeight() * 0.5f - 61), 46, 0, 8, 122);

		Spell spell = pSpellCaster.getSpell(ResourceLocation.tryParse(pSpellCaster.getEquippedSpells()[SelectedSpellIndex]));
		float transparency = ModUtil.waveFunc(0.3f) * 0.25f + 0.5f;
		RenderSystem.setShaderColor(0.25f, 0.6f, 1, transparency);
		if (spell != null && spell.ManaCostPrimary > pSpellCaster.getMana()) {
			RenderSystem.setShaderColor(1, 0f, 0f, transparency);
		}
		float missing_mana_percentage = 1f - pSpellCaster.getMana() / pSpellCaster.getMaxMana();
		float pixel_offset = missing_mana_percentage * 122;
		pGuiGraphics.blit(OVERLAY, pGuiGraphics.guiWidth() - 29, (int) (pGuiGraphics.guiHeight() * 0.5f - 59 + pixel_offset), 56, 2, 6, (int) (119 - pixel_offset));
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}

	@Override
	public boolean isPauseScreen () {
		return false;
	}

}
