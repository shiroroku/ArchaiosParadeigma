package teamforesight.arcpara.Capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface ISpellCaster extends INBTSerializable<CompoundTag> {

	float getMaxMana();

	void setMaxMana(float amount);

	float getMana();

	void setMana(float amount);

	boolean canSpendMana(float amount);

	void spendMana(float amount);

	void addMana(float amount);

	String[] getEquippedSpells();

	void setEquippedSpells(String[] spells);

	void setSpell(int slot, String spell_id);

	String getSpell(int slot);
}
