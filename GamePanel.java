package powerpong;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

// Panel where the actual gameplay takes place
public class GamePanel extends JPanel implements Runnable {

    // Constants for game dimensions and objects
    static final int GAME_WIDTH = 1000;
    static final int GAME_HEIGHT = (int) (GAME_WIDTH * (0.5555));
    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
    static final int BALL_DIAMETER = 20;
    static final int PADDLE_WIDTH = 25;
    static final int PADDLE_HEIGHT = 100;

    // Instance variables
    Thread gameThread;
    Image image;
    Graphics graphics;
    Random random;
    Paddle paddle1;
    Paddle paddle2;
    Ball ball;
    Score score;
    PowerUp powerUp;
    boolean ballTouchedPaddle1Last = false;
    boolean ballTouchedPaddle2Last = false;

    // Constructor
    GamePanel() {
        // Initialize paddles, ball, and score
        newPaddles();
        newBall();
        score = new Score(GAME_WIDTH, GAME_HEIGHT);

        // Set up the panel properties
        this.setFocusable(true);
        this.addKeyListener(new AL());
        this.setPreferredSize(SCREEN_SIZE);

        // Start the game thread
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void newBall() {
        random = new Random();
        ball = new Ball((GAME_WIDTH / 2) - (BALL_DIAMETER / 2), random.nextInt(GAME_HEIGHT - BALL_DIAMETER),
                BALL_DIAMETER,
                BALL_DIAMETER);
    }

    public void newPaddles() {
        paddle1 = new Paddle(0, (GAME_HEIGHT / 2) - (PADDLE_HEIGHT / 2), PADDLE_WIDTH, PADDLE_HEIGHT, 1);
        paddle2 = new Paddle(GAME_WIDTH - PADDLE_WIDTH, (GAME_HEIGHT / 2) - (PADDLE_HEIGHT / 2), PADDLE_WIDTH,
                PADDLE_HEIGHT, 2);
    }

    public void paint(Graphics g) {
        image = createImage(getWidth(), getHeight());
        graphics = image.getGraphics();
        draw(graphics);
        g.drawImage(image, 0, 0, this);
    }

    public void draw(Graphics g) {
        paddle1.draw(g);
        paddle2.draw(g);
        ball.draw(g);
        score.draw(g);
        if (powerUp != null) {
            powerUp.draw(g);
        }
    }

    public void move() {
        paddle1.move();
        paddle2.move();
        ball.move();
        handlePowerUp();
    }

    public void checkCollision() {
        // bounce ball off top and bottom window edges
        if (ball.y <= 0) {
            ball.setYDirection(-ball.yVelocity);
        }
        if (ball.y >= GAME_HEIGHT - BALL_DIAMETER) {
            ball.setYDirection(-ball.yVelocity);
        }
        // bounce ball off paddles
        if (ball.intersects(paddle1)) {
            ball.xVelocity = Math.abs(ball.xVelocity);
            ball.xVelocity++; // optional for more difficulty;
            if (ball.yVelocity > 0) {
                ball.yVelocity++; // optional for more difficulty;
            } else {
                ball.yVelocity--;
            }
            ball.setXDirection(ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }

        if (ball.intersects(paddle2)) {
            ball.xVelocity = Math.abs(ball.xVelocity);
            ball.xVelocity++; // optional for more difficulty;
            if (ball.yVelocity > 0) {
                ball.yVelocity++; // optional for more difficulty;
            } else {
                ball.yVelocity--;
            }
            ball.setXDirection(-ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }

        // Update paddle positions based on window boundaries
        if (paddle1.y <= 0) {
            paddle1.y = 0;
        } else if (paddle1.y >= (GAME_HEIGHT - paddle1.height)) {
            paddle1.y = GAME_HEIGHT - paddle1.height;
        }

        if (paddle2.y <= 0) {
            paddle2.y = 0;
        } else if (paddle2.y >= (GAME_HEIGHT - paddle2.height)) {
            paddle2.y = GAME_HEIGHT - paddle2.height;
        }
        // give a player 1 point and creates new paddles and ball
        if (ball.x <= 0) {
            score.player2++;
            newPaddles();
            newBall();
        }
        if (ball.x >= GAME_WIDTH - BALL_DIAMETER) {
            score.player1++;
            newPaddles();
            newBall();
        }
    }

    public void handlePowerUp() {
        // Randomly spawn a new power-up
        if (random.nextInt(1000) < 2 && powerUp == null) {
            int powerUpX = random.nextInt(GAME_WIDTH - 20);
            int powerUpY = random.nextInt(GAME_HEIGHT - 20);
            int powerUpType = random.nextInt(4) + 1;
            // 1: increase my points, 2: decrease opponent's points
            // 3: increase my paddle size, 4: decrease opponent's paddle size
            powerUp = new PowerUp(powerUpX, powerUpY, 35, 35, powerUpType);
        }

        if (ball.intersects(paddle1)) {
            ballTouchedPaddle1Last = true;
            ballTouchedPaddle2Last = false;
        } else if (ball.intersects(paddle2)) {
            ballTouchedPaddle2Last = true;
            ballTouchedPaddle1Last = false;
        }

        // Check collision with power-up
        if (powerUp != null && ball.intersects(powerUp)) {
            if (powerUp.type == 1) { // Increase my points
                if (ballTouchedPaddle1Last) {
                    score.player1++;
                    ballTouchedPaddle1Last = false;
                } else if (ballTouchedPaddle2Last) {
                    score.player2++;
                    ballTouchedPaddle2Last = false;
                }
            } else if (powerUp.type == 2) { // Decrease opponent's points
                if (ballTouchedPaddle1Last) {
                    if (score.player2 > 0) { // Check if opponent's points are greater than 0
                        score.player2--;
                    }
                    ballTouchedPaddle1Last = false;
                } else if (ballTouchedPaddle2Last) {
                    if (score.player1 > 0) { // Check if opponent's points are greater than 0
                        score.player1--;
                    }
                    ballTouchedPaddle2Last = false;
                }
            } else if (powerUp.type == 3) { // Increase my paddle size
                if (ballTouchedPaddle1Last) {
                    paddle1.height += 80;
                    ballTouchedPaddle1Last = false;
                } else if (ballTouchedPaddle2Last) {
                    paddle2.height += 80;
                    ballTouchedPaddle2Last = false;
                }
            } else if (powerUp.type == 4) { // Decrease opponent's paddle size
                if (ballTouchedPaddle1Last) {
                    paddle2.height -= 40;
                    ballTouchedPaddle1Last = false;
                } else if (ballTouchedPaddle2Last) {
                    paddle1.height -= 40;
                    ballTouchedPaddle2Last = false;
                }
            }

            powerUp = null; // Remove the power-up
        }
    }

    public void run() {
        // game loop
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1) {
                move();
                checkCollision();
                repaint();
                delta--;
            }
        }
    }

    // KeyAdapter for handling key events (paddle movement)
    public class AL extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            paddle1.keyPressed(e);
            paddle2.keyPressed(e);
        }

        public void keyReleased(KeyEvent e) {
            paddle1.keyReleased(e);
            paddle2.keyReleased(e);
        }
    }

}
