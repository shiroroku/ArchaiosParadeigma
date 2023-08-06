package teamforesight.arcpara.Client.CastingOverlay.ResearchTree;

import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Vector2f;
import teamforesight.arcpara.ArcPara;
import teamforesight.arcpara.Data.ResearchTree.ResearchNode;
import teamforesight.arcpara.Data.ResearchTree.ResearchTreeLoader;

import java.util.Arrays;
import java.util.List;

public class ResearchNodeRender {

	private static final ResourceLocation WIDGETS = new ResourceLocation(ArcPara.MODID, "textures/gui/research_tree/widgets.png");

	public final ResearchNode Node;
	private final SimpleTimer animationTimer;
	private final ItemStack Icon;
	private final List<ResearchNode> Parents;
	private final List<Component> ToolTip;

	public ResearchNodeRender (ResourceLocation pTreeID, ResourceLocation pID, ResearchNode pNode, SimpleTimer pAnimationTimer) {
		this.Node = pNode;
		this.animationTimer = pAnimationTimer;
		this.Icon = new ItemStack(ForgeRegistries.ITEMS.getValue(pNode.Icon));
		Parents = pNode.Parents.stream().map(p -> ResearchTreeLoader.TREES.get(pTreeID).get(p)).toList();
		String key = "research.%s.%s.%s".formatted(pID.getNamespace(), pTreeID.getPath(), pID.getPath());
		ToolTip = Arrays.asList(Component.translatable(key), Component.translatable(key + ".desc"));
	}

	public void render (GuiGraphics pGuiGraphics) {
		drawConnections(pGuiGraphics);
		pGuiGraphics.blit(WIDGETS, (int) Node.X - 16, (int) Node.Y - 16, 32, 0, 32, 32);
	}

	/**
	 * Separate than render because rendering items in-between connections and blits will cause weird transparency bugs.
	 */
	public void renderItem (GuiGraphics pGuiGraphics) {
		pGuiGraphics.renderItem(Icon, (int) Node.X - 8, (int) Node.Y - 8);
	}

	public void renderTooltip (GuiGraphics pGuiGraphics, double pMouseX, double pMouseY) {
		pGuiGraphics.renderComponentTooltip(Minecraft.getInstance().font, ToolTip, (int) pMouseX, (int) pMouseY);
	}

	public void drawConnections (GuiGraphics pGuiGraphics) {
		Parents.forEach(p -> {
			if (p != null) {
				renderLine(pGuiGraphics, new Vector2f(p.X, p.Y), new Vector2f(Node.X, Node.Y));
			}
		});
	}

	private void renderLine (GuiGraphics pGuiGraphics, Vector2f pStart, Vector2f pEnd) {
		pGuiGraphics.pose().pushPose();
		int a = (int) (pEnd.x - pStart.x);
		int b = (int) (pEnd.y - pStart.y);
		int distance = (int) Math.sqrt(Mth.square(a) + Mth.square(b));
		int arrow_texture_x = 0;
		int arrow_texture_y = 0;
		pGuiGraphics.pose().rotateAround(Axis.ZP.rotationDegrees((float) Math.toDegrees(Math.atan2(b, a))), pStart.x, pStart.y, 0);
		pGuiGraphics.setColor(1, 1, 1, 0.3f);
		pGuiGraphics.blit(WIDGETS, (int) (pStart.x + 10), (int) (pStart.y - 6), arrow_texture_x, arrow_texture_y, 5, 13);
		pGuiGraphics.blitRepeating(WIDGETS, (int) (pStart.x + 15), (int) (pStart.y - 6), distance - 41, 13, arrow_texture_x + 5, arrow_texture_y, 5, 13);
		pGuiGraphics.blit(WIDGETS, (int) (pStart.x + distance - 26), (int) (pStart.y - 6), arrow_texture_x + 10, arrow_texture_y, 16, 13);
		pGuiGraphics.setColor(1, 1, 1, 1 - Mth.abs(animationTimer.getPercentageProgress() * 2 - 1));
		float d_a = ((distance - 25) * animationTimer.getPercentageProgress()) + 10;
		pGuiGraphics.blit(WIDGETS, (int) (pStart.x + d_a), (int) (pStart.y - 6), arrow_texture_x, arrow_texture_y, 5, 13);
		pGuiGraphics.setColor(1, 1, 1, 1);
		pGuiGraphics.pose().popPose();
	}
}
