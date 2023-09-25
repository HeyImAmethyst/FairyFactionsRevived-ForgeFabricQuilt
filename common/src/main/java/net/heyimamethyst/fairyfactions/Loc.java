package net.heyimamethyst.fairyfactions;

import net.minecraft.locale.Language;

public enum Loc
{
    QUEEN("text.queen.prefix"),

    FACTION_1("text.faction.1.name"),
    FACTION_2("text.faction.2.name"),
    FACTION_3("text.faction.3.name"),
    FACTION_4("text.faction.4.name"),
    FACTION_5("text.faction.5.name"),
    FACTION_6("text.faction.6.name"),
    FACTION_7("text.faction.7.name"),
    FACTION_8("text.faction.8.name"),
    FACTION_9("text.faction.9.name"),
    FACTION_10("text.faction.10.name"),
    FACTION_11("text.faction.11.name"),
    FACTION_12("text.faction.12.name"),
    FACTION_13("text.faction.13.name"),
    FACTION_14("text.faction.14.name"),
    FACTION_15("text.faction.15.name"),

    DISBAND_QUEEN_1("text.disband.queen.1.message"),
    DISBAND_QUEEN_2("text.disband.queen.2.message"),
    DISBAND_OTHER_1("text.disband.other.1.message"),
    DISBAND_OTHER_2("text.disband.other.2.message"),
    DISBAND_OTHER_3("text.disband.other.3.message"),
    DISBAND_OTHER_4("text.disband.other.4.message"),
    DISBAND_OTHER_5("text.disband.other.5.message"),
    DISBAND_OTHER_6("text.disband.other.6.message"),

    TAME_QUEEN_1("text.tame.queen.1.message"),
    TAME_QUEEN_2("text.tame.queen.2.message"),
    TAME_QUEEN_3("text.tame.queen.3.message"),
    TAME_OTHER_1("text.tame.other.1.message"),
    TAME_OTHER_2("text.tame.other.2.message"),
    TAME_OTHER_3("text.tame.other.3.message"),
    TAME_OTHER_4("text.tame.other.4.message"),
    TAME_OTHER_5("text.tame.other.5.message"),
    TAME_OTHER_6("text.tame.other.6.message"),

    TAME_FAIL_PREFIX("text.tame.fail.prefix"),
    TAME_FAIL_ANGRY("text.tame.fail.angry.message"),
    TAME_FAIL_CRYING("text.tame.fail.crying.message"),
    TAME_FAIL_HAS_FOLLOWERS("text.tame.fail.has_followers.message"),
    TAME_FAIL_HAS_QUEEN("text.tame.fail.has_queen.message"),
    TAME_FAIL_TAME_QUEEN("text.tame.fail.tame_queen.message"),
    TAME_FAIL_TAME_FAIRY("text.tame.fail.tame_fairy.message"),
    TAME_FAIL_POSTED("text.tame.fail.posted.message"),
    TAME_FAIL_GLOWSTONE("text.tame.fail.glowstone.message"),
    TAME_FAIL_NOT_MELON("text.tame.fail.not_melon.message"),
    TAME_FAIL_NOT_SNACK("text.tame.fail.not_snack.message"),

    DEATH_1("text.death.1.message"),
    DEATH_2("text.death.2.message"),
    DEATH_3("text.death.3.message"),
    DEATH_4("text.death.4.message"),
    DEATH_5("text.death.5.message"),
    DEATH_6("text.death.6.message"),
    DEATH_7("text.death.7.message"),

    FAIRY_ABANDON_POST("text.fairy.abandon_post"),

    POSITION("text.position"),
    FAIRY_WAND_NO_ENTITY("text.fairy_wand.no_entity"),
    FAIRY_WAND_ENTITY_STORED("text.fairy_wand.entity_stored"),
    FAIRY_WAND_NO_LOCATION("text.fairy_wand.no_location"),
    FAIRY_WAND_POSITION_STORED("text.fairy_wand.position_stored"),
    FAIRY_WAND_STORED_ENTITY("text.fairy_wand.stored_entity"),
    FAIRY_WAND_POSITION_SET("text.fairy_wand.position_set"),

    FAIRY_WAND_DELIVERY_MODE_TRUE("text.fairy_wand.delivery_mode_set_true"),
    FAIRY_WAND_DELIVERY_MODE_FALSE("text.fairy_wand.delivery_mode_set_false"),

    FAIRY_STORE("text.fairy.store"),
    FAIRY_TAKE("text.fairy.take"),
    FAIRY_STORE_AND_TAKE("text.fairy.store_and_take"),
    FAIRY_CLEARED("text.fairy.cleared"),

    FAIRY_CANT_ASSIGN_TAKE_CHEST("text.fairy.cant_assign_take_chest"),

    FAIRY_SET_BED("text.fairy.set_bed");

    public final String key;

    private Loc(String key) {
        this.key = key;
    }

    private static final Language REG = Language.getInstance();

    public String get()
    {
        return getLoc(this.key);
    }

    public String toString()
    {
        return get();
    }

    public String getLoc(String key)
    {
        final String res = REG.getOrDefault(key);

        //ResourceManager languageManager = Minecraft.getInstance().getResourceManager();

        //final String res = ClientLanguage.loadFrom(languageManager, Minecraft.getInstance().getLanguageManager().getLanguages().stream().toList()).getOrDefault(key);

//        String res = "";
//
//        if(REG instanceof ClientLanguage)
//        {
//            res = REG.getOrDefault(key);
//        }

        if( res.isEmpty() )
            return "#"+key+"#";
        else
            return res;
    }
}
