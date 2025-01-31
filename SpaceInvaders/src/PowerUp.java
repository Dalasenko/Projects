

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;

/**
 * Represents a power-up that grants the player a temporary shield.
 */
public class PowerUp {

    private int x, y, size;
    private final int dy = 2; // Vertical speed.

    public PowerUp(int x, int y) {
        this.x = x;
        this.y = y;
        this.size = 20;
    }

    // Update power-up position.
    public void update() {
        y += dy;
    }

    // Draw the power-up.
    public void draw(Graphics g) {
        g.setColor(Color.cyan);
        g.fillOval(x, y, size, size);
    }

    // For collision detection.
    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }

    public int getY(){
        return y;
    }

    public int getSize(){
        return size;
    }
}

