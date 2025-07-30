import java.util.Random;

public class Dice {
    private Random random;
    private int sides;

    // Constructor: default 6-sided die
    public Dice() {
        this(6);
    }

    // Constructor for custom number of sides
    public Dice(int sides) {
        this.sides = sides;
        this.random = new Random();
    }

    // Method to roll the die
    public int roll() {
        return random.nextInt(sides) + 1;
    }
}

/* 
    // Example usage
    public static void main(String[] args) {
        Dice dice = new Dice();
        int result = dice.roll();
        System.out.println("You rolled: " + result);
    }
}*/