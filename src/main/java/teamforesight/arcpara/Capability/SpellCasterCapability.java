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
	private float MaxMana;
	private float Mana;
	private String[] EquippedSpells;
	/**
	 * Holds all spells the player has, spell data is NOT synced or saved.
	 */
	private List<Spell> Spells;

	public SpellCasterCapability (CompoundTag pNbt) {
		MaxMana = 100f;
		Mana = MaxMana;
		EquippedSpells = new String[]{"empty", "empty", "empty", "empty", "empty", "empty"};
		Spells = new ArrayList<>();
		deserializeNBT(pNbt);
	}

	public SpellCasterCapability () {
		this(new CompoundTag());
		MaxMana = 100f;
		Mana = MaxMana;
		EquippedSpells = new String[]{"empty", "empty", "empty", "empty", "empty", "empty"};
		Spells = new ArrayList<>();
	}

	@Override
	public float getMaxMana () {
		return MaxMana;
	}

	@Override
	public void setMaxMana (float pAmount) {
		MaxMana = pAmount;
		if (Mana > MaxMana) {
			Mana = MaxMana;
		}
	}

	@Override
	public float getMana () {
		return Mana;
	}

	@Override
	public void setMana (float pAmount) {
		Mana = pAmount;
	}

	@Override
	public boolean canSpendMana (float pAmount) {
		return pAmount <= getMana();
	}

	@Override
	public void spendMana (float pAmount) {
		setMana(getMana() - pAmount);
	}

	@Override
	public void addMana (float pAmount) {
		setMana(Math.min(getMaxMana(), getMana() + pAmount));
	}

	@Override
	public List<Spell> getSpells () {
		return Spells;
	}

	@Override
	public void setSpells (List<Spell> pSpells) {
		this.Spells = pSpells;
	}

	@Override
	public Spell getSpell (ResourceLocation pID) {
		return Spells.stream().filter(s -> s.ID.equals(pID)).findFirst().orElse(null);
	}

	@Override
	public String[] getEquippedSpells () {
		return EquippedSpells;
	}

	@Override
	public void setEquippedSpells (String[] pSpells) {
		EquippedSpells = pSpells;
	}

	@Override
	public void setEquippedSpell (int pSlot, String pSpellID) {
		getEquippedSpells()[pSlot] = pSpellID;
	}

	@Override
	public String getEquippedSpell (int pSlot) {
		return getEquippedSpells()[pSlot];
	}

	@Override
	public CompoundTag serializeNBT () {
		CompoundTag tag = new CompoundTag();
		tag.putFloat("max_mana", getMaxMana());
		tag.putFloat("mana", getMana());
		for (int i = 0; i < 6; i++) {
			tag.putString("equipped_spell_" + i, getEquippedSpell(i));
		}
		tag.putInt("spellcount", getSpells().size());
		for (int i = 0; i < getSpells().size(); i++) {
			tag.putString("spell_list_" + i, getSpells().get(i).ID.toString());
		}
		return tag;
	}

	@Override
	public void deserializeNBT (CompoundTag pNbt) {
		setMaxMana(pNbt.getFloat("max_mana"));
		setMana(pNbt.getFloat("mana"));
		for (int i = 0; i < 6; i++) {
			setEquippedSpell(i, pNbt.getString("equipped_spell_" + i));
		}
		int count = pNbt.getInt("spellcount");
		getSpells().clear();
		for (int i = 0; i < count; i++) {
			Spell s = SpellRegistry.createSpell(ResourceLocation.tryParse(pNbt.getString("spell_list_" + i)));
			if (s != null) { // && !getSpells().contains(s)
				getSpells().add(s);
			}

		}
	}

	public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
		private final ISpellCaster SpellCaster = new SpellCasterCapability();
		private final LazyOptional<ISpellCaster> OptionalData = LazyOptional.of(() -> SpellCaster);

		@Override
		public CompoundTag serializeNBT () {
			return this.SpellCaster.serializeNBT();
		}

		@Override
		public void deserializeNBT (CompoundTag nbt) {
			this.SpellCaster.deserializeNBT(nbt);
		}

		@Override
		public <T> LazyOptional<T> getCapability (Capability<T> cap, Direction side) {
			return CapabilityRegistry.SPELL_CASTER.orEmpty(cap, this.OptionalData);
		}
	}
}
