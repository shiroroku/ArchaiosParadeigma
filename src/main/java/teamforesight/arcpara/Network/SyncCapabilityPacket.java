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
    public ISpellCaster spell_caster;
    UUID player;

    public SyncCapabilityPacket(ISpellCaster spell_caster, UUID player) {
        this.spell_caster = spell_caster;
        this.player = player;
    }

    public static SyncCapabilityPacket decode(FriendlyByteBuf buf) {
        return new SyncCapabilityPacket(new SpellCasterCapability(buf.readNbt()), buf.readUUID());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(spell_caster.serializeNBT());
        buf.writeUUID(player);
    }

    public static class Handler {
        public static void onMessageReceived(final SyncCapabilityPacket message, Supplier<NetworkEvent.Context> ctxSupplier) {
            NetworkEvent.Context ctx = ctxSupplier.get();
            ctx.enqueueWork(() -> {
                LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
                ctx.setPacketHandled(true);
                if (sideReceived != LogicalSide.CLIENT) {
                    return;
                }
                Optional<Level> clientWorld = LogicalSidedProvider.CLIENTWORLD.get(sideReceived);
                if (clientWorld.isEmpty()) {
                    return;
                }
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> processMessage(clientWorld.get(), message));
            });
            ctx.setPacketHandled(true);
        }

        private static void processMessage(Level worldClient, SyncCapabilityPacket message) {
            CapabilityRegistry.getSpellCaster(worldClient.getPlayerByUUID(message.player)).ifPresent(cap -> {
                cap.setMaxMana(message.spell_caster.getMaxMana());
                cap.setMana(message.spell_caster.getMana());
                cap.setEquippedSpells(message.spell_caster.getEquippedSpells());
            });
        }
    }
}
