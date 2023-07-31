package teamforesight.arcpara;

import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ModUtil {

    public static BlockHitResult raycastBlock(Player player, float distance) {
        Level level = player.level();
        float cosX = -Mth.cos(-player.getXRot() * ((float) Math.PI / 180F));
        float YX = Mth.sin(-player.getYRot() * ((float) Math.PI / 180F) - (float) Math.PI) * cosX;
        float YXCos = Mth.cos(-player.getYRot() * ((float) Math.PI / 180F) - (float) Math.PI) * cosX;
        Vec3 eyePosition = player.getEyePosition();
        Vec3 raycast = eyePosition.add((double) YX * distance, (double) Mth.sin(-player.getXRot() * ((float) Math.PI / 180F)) * distance, (double) YXCos * distance);
        return level.clip(new ClipContext(eyePosition, raycast, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
    }

    public static LivingEntity raycastLivingEntity(Player player, float distance) {
        List<LivingEntity> entities = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBoxForCulling().inflate(distance));
        for (LivingEntity e : entities) {
            if (e.distanceToSqr(player) < distance * distance && e != player) {
                Vec3 normal_view = player.getViewVector(1.0F).normalize();
                Vec3 vec31 = new Vec3(e.getX() - player.getX(), e.getEyeY() - player.getEyeY(), e.getZ() - player.getZ());
                double d0 = vec31.length();
                vec31 = vec31.normalize();
                double d1 = normal_view.dot(vec31);
                if (d1 > 0.95D - 0.025D / d0 && player.hasLineOfSight(e)) {
                    return e;
                }
            }
        }
        return null;
    }

    //Sin wave in the range of 0 to 1
    public static float waveFunc(float speed) {
        return (Mth.sin(Mth.DEG_TO_RAD * Util.getMillis() * speed) + 1) / 2;
    }

}
