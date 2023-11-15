import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class emnity extends GameEngine {

    public static void main(String args[]) {
        createGame(new emnity(), 60);
    }

    boolean jump, left, right, down;

    final double GROUND = 450.0;
    
    final double GRAVITY = 9.81;
    final double HORIZONTAL_ACCELERATION = 10.0;
    final double VERTICAL_ACCELERATION = 10.0;
    final double MAX_VERTICAL_VELOCITY = 300;
    final double MAX_HORIZONTAL_VELOCITY = 200;

    /* --- PLAYER --- */
    double playerX, playerY; // position of player.
    double playerVX, playerVY; // velocity of player.

    public void initPlayer() {
        playerX = width()/2;
        playerY = GROUND;
        playerVX = 0;
        playerVY = 0;
    }

    public void updatePlayer(double dt) {
        playerX += playerVX*dt;
        playerY += playerVY*dt;

        if (left) { if (playerVX > -MAX_HORIZONTAL_VELOCITY) { playerVX-= ACCELERATION; } } 
        if (right) { if (playerVX < MAX_HORIZONTAL_VELOCITY) { playerVX+= ACCELERATION; } }
        if (!left && !right) { 
            if (playerVX < 0 && playerVX != 0) { playerVX+= ACCELERATION; } 
            if (playerVX > 0 && playerVX != 0) { playerVX-= ACCELERATION; }
        }

        if (jump) {
            if (playerY > GROUND) {
                playerY = GROUND;
                jump = false;
            } else { playerVY+= GRAVITY; }
        }
    }

    public void drawPlayer() {
        changeColor(white);
        drawSolidCircle(playerX, playerY, 10);
    }

    @Override
    public void init() {
        jump = false;
        left = false;
        right = false;
        down = false;
        initPlayer();

        System.out.println(width() + " " + height());
    }

    @Override
    public void update(double dt) {
       updatePlayer(dt);
    }

    @Override
    public void paintComponent() {
        changeBackgroundColor(black);
        clearBackground(width(), height());
        drawPlayer();
    }
    
    @Override
    public void keyPressed(KeyEvent e) 
    {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) { left = true; }  
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) { right = true; }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) { 
            if (!jump) {
                jump = true; 
                playerYV = MAX_VERTICAL_VELOCITY;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) { left = false; }  
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) { right = false; }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}
}
