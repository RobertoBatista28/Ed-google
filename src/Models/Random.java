package Models;

public class Random {
    public Random() {
    }

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
