package teamforesight.arcpara.Registry;

import net.minecraft.resources.ResourceLocation;
import teamforesight.arcpara.Spell.Fire.SparkSpell;
import teamforesight.arcpara.Spell.Spell;

import java.util.Arrays;
import java.util.Optional;

public class SpellRegistry {

    public enum SPELLS {
        spark(new SparkSpell());

        public final Spell spell;

        SPELLS(Spell spell) {
            this.spell = spell;
        }
    }

    public static Spell getSpell(ResourceLocation id) {
        Optional<Spell> search = Arrays.stream(SPELLS.values()).filter(s -> s.spell.id.equals(id)).findFirst().map(spells -> spells.spell);
        return search.orElse(null);
    }
}
