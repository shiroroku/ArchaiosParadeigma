package teamforesight.arcpara.Mixin.Client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import teamforesight.arcpara.Client.CastingOverlay.CastingOverlayInputHandler;
import teamforesight.arcpara.Client.CastingOverlay.CastingOverlayRenderer;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

	@Shadow
	@Final
	private Minecraft minecraft;

	/**
	 * Custom hook which bybasses the "overlay == null" check. (because we use overlay duh)
	 *
	 * @see CastingOverlayInputHandler#onOverlayMouseScroll(double)
	 */
	@Inject(at = @At("HEAD"), method = "onScroll(JDD)V")
	private void onScroll (long pWindowPointer, double pXOffset, double pYOffset, CallbackInfo pCallback) {
		if (pWindowPointer == Minecraft.getInstance().getWindow().getWindow() && (minecraft.getOverlay() instanceof CastingOverlayRenderer)) {
			double offset = pYOffset;
			if (Minecraft.ON_OSX && pYOffset == 0) {
				offset = pXOffset;
			}
			double d0 = (this.minecraft.options.discreteMouseScroll().get() ? Math.signum(offset) : offset) * this.minecraft.options.mouseWheelSensitivity().get();
			CastingOverlayInputHandler.onOverlayMouseScroll(d0);
		}
	}
}
