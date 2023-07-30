package teamforesight.arcpara.Registry;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import teamforesight.arcpara.ArcPara;
import teamforesight.arcpara.Command.ManaCommand;
import teamforesight.arcpara.Command.SetSpellCommand;

@Mod.EventBusSubscriber(modid = ArcPara.MODID)
public class CommandRegistry {
    @SubscribeEvent
    public static void onRegisterCommand(RegisterCommandsEvent event) {
        SetSpellCommand.register(event.getDispatcher());
        ManaCommand.register(event.getDispatcher());
    }
}
