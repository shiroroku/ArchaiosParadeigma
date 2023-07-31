package teamforesight.arcpara.Capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import teamforesight.arcpara.Spell.Spell;

import java.util.List;

public interface ISpellCaster extends INBTSerializable<CompoundTag> {

	float getMaxMana();

	void setMaxMana(float amount);

	float getMana();

	void setMana(float amount);

	boolean canSpendMana(float amount);

	void spendMana(float amount);

	void addMana(float amount);

	Spell getSpell(ResourceLocation id);

	void setSpells(List<Spell> spells);

	List<Spell> getSpells();

	String[] getEquippedSpells();

	void setEquippedSpells(String[] spells);

	void setEquippedSpell(int slot, String spell_id);

	String getEquippedSpell(int slot);
}
