package gr1mly4memes.papercut.optimizations;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Optimized property map implementation for BlockState property storage
 * Based on FerriteCore's approach to reduce memory usage from ~170MB to minimal
 * Uses compact storage with property indexing instead of full Map objects
 */
public class OptimizedPropertyMap {
    private final Property<?>[] properties;
    private final Comparable<?>[] values;
    private final int size;
    
    public OptimizedPropertyMap(Reference2ObjectArrayMap<Property<?>, Comparable<?>> original) {
        this.size = original.size();
        this.properties = new Property<?>[size];
        this.values = new Comparable<?>[size];
        
        int index = 0;
        for (Map.Entry<Property<?>, Comparable<?>> entry : original.entrySet()) {
            this.properties[index] = entry.getKey();
            this.values[index] = entry.getValue();
            index++;
        }
    }
    
    public OptimizedPropertyMap(Collection<Property<?>> properties, Collection<Comparable<?>> values) {
        this.size = properties.size();
        this.properties = properties.toArray(new Property<?>[0]);
        this.values = values.toArray(new Comparable<?>[0]);
    }
    
    public int size() {
        return size;
    }
    
    public boolean containsKey(Property<?> property) {
        for (int i = 0; i < size; i++) {
            if (properties[i] == property) {
                return true;
            }
        }
        return false;
    }
    
    @SuppressWarnings("unchecked")
    public <T extends Comparable<T>> T get(Property<T> property) {
        for (int i = 0; i < size; i++) {
            if (properties[i] == property) {
                return (T) values[i];
            }
        }
        return null;
    }
    
    public Set<Property<?>> keySet() {
        // Create a temporary set for compatibility
        // In practice, this should be avoided for performance
        Reference2ObjectArrayMap<Property<?>, Comparable<?>> temp = new Reference2ObjectArrayMap<>(size);
        for (int i = 0; i < size; i++) {
            temp.put(properties[i], values[i]);
        }
        return temp.keySet();
    }
    
    public Collection<Comparable<?>> values() {
        // Create a temporary collection for compatibility
        // In practice, this should be avoided for performance
        Reference2ObjectArrayMap<Property<?>, Comparable<?>> temp = new Reference2ObjectArrayMap<>(size);
        for (int i = 0; i < size; i++) {
            temp.put(properties[i], values[i]);
        }
        return temp.values();
    }
    
    public Set<Map.Entry<Property<?>, Comparable<?>>> entrySet() {
        // Create a temporary set for compatibility
        // In practice, this should be avoided for performance
        Reference2ObjectArrayMap<Property<?>, Comparable<?>> temp = new Reference2ObjectArrayMap<>(size);
        for (int i = 0; i < size; i++) {
            temp.put(properties[i], values[i]);
        }
        return temp.entrySet();
    }
    
    /**
     * Get the property at a specific index for fast access
     */
    public Property<?> getPropertyAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return properties[index];
    }
    
    /**
     * Get the value at a specific index for fast access
     */
    @SuppressWarnings("unchecked")
    public <T extends Comparable<T>> T getValueAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return (T) values[index];
    }
    
    /**
     * Find the index of a property for fast access
     */
    public int findPropertyIndex(Property<?> property) {
        for (int i = 0; i < size; i++) {
            if (properties[i] == property) {
                return i;
            }
        }
        return -1;
    }
}
