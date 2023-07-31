package teamforesight.arcpara.Network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import teamforesight.arcpara.ArcPara;
import teamforesight.arcpara.Registry.CapabilityRegistry;
import teamforesight.arcpara.Registry.SpellRegistry;
import teamforesight.arcpara.Spell.Spell;

import java.util.Arrays;
import java.util.function.Supplier;

public class SpellCastPacket {

    Vec3 vector;
    ResourceLocation spell;
    boolean phase;
    boolean primary;

    public SpellCastPacket(ResourceLocation spell, Vec3 vector, boolean phase, boolean primary) {
        this.vector = vector;
        this.spell = spell;
        this.phase = phase;
        this.primary = primary;
    }

    public static SpellCastPacket decode(FriendlyByteBuf buf) {
        return new SpellCastPacket(buf.readResourceLocation(), new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()), buf.readBoolean(), buf.readBoolean());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(spell);
        buf.writeDouble(vector.x);
        buf.writeDouble(vector.y);
        buf.writeDouble(vector.z);
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
            // Make sure the spell the player is trying to cast exists
            Spell casting_spell = SpellRegistry.getSpell(message.spell);
            if (casting_spell == null) {
                ArcPara.LOGGER.error("Recieved NULL spell cast packet from client!");
                return;
            }
            CapabilityRegistry.getSpellCaster(player).ifPresent(p -> {
                // Check if the player has the spell equipped
                if (Arrays.stream(p.getEquippedSpells()).anyMatch(s -> s.equals(message.spell.toString()))) {
                    if (!casting_spell.canCast(player, message.primary)) {
                        return;
                    }
                    if (message.phase) {
                        casting_spell.castStart(player, message.vector, message.primary);
                    } else {
                        casting_spell.castStop(player, message.vector, message.primary);
                    }
                } else {
                    ArcPara.LOGGER.error("Player tried to cast spell they dont have!");
                }
            });

        }
    }
}
