package teamforesight.arcpara.Data.ResearchTree;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ResearchNode {

	public float X;
	public float Y;
	public ResourceLocation Icon;
	public List<ResourceLocation> Parents;

	public ResearchNode (float pX, float pY, ResourceLocation pIconItem, List<ResourceLocation> pParents) {
		this.X = pX;
		this.Y = pY;
		this.Icon = pIconItem;
		this.Parents = pParents;
	}
}
