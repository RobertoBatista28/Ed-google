package Models;

public class Random {
    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------
    public Random() {
    }

    // ----------------------------------------------------------------
    // Methods
    // ----------------------------------------------------------------
    public int nextInt(int bound) {
        return (int) (Math.random() * bound);
    }
    
    public int nextInt() {
        return (int) (Math.random() * Integer.MAX_VALUE);
    }

    public double nextDouble() {
        return Math.random();
    }
}
