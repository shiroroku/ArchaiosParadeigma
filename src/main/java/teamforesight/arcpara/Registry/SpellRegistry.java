package teamforesight.arcpara.Registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import teamforesight.arcpara.ArcPara;
import teamforesight.arcpara.Spell.DebugSpell;
import teamforesight.arcpara.Spell.Fire.SparkSpell;
import teamforesight.arcpara.Spell.Spell;

import java.util.HashMap;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = ArcPara.MODID)
public class SpellRegistry {

	public static HashMap<ResourceLocation, Supplier<? extends Spell>> SPELLS = new HashMap<>() {{
		put(new ResourceLocation(ArcPara.MODID, "debug"), DebugSpell::new);
		put(new ResourceLocation(ArcPara.MODID, "spark"), SparkSpell::new);
	}};

	public static Spell createSpell (ResourceLocation id) {
		return SPELLS.get(id).get();
	}

	/**
	 * Handles spell charging serverside
	 */
	@SubscribeEvent
	public static void onPlayerTick (TickEvent.PlayerTickEvent pEvent) {
		if (pEvent.phase == TickEvent.Phase.START && pEvent.side == LogicalSide.SERVER) {
			CapabilityRegistry.getSpellCaster(pEvent.player).ifPresent(cap -> cap.getSpells().forEach(s -> {
				// Primary charge
				if (s.IsHoldingPrimary) { // Increase duration while holding is true, call spell function
					s.ChargeDurationPrimary++;
					s.castHold(pEvent.player, true);
				} else if (s.ChargeDurationPrimary != 0) { // If spell is not charging and our duration hasnt been reset, reset it
					s.ChargeDurationPrimary = 0;
				}
				if (s.ChargeDurationPrimary >= s.MaxChargePrimary) { // If we reached the max charge duration, stop charging
					s.stop(pEvent.player, true);
				}

				// Secondary charge
				if (s.IsHoldingSecondary) {
					s.ChargeDurationSecondary++;
					s.castHold(pEvent.player, false);
				} else if (s.ChargeDurationSecondary != 0) {
					s.ChargeDurationSecondary = 0;
				}
				if (s.ChargeDurationSecondary >= s.MaxChargeSecondary) {
					s.stop(pEvent.player, false);
				}
			}));
		}
	}
}
