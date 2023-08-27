package powerpong;

import java.awt.*;
import javax.swing.*;

// Frame that contains the game panel and sets up the window
public class GameFrame extends JFrame {

    // Constructor
    public GameFrame() {
        // Creates the game panel
        GamePanel panel = new GamePanel();

        // Sets up the frame
        this.add(panel);
        this.setTitle("PowerPong");
        this.setResizable(false);
        this.setBackground(Color.black);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

}
