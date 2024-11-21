package tconstruct.library.client;

public class FluidRenderProperties {

    public float minHeight, maxHeight, minX, maxX, minZ, maxZ;

    public FluidRenderProperties(float minHeight, float maxHeight, float minX, float maxX, float minZ, float maxZ) {
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.minX = minX;
        this.maxX = maxX;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }

    public enum Applications {

        TABLE(0.9375F, 1F, 0.0625F, 0.9375F, 0.062F, 0.9375F),
        BASIN(0.25F, 0.95F, 0.0625F, 0.9375F, 0.0625F, 0.9375F);

        public float minHeight, maxHeight, minX, maxX, minZ, maxZ;

        Applications(float minHeight, float maxHeight, float minX, float maxX, float minZ, float maxZ) {
            this.minHeight = minHeight;
            this.maxHeight = maxHeight;
            this.minX = minX;
            this.maxX = maxX;
            this.minZ = minZ;
            this.maxZ = maxZ;
        }
    }
}
