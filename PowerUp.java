package powerpong;

import java.awt.*;

public class PowerUp extends Rectangle {

    int type;

    // Constructor
    PowerUp(int x, int y, int width, int height, int type) {
        super(x, y, width, height);
        this.type = type;
    }

    // Draw the power-up
    public void draw(Graphics g) {
        if (type == 1) {
            g.setColor(Color.yellow);
        } else if (type == 2) {
            g.setColor(Color.orange);
        } else if (type == 3) {
            g.setColor(Color.cyan);
        } else if (type == 4) {
            g.setColor(Color.magenta);
        }
        g.fillOval(x, y, width, height);
    }

}
