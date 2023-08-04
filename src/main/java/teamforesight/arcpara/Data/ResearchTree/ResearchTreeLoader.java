package teamforesight.arcpara.Data.ResearchTree;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import teamforesight.arcpara.ArcPara;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResearchTreeLoader extends SimpleJsonResourceReloadListener {

	public static final Map<ResourceLocation, Map<ResourceLocation, ResearchNode>> TREES = new HashMap<>();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	public ResearchTreeLoader () {
		super(GSON, "research_tree");
	}

	@Override
	protected void apply (Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
		TREES.clear();
		for (Map.Entry<ResourceLocation, JsonElement> entry : pObject.entrySet()) {
			try {
				Map<ResourceLocation, ResearchNode> research_tree = new HashMap<>();
				entry.getValue().getAsJsonArray().forEach(j -> {
					JsonObject json = j.getAsJsonObject();

					ResourceLocation id = ResourceLocation.tryParse(json.get("id").getAsString());
					float x = json.get("x").getAsFloat();
					float y = -json.get("y").getAsFloat(); // Invert y so + numbers mean up
					ResourceLocation icon = ResourceLocation.tryParse(json.get("icon").getAsString());
					List<ResourceLocation> parents = json.get("parents").getAsJsonArray().asList().stream().map(s -> ResourceLocation.tryParse(s.getAsString())).toList();

					research_tree.put(id, new ResearchNode(x, y, icon, parents));
				});
				TREES.put(entry.getKey(), research_tree);
			} catch (Exception e) {
				ArcPara.LOGGER.error("Failed to load research tree [{}]: {}", entry.getKey(), e.getMessage());
			}
		}
	}
}
