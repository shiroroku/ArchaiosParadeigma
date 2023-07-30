package teamforesight.arcpara.Command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import teamforesight.arcpara.Registry.CapabilityRegistry;

public class ManaCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("arcpara").requires(cs -> cs.hasPermission(4)).then(Commands.literal("mana").then(Commands.argument("player", EntityArgument.player()).then(Commands.literal("set").then(Commands.argument("amount", FloatArgumentType.floatArg(0)).executes((cs -> {
            CapabilityRegistry.getSpellCaster(EntityArgument.getPlayer(cs, "player")).ifPresent(cap -> cap.setMana(Math.min(FloatArgumentType.getFloat(cs, "amount"), cap.getMaxMana())));
            CapabilityRegistry.sendCapabilityPacket(EntityArgument.getPlayer(cs, "player"));
            return 0;
        })))))).then(Commands.literal("max_mana").then(Commands.argument("player", EntityArgument.player()).then(Commands.literal("set").then(Commands.argument("amount", FloatArgumentType.floatArg(0)).executes((cs -> {
            CapabilityRegistry.getSpellCaster(EntityArgument.getPlayer(cs, "player")).ifPresent(cap -> cap.setMaxMana(FloatArgumentType.getFloat(cs, "amount")));
            CapabilityRegistry.sendCapabilityPacket(EntityArgument.getPlayer(cs, "player"));
            return 0;
        })))))));
    }
}
