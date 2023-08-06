package teamforesight.arcpara.Spell.Fire;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import teamforesight.arcpara.ArcPara;
import teamforesight.arcpara.ModUtil;
import teamforesight.arcpara.Spell.Spell;

public class SparkSpell extends Spell {
	public SparkSpell () {
		super(new ResourceLocation(ArcPara.MODID, "spark"), 25f, 25f);
	}

	@Override
	public void castStart (Player pPlayer, boolean pIsPrimary) {
		Level level = pPlayer.level();
		float distance = 5;

		LivingEntity e = ModUtil.raycastLivingEntity(pPlayer, distance);
		if (e != null) {
			e.setSecondsOnFire(2);
			level.playSound(null, e, SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.1F);
			level.playSound(null, e, SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.6F);
			consumeMana(pPlayer, ManaCostPrimary);
			return;
		}

		BlockHitResult hit = ModUtil.raycastBlock(pPlayer, distance);
		BlockPos hitpos = hit.getBlockPos().above();
		if (hit.getType() == HitResult.Type.BLOCK && level.isEmptyBlock(hitpos)) {
			if (level.setBlock(hitpos, BaseFireBlock.getState(pPlayer.level(), hit.getBlockPos()), 3)) {
				level.playSound(null, hitpos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.1F);
				level.playSound(null, hitpos, SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.6F);
				consumeMana(pPlayer, ManaCostPrimary);
			}
		}
	}

	@Override
	public void castHold (Player pPlayer, boolean pIsPrimary) {

	}

	@Override
	public void castEnd (Player pPlayer, boolean pIsPrimary, int pChargeDuration) {

	}
}
