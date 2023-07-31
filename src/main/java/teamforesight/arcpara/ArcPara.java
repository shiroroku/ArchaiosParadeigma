package teamforesight.arcpara;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import teamforesight.arcpara.Registry.ItemRegistry;


@Mod(ArcPara.MODID)
public class ArcPara {

	public static final String MODID = "arcpara";
	public static final Logger LOGGER = LogManager.getLogger();

	public ArcPara() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		ItemRegistry.ITEMS.register(bus);
	}
}
