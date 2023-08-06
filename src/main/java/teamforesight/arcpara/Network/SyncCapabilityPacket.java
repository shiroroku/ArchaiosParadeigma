package teamforesight.arcpara.Network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import teamforesight.arcpara.Capability.ISpellCaster;
import teamforesight.arcpara.Capability.SpellCasterCapability;
import teamforesight.arcpara.Registry.CapabilityRegistry;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncCapabilityPacket {
	public ISpellCaster SpellCaster;
	public UUID Player;

	public SyncCapabilityPacket (ISpellCaster pSpellCaster, UUID pPlayer) {
		this.SpellCaster = pSpellCaster;
		this.Player = pPlayer;
	}

	public static SyncCapabilityPacket decode (FriendlyByteBuf pBuf) {
		return new SyncCapabilityPacket(new SpellCasterCapability(pBuf.readNbt()), pBuf.readUUID());
	}

	public void encode (FriendlyByteBuf pBuf) {
		pBuf.writeNbt(SpellCaster.serializeNBT());
		pBuf.writeUUID(Player);
	}

	public static class Handler {
		public static void onMessageReceived (final SyncCapabilityPacket pMessage, Supplier<NetworkEvent.Context> pCTX) {
			NetworkEvent.Context ctx = pCTX.get();
			ctx.enqueueWork(() -> {
				LogicalSide side_received = ctx.getDirection().getReceptionSide();
				ctx.setPacketHandled(true);
				if (side_received != LogicalSide.CLIENT) {
					return;
				}
				Optional<Level> client_world = LogicalSidedProvider.CLIENTWORLD.get(side_received);
				if (client_world.isEmpty()) {
					return;
				}
				DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> processMessage(client_world.get(), pMessage));
			});
			ctx.setPacketHandled(true);
		}

		private static void processMessage (Level pLevel, SyncCapabilityPacket pMessage) {
			CapabilityRegistry.getSpellCaster(pLevel.getPlayerByUUID(pMessage.Player)).ifPresent(cap -> {
				cap.setMaxMana(pMessage.SpellCaster.getMaxMana());
				cap.setMana(pMessage.SpellCaster.getMana());
				cap.setEquippedSpells(pMessage.SpellCaster.getEquippedSpells());
				cap.setSpells(pMessage.SpellCaster.getSpells());
			});
		}
	}
}
