import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Emnity extends GameEngine {

    public static void main(String args[]) {
        createGame(new Emnity(), 60);
    }

    boolean jump, left, right, down;
    int jumpCount;

    /* --- EDIT THESE VALUES AS NEEDED TO CHANGE MOVEMENT --- */
    final static int FPS = 60; // frames per second;
    final double GROUND = mHeight-50.0; // y-coordinate of ground.
    final double GRAVITY = 40.0;
    final double HORIZONTAL_ACCELERATION = 80.0;
    final double HORIZONTAL_DECELERATION = 40.0;
    final double GROUND_POUND_ACCELERATION = 70.0;
    final double MAX_VERTICAL_VELOCITY = -600;
    final double MAX_HORIZONTAL_VELOCITY = 400;

    /* --- HELP MENU --- */
    boolean helpMenu;

    // exit button;
    int buttonX = (mWidth/2)-110; // x-coordinate from top-left corner;
    int buttonY = (mHeight/2)-100; // y-coordinate from top-left corner;
    int buttonW = 220; // width;
    int buttonH = 50; // height;

    /* --- PLAYER --- */
    Character player;

    public void updatePlayer(double dt) 
    {
        player.x += player.vX*dt;
        player.y += player.vY*dt;

        if (left) { 
            if (player.vX > 0) { player.vX-= HORIZONTAL_ACCELERATION + HORIZONTAL_DECELERATION; } 
            if (player.vX > -MAX_HORIZONTAL_VELOCITY) { 
                player.vX-= HORIZONTAL_ACCELERATION; 
            } 
        } 
        if (right) { 
            if (player.vX < 0) { player.vX+= HORIZONTAL_ACCELERATION + HORIZONTAL_DECELERATION; } 
            if (player.vX < MAX_HORIZONTAL_VELOCITY) { 
                player.vX+= HORIZONTAL_ACCELERATION; 
            } 
        }
        if (!left && !right) { 
            if (player.vX < 0 && player.vX != 0) { player.vX+= HORIZONTAL_DECELERATION; } 
            if (player.vX > 0 && player.vX != 0) { player.vX-= HORIZONTAL_DECELERATION; }
        }
        if (down && player.y < GROUND) { player.vY+= GROUND_POUND_ACCELERATION; }

        if (jump) {
            if (player.y > GROUND) {
                player.y = GROUND;
                player.vY = 0;
                jump = false;
                jumpCount = 0;
            } else { player.vY+= GRAVITY; }
        }
    }

    public void drawPlayer() {
        changeColor(white);
        drawRectangle((player.x-player.width), (player.y-player.height), player.width, player.height);
    }

    /* --- PLATFORMS --- */
    ArrayList<Platform> platforms = new ArrayList<>();

    public void initPlatforms() {
        platforms.add(new Platform((mWidth-350), (GROUND-100), 300, 20));
    }

    public void drawPlatforms() {
        changeColor(white);
        if (!platforms.isEmpty()) { 
            for (Platform p:platforms) { drawSolidRectangle(p.x, p.y, p.width, p.height); }
        }
    }

    @Override
    public void init() 
    {
        jump = false;
        left = false;
        right = false;
        down = false;
        jumpCount = 0;

        helpMenu = false;

        player = new Character(50.0, 70.0, (mWidth/2), GROUND, 0, 0);
        initPlatforms();
    }

    @Override
    public void update(double dt) {
        if (!helpMenu) { updatePlayer(dt); }
        else { //... 
        }
    }

    @Override
    public void paintComponent() {
        changeBackgroundColor(black);
        clearBackground(width(), height());
        drawPlayer();
        drawPlatforms();

        if (helpMenu) {
            changeColor(white);
            drawSolidRectangle(((mWidth/2)-150), ((mHeight/2)-200), 300, 400);
            changeColor(black);
            drawSolidRectangle(buttonX, buttonY, buttonW, buttonH);
        }
        
        drawLine(0, GROUND, mWidth, GROUND); // DEBUG;
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) { helpMenu = !helpMenu; } // stops the program.
        if (e.getKeyCode() == KeyEvent.VK_LEFT) { left = true; }  
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) { right = true; }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) { down = true; }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) { 
            if ((jump && jumpCount < 2) || (!jump && player.y == GROUND)) {
                jump = true; 
                player.vY = MAX_VERTICAL_VELOCITY;
                jumpCount++;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) { left = false; }  
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) { right = false; }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) { down = false; }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {
        if (helpMenu && (e.getX() >= buttonX && e.getX() <= buttonX+buttonW) && (e.getY() >= buttonY && e.getY() <= buttonY+buttonH)) {
            System.exit(10);
        } 
    }

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
