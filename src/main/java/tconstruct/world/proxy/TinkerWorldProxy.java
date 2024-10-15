package tconstruct.world.proxy;

public interface TinkerWorldProxy {

    void spawnParticle(String particle, double xPos, double yPos, double zPos, double velX, double velY, double velZ);

    void initialize();
}
