package teamforesight.arcpara.Capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import teamforesight.arcpara.Registry.CapabilityRegistry;
import teamforesight.arcpara.Registry.SpellRegistry;
import teamforesight.arcpara.Spell.Spell;

import java.util.ArrayList;
import java.util.List;

public class SpellCasterCapability implements ISpellCaster {
	private float maxMana;
	private float mana;
	private String[] equippedSpells;
	/**
	 * Holds all spells the player has, spell data is NOT synced or saved.
	 */
	private List<Spell> spells;

	public SpellCasterCapability(CompoundTag nbt) {
		maxMana = 100f;
		mana = maxMana;
		equippedSpells = new String[]{"empty", "empty", "empty", "empty", "empty", "empty"};
		spells = new ArrayList<>();
		deserializeNBT(nbt);
	}

	public SpellCasterCapability() {
		this(new CompoundTag());
		maxMana = 100f;
		mana = maxMana;
		equippedSpells = new String[]{"empty", "empty", "empty", "empty", "empty", "empty"};
		spells = new ArrayList<>();
	}

	@Override
	public float getMaxMana() {
		return maxMana;
	}

	@Override
	public void setMaxMana(float amount) {
		maxMana = amount;
		if (mana > maxMana) {
			mana = maxMana;
		}
	}

	@Override
	public float getMana() {
		return mana;
	}

	@Override
	public void setMana(float amount) {
		mana = amount;
	}

	@Override
	public boolean canSpendMana(float amount) {
		return amount <= getMana();
	}

	@Override
	public void spendMana(float amount) {
		setMana(getMana() - amount);
	}

	@Override
	public void addMana(float amount) {
		setMana(Math.min(getMaxMana(), getMana() + amount));
	}

	@Override
	public List<Spell> getSpells() {
		return spells;
	}

	@Override
	public void setSpells(List<Spell> spells) {
		this.spells = spells;
	}

	@Override
	public Spell getSpell(ResourceLocation id) {
		return spells.stream().filter(s -> s.id.equals(id)).findFirst().orElse(null);
	}

	@Override
	public String[] getEquippedSpells() {
		return equippedSpells;
	}

	@Override
	public void setEquippedSpells(String[] spells) {
		equippedSpells = spells;
	}

	@Override
	public void setEquippedSpell(int slot, String spell_id) {
		getEquippedSpells()[slot] = spell_id;
	}

	@Override
	public String getEquippedSpell(int slot) {
		return getEquippedSpells()[slot];
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putFloat("max_mana", getMaxMana());
		tag.putFloat("mana", getMana());
		for (int i = 0; i < 6; i++) {
			tag.putString("equipped_spell_" + i, getEquippedSpell(i));
		}
		tag.putInt("spellcount", getSpells().size());
		for (int i = 0; i < getSpells().size(); i++) {
			tag.putString("spell_list_" + i, getSpells().get(i).id.toString());
		}
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		setMaxMana(nbt.getFloat("max_mana"));
		setMana(nbt.getFloat("mana"));
		for (int i = 0; i < 6; i++) {
			setEquippedSpell(i, nbt.getString("equipped_spell_" + i));
		}
		int count = nbt.getInt("spellcount");
		getSpells().clear();
		for (int i = 0; i < count; i++) {
			Spell s = SpellRegistry.createSpell(ResourceLocation.tryParse(nbt.getString("spell_list_" + i)));
			if (s != null) { // && !getSpells().contains(s)
				getSpells().add(s);
			}

		}
	}

	public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
		private final ISpellCaster spell_caster = new SpellCasterCapability();
		private final LazyOptional<ISpellCaster> optionalData = LazyOptional.of(() -> spell_caster);

		@Override
		public CompoundTag serializeNBT() {
			return this.spell_caster.serializeNBT();
		}

		@Override
		public void deserializeNBT(CompoundTag nbt) {
			this.spell_caster.deserializeNBT(nbt);
		}

		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			return CapabilityRegistry.spell_caster.orEmpty(cap, this.optionalData);
		}
	}
}
