package teamforesight.arcpara.Network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import teamforesight.arcpara.ArcPara;
import teamforesight.arcpara.Registry.CapabilityRegistry;
import teamforesight.arcpara.Spell.Spell;

import java.util.Arrays;
import java.util.function.Supplier;

public class SpellCastPacket {

	ResourceLocation Spell;
	boolean Phase;
	boolean Primary;

	/**
	 * @param pSpell Spell id
	 * @param pPhase True = start cast, false = stop cast
	 * @param pPrimary Primary cast or secondary
	 */
	public SpellCastPacket (ResourceLocation pSpell, boolean pPhase, boolean pPrimary) {
		this.Spell = pSpell;
		this.Phase = pPhase;
		this.Primary = pPrimary;
	}

	public static SpellCastPacket decode (FriendlyByteBuf pBuf) {
		return new SpellCastPacket(pBuf.readResourceLocation(), pBuf.readBoolean(), pBuf.readBoolean());
	}

	public void encode (FriendlyByteBuf pBuf) {
		pBuf.writeResourceLocation(Spell);
		pBuf.writeBoolean(Phase);
		pBuf.writeBoolean(Primary);
	}

	public static class Handler {
		public static void onMessageReceived (final SpellCastPacket pMessage, Supplier<NetworkEvent.Context> pCTX) {
			NetworkEvent.Context ctx = pCTX.get();
			ctx.enqueueWork(() -> {
				LogicalSide side_received = ctx.getDirection().getReceptionSide();
				ctx.setPacketHandled(true);
				if (side_received != LogicalSide.SERVER) {
					return;
				}
				final ServerPlayer sending_player = ctx.getSender();
				if (sending_player == null) {
					return;
				}
				processMessage(sending_player, pMessage);
			});
			ctx.setPacketHandled(true);
		}

		/**
		 * Recieved when the player clicks in casting overlay. Does some checks then casts the spell on server.
		 */
		private static void processMessage (ServerPlayer pPlayer, SpellCastPacket pMessage) {
			CapabilityRegistry.getSpellCaster(pPlayer).ifPresent(p -> {
				// Make sure the spell the player is trying to cast exists
				Spell casting_spell = p.getSpell(pMessage.Spell);
				if (casting_spell == null) {
					ArcPara.LOGGER.error("Recieved invalid spell cast packet from client!");
					return;
				}
				// Check if the player has the spell equipped
				if (Arrays.stream(p.getEquippedSpells()).anyMatch(s -> s.equals(pMessage.Spell.toString()))) {
					if (casting_spell.cantCast(pPlayer, pMessage.Primary)) {
						return;
					}
					if (pMessage.Phase) { // When starting
						casting_spell.start(pPlayer, pMessage.Primary);
					} else { // When stopping
						if (pMessage.Primary && !casting_spell.HasStoppedPrimary) { // If we didnt stop early on primary, stop
							casting_spell.stop(pPlayer, pMessage.Primary);
						}
						if (!pMessage.Primary && !casting_spell.HasStoppedSecondary) { // If we didnt stop early on secondary, stop
							casting_spell.stop(pPlayer, pMessage.Primary);
						}
					}
				} else {
					ArcPara.LOGGER.error("Player tried to cast spell they dont have!");
				}
			});
		}
	}
}
