package teamforesight.arcpara.Spell;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import teamforesight.arcpara.ArcPara;

public class Spell {

    public final ResourceLocation id;
    public final float manaCost;

    public Spell(ResourceLocation id, float manaCost) {
        this.id = id;
        this.manaCost = manaCost;
    }

    public void castStart(Player player, Vec3 angle, boolean isPrimary) {
        ArcPara.LOGGER.debug("[{}][{}] Starting cast.", id.toString(), isPrimary);
    }

    public void castStop(Player player, Vec3 angle, boolean isPrimary) {
        ArcPara.LOGGER.debug("[{}][{}] Stopping cast.", id.toString(), isPrimary);
    }
}
