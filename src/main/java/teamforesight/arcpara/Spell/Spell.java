package teamforesight.arcpara.Spell;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class Spell {

    public final ResourceLocation id;
    public final float manaCost;

    public Spell(ResourceLocation id, float mana_cost) {
        this.id = id;
        this.manaCost = mana_cost;
    }

    public void castStart(Player player) {

    }

    public void castStop(Player player) {

    }
}
