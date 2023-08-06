package teamforesight.arcpara;

import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import teamforesight.arcpara.Data.ResearchTree.ResearchTreeLoader;
import teamforesight.arcpara.Registry.CapabilityRegistry;

@Mod.EventBusSubscriber(modid = ArcPara.MODID)
public class Events {

	@SubscribeEvent
	public static void onAddReloadListenerEvent (AddReloadListenerEvent pEvent) {
		pEvent.addListener(new ResearchTreeLoader());
	}

	@SubscribeEvent
	public static void regenMana (TickEvent.PlayerTickEvent pEvent) {
		if (pEvent.side == LogicalSide.SERVER && pEvent.phase == TickEvent.Phase.END && (pEvent.player.tickCount % 20 == 0)) {
			CapabilityRegistry.getSpellCaster(pEvent.player).ifPresent(cap -> {
				if (cap.getMana() < cap.getMaxMana()) {
					cap.addMana(1f);
					CapabilityRegistry.sendCapabilityPacket(pEvent.player);
				}
			});
		}
	}
}
