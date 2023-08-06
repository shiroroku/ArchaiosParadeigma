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
import teamforesight.arcpara.Network.NetworkSetup;
import teamforesight.arcpara.Network.SyncCapabilityPacket;

@Mod.EventBusSubscriber(modid = ArcPara.MODID)
public class CapabilityRegistry {

	public static final Capability<ISpellCaster> SPELL_CASTER = CapabilityManager.get(new CapabilityToken<>() {});

	@SubscribeEvent
	public static void attachCapabilities (AttachCapabilitiesEvent<Entity> pEvent) {
		if (pEvent.getObject() instanceof Player) {
			pEvent.addCapability(new ResourceLocation(ArcPara.MODID, "spell_caster"), new SpellCasterCapability.Provider());
		}
	}

	@SubscribeEvent
	public static void registerCapabilities (RegisterCapabilitiesEvent pEvent) {
		pEvent.register(ISpellCaster.class);
	}

	public static LazyOptional<ISpellCaster> getSpellCaster (LivingEntity pEvent) {
		return pEvent.getCapability(SPELL_CASTER);
	}

	@SubscribeEvent
	public static void onPlayerLogin (PlayerEvent.PlayerLoggedInEvent pEvent) {
		sendCapabilityPacket(pEvent.getEntity());
	}

	@SubscribeEvent
	public static void onPlayerRespawn (PlayerEvent.PlayerRespawnEvent pEvent) {
		sendCapabilityPacket(pEvent.getEntity());
	}

	@SubscribeEvent
	public static void onPlayerStartTracking (PlayerEvent.StartTracking pEvent) {
		sendCapabilityPacket(pEvent.getEntity());
	}

	@SubscribeEvent
	public static void onPlayerDimensionChange (PlayerEvent.PlayerChangedDimensionEvent pEvent) {
		sendCapabilityPacket(pEvent.getEntity());
	}

	public static void sendCapabilityPacket (Player pPlayer) {
		if (EffectiveSide.get() == LogicalSide.SERVER && pPlayer instanceof ServerPlayer server_player) {
			NetworkSetup.CHANNEL.send(PacketDistributor.PLAYER.with(() -> server_player), new SyncCapabilityPacket(CapabilityRegistry.getSpellCaster(pPlayer).orElse(new SpellCasterCapability()), pPlayer.getUUID()));
		}
	}
}
