package teamforesight.arcpara.Spell;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import teamforesight.arcpara.ArcPara;
import teamforesight.arcpara.Network.SpellCastPacket;
import teamforesight.arcpara.Registry.CapabilityRegistry;
import teamforesight.arcpara.Registry.SpellRegistry;

public abstract class Spell {

	public final ResourceLocation id;

	// These values are NOT saved or synced and shouldnt be acessed on client
	public boolean hasStoppedPrimary = true;
	public boolean hasStoppedSecondary = true;
	public final float manaCostPrimary;
	public final float manaCostSecondary;
	public boolean isHoldingPrimary = false;
	public boolean isHoldingSecondary = false;
	public int chargeDurationPrimary = 0;
	public int chargeDurationSecondary = 0;
	public final int maxChargePrimary = 100;
	public final int maxChargeSecondary = 100;


	public Spell(ResourceLocation id, float manaCostPrimary, float manaCostSecondary) {
		this.id = id;
		this.manaCostPrimary = manaCostPrimary;
		this.manaCostSecondary = manaCostSecondary;
	}

	public Spell(ResourceLocation id, float sharedCost) {
		this(id, sharedCost, sharedCost);
	}

	/**
	 * Called when the cast starts
	 */
	public abstract void castStart(Player player, boolean isPrimary);

	/**
	 * Called every tick while isHolding is true
	 */
	public abstract void castHold(Player player, boolean isPrimary);

	/**
	 * Called when the cast finishes
	 */
	public abstract void castEnd(Player player, boolean isPrimary, int chargeDuration);

	/**
	 * Called when server gets client packet.
	 *
	 * @see SpellCastPacket
	 */
	public final void start(Player player, boolean isPrimary) {
		ArcPara.LOGGER.debug("[{}][{}] Starting cast.", id.toString(), isPrimary);
		// Resets hasStopped, used so the server knows when to ignore client stop packet
		if (isPrimary) {
			isHoldingPrimary = true;
			hasStoppedPrimary = false;
		} else {
			isHoldingSecondary = true;
			hasStoppedSecondary = false;
		}
		castStart(player, isPrimary);
	}

	/**
	 * Called when server gets client packet if stoppedCasting is false, and when the charge is cancelled by reaching maxCharge.
	 * This is separate from castEnd because we dont want to clear charge durations before we use them.
	 *
	 * @see SpellCastPacket
	 * @see SpellRegistry#onPlayerTick(TickEvent.PlayerTickEvent)
	 */
	public final void stop(Player player, boolean isPrimary) {
		ArcPara.LOGGER.debug("[{}][{}] Stopping cast.", id.toString(), isPrimary);
		castEnd(player, isPrimary, (isPrimary ? chargeDurationPrimary : chargeDurationSecondary));
		// Sets hasStoppped to true, when this is true, the server will ignore any client stop packet
		if (isPrimary) {
			hasStoppedPrimary = true;
		} else {
			hasStoppedSecondary = true;
		}
		stopCharging(isPrimary);
	}

	/**
	 * Returns the matching holdDuration for primary or secondary
	 */
	public int getChargeDuration(boolean isPrimary) {
		return isPrimary ? this.chargeDurationPrimary : this.chargeDurationSecondary;
	}

	/**
	 * Resets holdDuration and isHolding, called automatically when maxHold is reached
	 */
	public void stopCharging(boolean primary) {
		if (primary) {
			isHoldingPrimary = false;
			chargeDurationPrimary = 0;
		} else {
			isHoldingSecondary = false;
			chargeDurationSecondary = 0;
		}
	}

	protected void consumeMana(Player player, float amount) {
		CapabilityRegistry.getSpellCaster(player).ifPresent(p -> {
			p.spendMana(amount);
			CapabilityRegistry.sendCapabilityPacket(player);
		});
	}

	public boolean cantCast(Player player, boolean isPrimary) {
		return !(CapabilityRegistry.getSpellCaster(player).orElseGet(null).getMana() >= (isPrimary ? manaCostPrimary : manaCostSecondary));
	}

}
