package teamforesight.arcpara.Registry;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import teamforesight.arcpara.ArcPara;
import teamforesight.arcpara.Spell.Earth.Brickshot.BrickshotEntityRenderer;

@Mod.EventBusSubscriber(modid = ArcPara.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EntityRenderingRegistry {

	public static final ModelLayerLocation BRICKSHOT_MODEL = new ModelLayerLocation(new ResourceLocation(ArcPara.MODID, "brickshot"), "main");

	@SubscribeEvent
	public static void registerEntityModels (EntityRenderersEvent.RegisterLayerDefinitions pEvent) {
		pEvent.registerLayerDefinition(BRICKSHOT_MODEL, BrickshotEntityRenderer::createModel);
	}

	@SubscribeEvent
	public static void registerEntityRenderers (EntityRenderersEvent.RegisterRenderers pEvent) {
		pEvent.registerEntityRenderer(EntityRegistry.Brickshot.get(), BrickshotEntityRenderer::new);
	}

}
