package teamforesight.arcpara.Spell;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import teamforesight.arcpara.ArcPara;

public class DebugSpell extends Spell {
	public DebugSpell () {
		super(new ResourceLocation(ArcPara.MODID, "debug"), 5f);
	}

	@Override
	public void castStart (Player pPlayer, boolean pIsPrimary) {
	}

	@Override
	public void castHold (Player pPlayer, boolean pIsPrimary) {
		ArcPara.LOGGER.debug("[{}] Debug spell [{}] is holding for {} ticks", pPlayer.getStringUUID(), pIsPrimary ? "Primary" : "Secondary", getChargeDuration(pIsPrimary));
	}

	@Override
	public void castEnd (Player pPlayer, boolean pIsPrimary, int pChargeDuration) {
		ArcPara.LOGGER.debug("[{}] Debug spell [{}] finished casting for {} ticks", pPlayer.getStringUUID(), pIsPrimary ? "Primary" : "Secondary", getChargeDuration(pIsPrimary));
	}
}
