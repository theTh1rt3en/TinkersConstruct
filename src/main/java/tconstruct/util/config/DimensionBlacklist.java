package tconstruct.util.config;

import static tconstruct.util.Reference.MOD_ID;

import java.io.File;
import java.util.ArrayList;

import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.common.Loader;
import lombok.extern.log4j.Log4j2;

@Log4j2(topic = MOD_ID)
public class DimensionBlacklist {

    public static ArrayList<Integer> blacklistedDims = new ArrayList<>();
    public static ArrayList<Integer> noPoolDims = new ArrayList<>();

    public static int promisedLandDimensionID = -100;
    public static int twilightForestDimensionID = -100;

    public static void getBadBimensions() {
        updateModDimIDs();

        blacklistedDims.add(1);
        if (twilightForestDimensionID != -100) {
            blacklistedDims.add(twilightForestDimensionID);
        }
        if (PHConstruct.cfgDimBlackList.length > 0) {
            for (int numdim = 0; numdim < PHConstruct.cfgDimBlackList.length; numdim++) {
                blacklistedDims.add(PHConstruct.cfgDimBlackList[numdim]);
            }
        }
        if (promisedLandDimensionID != -100) {
            noPoolDims.add(promisedLandDimensionID);
        }
    }

    public static boolean isDimInBlacklist(int dim) {
        if (dim < 0) return false;
        if (dim == 0) return PHConstruct.slimeIslGenDim0;
        if (PHConstruct.slimeIslGenDim0Only) {
            return false;
        }
        for (Integer blacklistedDim : blacklistedDims) {
            if (blacklistedDim == dim) return false;
        }
        return true;
    }

    public static boolean isDimNoPool(int dim) {
        return noPoolDims.contains(dim);
    }

    private static void updateModDimIDs() {
        updateTwiForestID();
        updateBoPID();
    }

    private static void updateTwiForestID() {
        String location = Loader.instance().getConfigDir().getPath();
        File newFile = new File(location + File.separator + "TwilightForest.cfg");
        if (newFile.exists()) {
            Configuration config = new Configuration(newFile);

            config.load();

            twilightForestDimensionID = config.get("dimension", "dimensionID", -100).getInt();
            log.trace("Twilight Forest Dim ID: " + twilightForestDimensionID);
        } else twilightForestDimensionID = -100;
    }

    private static void updateBoPID() {
        String location = Loader.instance().getConfigDir().getPath();
        File newFile = new File(location + File.separator + "biomesoplenty" + File.separator + "ids.cfg");
        if (newFile.exists()) {
            Configuration config = new Configuration(newFile);
            config.load();

            promisedLandDimensionID = config.get("dimension settings", "Promised Land Dimension ID", -200).getInt();
            log.trace("Promised Lands Dim ID: " + promisedLandDimensionID);
        } else promisedLandDimensionID = -100;
    }
}
