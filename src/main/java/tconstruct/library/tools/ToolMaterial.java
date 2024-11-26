package tconstruct.library.tools;

import net.minecraft.util.StatCollector;

import lombok.Getter;

/*
 * Dynamic substitute for an enum. It carries a lot of information
 */
public class ToolMaterial {

    public final String materialName;
    @Getter
    public final int harvestLevel;
    @Getter
    public final int durability;
    @Getter
    public final int miningSpeed; // <-- divided by 100
    @Getter
    public final int attack;
    @Getter
    public final float handleModifier;
    @Getter
    public final int reinforced;
    @Getter
    public final float stonebound;
    @Getter
    public final String tipStyle;
    @Getter
    public final int primaryColor;

    public final String localizationString;

    @Deprecated
    public String displayName;

    @Deprecated
    public String ability;

    @Deprecated
    public ToolMaterial(String name, String displayName, int level, int durability, int speed, int damage, float handle,
            int reinforced, float stonebound, String style, String ability) {
        this(name, level, durability, speed, damage, handle, reinforced, stonebound, style, 0xFFFFFF);
    }

    @Deprecated
    public ToolMaterial(String name, int level, int durability, int speed, int damage, float handle, int reinforced,
            float stonebound, String style, String ability) {
        this(name, level, durability, speed, damage, handle, reinforced, stonebound, style, 0xFFFFFF);
    }

    @Deprecated
    public ToolMaterial(String name, int level, int durability, int speed, int damage, float handle, int reinforced,
            float stonebound, String style) {
        this(name, level, durability, speed, damage, handle, reinforced, stonebound, style, 0xFFFFFF);
    }

    public ToolMaterial(String name, int level, int durability, int speed, int damage, float handle, int reinforced,
            float stonebound, String style, int primaryColor) {
        this(
                name,
                "material." + name.toLowerCase().replaceAll(" ", ""),
                level,
                durability,
                speed,
                damage,
                handle,
                reinforced,
                stonebound,
                style,
                primaryColor);
    }

    public ToolMaterial(String name, String localizationString, int level, int durability, int speed, int damage,
            float handle, int reinforced, float stonebound, String style, int primaryColor) {
        this.materialName = name;
        this.harvestLevel = level;
        this.durability = durability;
        this.miningSpeed = speed;
        this.attack = damage;
        this.handleModifier = handle;
        this.reinforced = reinforced;
        this.stonebound = stonebound;
        this.tipStyle = style;
        this.primaryColor = primaryColor;

        this.localizationString = localizationString;

        this.displayName = prefixName();
        this.ability = getAbility();
    }

    public String name() {
        return materialName;
    }

    public String localizedName() {
        return StatCollector.translateToLocal(localizationString);
    }

    public String prefixName() {
        // check if there's a special name, otherwise use the regular one
        if (StatCollector.canTranslate(String.format("%s.display", localizationString)))
            return StatCollector.translateToLocal(String.format("%s.display", localizationString));
        return localizedName();
    }

    /**
     * Returns the ability of the tool to display. ONLY USE THIS FOR DISPLAY PURPOSES. It is not data you can rely on.
     * Use the material-ids for that.
     */
    public String getAbility() {
        if (StatCollector.canTranslate(String.format("%s.ability", localizationString)))
            return StatCollector.translateToLocal(String.format("%s.ability", localizationString));
        return "";
    }
}
