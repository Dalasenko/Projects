

import java.awt.Graphics;
import java.awt.Color;

/**
 * Represents a simple explosion effect.
 */
public class Explosion {

    private int x, y, width, height;
    private int duration = 30; // Duration in game ticks.
    private int tick = 0;

    public Explosion(int x, int y, int width, int height) {
        // Center the explosion on the enemy's area.
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Update explosion animation.
    public void update() {
        tick++;
    }

    // Check if the explosion is finished.
    public boolean isFinished() {
        return tick > duration;
    }

    // Draw the explosion with a fading effect.
    public void draw(Graphics g) {
        int alpha = Math.max(255 - (tick * 8), 0);
        Color explosionColor = new Color(255, 165, 0, alpha);
        g.setColor(explosionColor);
        g.fillOval(x, y, width, height);
    }
}
