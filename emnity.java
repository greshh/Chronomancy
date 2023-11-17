import java.awt.RenderingHints.Key;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Emnity extends GameEngine {

    public static void main(String args[]) {
        createGame(new Emnity(), 60);
    }

    boolean space, left, right, down, shift;
    boolean jump, dash;
    int jumpCount;

    /* --- EDIT THESE VALUES AS NEEDED TO CHANGE MOVEMENT --- */
    final static int FPS = 60; // frames per second;
    final double GROUND = mHeight-50.0; // y-coordinate of ground.
    final double GRAVITY = 40.0;
    final double HORIZONTAL_ACCELERATION = 80.0;
    final double HORIZONTAL_DECELERATION = 40.0;
    final double GROUND_POUND_ACCELERATION = 70.0;
    final double MAX_VERTICAL_VELOCITY = -600;
    final double MAX_HORIZONTAL_VELOCITY = 400; // without dash;

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
            if (player.vX < -400 && !dash) {player.vX = -400; }
            player.direction = -1;
        } 
        if (right) { 
            if (player.vX < 0) { player.vX+= HORIZONTAL_ACCELERATION + HORIZONTAL_DECELERATION; } 
            if (player.vX < MAX_HORIZONTAL_VELOCITY) { 
                player.vX+= HORIZONTAL_ACCELERATION; 
            } 
            if (player.vX > 400 && !dash) {player.vX = 400; }
            player.direction = 1;
        }
        if (!left && !right) { 
            if (player.vX < 0 && player.direction < 0) { player.vX+= HORIZONTAL_DECELERATION; } // left
            if (player.vX > 0 && player.direction > 0) { player.vX-= HORIZONTAL_DECELERATION; } // right
            if (player.vX > -40 && player.vX < 40) { player.vX = 0; }
            if (player.vX == 0) { player.direction = 0; }
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

        if (dash) {
            if (player.direction < 0) { // left
                if (player.vX >= -MAX_HORIZONTAL_VELOCITY) { 
                    dash = false; 
                } else { 
                    player.vX+= 50.0; 
                }
            } else if (player.direction > 0) { // right
                if (player.vX <= MAX_HORIZONTAL_VELOCITY) {
                    dash = false;
                } else {
                    player.vX-= 50.0;
                }
            }
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
        space = false;
        left = false;
        right = false;
        down = false;
        shift = false;
        jumpCount = 0;

        jump = false;
        dash = false;
        
        helpMenu = false;

        player = new Character(50.0, 70.0, (mWidth/2), GROUND, 0, 0);
        initPlatforms();
    }

    @Override
    public void update(double dt) {
        if (!helpMenu) { updatePlayer(dt); }
    }

    @Override
    public void paintComponent() {
        changeBackgroundColor(black);
        clearBackground(width(), height());
        drawPlayer();
        drawPlatforms();

        drawLine(0, GROUND, mWidth, GROUND); // DEBUG;

        drawBoldText(5, 45, player.vX + "");
        drawBoldText(5, 90, player.direction + "");
        drawBoldText(5, 115, "dash: " + dash);

        if (helpMenu) {
            changeColor(white);
            drawSolidRectangle(((mWidth/2)-150), ((mHeight/2)-200), 300, 400);
            changeColor(black);
            drawRectangle(buttonX, buttonY, buttonW, buttonH);
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) { helpMenu = !helpMenu; }
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) { left = true; }  
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) { right = true; }
        if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) { down = true; }
        if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_W) { 
            if (jumpCount < 2 && !space) {
                jump = true; 
                player.vY = MAX_VERTICAL_VELOCITY;
                jumpCount++;
            }
            space = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_SHIFT || e.getKeyCode() == KeyEvent.VK_L) {
            if (!shift) {
                dash = true;
                player.vX = 1000*player.direction;
            }
            shift = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) { left = false; }  
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) { right = false; }
        if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) { down = false; }
        if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_W) { space = false; }
        if (e.getKeyCode() == KeyEvent.VK_SHIFT || e.getKeyCode() == KeyEvent.VK_L) { shift = false; }
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
