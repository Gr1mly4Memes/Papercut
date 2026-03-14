package gr1mly4memes.papercut.optimizations;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache deduplicator for BlockState caches based on FerriteCore's approach
 * Reduces memory usage by reusing identical cache instances across different blockstates
 * Target memory savings: ~200MB
 */
public class CacheDeduplicator {
    private static final Map<CacheKey, BlockBehaviour.BlockStateBase.Cache> CACHE_MAP = new ConcurrentHashMap<>();
    private static final Map<VoxelShape, VoxelShape> SHAPE_DEDUPLICATION_MAP = new ConcurrentHashMap<>();
    
    /**
     * Key for identifying identical caches
     */
    private static class CacheKey {
        private final VoxelShape collisionShape;
        private final boolean largeCollisionShape;
        private final boolean[] faceSturdy;
        private final boolean isCollisionShapeFullBlock;
        private final int hashCode;
        
        public CacheKey(VoxelShape collisionShape, boolean largeCollisionShape, 
                       boolean[] faceSturdy, boolean isCollisionShapeFullBlock) {
            this.collisionShape = deduplicateShape(collisionShape);
            this.largeCollisionShape = largeCollisionShape;
            this.faceSturdy = faceSturdy.clone();
            this.isCollisionShapeFullBlock = isCollisionShapeFullBlock;
            this.hashCode = calculateHashCode();
        }
        
        private int calculateHashCode() {
            int result = collisionShape.hashCode();
            result = 31 * result + (largeCollisionShape ? 1 : 0);
            result = 31 * result + (isCollisionShapeFullBlock ? 1 : 0);
            result = 31 * result + java.util.Arrays.hashCode(faceSturdy);
            return result;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof CacheKey)) return false;
            
            CacheKey other = (CacheKey) obj;
            return collisionShape.equals(other.collisionShape) &&
                   largeCollisionShape == other.largeCollisionShape &&
                   isCollisionShapeFullBlock == other.isCollisionShapeFullBlock &&
                   java.util.Arrays.equals(faceSturdy, other.faceSturdy);
        }
        
        @Override
        public int hashCode() {
            return hashCode;
        }
    }
    
    /**
     * Get or create a deduplicated cache instance
     */
    public static BlockBehaviour.BlockStateBase.Cache getOrCreateCache(BlockState state) {
        // Create a temporary cache to extract the data
        BlockBehaviour.BlockStateBase.Cache tempCache = new BlockBehaviour.BlockStateBase.Cache(state);
        
        // Create key for deduplication
        CacheKey key = new CacheKey(
            tempCache.collisionShape,
            tempCache.largeCollisionShape,
            getFaceSturdyArray(tempCache),
            tempCache.isCollisionShapeFullBlock
        );
        
        // Return existing cache or create new one
        return CACHE_MAP.computeIfAbsent(key, k -> tempCache);
    }
    
    /**
     * Deduplicate voxel shapes to reduce memory usage
     */
    private static VoxelShape deduplicateShape(VoxelShape shape) {
        return SHAPE_DEDUPLICATION_MAP.computeIfAbsent(shape, k -> k);
    }
    
    /**
     * Extract face sturdy array from cache (using reflection if necessary)
     */
    private static boolean[] getFaceSturdyArray(BlockBehaviour.BlockStateBase.Cache cache) {
        // Since faceSturdy is private, we need to reconstruct it
        Direction[] directions = Direction.values();
        SupportType[] supportTypes = SupportType.values();
        boolean[] faceSturdy = new boolean[directions.length * supportTypes.length];
        
        for (int i = 0; i < directions.length; i++) {
            for (int j = 0; j < supportTypes.length; j++) {
                faceSturdy[i * supportTypes.length + j] = cache.isFaceSturdy(directions[i], supportTypes[j]);
            }
        }
        
        return faceSturdy;
    }
    
    /**
     * Get statistics about cache deduplication
     */
    public static String getStatistics() {
        return String.format("CacheDeduplicator: %d unique caches, %d deduplicated shapes", 
                           CACHE_MAP.size(), SHAPE_DEDUPLICATION_MAP.size());
    }
    
    /**
     * Clear all deduplicated caches (for testing/debugging)
     */
    public static void clear() {
        CACHE_MAP.clear();
        SHAPE_DEDUPLICATION_MAP.clear();
    }
}
