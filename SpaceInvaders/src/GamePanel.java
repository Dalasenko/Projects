

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * GamePanel is the main panel for rendering the game and handling the game loop.
 */
public class GamePanel extends JPanel implements KeyListener {

    // Panel dimensions and game loop delay.
    public static final int PANEL_WIDTH = 800;
    public static final int PANEL_HEIGHT = 600;
    private final int DELAY = 15; // ~60 FPS

    // Game objects.
    private Player player;
    private ArrayList<Enemy> enemies;
    private ArrayList<Bullet> bullets;
    private ArrayList<EnemyBullet> enemyBullets; // NEW: for enemy-fired bullets.
    private ArrayList<Explosion> explosions;
    private ArrayList<PowerUp> powerUps; // NEW: list of power-ups.
    private Score score;

    // Timer for game loop.
    private Timer timer;

    // Random generator for power-up spawning and enemy shooting.
    private Random rand = new Random();

    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);
        // Initialize game objects.
        initGame();
        // Start the game loop.
        startGameLoop();
    }

    // Ensure the panel gets focus when added to a container.
    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    // Initialize game objects and variables.
    private void initGame() {
        // Create the player near the bottom center.
        player = new Player(PANEL_WIDTH / 2 - 25, PANEL_HEIGHT - 60, 50, 30);
        bullets = new ArrayList<>();
        enemyBullets = new ArrayList<>(); // NEW: initialize enemy bullets list.
        explosions = new ArrayList<>();
        score = new Score();

        // Initialize enemy list.
        enemies = new ArrayList<>();
        int rows = 4;
        int cols = 8;
        int enemyWidth = 40;
        int enemyHeight = 30;
        int padding = 20;
        int startX = 50;
        int startY = 50;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = startX + col * (enemyWidth + padding);
                int y = startY + row * (enemyHeight + padding);
                enemies.add(new Enemy(x, y, enemyWidth, enemyHeight));
            }
        }

        // Initialize the power-ups list.
        powerUps = new ArrayList<>();
    }

    // Start the game loop using a Swing Timer.
    private void startGameLoop() {
        timer = new Timer(DELAY, e -> {
            updateGame();
            repaint();
        });
        timer.start();
    }

    // Update game objects and check for collisions.
    private void updateGame() {
        // Update player movement.
        player.update();
        player.updateShield(); // Update shield timer if active.

        // Update bullets: move upward and remove if off-screen.
        Iterator<Bullet> bulletIter = bullets.iterator();
        while (bulletIter.hasNext()) {
            Bullet bullet = bulletIter.next();
            bullet.update();
            if (bullet.getY() + bullet.getHeight() < 0) {  // off-screen check
                bulletIter.remove();
            }
        }

        // Update enemy positions.
        for (Enemy enemy : enemies) {
            enemy.update();
            // NEW: Each enemy has a small chance to fire an enemy bullet.
            if (rand.nextInt(1000) == 0) {  // Adjust probability as needed.
                int bulletX = enemy.getX() + enemy.getWidth() / 2 - 2;
                int bulletY = enemy.getY() + enemy.getHeight();
                enemyBullets.add(new EnemyBullet(bulletX, bulletY));
                System.out.println("Enemy bullet fired from (" + bulletX + ", " + bulletY + ")");
            }
        }

        // Update enemy bullets.
        Iterator<EnemyBullet> enemyBulletIter = enemyBullets.iterator();
        while (enemyBulletIter.hasNext()) {
            EnemyBullet eb = enemyBulletIter.next();
            eb.update();
            // Remove enemy bullet if it goes off-screen.
            if (eb.getY() > PANEL_HEIGHT) {
                enemyBulletIter.remove();
            } else if (eb.getBounds().intersects(player.getBounds())) {
                // Collision detected: if shield is active, absorb the bullet; otherwise, game over.
                if (player.isShieldActive()) {
                    // Optionally, you could cancel the shield or just absorb the bullet.
                    enemyBulletIter.remove();
                    System.out.println("Enemy bullet absorbed by shield.");
                } else {
                    // Game over.
                    timer.stop();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                                "Game Over! You were hit by an enemy bullet.",
                                "Game Over", JOptionPane.ERROR_MESSAGE);
                    });
                    return;
                }
            }
        }

        // Check for bullet-enemy collisions.
        checkCollisions();

        // Update explosions.
        Iterator<Explosion> expIter = explosions.iterator();
        while (expIter.hasNext()) {
            Explosion exp = expIter.next();
            exp.update();
            if (exp.isFinished()) {
                expIter.remove();
            }
        }

        // NEW: Spawn power-ups at random intervals.
        if (rand.nextInt(600) == 0) {
            int x = rand.nextInt(PANEL_WIDTH - 20);
            powerUps.add(new PowerUp(x, 0));
        }

        // Update power-ups and check for collision with player.
        Iterator<PowerUp> powerIter = powerUps.iterator();
        while (powerIter.hasNext()) {
            PowerUp power = powerIter.next();
            power.update();
            // Remove if off-screen.
            if (power.getY() > PANEL_HEIGHT) {
                powerIter.remove();
            } else if (power.getBounds().intersects(player.getBounds())) {
                // Activate the player's shield.
                player.activateShield();
                powerIter.remove();
            }
        }

        // Check if all enemies have been destroyed.
        if (enemies.isEmpty()) {
            timer.stop();
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this,
                        "Congratulations! You have defeated all the enemies!",
                        "Victory", JOptionPane.INFORMATION_MESSAGE);
            });
        }
    }

    // Check collisions between bullets and enemies.
    private void checkCollisions() {
        Iterator<Bullet> bulletIter = bullets.iterator();
        while (bulletIter.hasNext()) {
            Bullet bullet = bulletIter.next();
            Iterator<Enemy> enemyIter = enemies.iterator();
            while (enemyIter.hasNext()) {
                Enemy enemy = enemyIter.next();
                if (bullet.getBounds().intersects(enemy.getBounds())) {
                    // Collision detected: remove enemy and bullet, add explosion, update score.
                    enemyIter.remove();
                    bulletIter.remove();
                    explosions.add(new Explosion(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight()));
                    score.addPoints(10);
                    SoundManager.playExplosion();
                    break;
                }
            }
        }
    }

    // Render game objects.
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw score.
        score.draw(g);

        // Draw player.
        player.draw(g);

        // Draw enemies.
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }

        // Draw bullets.
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }

        // Draw enemy bullets.
        for (EnemyBullet eb : enemyBullets) {
            eb.draw(g);
        }

        // Draw explosions.
        for (Explosion explosion : explosions) {
            explosion.draw(g);
        }

        // Draw power-ups.
        for (PowerUp power : powerUps) {
            power.draw(g);
        }
    }

    // KeyListener implementation.
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        // Move left.
        if (key == KeyEvent.VK_LEFT) {
            player.setDx(-5);
        }
        // Move right.
        else if (key == KeyEvent.VK_RIGHT) {
            player.setDx(5);
        }
        // Fire a bullet.
        else if (key == KeyEvent.VK_SPACE) {
            int bulletX = player.getX() + player.getWidth() / 2 - 2;
            int bulletY = player.getY();
            bullets.add(new Bullet(bulletX, bulletY));
            System.out.println("Bullet created at (" + bulletX + ", " + bulletY + ")");
            SoundManager.playShoot();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        // Stop horizontal movement when arrow keys are released.
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
            player.setDx(0);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used.
    }
}
