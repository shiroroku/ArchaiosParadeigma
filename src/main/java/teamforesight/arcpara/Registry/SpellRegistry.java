package teamforesight.arcpara.Registry;

import teamforesight.arcpara.Spell.Fire.SparkSpell;
import teamforesight.arcpara.Spell.Spell;

public class SpellRegistry {

    public enum SPELLS {
        spark(new SparkSpell());

        public final Spell spell;

        SPELLS(Spell spell) {
            this.spell = spell;
        }
    }
}
