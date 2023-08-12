package teamforesight.arcpara.Spell.Earth.Brickshot;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import teamforesight.arcpara.ArcPara;
import teamforesight.arcpara.Registry.EntityRegistry;

public class BrickshotEntity extends Projectile {

	public int MaxAge = 400;
	public int Age;

	public BrickshotEntity (EntityType<BrickshotEntity> pSpell, Level pLevel) {
		super(pSpell, pLevel);
	}

	public BrickshotEntity (Level pLevel, double pX, double pY, double pZ) {
		super(EntityRegistry.Brickshot.get(), pLevel);
		this.setPos(pX, pY, pZ);
	}

	public void castShot (Player pPlayer, float pAngle) {
		Vec3 player_up = pPlayer.getUpVector(1.0F);
		Quaternionf quat = (new Quaternionf()).setAngleAxis(Math.toRadians(pAngle), player_up.x, player_up.y, player_up.z);
		Vector3f direction = pPlayer.getViewVector(1.0F).toVector3f().rotate(quat);
		this.shoot((double) direction.x(), (double) direction.y(), (double) direction.z(), 1, 30);
		this.setOwner(pPlayer);
	}

	@Override
	public EntityDimensions getDimensions (Pose pPose) {
		return new EntityDimensions(1, 1, true);
	}

	@Override
	public void tick () {
		super.tick();
		HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
		if (hitresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
			this.onHit(hitresult);
		}
		Vec3 vec3 = this.getDeltaMovement();
		double d0 = this.getX() + vec3.x;
		double d1 = this.getY() + vec3.y;
		double d2 = this.getZ() + vec3.z;
		this.updateRotation();
		if (this.level().getBlockStates(this.getBoundingBox()).noneMatch(BlockBehaviour.BlockStateBase::isAir)) {
			this.discard();
		} else if (this.isInWaterOrBubble()) {
			this.discard();
		} else {
			this.setDeltaMovement(vec3.scale((double) 0.99F));
			if (!this.isNoGravity()) {
				this.setDeltaMovement(this.getDeltaMovement().add(0.0D, (double) -0.02F, 0.0D));
			}

			this.setPos(d0, d1, d2);
		}
		if (this.level().isClientSide()) {
			this.level().addParticle(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
		}
		++this.Age;
		if (this.Age >= MaxAge) {
			this.discard();
		}
	}

	@Override
	public void recreateFromPacket (ClientboundAddEntityPacket pPacket) {
		super.recreateFromPacket(pPacket);
		this.setDeltaMovement(pPacket.getXa(), pPacket.getYa(), pPacket.getZa());
	}

	@Override
	protected void onHitEntity (EntityHitResult pResult) {
		super.onHitEntity(pResult);
		Entity entity = this.getOwner();
		if (entity instanceof LivingEntity living_entity) {
			float damage = Math.min(2.5f * (1 - (float) this.Age / 10f), 2.5f);
			if (pResult.getEntity().hurt(this.damageSources().mobProjectile(this, living_entity), damage)) {
				ArcPara.LOGGER.debug("Brickshot hit entity [{}] for {} damage", pResult.getEntity(), damage);

			}
		}

	}

	@Override
	protected void onHitBlock (BlockHitResult pResult) {
		super.onHitBlock(pResult);
		if (this.level().isClientSide()) {
			this.level().addParticle(ParticleTypes.CLOUD, this.getX(), this.getY(), this.getZ(), 0, 0.1f, 0);
		}
		if (!this.level().isClientSide()) {
			this.discard();
		}

	}

	@Override
	protected void defineSynchedData () {

	}

	@Override
	protected void readAdditionalSaveData (CompoundTag pCompound) {
		super.readAdditionalSaveData(pCompound);
	}

	@Override
	protected void addAdditionalSaveData (CompoundTag pCompound) {
		super.addAdditionalSaveData(pCompound);
	}
}
