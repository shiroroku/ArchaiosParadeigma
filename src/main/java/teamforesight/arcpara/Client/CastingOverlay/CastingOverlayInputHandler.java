package teamforesight.arcpara.Client.CastingOverlay;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import teamforesight.arcpara.ArcPara;
import teamforesight.arcpara.Capability.ISpellCaster;
import teamforesight.arcpara.Client.CastingOverlay.ResearchTree.ResearchTreeScreen;
import teamforesight.arcpara.Client.KeyMappingRegistry;
import teamforesight.arcpara.Network.NetworkSetup;
import teamforesight.arcpara.Network.SpellCastPacket;
import teamforesight.arcpara.Registry.CapabilityRegistry;
import teamforesight.arcpara.Spell.Spell;

@Mod.EventBusSubscriber(modid = ArcPara.MODID, value = Dist.CLIENT)
public class CastingOverlayInputHandler {

	private static boolean inCastOverlay = false;
	private static final Lazy<CastingOverlayRenderer> overlay = Lazy.of(() ->
			new CastingOverlayRenderer(Minecraft.getInstance()));

	/**
	 * Enables and disables overlay when the keybind is held.
	 */
	@SubscribeEvent
	public static void onClientTickStart(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			if (KeyMappingRegistry.CAST_MODE.get().isDown()) {
				if (!inCastOverlay) {
					enableOverlay();
				}
			} else if (inCastOverlay && Minecraft.getInstance().getOverlay() instanceof CastingOverlayRenderer) {
				disableOverlay();
			}

		}
	}

	private static void enableOverlay() {
		inCastOverlay = true;
		Minecraft.getInstance().setOverlay(overlay.get());
	}

	private static void disableOverlay() {
		inCastOverlay = false;
		Minecraft.getInstance().setOverlay(null);
	}

	/**
	 * Calls start and end on spell cast when mouse buttons are pressed.
	 */
	@SubscribeEvent
	public static void onMousePress(InputEvent.MouseButton.Pre event) {
		if (inCastOverlay) {
			ISpellCaster cap = CapabilityRegistry.getSpellCaster(Minecraft.getInstance().player).orElse(null);
			ResourceLocation equippedSpell = ResourceLocation.tryParse(cap.getEquippedSpells()[overlay.get().selectedSpellIndex]);
			Spell spell = cap.getSpell(equippedSpell);
			if (spell == null) {
				return;
			}
			if (event.getAction() == InputConstants.PRESS) {
				// MOUSE PRESS
				if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
					ArcPara.LOGGER.debug("[Client][{}] Start Cast primary", equippedSpell);
					// A light check to see if we can cast this spell to save a packet
					// The server ultimately decides in SpellCastPacket
					if (spell.cantCast(Minecraft.getInstance().player, true)) {
						return;
					}
					NetworkSetup.CHANNEL.sendToServer(new SpellCastPacket(equippedSpell, true, true));
				}
				if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
					ArcPara.LOGGER.debug("[Client][{}] Start Cast secondary", equippedSpell);
					if (spell.cantCast(Minecraft.getInstance().player, false)) {
						return;
					}
					NetworkSetup.CHANNEL.sendToServer(new SpellCastPacket(equippedSpell, true, false));
				}
			} else {
				// MOUSE RELEASE
				if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
					ArcPara.LOGGER.debug("[Client][{}] End Cast primary", equippedSpell);
					NetworkSetup.CHANNEL.sendToServer(new SpellCastPacket(equippedSpell, false, true));
				}
				if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
					ArcPara.LOGGER.debug("[Client][{}] End Cast secondary", equippedSpell);
					NetworkSetup.CHANNEL.sendToServer(new SpellCastPacket(equippedSpell, false, false));
				}
			}
		}
	}

	/**
	 * Changes selected spell on scroll.
	 */
	public static void onOverlayMouseScroll(double delta) {
		if (inCastOverlay) {
			int scroll = (overlay.get().selectedSpellIndex - (int) delta) % 6;
			overlay.get().selectedSpellIndex = scroll < 0 ? 5 : scroll;
		}
	}

	/**
	 * Changes selected spell on keys 1-6. Also opens research tree on inventory.
	 */
	@SubscribeEvent
	public static void onClientTickEnd(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			Minecraft mc = Minecraft.getInstance();
			// We use isdown instead of consumeclick because for some reason consumeclick overrides vanilla binds.
			if (inCastOverlay) {
				//Research Tree
				if (mc.options.keyInventory.isDown()) {
					Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1F, 0.70F));
					Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.ENCHANTMENT_TABLE_USE, 0.5F, 0.70F));
					Minecraft.getInstance().setScreen(new ResearchTreeScreen());
					disableOverlay();
					return;
				}
				//Spellbar 1-6
				for (int i = 0; i < 6; i++) {
					if (mc.options.keyHotbarSlots[i].isDown()) {
						overlay.get().selectedSpellIndex = i;
					}
				}
			}
		}
	}
}
