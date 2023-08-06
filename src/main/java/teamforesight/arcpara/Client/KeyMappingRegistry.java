package teamforesight.arcpara.Client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import teamforesight.arcpara.ArcPara;

@Mod.EventBusSubscriber(modid = ArcPara.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyMappingRegistry {

	public static final Lazy<KeyMapping> CAST_MODE = Lazy.of(() -> new KeyMapping("key.arcpara.cast_mode", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, "key.categories.arcpara.category"));

	@SubscribeEvent
	public static void registerBindings (RegisterKeyMappingsEvent pEvent) {
		pEvent.register(CAST_MODE.get());
	}
}
