package teamforesight.arcpara.Spell.Earth.Brickshot;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Random;
import teamforesight.arcpara.ArcPara;
import teamforesight.arcpara.Spell.Spell;

public class BrickshotSpell extends Spell {
	public BrickshotSpell () {
		super(new ResourceLocation(ArcPara.MODID, "brickshot"), 15, 15);
	}

	@Override
	public void castStart (Player pPlayer, boolean pIsPrimary) {

	}

	@Override
	public void castHold (Player pPlayer, boolean pIsPrimary) {

	}

	@Override
	public void castEnd (Player pPlayer, boolean pIsPrimary, int pChargeDuration) {
		if (pIsPrimary) {
			Vec3 player_pos = pPlayer.getPosition(0);
			Random r = new Random();

			for (int i = -2; i <= 2; i++) {
				BrickshotEntity pebble = new BrickshotEntity(pPlayer.level(), player_pos.x, player_pos.y + pPlayer.getEyeHeight() * 0.85f, player_pos.z);
				pebble.castShot(pPlayer, i * 5);
				pPlayer.level().addFreshEntity(pebble);
			}
		}
	}
}
