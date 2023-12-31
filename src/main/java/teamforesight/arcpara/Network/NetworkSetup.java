package teamforesight.arcpara.Network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import teamforesight.arcpara.ArcPara;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = ArcPara.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NetworkSetup {
	public static SimpleChannel CHANNEL;

	@SubscribeEvent
	public static void setup (final FMLCommonSetupEvent pEvent) {
		CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(ArcPara.MODID, "channel"), () -> "1.0", s -> true, s -> true);
		int id = 0;
		CHANNEL.registerMessage(id++, SyncCapabilityPacket.class, SyncCapabilityPacket::encode, SyncCapabilityPacket::decode, SyncCapabilityPacket.Handler::onMessageReceived, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		CHANNEL.registerMessage(id++, SpellCastPacket.class, SpellCastPacket::encode, SpellCastPacket::decode, SpellCastPacket.Handler::onMessageReceived, Optional.of(NetworkDirection.PLAY_TO_SERVER));
	}
}
