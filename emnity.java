import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Emnity extends GameEngine {

    public static void main(String args[]) {
        createGame(new Emnity(), 60);
    }

    boolean space, left, right, down, shift, f, q;
    boolean leftClick, rightClick;
    boolean jump, dash;

    int jumpCount;

    /* --- EDIT THESE VALUES AS NEEDED TO CHANGE MOVEMENT --- */
    final static int FPS = 60; // frames per second;
    final double GROUND = mHeight-50.0; // y-coordinate of ground.
    final double GRAVITY = 40.0;
    final double HORIZONTAL_ACCELERATION = 80.0;
    final double HORIZONTAL_DECELERATION = 40.0;
    final double GROUND_POUND_ACCELERATION = 100.0;
    final double MAX_VERTICAL_VELOCITY = -600;
    final double MAX_HORIZONTAL_VELOCITY = 400; // without dash;

    /* --- HELP MENU --- */
    boolean debug;
    boolean helpMenu;

    // exit button;
    int buttonX = (mWidth/2)-110; // x-coordinate from top-left corner;
    int buttonY = (mHeight/2)-100; // y-coordinate from top-left corner;
    int buttonW = 220; // width;
    int buttonH = 50; // height;

    /* --- PLATFORMS --- */
    ArrayList<Platform> platforms = new ArrayList<>();

    public void initPlatforms() {
        platforms.add(new Platform((mWidth-350), (GROUND-200), 300, 100));
        platforms.add(new Platform(100, 250, 300, 100));
    }
    
    public void drawPlatforms() {
        changeColor(white);
        if (!platforms.isEmpty()) { 
            for (Platform p:platforms) { drawSolidRectangle(p.x, p.y, p.width, p.height); }
        }
    }

    /* --- PLAYER --- */
    Character player;

    double floorY;

    double lastvalue;
    double steps;
    
    public boolean checkCollision(Platform p) {
        if (player.y > GROUND 
            || (player.y > p.y && player.y-player.height < p.y+p.height 
            && player.x > p.x && player.x-player.width < p.x+p.width)) {
            return true;
        } else { 
            return false; 
        }
    }

    public void updatePlayer(double dt) 
    {
        //HORIZONTAL MOVEMENT;
        steps = Math.abs(player.vX);
        // this code makes the player move until the object is collided, instead of going inside the object and moving out like it was before;
        for (int i = (int)steps; i > 0; i-- ) {
            lastvalue = player.x;
            player.x += player.vX/steps*dt;

            // detecting for collisions;
            for (Platform p:platforms) {
                if (checkCollision(p) == true) {
                    player.x = lastvalue;
                    player.vX = 0;
                }
            }
        }

        // VERTICAL MOVEMENT;
        steps = Math.abs(player.vY);
        for (int i = (int)steps; i > 0; i-- ) {
            lastvalue = player.y;
            player.y += player.vY/steps*dt;
            
            for (Platform p:platforms) {
                if (checkCollision(p) == true) {
                    player.y = lastvalue;
                    if (player.vY > 0) {
                        jump = false;
                        jumpCount = 0;
                    }
                    player.vY = 0;
                }
            }
        }

        if (left) { 
            player.state = 1; // change state to running;
            if (player.vX > 0) { player.vX-= HORIZONTAL_ACCELERATION + HORIZONTAL_DECELERATION; } 
            if (player.vX > -MAX_HORIZONTAL_VELOCITY) { 
                player.vX-= HORIZONTAL_ACCELERATION; 
            } 
            if (player.vX < -400 && !dash) {player.vX = -400; }
            player.direction = -1;
        } 
        if (right) { 
            player.state = 1; // change state to running;
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
            if (player.vX == 0) { 
                player.direction = 0; 
                player.state = 0; // change state to idle;
            }
        }

        player.vY+= GRAVITY;
        
        if (down) { player.vY+= GROUND_POUND_ACCELERATION; }

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
        switch (player.state) {
            case 1: // running;
                changeColor(red);
                break;
            case 2: // light sword attack;
                changeColor(blue);
                break;
            case 3: // heavy sword attack;
                changeColor(purple);
                break;
            default:
                changeColor(white);
        }
        drawRectangle((player.x-player.width), (player.y-player.height), player.width, player.height);
    }

    @Override
    public void init() 
    {
        space = false;
        left = false;
        right = false;
        down = false;
        shift = false;
        f = false;
        q = false;
        leftClick = false;
        rightClick = false;

        jump = false;
        dash = false;
        jumpCount = 0;

        debug = false;
        helpMenu = false;

        player = new Character(50.0, 70.0, (mWidth/2), GROUND, 0, 0);
        initPlatforms();
    }

    @Override
    public void update(double dt) {
        if (!helpMenu) { 
            updatePlayer(dt); 
        }
    }

    @Override
    public void paintComponent() {
        changeBackgroundColor(black);
        clearBackground(width(), height());
        drawPlayer();
        drawPlatforms();

        drawLine(0, GROUND, mWidth, GROUND); // DEBUG;

        /* press TAB to see the debug menu */
        if (debug) {
            drawBoldText(5, 45, "player.vX: " + player.vX + "");
            drawBoldText(5, 90, "player.vY: " + player.vY + "");
            drawBoldText(5, 135, "platforms: " + platforms.size());
            drawBoldText(5, 180, "player.x: " + player.x + "");
            drawBoldText(5, 225, "player.y: " + player.y + "");
            drawBoldText(5, 270, "jump? " + jump + "");
            drawBoldText(5, 315, "insidewall? " + checkCollision(platforms.get(0)) + "");
            drawBoldText(5, 360, "jumpCount: " + jumpCount + "");
        }

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
            if (player.vY > 150 && !jump) { jumpCount = 1; }
            if (jumpCount < 2 && !space) {
                player.vY = MAX_VERTICAL_VELOCITY;
                jumpCount++;
                jump = true; 
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
        if (e.getKeyCode() == KeyEvent.VK_TAB) { debug = !debug; }  
        if (e.getKeyCode() == KeyEvent.VK_F) { 
            f = true;
            player.state = 4;
        }
        if (e.getKeyCode() == KeyEvent.VK_Q) {
            q = true;
            player.state = 5;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) { left = false; }  
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) { right = false; }
        if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) { down = false; }
        if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_W) { space = false; }
        if (e.getKeyCode() == KeyEvent.VK_SHIFT || e.getKeyCode() == KeyEvent.VK_L) { shift = false; }
        if (e.getKeyCode() == KeyEvent.VK_F) { f = false; }
        if (e.getKeyCode() == KeyEvent.VK_Q) { q = false; }
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
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && !helpMenu) { player.state = 2; } // left click
        if (e.getButton() == MouseEvent.BUTTON3 && !helpMenu) { player.state = 3; } // right click
    }

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
