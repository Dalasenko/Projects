

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;

/**
 * Represents an enemy alien.
 */
public class Enemy {

    private int x, y, width, height;
    private int dx = 2; // Horizontal movement speed.

    public Enemy(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Update enemy position.
    public void update() {
        x += dx;
        // Reverse direction if hitting panel edges and move downward.
        if (x < 0 || x + width > GamePanel.PANEL_WIDTH) {
            dx = -dx;
            y += 10;
        }
    }

    // Draw the enemy.
    public void draw(Graphics g) {
        g.setColor(Color.red);
        g.fillRect(x, y, width, height);
        // Draw simple eyes.
        g.setColor(Color.white);
        g.fillRect(x + width / 4, y + height / 4, width / 5, height / 5);
        g.fillRect(x + (width * 2 / 3), y + height / 4, width / 5, height / 5);
    }

    // For collision detection.
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // Getters for explosion positioning.
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
}

