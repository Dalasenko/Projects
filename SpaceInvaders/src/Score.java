

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

/**
 * Manages the player's score.
 */
public class Score {

    private int points;

    public Score() {
        points = 0;
    }

    // Add points.
    public void addPoints(int p) {
        points += p;
    }

    // Draw the score.
    public void draw(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + points, 10, 25);
    }
}

