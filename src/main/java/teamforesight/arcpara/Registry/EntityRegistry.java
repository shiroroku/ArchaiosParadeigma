package teamforesight.arcpara.Registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import teamforesight.arcpara.ArcPara;
import teamforesight.arcpara.Spell.Earth.Brickshot.BrickshotEntity;

public class EntityRegistry {
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ArcPara.MODID);

	public static final RegistryObject<EntityType<BrickshotEntity>> Brickshot = ENTITIES.register("brickshot", () -> EntityType.Builder.<BrickshotEntity>of(BrickshotEntity::new, MobCategory.MISC).clientTrackingRange(4).build("brickshot"));

}
