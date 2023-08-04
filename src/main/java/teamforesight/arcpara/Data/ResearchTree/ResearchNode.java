package teamforesight.arcpara.Data.ResearchTree;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ResearchNode {

	public float x;
	public float y;
	public ResourceLocation icon;
	public List<ResourceLocation> parents;

	public ResearchNode(float x, float y, ResourceLocation icon, List<ResourceLocation> parents) {
		this.x = x;
		this.y = y;
		this.icon = icon;
		this.parents = parents;
	}
}
