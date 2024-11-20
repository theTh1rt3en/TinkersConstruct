package tconstruct.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

    public static final int LIQUID_VALUE_INGOT = 144;
    public static final int LIQUID_VALUE_ORE = LIQUID_VALUE_INGOT * 2;
    public static final int LIQUID_VALUE_BLOCK = LIQUID_VALUE_INGOT * 9;
    public static final int LIQUID_VALUE_STONE = LIQUID_VALUE_INGOT / 8;
    public static final int LIQUID_VALUE_CHUNK = LIQUID_VALUE_INGOT / 2;
    public static final int LIQUID_VALUE_NUGGET = LIQUID_VALUE_INGOT / 9;

    public static final int LIQUID_UPDATE_AMOUNT = 6;
    public static final int VILLAGER_ID = 78943;
}
