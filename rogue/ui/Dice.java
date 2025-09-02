package rogue.ui;
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


    public static int rollNDice(int n, int sides) {
    Dice dice = new Dice(sides);
    int sum = 0;
    for (int i = 0; i < n; i++) {
        sum += dice.roll();
    }
    return sum;
}
}

