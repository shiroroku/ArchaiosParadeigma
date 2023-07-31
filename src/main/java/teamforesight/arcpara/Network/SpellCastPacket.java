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

	ResourceLocation spell;
	boolean phase;
	boolean primary;

	/**
	 * @param spell   Spell id
	 * @param phase   True = start cast, false = stop cast
	 * @param primary Primary cast or secondary
	 */
	public SpellCastPacket(ResourceLocation spell, boolean phase, boolean primary) {
		this.spell = spell;
		this.phase = phase;
		this.primary = primary;
	}

	public static SpellCastPacket decode(FriendlyByteBuf buf) {
		return new SpellCastPacket(buf.readResourceLocation(), buf.readBoolean(), buf.readBoolean());
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeResourceLocation(spell);
		buf.writeBoolean(phase);
		buf.writeBoolean(primary);
	}

	public static class Handler {
		public static void onMessageReceived(final SpellCastPacket message, Supplier<NetworkEvent.Context> ctxSupplier) {
			NetworkEvent.Context ctx = ctxSupplier.get();
			ctx.enqueueWork(() -> {
				LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
				ctx.setPacketHandled(true);
				if (sideReceived != LogicalSide.SERVER) {
					return;
				}
				final ServerPlayer sendingPlayer = ctx.getSender();
				if (sendingPlayer == null) {
					return;
				}
				processMessage(sendingPlayer, message);
			});
			ctx.setPacketHandled(true);
		}

		/**
		 * Recieved when the player clicks in casting overlay. Does some checks then casts the spell on server.
		 */
		private static void processMessage(ServerPlayer player, SpellCastPacket message) {
			CapabilityRegistry.getSpellCaster(player).ifPresent(p -> {
				// Make sure the spell the player is trying to cast exists
				Spell casting_spell = p.getSpell(message.spell);
				if (casting_spell == null) {
					ArcPara.LOGGER.error("Recieved invalid spell cast packet from client!");
					return;
				}

				// Check if the player has the spell equipped
				if (Arrays.stream(p.getEquippedSpells()).anyMatch(s -> s.equals(message.spell.toString()))) {
					if (casting_spell.cantCast(player, message.primary)) {
						return;
					}
					if (message.phase) { // When starting
						casting_spell.start(player, message.primary);
					} else { // When stopping
						if(message.primary && !casting_spell.hasStoppedPrimary) { // If we didnt stop early on primary, stop
							casting_spell.stop(player, message.primary);
						}
						if(!message.primary && !casting_spell.hasStoppedSecondary) { // If we didnt stop early on secondary, stop
							casting_spell.stop(player, message.primary);
						}
					}
				} else {
					ArcPara.LOGGER.error("Player tried to cast spell they dont have!");
				}
			});

		}
	}
}
