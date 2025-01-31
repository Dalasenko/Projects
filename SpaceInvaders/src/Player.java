



import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;

/**
 * Represents the player's spaceship.
 */
public class Player {

    private int x, y, width, height;
    private int dx = 0; // Horizontal velocity.

    // Shield variables.
    private boolean shieldActive = false;
    private int shieldTimer = 0;
    private final int SHIELD_DURATION = 300; // Duration in frames (approx. 5 sec at 60 FPS)

    public Player(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Update the player's position.
    public void update() {
        x += dx;
        // Keep the player within panel bounds.
        if (x < 0) {
            x = 0;
        }
        if (x + width > GamePanel.PANEL_WIDTH) {
            x = GamePanel.PANEL_WIDTH - width;
        }
    }

    // Update the shield timer.
    public void updateShield() {
        if (shieldActive) {
            shieldTimer--;
            if (shieldTimer <= 0) {
                shieldActive = false;
            }
        }
    }

    // Activate the shield.
    public void activateShield() {
        shieldActive = true;
        shieldTimer = SHIELD_DURATION;
        System.out.println("Shield activated!");
    }

    // Check if shield is active.
    public boolean isShieldActive() {
        return shieldActive;
    }

    // Draw the player's spaceship.
    public void draw(Graphics g) {
        g.setColor(Color.green);
        g.fillRect(x, y, width, height);
        // Draw a simple cockpit.
        g.setColor(Color.white);
        g.fillRect(x + width / 3, y + 5, width / 3, height / 3);

        // If shield is active, draw a blue aura around the ship.
        if (shieldActive) {
            g.setColor(new Color(0, 0, 255, 128)); // Semi-transparent blue.
            g.fillOval(x - 5, y - 5, width + 10, height + 10);
        }
    }

    // Getters and setters.
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
    public void setDx(int dx) {
        this.dx = dx;
    }

    // For collision detection.
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
