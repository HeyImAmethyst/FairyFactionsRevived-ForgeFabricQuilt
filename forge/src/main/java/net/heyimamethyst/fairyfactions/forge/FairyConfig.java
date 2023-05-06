package net.heyimamethyst.fairyfactions.forge;

import net.heyimamethyst.fairyfactions.FairyConfigValues;
import net.minecraftforge.common.ForgeConfigSpec;

public class FairyConfig
{
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> SPAWN_FACTION_MIN_SIZE;
    public static final ForgeConfigSpec.ConfigValue<Integer> SPAWN_FACTION_MAX_SIZE;

    public static final ForgeConfigSpec.ConfigValue<Double> GENERAL_HEALTH_BASE;
    public static final ForgeConfigSpec.DoubleValue GENERAL_SPEED_BASE;
    public static final ForgeConfigSpec.DoubleValue GENERAL_SPEED_SCOUT;
    public static final ForgeConfigSpec.DoubleValue GENERAL_SPEED_WITHER_MULT;

    public static final ForgeConfigSpec.ConfigValue<Integer> BEHAVIOR_PATH_RANGE;
    public static final ForgeConfigSpec.ConfigValue<Double> BEHAVIOR_PURSUE_RANGE;
    public static final ForgeConfigSpec.ConfigValue<Double> BEHAVIOR_DEFEND_RANGE;
    public static final ForgeConfigSpec.ConfigValue<Double> pursue_range_mult;
    public static final ForgeConfigSpec.ConfigValue<Double> defend_range_mult;

    public static final ForgeConfigSpec.DoubleValue BEHAVIOR_FEAR_RANGE;

    // how long will tame fairies stay mad? 3x for wild
    public static final ForgeConfigSpec.ConfigValue<Integer> BEHAVIOR_AGGRO_TIMER;

    // fall speed
    public static final ForgeConfigSpec.ConfigValue<Double> DEF_FLOAT_RATE;

    // fly speed
    public static final ForgeConfigSpec.ConfigValue<Double> DEF_FLAP_RATE;

    // bonus to flight while unburdened
    public static final ForgeConfigSpec.ConfigValue<Double> DEF_SOLO_FLAP_MULT;

    // bonus to flight when launching
    public static final ForgeConfigSpec.ConfigValue<Double> DEF_LIFTOFF_MULT;

    public static final ForgeConfigSpec.ConfigValue<Integer> DEF_MAX_PARTICLES;

    static
    {
        BUILDER.push("Spawning behaviors");

        /**
         * Spawning behaviors
         */

        SPAWN_FACTION_MAX_SIZE = BUILDER.comment("maximum fairy spawn group size")
                .defineInRange("Spawn Faction Min Size", 10, 0, 30);

        SPAWN_FACTION_MIN_SIZE = BUILDER.comment("maximum fairy spawn group size")
                .defineInRange("Spawn Faction Min Size", 8, 0, 30);

        BUILDER.pop();

        BUILDER.push("General fairy stats");

        /**
         * General fairy stats
         */

        GENERAL_HEALTH_BASE = BUILDER.comment("base maximum health")
                .defineInRange("General Health Base", 15.0D, 1F, 40F);

        GENERAL_SPEED_BASE = BUILDER.comment("base move speed")
                .defineInRange("General Speed Base", 0.9F, 0.1F, 2.0F);

        GENERAL_SPEED_SCOUT = BUILDER.comment("move speed for scouts")
                .defineInRange("General Speed Scout", 1.05F, 0.1F, 2.0F);

        GENERAL_SPEED_WITHER_MULT = BUILDER.comment("multiplier to speed for wither debuff (lower is slower)")
                .defineInRange("Wither Speed Mult", 0.08F, 0.05F, 0.95F);

        BUILDER.pop();

        BUILDER.push("Behavior modifiers");

        /**
         * Behavior modifiers
         */

        BEHAVIOR_PATH_RANGE = BUILDER.comment("how far will we path to something?")
                .defineInRange("Behavior Path Range", 16, 4, 32);

        pursue_range_mult = BUILDER.comment("how much farther will we chase something than our normal pathing?")
            .defineInRange("pursue_range_mult", 1.0F, 0.25F, 2F);

        defend_range_mult = BUILDER.comment("how close will guards stay to the queen?")
                .defineInRange("defend_range_mult", 0.5F, 0.25F, 2F);

        BEHAVIOR_FEAR_RANGE = BUILDER.comment("how far will we run away when afraid?")
                .defineInRange("Behavior Fear Range", 12F, 4.0F, 32F);

        BEHAVIOR_PURSUE_RANGE = BUILDER.define("BEHAVIOR_PURSUE_RANGE", 0.0D);
        BEHAVIOR_DEFEND_RANGE = BUILDER.define("BEHAVIOR_DEFEND_RANGE", 0.0D);

        BEHAVIOR_AGGRO_TIMER = BUILDER.comment("how long will tame fairies stay angry? (wild are 3x this)")
                .defineInRange("BEHAVIOR_AGGRO_TIMER", 15, 1, 100);

        DEF_FLOAT_RATE = BUILDER.comment("fall speed").define("DEF_FLOAT_RATE", -0.2D);
        DEF_FLAP_RATE = BUILDER.comment("fly speed").define("DEF_FLAP_RATE", 0.15D);
        DEF_SOLO_FLAP_MULT = BUILDER.comment("bonus to flight while unburdened").define("DEF_SOLO_FLAP_MULT", -0.9375D);
        DEF_LIFTOFF_MULT = BUILDER.comment("bonus to flight when launching").define("DEF_LIFTOFF_MULT", 2.0D);

        DEF_MAX_PARTICLES = BUILDER.define("DEF_MAX_PARTICLES", 5);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static void passConfigValues()
    {
        FairyConfigValues.SPAWN_FACTION_MIN_SIZE = SPAWN_FACTION_MIN_SIZE.get();
        FairyConfigValues.SPAWN_FACTION_MAX_SIZE = SPAWN_FACTION_MAX_SIZE.get();

        FairyConfigValues.GENERAL_HEALTH_BASE = GENERAL_HEALTH_BASE.get();
        FairyConfigValues.GENERAL_SPEED_BASE = GENERAL_SPEED_BASE.get();
        FairyConfigValues.GENERAL_SPEED_SCOUT = GENERAL_SPEED_SCOUT.get();
        FairyConfigValues.GENERAL_SPEED_WITHER_MULT = GENERAL_SPEED_WITHER_MULT.get();

        FairyConfigValues.BEHAVIOR_PATH_RANGE = BEHAVIOR_PATH_RANGE.get();
        FairyConfigValues.BEHAVIOR_PURSUE_RANGE = BEHAVIOR_PURSUE_RANGE.get();
        FairyConfigValues.BEHAVIOR_DEFEND_RANGE = BEHAVIOR_DEFEND_RANGE.get();
        FairyConfigValues.pursue_range_mult = pursue_range_mult.get();
        FairyConfigValues.defend_range_mult = defend_range_mult.get();

        FairyConfigValues.BEHAVIOR_FEAR_RANGE = BEHAVIOR_FEAR_RANGE.get();

        FairyConfigValues.BEHAVIOR_AGGRO_TIMER = BEHAVIOR_AGGRO_TIMER.get();
        FairyConfigValues.DEF_FLOAT_RATE = DEF_FLOAT_RATE.get();
        FairyConfigValues.DEF_FLAP_RATE = DEF_FLAP_RATE.get();
        FairyConfigValues.DEF_SOLO_FLAP_MULT = DEF_SOLO_FLAP_MULT.get();
        FairyConfigValues.DEF_LIFTOFF_MULT = DEF_LIFTOFF_MULT.get();

        FairyConfigValues.DEF_MAX_PARTICLES = DEF_MAX_PARTICLES.get();
    }
}
