package teamforesight.arcpara.Registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.network.PacketDistributor;
import teamforesight.arcpara.ArcPara;
import teamforesight.arcpara.Capability.ISpellCaster;
import teamforesight.arcpara.Capability.SpellCasterCapability;
import teamforesight.arcpara.Network.SpellCasterPacket;
import teamforesight.arcpara.SetupNetwork;

@Mod.EventBusSubscriber(modid = ArcPara.MODID)
public class CapabilityRegistry {

    public static final Capability<ISpellCaster> spell_caster = CapabilityManager.get(new CapabilityToken<>() {});

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(new ResourceLocation(ArcPara.MODID, "spell_caster"), new SpellCasterCapability.Provider());
        }
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(ISpellCaster.class);
    }

    public static LazyOptional<ISpellCaster> getSpellCaster(LivingEntity entity) {
        return entity.getCapability(spell_caster);
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        sendCapabilityPacket(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        sendCapabilityPacket(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerStartTracking(PlayerEvent.StartTracking event) {
        sendCapabilityPacket(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        sendCapabilityPacket(event.getEntity());
    }

    public static void sendCapabilityPacket(Player player) {
        if (EffectiveSide.get() == LogicalSide.SERVER && player instanceof ServerPlayer serverPlayer) {
            SetupNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SpellCasterPacket(CapabilityRegistry.getSpellCaster(player).orElse(new SpellCasterCapability()), player.getUUID()));
        }
    }
}
