package gr1mly4memes.papercut.optimizations;

import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;
import java.util.Map;

/**
 * FastMap implementation for BlockState neighbor lookup optimization
 * Based on FerriteCore's FastMap design for O(number of states) memory usage
 * instead of O((number of states) * sum(number of values per property))
 */
public class FastMap<S> {
    private final List<Property<?>> properties;
    private final int[] propertySizes;
    private final int[] propertyMultipliers;
    private final S[] states;
    private final int totalStates;

    @SuppressWarnings("unchecked")
    public FastMap(Map<Map<Property<?>, Comparable<?>>, S> possibleStates, List<Property<?>> properties) {
        this.properties = properties;
        this.propertySizes = new int[properties.size()];
        this.propertyMultipliers = new int[properties.size()];
        
        // Calculate property sizes and multipliers
        int multiplier = 1;
        for (int i = 0; i < properties.size(); i++) {
            Property<?> property = properties.get(i);
            propertySizes[i] = property.getPossibleValues().size();
            propertyMultipliers[i] = multiplier;
            multiplier *= propertySizes[i];
        }
        
        this.totalStates = multiplier;
        this.states = (S[]) new StateHolder[totalStates];
        
        // Populate the states array
        for (S state : possibleStates.values()) {
            int index = calculateIndex(state);
            states[index] = state;
        }
    }

    /**
     * Calculate the index for a given state based on its property values
     */
    private int calculateIndex(S state) {
        int index = 0;
        for (int i = 0; i < properties.size(); i++) {
            Property<?> property = properties.get(i);
            Comparable<?> value = ((StateHolder<?, ?>) state).getValue(property);
            int valueIndex = property.getPossibleValues().indexOf(value);
            index += valueIndex * propertyMultipliers[i];
        }
        return index;
    }

    /**
     * Get the neighbor state by changing a single property value
     */
    public S getNeighbor(S currentState, Property<?> property, Comparable<?> newValue) {
        int currentIndex = calculateIndex(currentState);
        int propertyIndex = properties.indexOf(property);
        
        if (propertyIndex == -1) {
            return null;
        }
        
        int valueIndex = property.getPossibleValues().indexOf(newValue);
        if (valueIndex == -1) {
            return null;
        }
        
        int currentValueIndex = property.getPossibleValues().indexOf(((StateHolder<?, ?>) currentState).getValue(property));
        int newIndex = currentIndex + (valueIndex - currentValueIndex) * propertyMultipliers[propertyIndex];
        
        if (newIndex < 0 || newIndex >= totalStates) {
            return null;
        }
        
        return states[newIndex];
    }

    /**
     * Get all possible states
     */
    public S[] getStates() {
        return states;
    }

    /**
     * Get the total number of states
     */
    public int getTotalStates() {
        return totalStates;
    }
}
