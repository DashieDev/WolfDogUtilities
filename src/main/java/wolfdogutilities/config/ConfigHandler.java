package wolfdogutilities.config;

import java.util.Map;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigHandler {

    private static ForgeConfigSpec CONFIG_SERVER_SPEC;
    public static ServerConfig SERVER;

    public static void init(IEventBus modEventBus) {
        Pair<ServerConfig, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        CONFIG_SERVER_SPEC = commonPair.getRight();
        SERVER = commonPair.getLeft();

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, CONFIG_SERVER_SPEC);
    }

    public static class ServerConfig {

        public ForgeConfigSpec.BooleanValue SAVE_WILD_WOLF;
        public ForgeConfigSpec.BooleanValue WOLF_TAME_LIMIT;
        public ForgeConfigSpec.IntValue WOLF_TAME_LIMIT_VALUE;
        public ForgeConfigSpec.BooleanValue DOG_TRAIN_LIMIT;
        public ForgeConfigSpec.IntValue DOG_TRAIN_LIMIT_VALUE;
        public ForgeConfigSpec.BooleanValue CAN_WOLF_BREED_OVER_LIMIT;
        public ForgeConfigSpec.BooleanValue CAN_DOG_BREED_OVER_LIMIT;
        public ForgeConfigSpec.BooleanValue SHOW_WOLF_TAME_COUNT;
        public ForgeConfigSpec.BooleanValue SHOW_WOLF_BREED_COUNT;
        public ForgeConfigSpec.BooleanValue SHOW_WOLF_DEATH_COUNT;

        public ServerConfig(ForgeConfigSpec.Builder builder) {

            builder.push("Wolf Dog Utilities");
            builder.comment("\"Dog\" refers to the dog from DoggyTalents.");
            builder.comment("If DoggyTalents is not installed, every config below involving \"Dog\" will simply have no effect.");
            builder.comment("");

            SAVE_WILD_WOLF = builder
                    .comment("This allows random wolves who dies in the wild to notify all player and get another chance to be tamed. ")
                    .translation("config.save_wild_wolf")
                    .define("save_wild_wolf", false);
            WOLF_TAME_LIMIT = builder
                    .comment("This put a limit on how many wolves each player can tame.")
                    .translation("config.new_uuid")
                    .define("wolf_tame_limit", false);
            WOLF_TAME_LIMIT_VALUE = builder
                .comment("This define the limit of how many wolves each player can tame.")
                .translation("config.wolf_tame_limit_value")
                .defineInRange("wolf_tame_limit_value", 7, 1, Integer.MAX_VALUE);
            DOG_TRAIN_LIMIT = builder
                    .comment("This put a limit on how many dogs each player can train.")
                    .translation("config.dog_train_limit")
                    .define("dog_train_limit", false);
            DOG_TRAIN_LIMIT_VALUE = builder
                .comment("This define the limit on how many dogs each player can train.")
                .translation("config.dog_train_limit_value")
                .defineInRange("dog_train_limit_value", 7, 1, Integer.MAX_VALUE);
            CAN_WOLF_BREED_OVER_LIMIT = builder
                .comment("Specifies whether wolves of the owner can breed while tame limit is reached.")
                .translation("config.wolf_breed_over_limit")
                .define("wolf_breed_over_limit", false);
            CAN_DOG_BREED_OVER_LIMIT = builder
                .comment("Specifies whether dogs of the owner can breed while train limit is reached.")
                .translation("config.dog_breed_over_limit")
                .define("dog_breed_over_limit", false);
            SHOW_WOLF_TAME_COUNT = builder
                .comment("This allows player to see how many wolves they got by Shift+RightClicking any wolves with a Bone in hand")
                .translation("config.show_wolf_tame_count")
                .define("show_wolf_tame_count", true);
            SHOW_WOLF_BREED_COUNT = builder
                .comment("This allows player to see how many wolves they have bred by Shift+RightClicking any wolves with a Poppy in hand")
                .translation("config.show_wolf_breed_count")
                .define("show_wolf_breed_count", true);
            SHOW_WOLF_DEATH_COUNT = builder
                .comment("This allows player to see how many wolves have died by Shift+RightClicking any wolves with an Axe in hand")
                .translation("config.show_wolf_death_count")
                .define("show_wolf_death_count", true);
            
            builder.pop();

            
        }

        public static<T> T getConfig(ConfigValue<T> config) {
                if (CONFIG_SERVER_SPEC.isLoaded()) {
                        return config.get();
                }
                return config.getDefault();
        }

    }
}
