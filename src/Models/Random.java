package Models;

/**
 * Random is a utility class for generating random numbers.
 * Provides methods to generate random integers with or without bounds,
 * and random double values using Java's Math.random() function.
 *
 */
public class Random {
    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------
    /**
     * Creates a new Random number generator instance.
     *
     */
    public Random() {
    }

    // ----------------------------------------------------------------
    // Methods
    // ----------------------------------------------------------------
    /**
     * Generates a random integer between 0 (inclusive) and the specified bound (exclusive).
     * Uses Math.random() multiplied by the bound and cast to integer.
     *
     * @param bound the upper bound (exclusive) for the random integer
     * @return a random integer in the range [0, bound)
     */
    public int nextInt(int bound) {
        return (int) (Math.random() * bound);
    }
    
    /**
     * Generates a random integer across the full range of Integer.MAX_VALUE.
     * Uses Math.random() multiplied by Integer.MAX_VALUE and cast to integer.
     *
     * @return a random integer in the range [0, Integer.MAX_VALUE)
     */
    public int nextInt() {
        return (int) (Math.random() * Integer.MAX_VALUE);
    }

    /**
     * Generates a random double value between 0.0 (inclusive) and 1.0 (exclusive).
     * Returns the result of Math.random() directly.
     *
     * @return a random double value in the range [0.0, 1.0)
     */
    public double nextDouble() {
        return Math.random();
    }
}
