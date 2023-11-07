package net.heyimamethyst.fairyfactions;

import com.mojang.datafixers.util.Pair;
import net.heyimamethyst.fairyfactions.config.ModConfigProvider;
import net.heyimamethyst.fairyfactions.config.SimpleConfig;

public class FairyConfig
{
    public static SimpleConfig CONFIG;
    private static ModConfigProvider configs;

    public static int SPAWN_FACTION_MIN_SIZE;
    public static int SPAWN_FACTION_MAX_SIZE;

    public static boolean ALWAYS_FAIRY_TELETPORT;
    public static int TELEPORT_RANGE;

    public static double BASE_EMOTIONAL_PERCENT_CHANCE;

    public static double GENERAL_HEALTH_BASE;
    public static double GENERAL_SPEED_BASE;
    public static double GENERAL_SPEED_SCOUT;
    public static double GENERAL_SPEED_WITHER_MULT;

    public static int BEHAVIOR_PATH_RANGE;
    public static double BEHAVIOR_PURSUE_RANGE = 0;
    public static double BEHAVIOR_DEFEND_RANGE = 0;
    public static double pursue_range_mult;
    public static double defend_range_mult;

    public static double BEHAVIOR_FEAR_RANGE;

    // how long will tame fairies stay mad? 3x for wild
    public static int BEHAVIOR_AGGRO_TIMER;

    // fall speed
    public static double DEF_FLOAT_RATE;

    // fly speed
    public static double DEF_FLAP_RATE;

    // bonus to flight while unburdened
    public static double DEF_SOLO_FLAP_MULT;

    // bonus to flight when launching
    public static double DEF_LIFTOFF_MULT;

    public static int DEF_MAX_PARTICLES;

    public static boolean EXTEND_HANGING_SIGN_TEXT_LIMIT;

    public static boolean ENABLE_FAIRY_WING_SOUNDS;

    public static String minSpawnGroupString = "Minimum.Fairy.Spawn.Group.Size";
    public static String maxSpawnGroupString = "Maximum.Fairy.Spawn.Group.Size";
    public static String baseMaximumHealthString = "Base.Maximum.Health";
    public static String baseMovememtSpeedString = "Base.Move.Speed";
    public static String scoutMovementSpeedString = "Move.Speed.For.Scouts";
    public static String witheredMovementSpeedMultString = "Move.Speed.Wither.Mult";
    public static String pathingRangeString = "Path.Range";
    public static String pursueRangeMultString = "Pursue.Range.Mult";
    public static String defendRangeMultString = "Defend.Range.Mult";
    public static String fearRangeString = "Fear.Range";
    public static String behaviorAggroTimerString = "BEHAVIOR.AGGRO.TIMER";
    public static String fallSpeedString = "Fall.Speed";
    public static String flySpeedString = "Fly.Speed";
    public static String soloFlapMultString = "DEF.SOLO.FLAP.MULT";
    public static String liftoffMultString = "DEF.LIFTOFF.MULT";
    public static String maxParticlesString = "DEF.MAX.PARTICLES";
    public static String enableAlwaysTeleportString = "Always.Fairy.Teleport";
    public static String fairyTeleportRangeString = "Always.Fairy.Teleport.Range";
    public static String baseEmotionalPercentChanceString = "Base.Emotional.Percent.Chance";
    public static String extendHangingSignTextLimitString = "Extend.Hanging.Sign.Text.Limit";
    public static String enableFairyWingSoundsString = "Enable.Fairy.Wing.Sounds";

    public static void registerConfigs()
    {
        configs = new ModConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(FairyFactions.MOD_ID + "config").provider(configs).request();

        assignConfigs();
    }

    private static void createConfigs()
    {
        /**
         * Spawning behaviors
         */

        configs.addComment("Spawning Behaviors");

        configs.addKeyValuePair(new Pair<>(minSpawnGroupString, 10), 0, 30, "maximum fairy spawn group size");
        configs.addKeyValuePair(new Pair<>(maxSpawnGroupString, 8), 0, 30, "maximum fairy spawn group size");

        /**
         * General fairy stats
         */

        configs.addComment("General Fairy Stats");

        configs.addKeyValuePair(new Pair<>(baseMaximumHealthString, 15.0D), 1F, 40F, "base maximum health");
        configs.addKeyValuePair(new Pair<>(baseMovememtSpeedString, 0.9F), 0.1F, 2.0F, "base move speed");
        configs.addKeyValuePair(new Pair<>(scoutMovementSpeedString, 1.05F), 0.1F, 2.0F, "move speed for scouts");
        configs.addKeyValuePair(new Pair<>(witheredMovementSpeedMultString, 0.08F), 0.05F, 0.95F, "multiplier to speed for wither debuff (lower is slower)");

        /**
         * Behavior modifiers
         */

        configs.addComment("Behavior Modifiers");

        configs.addKeyValuePair(new Pair<>(pathingRangeString, 16), 4, 32, "how far will we path to something?");
        configs.addKeyValuePair(new Pair<>(pursueRangeMultString, 1.0F), 0.25F, 2F, "how much farther will we chase something than our normal pathing?");
        configs.addKeyValuePair(new Pair<>(defendRangeMultString, 0.5F),  0.25F, 2F, "how close will guards stay to the queen?");
        configs.addKeyValuePair(new Pair<>(fearRangeString, 12F), 4.0F, 32F, "how far will we run away when afraid?");

        configs.addKeyValuePair(new Pair<>(behaviorAggroTimerString, 15), 1, 100, "how long will tame fairies stay angry? (wild are 3x this)");

        configs.addKeyValuePair(new Pair<>(fallSpeedString, -0.2D), "how much farther will we chase something than our normal pathing?");
        configs.addKeyValuePair(new Pair<>(flySpeedString, 0.15D), "how close will guards stay to the queen?");
        configs.addKeyValuePair(new Pair<>(soloFlapMultString,  -0.9375D), "bonus to flight while unburdened");
        configs.addKeyValuePair(new Pair<>(liftoffMultString,  2.0D), "bonus to flight when launching");
        configs.addKeyValuePair(new Pair<>(maxParticlesString,  5), "max particles");

        /**
         * Other settings
         */

        configs.addComment("Other Settings");

        configs.addKeyValuePair(new Pair<>(enableAlwaysTeleportString, true), "whether or not a fairy will always teleport to a player who is its ruler, regardless if the player has an ender pearl");
        configs.addKeyValuePair(new Pair<>(fairyTeleportRangeString, 6), "the distance between between the fairy and its ruler for teleportation");

        configs.addKeyValuePair(new Pair<>(baseEmotionalPercentChanceString, 0.02D), "the percent chance for a fairy to request food");
        configs.addKeyValuePair(new Pair<>(extendHangingSignTextLimitString, true), "whether to extend the text limit for hanging signs to be able to put fairies with longer names on them");

        configs.addKeyValuePair(new Pair<>(enableFairyWingSoundsString, false), "whether to have the fairies make wing flapping noises while flying like in the older beta version");

    }

//    public static String TEST;
//    public static int SOME_INT;
//    public static double SOME_DOUBLE;
//    public static int MAX_DAMAGE_DOWSING_ROD;

    private static void assignConfigs()
    {
//        TEST = CONFIG.getOrDefault("key.test.value1", "Nothing");
//        SOME_INT = CONFIG.getOrDefault("key.test.value2", 42);
//        SOME_DOUBLE = CONFIG.getOrDefault("key.test.value3", 42.0d);
//        MAX_DAMAGE_DOWSING_ROD = CONFIG.getOrDefault("dowsing.rod.max.damage", 32);

        /**
         * Spawning behaviors
         */

        SPAWN_FACTION_MAX_SIZE = CONFIG.getOrDefault(minSpawnGroupString, 10);
        SPAWN_FACTION_MIN_SIZE = CONFIG.getOrDefault(maxSpawnGroupString, 8);

        /**
         * General fairy stats
         */

        GENERAL_HEALTH_BASE = CONFIG.getOrDefault(baseMaximumHealthString, 15.0D);
        GENERAL_SPEED_BASE = CONFIG.getOrDefault(baseMovememtSpeedString, 0.9F);
        GENERAL_SPEED_SCOUT = CONFIG.getOrDefault(scoutMovementSpeedString, 1.05F);
        GENERAL_SPEED_WITHER_MULT = CONFIG.getOrDefault(witheredMovementSpeedMultString, 0.08F);

        /**
         * Behavior modifiers
         */

        BEHAVIOR_PATH_RANGE = CONFIG.getOrDefault(pathingRangeString, 16);
        pursue_range_mult = CONFIG.getOrDefault(pursueRangeMultString, 1.0F);
        defend_range_mult = CONFIG.getOrDefault(defendRangeMultString, 0.5F);
        BEHAVIOR_FEAR_RANGE = CONFIG.getOrDefault(fearRangeString, 12F);

        BEHAVIOR_AGGRO_TIMER = CONFIG.getOrDefault(behaviorAggroTimerString, 15);

        //---

        DEF_FLOAT_RATE = CONFIG.getOrDefault(fallSpeedString, -0.2D);
        DEF_FLAP_RATE = CONFIG.getOrDefault(flySpeedString, 0.15D);
        DEF_SOLO_FLAP_MULT = CONFIG.getOrDefault(soloFlapMultString,  -0.9375D);
        DEF_LIFTOFF_MULT = CONFIG.getOrDefault(liftoffMultString,  2.0D);

        DEF_MAX_PARTICLES = CONFIG.getOrDefault(maxParticlesString,  5);

        /**
         * Other settings
         */

        ALWAYS_FAIRY_TELETPORT = CONFIG.getOrDefault(enableAlwaysTeleportString, true);
        TELEPORT_RANGE = CONFIG.getOrDefault(fairyTeleportRangeString, 16);

        BASE_EMOTIONAL_PERCENT_CHANCE = CONFIG.getOrDefault(baseEmotionalPercentChanceString, 0.02D);
        EXTEND_HANGING_SIGN_TEXT_LIMIT = CONFIG.getOrDefault(extendHangingSignTextLimitString, true);
        ENABLE_FAIRY_WING_SOUNDS = CONFIG.getOrDefault(enableFairyWingSoundsString, false);

        System.out.println("All " + configs.getConfigsList().size() + " have been set properly");
    }

    public static void passConfigValues()
    {
        FairyConfigValues.SPAWN_FACTION_MIN_SIZE = SPAWN_FACTION_MIN_SIZE;
        FairyConfigValues.SPAWN_FACTION_MAX_SIZE = SPAWN_FACTION_MAX_SIZE;

        FairyConfigValues.GENERAL_HEALTH_BASE = GENERAL_HEALTH_BASE;
        FairyConfigValues.GENERAL_SPEED_BASE = GENERAL_SPEED_BASE;
        FairyConfigValues.GENERAL_SPEED_SCOUT = GENERAL_SPEED_SCOUT;
        FairyConfigValues.GENERAL_SPEED_WITHER_MULT = GENERAL_SPEED_WITHER_MULT;

        FairyConfigValues.BEHAVIOR_PATH_RANGE = BEHAVIOR_PATH_RANGE;
        FairyConfigValues.BEHAVIOR_PURSUE_RANGE = BEHAVIOR_PURSUE_RANGE;
        FairyConfigValues.BEHAVIOR_DEFEND_RANGE = BEHAVIOR_DEFEND_RANGE;
        FairyConfigValues.pursue_range_mult = pursue_range_mult;
        FairyConfigValues.defend_range_mult = defend_range_mult;

        FairyConfigValues.BEHAVIOR_FEAR_RANGE = BEHAVIOR_FEAR_RANGE;

        FairyConfigValues.BEHAVIOR_AGGRO_TIMER = BEHAVIOR_AGGRO_TIMER;
        FairyConfigValues.DEF_FLOAT_RATE = DEF_FLOAT_RATE;
        FairyConfigValues.DEF_FLAP_RATE = DEF_FLAP_RATE;
        FairyConfigValues.DEF_SOLO_FLAP_MULT = DEF_SOLO_FLAP_MULT;
        FairyConfigValues.DEF_LIFTOFF_MULT = DEF_LIFTOFF_MULT;

        FairyConfigValues.DEF_MAX_PARTICLES = DEF_MAX_PARTICLES;

        FairyConfigValues.BEHAVIOR_PURSUE_RANGE = BEHAVIOR_PATH_RANGE * FairyConfig.pursue_range_mult;
        FairyConfigValues.BEHAVIOR_DEFEND_RANGE = BEHAVIOR_PATH_RANGE * FairyConfig.defend_range_mult;

        FairyConfigValues.ALWAYS_FAIRY_TELEPORT = ALWAYS_FAIRY_TELETPORT;
        FairyConfigValues.TELEPORT_RANGE = TELEPORT_RANGE;
        FairyConfigValues.BASE_EMOTIONAL_PERCENT_CHANCE = BASE_EMOTIONAL_PERCENT_CHANCE;
        FairyConfigValues.EXTEND_HANGING_SIGN_TEXT_LIMIT = EXTEND_HANGING_SIGN_TEXT_LIMIT;
        FairyConfigValues.ENABLE_FAIRY_WING_SOUNDS = ENABLE_FAIRY_WING_SOUNDS;
    }
}
