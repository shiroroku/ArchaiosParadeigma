package teamforesight.arcpara.Registry;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import teamforesight.arcpara.ArcPara;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ArcPara.MODID);

    //public static final RegistryObject<Item> itema = ITEMS.register("itema", aofjoja::new);
}
