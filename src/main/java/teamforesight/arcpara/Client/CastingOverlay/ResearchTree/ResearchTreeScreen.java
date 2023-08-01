package teamforesight.arcpara.Client.CastingOverlay.ResearchTree;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Vector2f;
import teamforesight.arcpara.ArcPara;
import teamforesight.arcpara.ModUtil;

import java.util.Random;

public class ResearchTreeScreen extends Screen {
	private static final ResourceLocation BORDER = new ResourceLocation(ArcPara.MODID, "textures/gui/research_tree/border.png");
	private static final ResourceLocation BACKGROUND_1 = new ResourceLocation(ArcPara.MODID, "textures/gui/research_tree/background_1.png");
	private static final ResourceLocation BACKGROUND_2 = new ResourceLocation(ArcPara.MODID, "textures/gui/research_tree/background_2.png");

	// Texture dimensions
	private static final int WIDTH = 256;
	private static final int HEIGHT = 224;

	private double view_x = 0;
	private double view_y = 0;

	private SimpleAnimation soundAnimation = new SimpleAnimation(40, true);
	private SimpleAnimation lineAnimation = new SimpleAnimation(60, true);


	public ResearchTreeScreen() {
		super(GameNarrator.NO_TITLE);
		lineAnimation.start();
		soundAnimation.start();
	}

	@Override
	public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
		if (pButton == 1) {

			double mouseXConverted = pMouseX - (this.width - WIDTH) * 0.5f - WIDTH * 0.5f - view_x;
			double mouseYConverted = pMouseY - (this.height - HEIGHT) * 0.5f - HEIGHT * 0.5f - view_y;
			if (mouseXConverted > point1.x - 8 && mouseYConverted > point1.y - 8 && mouseXConverted < point1.x + 8 && mouseYConverted < point1.y + 8) {
				point1.x += (float) pDragX;
				point1.y += (float) pDragY;
			}
			return true;
		}

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
		if(soundAnimation.getTicks() % 40 == 0) {
			Random rand = new Random();
			if(rand.nextBoolean()){
				Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.AMETHYST_BLOCK_CHIME, (float) (1.5 - rand.nextDouble() * 0.5f), 1F));
			}
		}

		soundAnimation.tick(pPartialTick);
		lineAnimation.tick(pPartialTick);
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

	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
		return super.mouseClicked(pMouseX, pMouseY, pButton);
	}

	@Override
	public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
		return super.mouseReleased(pMouseX, pMouseY, pButton);
	}

	Vector2f point1 = new Vector2f(-70, 60);
	Vector2f point2 = new Vector2f(40, -20);
	Vector2f point3 = new Vector2f(-60, -15);

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


		renderArrow(pGuiGraphics, point1, point2);
		renderArrow(pGuiGraphics, point2, point3);
		renderArrow(pGuiGraphics, point3, point1);
		pGuiGraphics.blit(BORDER, (int) point1.x - 16, (int) point1.y - 16, 32, 224, 32, 32);


		pGuiGraphics.renderItem(new ItemStack(Items.GOLDEN_SWORD), (int) point1.x - 8, (int) point1.y - 8);


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

	private void renderArrow(GuiGraphics pGuiGraphics, Vector2f start, Vector2f end) {
		pGuiGraphics.pose().pushPose();
		int a = (int) (end.x - start.x);
		int b = (int) (end.y - start.y);
		int distance = (int) Math.sqrt(Mth.square(a) + Mth.square(b));
		float angle = (float) Math.toDegrees(Math.atan2(b, a));
		int arrow_texture_x = 0;
		int arrow_texture_y = 226;
		pGuiGraphics.pose().rotateAround(Axis.ZP.rotationDegrees(angle), start.x, start.y, 0);
		pGuiGraphics.setColor(1, 1, 1, 0.3f);
		pGuiGraphics.blit(BORDER, (int) (start.x + 10), (int) (start.y - 6), arrow_texture_x, arrow_texture_y, 5, 13);
		pGuiGraphics.blitRepeating(BORDER, (int) (start.x + 15), (int) (start.y - 6), distance - 41, 13, arrow_texture_x + 5, arrow_texture_y, 5, 13);
		pGuiGraphics.blit(BORDER, (int) (start.x + distance - 26), (int) (start.y - 6), arrow_texture_x + 10, arrow_texture_y, 16, 13);

		pGuiGraphics.setColor(1, 1, 1, 1 - Mth.abs(lineAnimation.getPercentageProgress() * 2 - 1));
		float d_a = ((distance - 25) * lineAnimation.getPercentageProgress()) + 10;
		pGuiGraphics.blit(BORDER, (int) (start.x + d_a), (int) (start.y - 6), arrow_texture_x, arrow_texture_y, 5, 13);
		pGuiGraphics.setColor(1, 1, 1, 1);
		pGuiGraphics.pose().popPose();
	}

}
