

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;

/**
 * Represents a bullet fired by an enemy.
 */
public class EnemyBullet {

    private int x, y;
    private final int width = 4;
    private final int height = 10;
    private final int dy = 5; // Moves downward.

    public EnemyBullet(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Update the bullet's position.
    public void update() {
        y += dy;
    }

    // Draw the enemy bullet.
    public void draw(Graphics g) {
        g.setColor(Color.orange);
        g.fillRect(x, y, width, height);
    }

    // Get the bounding rectangle for collision detection.
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getY() {
        return y;
    }
}

