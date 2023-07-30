package teamforesight.arcpara.Command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import teamforesight.arcpara.Registry.CapabilityRegistry;

public class SetSpellCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("arcpara").requires(cs -> cs.hasPermission(4)).then(Commands.literal("set_spell").then(Commands.argument("player", EntityArgument.player()).then(Commands.argument("slot", IntegerArgumentType.integer(0, 5)).then(Commands.argument("spell", StringArgumentType.string()).executes((cs -> {
            String spell = StringArgumentType.getString(cs, "spell");
            if (spell.isEmpty()) {
                spell = null;
            }
            String finalSpell = spell;
            CapabilityRegistry.getSpellCaster(EntityArgument.getPlayer(cs, "player")).ifPresent(cap -> cap.setSpell(IntegerArgumentType.getInteger(cs, "slot"), finalSpell));
            CapabilityRegistry.sendCapabilityPacket(EntityArgument.getPlayer(cs, "player"));
            return 0;
        })))))));
    }
}
