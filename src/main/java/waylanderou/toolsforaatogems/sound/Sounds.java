package waylanderou.toolsforaatogems.sound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import waylanderou.toolsforaatogems.ToolsForAatOGems;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class Sounds {

	@ObjectHolder(ToolsForAatOGems.MOD_ID + ":scythe_used")
	public static final SoundEvent SCYTHE_USED = null;

	@SubscribeEvent
	public static void registerSounds(final RegistryEvent.Register<SoundEvent> event) {
		ResourceLocation location = new ResourceLocation("toolsforaatogems", "scythe_used");
		SoundEvent soundEvent = new SoundEvent(location).setRegistryName(location);
		event.getRegistry().register(soundEvent);
	}

}
