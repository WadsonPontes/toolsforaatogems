package waylanderou.toolsforaatogems;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.loading.FMLPaths;

public class Config {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static ForgeConfigSpec spec;

	public static ForgeConfigSpec.IntValue scytheRadius;
	public static ForgeConfigSpec.BooleanValue scytheDropSeeds;
	public static ForgeConfigSpec.IntValue lessDurability;
	public static ForgeConfigSpec.BooleanValue playScytheSound;

	static {

		BUILDER.push("general");
		scytheRadius = BUILDER.comment("Scythe harvest radius (default: 2)").defineInRange("scytheRadius", 2, 1, 10);
		scytheDropSeeds = BUILDER.comment("Should harvesting crops with a scythe drop seeds (default: true)").define("scytheDropSeeds", true);
		lessDurability = BUILDER
				.comment("How much durability your scythe should lose everytime you use it. 2 is default. 0 makes it lose as much durability as it has harvested crops")
				.defineInRange("scytheDurability", 2, 0, 5);
		playScytheSound = BUILDER.comment("Should a sound be played when using scythe").define("playScytheSound", true);
		BUILDER.pop();

		spec = BUILDER.build();
	}

	public static void loadConfig() {
		final CommentedFileConfig configData = CommentedFileConfig.builder(FMLPaths.CONFIGDIR.get().resolve(ToolsForAatOGems.MOD_ID + "-common.toml"))
				.sync()
				.autosave()
				.writingMode(WritingMode.REPLACE)
				.build();
		configData.load();
		spec.setConfig(configData);
	}

}
