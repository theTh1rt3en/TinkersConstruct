package tconstruct.util;

import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import tconstruct.util.config.PHConstruct;

public class SpawnInterceptor {

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityItem ourGuy && ourGuy.lifespan == 6000) {
            ourGuy.lifespan = PHConstruct.globalDespawn;
        }
    }
}
