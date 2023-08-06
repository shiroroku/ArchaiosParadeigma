package teamforesight.arcpara.Spell;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import teamforesight.arcpara.ArcPara;
import teamforesight.arcpara.Network.SpellCastPacket;
import teamforesight.arcpara.Registry.CapabilityRegistry;
import teamforesight.arcpara.Registry.SpellRegistry;

public abstract class Spell {

	public final ResourceLocation ID;

	// These values are NOT saved or synced and shouldnt be acessed on client
	public boolean HasStoppedPrimary = true;
	public boolean HasStoppedSecondary = true;
	public final float ManaCostPrimary;
	public final float ManaCostSecondary;
	public boolean IsHoldingPrimary = false;
	public boolean IsHoldingSecondary = false;
	public int ChargeDurationPrimary = 0;
	public int ChargeDurationSecondary = 0;
	public final int MaxChargePrimary = 100;
	public final int MaxChargeSecondary = 100;


	public Spell (ResourceLocation pID, float pManaCostPrimary, float pManaCostSecondary) {
		this.ID = pID;
		this.ManaCostPrimary = pManaCostPrimary;
		this.ManaCostSecondary = pManaCostSecondary;
	}

	public Spell (ResourceLocation pID, float pSharedCost) {
		this(pID, pSharedCost, pSharedCost);
	}

	/**
	 * Called when the cast starts
	 */
	public abstract void castStart (Player pPlayer, boolean pIsPrimary);

	/**
	 * Called every tick while isHolding is true
	 */
	public abstract void castHold (Player pPlayer, boolean pIsPrimary);

	/**
	 * Called when the cast finishes
	 */
	public abstract void castEnd (Player pPlayer, boolean pIsPrimary, int pChargeDuration);

	/**
	 * Called when server gets client packet.
	 *
	 * @see SpellCastPacket
	 */
	public final void start (Player pPlayer, boolean pIsPrimary) {
		ArcPara.LOGGER.debug("[{}][{}] Starting cast.", ID, pIsPrimary);
		// Resets hasStopped, used so the server knows when to ignore client stop packet
		if (pIsPrimary) {
			IsHoldingPrimary = true;
			HasStoppedPrimary = false;
		} else {
			IsHoldingSecondary = true;
			HasStoppedSecondary = false;
		}
		castStart(pPlayer, pIsPrimary);
	}

	/**
	 * Called when server gets client packet if stoppedCasting is false, and when the charge is cancelled by reaching maxCharge.
	 * This is separate from castEnd because we dont want to clear charge durations before we use them.
	 *
	 * @see SpellCastPacket
	 * @see SpellRegistry#onPlayerTick(TickEvent.PlayerTickEvent)
	 */
	public final void stop (Player player, boolean isPrimary) {
		ArcPara.LOGGER.debug("[{}][{}] Stopping cast.", ID, isPrimary);
		castEnd(player, isPrimary, (isPrimary ? ChargeDurationPrimary : ChargeDurationSecondary));
		// Sets hasStoppped to true, when this is true, the server will ignore any client stop packet
		if (isPrimary) {
			HasStoppedPrimary = true;
		} else {
			HasStoppedSecondary = true;
		}
		stopCharging(isPrimary);
	}

	/**
	 * Returns the matching holdDuration for primary or secondary
	 */
	public int getChargeDuration (boolean pIsPrimary) {
		return pIsPrimary ? this.ChargeDurationPrimary : this.ChargeDurationSecondary;
	}

	/**
	 * Resets holdDuration and isHolding, called automatically when maxHold is reached
	 */
	public void stopCharging (boolean pIsPrimary) {
		if (pIsPrimary) {
			IsHoldingPrimary = false;
			ChargeDurationPrimary = 0;
		} else {
			IsHoldingSecondary = false;
			ChargeDurationSecondary = 0;
		}
	}

	protected void consumeMana (Player pPlayer, float pAmount) {
		CapabilityRegistry.getSpellCaster(pPlayer).ifPresent(p -> {
			p.spendMana(pAmount);
			CapabilityRegistry.sendCapabilityPacket(pPlayer);
		});
	}

	public boolean cantCast (Player pPlayer, boolean pIsPrimary) {
		return CapabilityRegistry.getSpellCaster(pPlayer).orElseGet(null).getMana() < (pIsPrimary ? ManaCostPrimary : ManaCostSecondary);
	}

}
