package gr1mly4memes.papercut.optimizations;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

/**
 * Optimized NBT data handling for partially loaded chunks
 * Based on FerriteCore's approach to reduce memory usage by ~90-100MB
 * Only retains essential entity and block entity data during two-step loading
 */
public class OptimizedChunkNBT {
    
    /**
     * Create an optimized NBT compound that contains only the data needed
     * for the second loading step (entities and block entities)
     */
    public static CompoundTag createOptimizedPartialNBT(CompoundTag fullNBT) {
        CompoundTag optimized = new CompoundTag();
        
        // Copy essential metadata
        for (String key : fullNBT.keySet()) {
            if (isEssentialKey(key)) {
                optimized.put(key, fullNBT.get(key).copy());
            }
        }
        
        // Extract and copy only entities and block entities
        extractEntities(fullNBT, optimized);
        extractBlockEntities(fullNBT, optimized);
        
        return optimized;
    }
    
    /**
     * Check if a key contains essential metadata that should be preserved
     */
    private static boolean isEssentialKey(String key) {
        return switch (key) {
            case "xPos", "zPos", "LastUpdate", "InhabitedTime", "Status", 
                 "UpgradeData", "below_zero_retrogen", "blending_data" -> true;
            default -> false;
        };
    }
    
    /**
     * Extract entities from the full NBT and add to optimized NBT
     */
    private static void extractEntities(CompoundTag source, CompoundTag target) {
        ListTag entities = source.getListOrEmpty("entities");
        if (!entities.isEmpty()) {
            target.put("entities", entities.copy());
        }
    }
    
    /**
     * Extract block entities from the full NBT and add to optimized NBT
     */
    private static void extractBlockEntities(CompoundTag source, CompoundTag target) {
        ListTag blockEntities = source.getListOrEmpty("block_entities");
        if (!blockEntities.isEmpty()) {
            target.put("block_entities", blockEntities.copy());
        }
    }
    
    /**
     * Estimate memory savings of using optimized NBT
     */
    public static int estimateMemorySavings(CompoundTag fullNBT) {
        int fullSize = estimateNBTSize(fullNBT);
        CompoundTag optimized = createOptimizedPartialNBT(fullNBT);
        int optimizedSize = estimateNBTSize(optimized);
        return fullSize - optimizedSize;
    }
    
    /**
     * Rough estimation of NBT size in bytes
     */
    private static int estimateNBTSize(CompoundTag nbt) {
        // This is a rough estimation - actual size would require serialization
        int size = 0;
        for (String key : nbt.keySet()) {
            size += key.length() * 2; // UTF-16 characters
            size += estimateTagSize(nbt.get(key));
        }
        return size;
    }
    
    private static int estimateTagSize(net.minecraft.nbt.Tag tag) {
        if (tag instanceof CompoundTag compoundTag) {
            int size = 8; // Compound tag overhead
            for (String key : compoundTag.keySet()) {
                size += key.length() * 2;
                size += estimateTagSize(compoundTag.get(key));
            }
            return size;
        } else if (tag instanceof ListTag listTag) {
            return 8 + listTag.size() * 64; // Rough estimate per list item
        } else {
            return 32; // Rough estimate for primitive tags
        }
    }
    
    /**
     * Validate that the optimized NBT contains all necessary data
     */
    public static boolean validateOptimizedNBT(CompoundTag original, CompoundTag optimized) {
        // Check that essential keys are preserved
        for (String key : original.keySet()) {
            if (isEssentialKey(key) && !optimized.contains(key)) {
                return false;
            }
        }
        
        // Check that entities and block entities are preserved
        if (original.contains("entities") && !optimized.contains("entities")) {
            return false;
        }
        
        if (original.contains("block_entities") && !optimized.contains("block_entities")) {
            return false;
        }
        
        return true;
    }
}
