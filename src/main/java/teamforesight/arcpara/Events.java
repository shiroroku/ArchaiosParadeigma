package teamforesight.arcpara;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import teamforesight.arcpara.Registry.CapabilityRegistry;

@Mod.EventBusSubscriber(modid = ArcPara.MODID)
public class Events {

    @SubscribeEvent
    public static void regenMana(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END) {
            if (event.player.tickCount % 20 == 0) {
                CapabilityRegistry.getSpellCaster(event.player).ifPresent(cap -> {
                    if (cap.getMana() < cap.getMaxMana()) {
                        cap.addMana(1f);
                        CapabilityRegistry.sendCapabilityPacket(event.player);
                    }
                });
            }
        }
    }
}
