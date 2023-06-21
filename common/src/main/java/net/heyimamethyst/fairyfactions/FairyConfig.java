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

//    public String minSpawnGroupString = "minimum.fairy.spawn.group.size";
//    public String maxSpawnGroupString = "maximum.fairy.spawn.group.size";
//    public String baseMaximumHealthString = "base.maximum.health";
//    public String minSpawnGroupString = "minimum.fairy.spawn.group.size";
//    public String minSpawnGroupString = "minimum.fairy.spawn.group.size";
//    public String minSpawnGroupString = "minimum.fairy.spawn.group.size";
//    public String minSpawnGroupString = "minimum.fairy.spawn.group.size";
//    public String minSpawnGroupString = "minimum.fairy.spawn.group.size";

    public static void registerConfigs()
    {
        configs = new ModConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(FairyFactions.MOD_ID + "config").provider(configs).request();

        assignConfigs();
    }

    private static void createConfigs()
    {
        configs.addKeyValuePair(new Pair<>("minimum.fairy.spawn.group.size", 10), 0, 30, "maximum fairy spawn group size");
        configs.addKeyValuePair(new Pair<>("maximum.fairy.spawn.group.size", 8), 0, 30, "maximum fairy spawn group size");
        configs.addKeyValuePair(new Pair<>("always.fairy.teleport", true), "whether or not a fairy will always teleport to a player who is its ruler, regardless if the player has an ender pearl");

        /**
         * General fairy stats
         */

        configs.addKeyValuePair(new Pair<>("base.maximum.health", 15.0D), 1F, 40F, "base maximum health");
        configs.addKeyValuePair(new Pair<>("base.move.speed", 0.9F), 0.1F, 2.0F, "base move health");
        configs.addKeyValuePair(new Pair<>("move.speed.for.scouts", 1.05F), 0.1F, 2.0F, "move speed for scouts");
        configs.addKeyValuePair(new Pair<>("move.speed.wither.mult", 0.08F), 0.05F, 0.95F, "multiplier to speed for wither debuff (lower is slower)");

        /**
         * Behavior modifiers
         */

        configs.addKeyValuePair(new Pair<>("path.range", 16), 4, 32, "how far will we path to something?");
        configs.addKeyValuePair(new Pair<>("pursue.range.mult", 1.0F), 0.25F, 2F, "how much farther will we chase something than our normal pathing?");
        configs.addKeyValuePair(new Pair<>("defend.range.mult", 0.5F),  0.25F, 2F, "how close will guards stay to the queen?");
        configs.addKeyValuePair(new Pair<>("fear.range", 12F), 4.0F, 32F, "how far will we run away when afraid?");

        configs.addKeyValuePair(new Pair<>("BEHAVIOR.AGGRO.TIMER", 15), 1, 100, "how long will tame fairies stay angry? (wild are 3x this)");

        configs.addKeyValuePair(new Pair<>("fall.speed", -0.2D), "how much farther will we chase something than our normal pathing?");
        configs.addKeyValuePair(new Pair<>("fly.speed", 0.15D), "how close will guards stay to the queen?");
        configs.addKeyValuePair(new Pair<>("DEF.SOLO.FLAP.MULT",  -0.9375D), "bonus to flight while unburdened");
        configs.addKeyValuePair(new Pair<>("DEF.LIFTOFF.MULT",  2.0D), "bonus to flight when launching");
        configs.addKeyValuePair(new Pair<>("DEF.MAX.PARTICLES",  5), "max particles");

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

        ALWAYS_FAIRY_TELETPORT = CONFIG.getOrDefault("always.fairy.teleport", true);

        /**
         * Spawning behaviors
         */

        SPAWN_FACTION_MAX_SIZE = CONFIG.getOrDefault("minimum.fairy.spawn.group.size", 10);
        SPAWN_FACTION_MIN_SIZE = CONFIG.getOrDefault("maximum.fairy.spawn.group.size", 8);

        /**
         * General fairy stats
         */

        GENERAL_HEALTH_BASE = CONFIG.getOrDefault("base.maximum.health", 15.0D);
        GENERAL_SPEED_BASE = CONFIG.getOrDefault("base.move.speed", 0.9F);
        GENERAL_SPEED_SCOUT = CONFIG.getOrDefault("move.speed.for.scouts", 1.05F);
        GENERAL_SPEED_WITHER_MULT = CONFIG.getOrDefault("move.speed.wither.mult", 0.08F);

        /**
         * Behavior modifiers
         */

        BEHAVIOR_PATH_RANGE = CONFIG.getOrDefault("path.range", 16);
        pursue_range_mult = CONFIG.getOrDefault("pursue.range.mult", 1.0F);
        defend_range_mult = CONFIG.getOrDefault("defend.range.mult", 0.5F);
        BEHAVIOR_FEAR_RANGE = CONFIG.getOrDefault("fear.range", 12F);

        BEHAVIOR_AGGRO_TIMER = CONFIG.getOrDefault("BEHAVIOR.AGGRO.TIMER", 15);

        //---

        DEF_FLOAT_RATE = CONFIG.getOrDefault("fall.speed", -0.2D);
        DEF_FLAP_RATE = CONFIG.getOrDefault("fly.speed", 0.15D);
        DEF_SOLO_FLAP_MULT = CONFIG.getOrDefault("DEF.SOLO.FLAP.MULT",  -0.9375D);
        DEF_LIFTOFF_MULT = CONFIG.getOrDefault("DEF.LIFTOFF.MULT",  2.0D);

        DEF_MAX_PARTICLES = CONFIG.getOrDefault("DEF.MAX.PARTICLES",  5);

        System.out.println("All " + configs.getConfigsList().size() + " have been set properly");
    }

    public static void passConfigValues()
    {
        FairyConfigValues.SPAWN_FACTION_MIN_SIZE = SPAWN_FACTION_MIN_SIZE;
        FairyConfigValues.SPAWN_FACTION_MAX_SIZE = SPAWN_FACTION_MAX_SIZE;

        FairyConfigValues.ALWAYS_FAIRY_TELEPORT = ALWAYS_FAIRY_TELETPORT;

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
    }
}
