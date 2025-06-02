package tconstruct.util;

import tconstruct.Tags;

public final class Reference {

    private Reference() {}

    public static final String MOD_ID = "TConstruct";
    public static final String MOD_NAME = "Tinkers' Construct";
    public static final String MOD_VERSION = Tags.VERSION;
    public static final String DEPENDENCIES = "required-after:Forge@[10.13.3.1384,11.14);"
            + "required-after:Mantle@[0.3.2,1.7.10),[1.7.10-0.3.2,);"
            // make sure we still have the 0.3.2 requirement, even without the 1.7.10 prefix
            + "after:MineFactoryReloaded@[1.7.10R2.8.0RC7,);"
            + "after:ThermalExpansion@[1.7.10R4.0.0RC2,);"
            + "after:ThermalFoundation@[1.7.10R1.0.0RC3,);"
            + "after:armourersWorkshop@[1.7.10-0.28.0,);"
            + "after:CoFHAPI|energy;"
            + "after:CoFHCore;"
            + "after:battlegear2;"
            + "after:ZeldaItemAPI;"
            + "after:DynamicSkillsAPI;"
            + "after:NotEnoughItems;"
            + "after:Waila;"
            + "before:GalacticraftCore;"
            + "before:UndergroundBiomes";

    public static final String RESOURCE = "tinker";

    public static String resource(String res) {
        return String.format("%s:%s", RESOURCE, res);
    }

    public static String prefix(String name) {
        return String.format("tconstruct.%s", name);
    }
}
