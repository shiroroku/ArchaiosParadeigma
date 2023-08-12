package teamforesight.arcpara.Spell.Earth.Brickshot;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import teamforesight.arcpara.ArcPara;
import teamforesight.arcpara.Registry.EntityRenderingRegistry;

public class BrickshotEntityRenderer extends EntityRenderer<BrickshotEntity> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(ArcPara.MODID, "textures/spells/brickshot.png");
	private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE);
	private final ModelPart Pebble;

	public BrickshotEntityRenderer (EntityRendererProvider.Context pContext) {
		super(pContext);
		ModelPart root = pContext.bakeLayer(EntityRenderingRegistry.BRICKSHOT_MODEL);
		this.Pebble = root.getChild("pebble");
	}

	@Override
	public void render (BrickshotEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
		super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
		VertexConsumer buf = pBuffer.getBuffer(RENDER_TYPE);
		pPoseStack.pushPose();
		this.Pebble.render(pPoseStack, buf, pPackedLight, OverlayTexture.NO_OVERLAY);
		pPoseStack.popPose();
	}

	public static LayerDefinition createModel () {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition part = mesh.getRoot();
		part.addOrReplaceChild("pebble", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 2.0F, 2.0F, 2.0F), PartPose.offset(-1, -1, -1));
		return LayerDefinition.create(mesh, 16, 16);
	}

	@Override
	public ResourceLocation getTextureLocation (BrickshotEntity pEntity) {
		return TEXTURE;
	}
}
