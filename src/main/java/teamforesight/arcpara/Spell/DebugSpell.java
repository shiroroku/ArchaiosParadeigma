package teamforesight.arcpara.Spell;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import teamforesight.arcpara.ArcPara;

public class DebugSpell extends Spell {
	public DebugSpell() {
		super(new ResourceLocation(ArcPara.MODID, "debug"), 5f);
	}

	@Override
	public void castStart(Player player, boolean isPrimary) {
	}

	@Override
	public void castHold(Player player, boolean isPrimary) {
		ArcPara.LOGGER.debug("[{}] Debug spell [{}] is holding for {} ticks", player.getStringUUID(), isPrimary ? "Primary" : "Secondary", getChargeDuration(isPrimary));
	}

	@Override
	public void castEnd(Player player, boolean isPrimary, int chargeDuration) {
		ArcPara.LOGGER.debug("[{}] Debug spell [{}] finished casting for {} ticks", player.getStringUUID(), isPrimary ? "Primary" : "Secondary", getChargeDuration(isPrimary));
	}
}
