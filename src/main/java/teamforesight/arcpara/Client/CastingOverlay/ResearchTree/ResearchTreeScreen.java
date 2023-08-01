package teamforesight.arcpara.Client.CastingOverlay.ResearchTree;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Vector2i;
import teamforesight.arcpara.ArcPara;
import teamforesight.arcpara.ModUtil;

public class ResearchTreeScreen extends Screen {
	private static final ResourceLocation BORDER = new ResourceLocation(ArcPara.MODID, "textures/gui/research_tree/border.png");
	private static final ResourceLocation BACKGROUND_1 = new ResourceLocation(ArcPara.MODID, "textures/gui/research_tree/background_1.png");
	private static final ResourceLocation BACKGROUND_2 = new ResourceLocation(ArcPara.MODID, "textures/gui/research_tree/background_2.png");

	// Texture dimensions
	private static final int WIDTH = 256;
	private static final int HEIGHT = 224;

	private double view_x = 0;
	private double view_y = 0;

	public ResearchTreeScreen() {
		super(GameNarrator.NO_TITLE);
	}

	@Override
	public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
		view_x += pDragX;
		view_y += pDragY;
		return true;
	}

	/**
	 * Handles closing the menu when inventory key is pressed
	 */
	@Override
	public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
		if (super.keyPressed(pKeyCode, pScanCode, pModifiers)) {
			return true;
		} else if (this.minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(pKeyCode, pScanCode))) {
			this.onClose();
			return true;
		}
		return false;
	}

	@Override
	public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		int x = (this.width - WIDTH) / 2;
		int y = (this.height - HEIGHT) / 2;
		this.renderBackground(pGuiGraphics);
		this.renderContent(pGuiGraphics, x, y);
		this.renderBorder(pGuiGraphics, x, y);
		super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
	}

	public void renderBorder(GuiGraphics pGuiGraphics, int leftX, int topY) {
		RenderSystem.enableBlend();
		pGuiGraphics.blit(BORDER, leftX, topY, 0, 0, WIDTH, HEIGHT);
		RenderSystem.disableBlend();
	}

	public void renderContent(GuiGraphics pGuiGraphics, int leftX, int topY) {
		int border_size = 5;
		int half_x = (leftX + WIDTH / 2);
		int half_y = (topY + HEIGHT / 2);

		pGuiGraphics.enableScissor(leftX + border_size, topY + border_size, leftX + WIDTH - border_size, topY + HEIGHT - border_size);
		RenderSystem.enableBlend();
		pGuiGraphics.pose().pushPose();

		//Star fade
		float transparency = ModUtil.waveFunc(0.1f);
		pGuiGraphics.blitRepeating(BACKGROUND_1, leftX, topY, WIDTH, HEIGHT, (int) -view_x, (int) -view_y, WIDTH, HEIGHT);
		pGuiGraphics.setColor(1, 1, 1, transparency);
		pGuiGraphics.blitRepeating(BACKGROUND_2, leftX, topY, WIDTH, HEIGHT, (int) -view_x, (int) -view_y, WIDTH, HEIGHT);
		pGuiGraphics.setColor(1, 1, 1, 1);

		pGuiGraphics.pose().pushPose();
		pGuiGraphics.pose().translate(view_x + half_x, view_y + half_y, 0);

		Vector2i point1 = new Vector2i(-70, 60);
		Vector2i point2 = new Vector2i(40, -20);
		Vector2i point3 = new Vector2i(-60, -15);

		renderScaledItem(new ItemStack(Items.AMETHYST_BLOCK), pGuiGraphics, point1.x, point1.y, 1);
		renderScaledItem(new ItemStack(Items.ACACIA_LOG), pGuiGraphics, point2.x, point2.y, 1);
		renderScaledItem(new ItemStack(Items.IRON_SWORD), pGuiGraphics, point3.x, point3.y, 1);

		renderArrow(pGuiGraphics, point1, point2);
		renderArrow(pGuiGraphics, point2, point3);
		renderArrow(pGuiGraphics, point3, point1);

		pGuiGraphics.pose().popPose();

		//Debug info
		if (Minecraft.getInstance().options.renderDebug) {
			//Coordinates
			pGuiGraphics.drawString(font, "[%d,%d]".formatted((int) view_x, (int) view_y), leftX + 5, topY + 5, -1);
		}

		pGuiGraphics.pose().popPose();
		RenderSystem.disableBlend();
		pGuiGraphics.disableScissor();
	}

	private void renderArrow(GuiGraphics pGuiGraphics, Vector2i start, Vector2i end) {
		pGuiGraphics.pose().pushPose();
		int a = end.x - start.x;
		int b = end.y - start.y;
		int distance = (int) Math.sqrt(Mth.square(a) + Mth.square(b));
		float angle = (float) Math.toDegrees(Math.atan2(b, a));
		pGuiGraphics.pose().rotateAround(Axis.ZP.rotationDegrees(angle), start.x, start.y, 0);
		pGuiGraphics.blit(BORDER, start.x + 10, start.y - 2, 0, 230, 5, 5);
		pGuiGraphics.blitRepeating(BORDER, start.x + 15, start.y - 2, distance - 43, 5, 5, 230, 5, 5);
		pGuiGraphics.blit(BORDER, start.x + distance - 28, start.y - 6, 10, 226, 16, 13);
		pGuiGraphics.pose().popPose();
	}

	public void renderScaledItem(ItemStack pStack, GuiGraphics gui, double pX, double pY, float scale) {
		gui.pose().pushPose();
		gui.pose().translate((-16 * scale) / 2, (-16 * scale) / 2, 0);
		gui.pose().scale(scale, scale, scale);
		gui.pose().translate(pX / scale, pY / scale, 0);
		gui.renderItem(pStack, 0, 0);
		gui.pose().popPose();
	}
}
