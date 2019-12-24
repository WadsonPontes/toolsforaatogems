package waylanderou.toolsforaatogems;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod("toolsforaatogems")
public class ToolsForAatOGems
{
	public static final String MOD_ID = "toolsforaatogems";

	public ToolsForAatOGems() {

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ToolsConfig.spec);        
		ToolsConfig.loadConfig();

	}
}
