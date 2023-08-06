package teamforesight.arcpara;

import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class ModUtil {

	public static BlockHitResult raycastBlock (Player pPlayer, float pDistance) {
		Level level = pPlayer.level();
		float cosX = -Mth.cos(-pPlayer.getXRot() * ((float) Math.PI / 180F));
		float YX = Mth.sin(-pPlayer.getYRot() * ((float) Math.PI / 180F) - (float) Math.PI) * cosX;
		float YXCos = Mth.cos(-pPlayer.getYRot() * ((float) Math.PI / 180F) - (float) Math.PI) * cosX;
		Vec3 eyePosition = pPlayer.getEyePosition();
		Vec3 raycast = eyePosition.add((double) YX * pDistance, (double) Mth.sin(-pPlayer.getXRot() * ((float) Math.PI / 180F)) * pDistance, (double) YXCos * pDistance);
		return level.clip(new ClipContext(eyePosition, raycast, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, pPlayer));
	}

	public static LivingEntity raycastLivingEntity (Player pPlayer, float pDistance) {
		for (LivingEntity e : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBoxForCulling().inflate(pDistance)).stream().filter(e -> e.distanceToSqr(pPlayer) < pDistance * pDistance && e != pPlayer).toList()) {
			Vec3 normal_view = pPlayer.getViewVector(1.0F).normalize();
			Vec3 vec = new Vec3(e.getX() - pPlayer.getX(), e.getEyeY() - pPlayer.getEyeY(), e.getZ() - pPlayer.getZ());
			if (normal_view.dot(vec.normalize()) > 0.95D - 0.025D / vec.length() && pPlayer.hasLineOfSight(e)) {
				return e;
			}
		}
		return null;
	}

	//Sin wave in the range of 0 to 1
	public static float waveFunc (float pSpeed) {
		return (Mth.sin(Mth.DEG_TO_RAD * Util.getMillis() * pSpeed) + 1) / 2;
	}

}
