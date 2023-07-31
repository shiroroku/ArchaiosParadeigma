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

	public static Spell createSpell(ResourceLocation id) {
		return SPELLS.get(id).get();
	}

	/**
	 * Handles spell charging serverside
	 */
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.START && event.side == LogicalSide.SERVER) {
			CapabilityRegistry.getSpellCaster(event.player).ifPresent((cap) -> cap.getSpells().forEach(s -> {
				// Primary charge
				if (s.isHoldingPrimary) { // Increase duration while holding is true, call spell function
					s.chargeDurationPrimary++;
					s.castHold(event.player, true);
				} else if (s.chargeDurationPrimary != 0) { // If spell is not charging and our duration hasnt been reset, reset it
					s.chargeDurationPrimary = 0;
				}
				if (s.chargeDurationPrimary >= s.maxChargePrimary) { // If we reached the max charge duration, stop charging
					s.stop(event.player, true);
				}

				// Secondary charge
				if (s.isHoldingSecondary) {
					s.chargeDurationSecondary++;
					s.castHold(event.player, false);
				} else if (s.chargeDurationSecondary != 0) {
					s.chargeDurationSecondary = 0;
				}
				if (s.chargeDurationSecondary >= s.maxChargeSecondary) {
					s.stop(event.player, false);
				}
			}));
		}
	}
}
