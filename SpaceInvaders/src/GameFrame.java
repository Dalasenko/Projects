

import javax.swing.JFrame;

/**
 * GameFrame sets up the main window for the game.
 */
public class GameFrame extends JFrame {

    public GameFrame() {
        setTitle("Space Invaders Clone");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Add the game panel where the game loop and rendering happen.
        add(new GamePanel());
        pack(); // Adjust frame to preferred size of contents.
        setLocationRelativeTo(null); // Center the window on screen.
        setVisible(true);
    }
}
