package teamforesight.arcpara;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import teamforesight.arcpara.Registry.EntityRegistry;
import teamforesight.arcpara.Spell.Earth.Brickshot.BrickshotEntityRenderer;

@Mod.EventBusSubscriber(modid = ArcPara.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

}
