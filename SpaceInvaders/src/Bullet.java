

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;

/**
 * Represents a bullet fired by the player.
 */
public class Bullet {

    private int x, y;
    private final int width = 4;
    private final int height = 10;
    private final int dy = -7; // Moves upward.

    public Bullet(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Update bullet position.
    public void update() {
        y += dy;
    }

    // Draw the bullet.
    public void draw(Graphics g) {
        g.setColor(Color.yellow);
        g.fillRect(x, y, width, height);
    }

    // For collision detection.
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // Getters for checking position.
    public int getY() {
        return y;
    }
    public int getHeight() {
        return height;
    }
}

