package teamforesight.arcpara.Spell;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import teamforesight.arcpara.ArcPara;
import teamforesight.arcpara.Registry.CapabilityRegistry;

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

    protected void consumeMana(Player player, float amount){
        CapabilityRegistry.getSpellCaster(player).ifPresent(p -> {
            p.spendMana(amount);
            CapabilityRegistry.sendCapabilityPacket(player);
        });
    }

    public boolean canCast(Player player, boolean isPrimary) {
        return CapabilityRegistry.getSpellCaster(player).orElseGet(null).getMana() >= manaCost;
    }
}
