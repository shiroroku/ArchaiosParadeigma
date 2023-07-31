package teamforesight.arcpara.Command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.resources.ResourceLocation;
import teamforesight.arcpara.ArcPara;
import teamforesight.arcpara.Registry.CapabilityRegistry;
import teamforesight.arcpara.Registry.SpellRegistry;

public class SetSpellCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("arcpara").requires(cs -> cs.hasPermission(4)).then(Commands.argument("player", EntityArgument.player()).then(Commands.literal("set_equipped_spell").then(Commands.argument("slot", IntegerArgumentType.integer(0, 5)).then(Commands.argument("spell", StringArgumentType.string()).executes(cs -> {
			CapabilityRegistry.getSpellCaster(EntityArgument.getPlayer(cs, "player")).ifPresent(cap -> {
				String spell = StringArgumentType.getString(cs, "spell");
				if (spell.isEmpty()) {
					spell = null;
				}
				String finalSpell = spell;
				if (cap.getSpells().stream().noneMatch(s -> s.id.toString().equals(finalSpell))) {
					ArcPara.LOGGER.error("Cannot set equipped spell, player does not have the spell: [{}]!", finalSpell);
					return;
				}
				cap.setEquippedSpell(IntegerArgumentType.getInteger(cs, "slot"), finalSpell);
			});
			CapabilityRegistry.sendCapabilityPacket(EntityArgument.getPlayer(cs, "player"));
			return 0;
		})))).then(Commands.literal("add_spell").then(Commands.argument("spell", StringArgumentType.string()).executes(cs -> {
			CapabilityRegistry.getSpellCaster(EntityArgument.getPlayer(cs, "player")).ifPresent(cap -> {
				String spell = StringArgumentType.getString(cs, "spell");
				if (spell.isEmpty()) {
					spell = null;
				}
				ResourceLocation spell_id = ResourceLocation.tryParse(spell);
				if (spell_id == null) {
					ArcPara.LOGGER.error("Cannot add spell, [{}] is not a spell!", spell_id);
				}
				cap.getSpells().add(SpellRegistry.createSpell(spell_id));
			});
			CapabilityRegistry.sendCapabilityPacket(EntityArgument.getPlayer(cs, "player"));
			return 0;
		}))).then(Commands.literal("clear_spells").executes(cs -> {
			CapabilityRegistry.getSpellCaster(EntityArgument.getPlayer(cs, "player")).ifPresent(cap -> cap.getSpells().clear());
			CapabilityRegistry.sendCapabilityPacket(EntityArgument.getPlayer(cs, "player"));
			return 0;
		})).then(Commands.literal("clear_equipped_spells").executes(cs -> {
			CapabilityRegistry.getSpellCaster(EntityArgument.getPlayer(cs, "player")).ifPresent(cap -> cap.setEquippedSpells(new String[]{"empty", "empty", "empty", "empty", "empty", "empty"}));
			CapabilityRegistry.sendCapabilityPacket(EntityArgument.getPlayer(cs, "player"));
			return 0;
		}))));
	}
}
