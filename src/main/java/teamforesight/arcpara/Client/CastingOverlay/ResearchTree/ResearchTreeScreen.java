package teamforesight.arcpara.Client.CastingOverlay.ResearchTree;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import teamforesight.arcpara.ArcPara;
import teamforesight.arcpara.Data.ResearchTree.ResearchTreeLoader;
import teamforesight.arcpara.ModUtil;

import java.util.List;
import java.util.Random;

public class ResearchTreeScreen extends Screen {
	private static final ResourceLocation BORDER = new ResourceLocation(ArcPara.MODID, "textures/gui/research_tree/border.png");
	private static final ResourceLocation BACKGROUND_1 = new ResourceLocation(ArcPara.MODID, "textures/gui/research_tree/background_1.png");
	private static final ResourceLocation BACKGROUND_2 = new ResourceLocation(ArcPara.MODID, "textures/gui/research_tree/background_2.png");

	// Texture dimensions
	private static final int WIDTH = 256;
	private static final int HEIGHT = 224;
	private final SimpleTimer SoundTimer = new SimpleTimer(40, true);
	private final SimpleTimer LineTimer = new SimpleTimer(60, true);
	private final Random Rand = new Random();
	private double ViewX = 0;
	private double ViewY = 0;
	private final List<ResearchNodeRender> Nodes;

	public ResearchTreeScreen () {
		super(GameNarrator.NO_TITLE);
		SoundTimer.start();
		LineTimer.start();
		ResourceLocation tree_id = new ResourceLocation(ArcPara.MODID, "tree_1");
		Nodes = ResearchTreeLoader.TREES.get(tree_id).entrySet().stream().map(es -> new ResearchNodeRender(tree_id, es.getKey(), es.getValue(), LineTimer)).toList();
	}

	@Override
	public void render (GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		if (SoundTimer.getTicks() % 40 == 0 && Rand.nextBoolean()) {
			Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.AMETHYST_BLOCK_CHIME, (float) (1.5 - Rand.nextDouble() * 0.5f), 1F));
		}
		SoundTimer.tick(pPartialTick);
		LineTimer.tick(pPartialTick);

		int x_gui_left = (int) ((this.width - WIDTH) * 0.5f);
		int y_gui_top = (int) ((this.height - HEIGHT) * 0.5f);
		int x_gui_center = (int) (x_gui_left + WIDTH * 0.5f);
		int y_gui_center = (int) (y_gui_top + HEIGHT * 0.5f);
		final double mouse_x = pMouseX - x_gui_center - ViewX;
		final double mouse_y = pMouseY - y_gui_center - ViewY;

		// Game tint
		this.renderBackground(pGuiGraphics);

		// Start Background
		RenderSystem.enableBlend();
		pGuiGraphics.enableScissor(x_gui_left + 5, y_gui_top + 5, x_gui_left + WIDTH - 5, y_gui_top + HEIGHT - 5);
		pGuiGraphics.pose().pushPose();
		float transparency = ModUtil.waveFunc(0.1f);
		pGuiGraphics.blitRepeating(BACKGROUND_1, x_gui_left, y_gui_top, WIDTH, HEIGHT, (int) -ViewX, (int) -ViewY, WIDTH, HEIGHT);
		pGuiGraphics.setColor(1, 1, 1, transparency);
		pGuiGraphics.blitRepeating(BACKGROUND_2, x_gui_left, y_gui_top, WIDTH, HEIGHT, (int) -ViewX, (int) -ViewY, WIDTH, HEIGHT);
		pGuiGraphics.setColor(1, 1, 1, 1);

		// Nodes
		pGuiGraphics.pose().pushPose();
		pGuiGraphics.pose().translate(ViewX + x_gui_center, ViewY + y_gui_center, 0);
		Nodes.forEach(n -> n.render(pGuiGraphics));
		Nodes.forEach(n -> n.renderItem(pGuiGraphics));
		pGuiGraphics.pose().popPose();
		// End Nodes

		//Debug info
		if (Minecraft.getInstance().options.renderDebug) {
			//Coordinates
			pGuiGraphics.drawString(font, "[%d,%d]".formatted((int) ViewX, (int) ViewY), x_gui_left + 5, y_gui_top + 5, -1);
		}

		pGuiGraphics.pose().popPose();
		pGuiGraphics.disableScissor();
		// End Background

		// Border
		RenderSystem.enableBlend();
		pGuiGraphics.blit(BORDER, x_gui_left, y_gui_top, 0, 0, WIDTH, HEIGHT);

		// Tooltips
		pGuiGraphics.pose().pushPose();
		pGuiGraphics.pose().translate(ViewX + x_gui_center, ViewY + y_gui_center, 0);
		Nodes.forEach(n -> {
			if (isMouseOver((int) n.Node.X - 8, (int) n.Node.Y - 8, 16, 16, mouse_x, mouse_y)) {
				n.renderTooltip(pGuiGraphics, mouse_x, mouse_y);
			}
		});
		pGuiGraphics.pose().popPose();

		RenderSystem.disableBlend();
		super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
	}


	/**
	 * Handles closing the menu when inventory key is pressed
	 */
	@Override
	public boolean keyPressed (int pKeyCode, int pScanCode, int pModifiers) {
		if (super.keyPressed(pKeyCode, pScanCode, pModifiers)) {
			return true;
		} else if (this.minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(pKeyCode, pScanCode))) {
			this.onClose();
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseClicked (double pMouseX, double pMouseY, int pButton) {
		return super.mouseClicked(pMouseX, pMouseY, pButton);
	}

	@Override
	public boolean mouseReleased (double pMouseX, double pMouseY, int pButton) {
		return super.mouseReleased(pMouseX, pMouseY, pButton);
	}

	/**
	 * Handles screen panning.
	 */
	@Override
	public boolean mouseDragged (double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
		ViewX += pDragX;
		ViewY += pDragY;
		return true;
	}

	private boolean isMouseOver (int pX, int pY, int pWidth, int pHeight, double pMouseX, double pMouseY) {
		return pMouseX >= (pX - 1) && pMouseX < (pX + pWidth + 1) && pMouseY >= (pY - 1) && pMouseY < (pY + pHeight + 1);
	}

}
