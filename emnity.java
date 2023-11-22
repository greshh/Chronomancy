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

    int jumpCount, dashCount;

    double xPush; // the amount the frame gets shifted by as a result of the player moving out of screen.
    final double frameXR = mWidth-(mWidth/8); // the right x-coordinate where the frame starts moving.
    final double frameXL = mWidth/8; // the left x-coordinate where the frame starts moving.

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
    boolean menu;
    boolean help;

    final int buttonX = (mWidth/2)-110; // x-coordinate from top-left corner;
    final int buttonW = 220; // width;
    final int buttonH = 50; // height;

    // help button;
    int helpButtonY = (mHeight/2)-100;

    // exit button;
    int exitButtonY = mHeight/2; // y-coordinate from top-left corner;

    /* --- PLATFORMS --- */
    ArrayList<Platform> platforms = new ArrayList<>();

    public void initPlatforms() {
        int platformWidth = 300;
        int platformHeight = 50;
        platforms.add(new Platform(900, 500, platformWidth, platformHeight));
        platforms.add(new Platform(100, 400, platformWidth, platformHeight));
        platforms.add(new Platform(1100, 200, platformWidth, platformHeight));
        platforms.add(new Platform(1500, 400, platformWidth, platformHeight));
    }

    public void drawPlatforms() {
        changeColor(white);
        if (!platforms.isEmpty()) { 
            for (Platform p:platforms) { drawSolidRectangle(p.x+xPush, p.y, p.width, p.height); }
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
            && player.x > p.x+xPush && player.x-player.width < p.x+xPush+p.width)) {
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

        if (player.x+player.width > frameXR) { 
            xPush+= (frameXR-player.x-player.width);
            player.x = frameXR-player.width;
        } 
        if (player.x < frameXL) {
            if (xPush < 0) { xPush+= (frameXL-player.x); }
            player.x = frameXL;
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
                        dashCount = 0;
                    }
                    player.vY = 0;
                }
            }
        }

        if (player.x-player.width <= 0 && xPush == 0) {
            player.x = player.width;
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
        dashCount = 0;

        xPush = 0;

        debug = false;
        menu = false;

        player = new Character(50.0, 70.0, (mWidth/2), GROUND, 0, 0);
        initPlatforms();
    }

    @Override
    public void update(double dt) {
        if (!menu) { 
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
            drawBoldText(5, 45, "dashCount: " + dashCount + "");
            drawBoldText(5, 90, "xPush: " + xPush + "");
            // drawBoldText(5, 135, "platforms: " + platforms.size());
            drawBoldText(5, 180, "player.x: " + player.x + "");
            // drawBoldText(5, 225, "player.y: " + player.y + "");
            drawBoldText(5, 270, "dash? " + dash + "");
            // drawBoldText(5, 315, "insidewall? " + checkCollision(platforms.get(0)) + "");
            // drawBoldText(5, 360, "jumpCount: " + jumpCount + "");
        }

        if (menu) {
            changeColor(white);
            drawSolidRectangle(((mWidth/2)-150), ((mHeight/2)-200), 300, 400);
            //drawBoldText((mWidth/2)-50, GRAVITY, null);
            changeColor(black);
            drawRectangle(buttonX, exitButtonY, buttonW, buttonH);
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) { menu = !menu; }
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
                if ((jump && dashCount<1) || !jump) {
                    if (!dash) {
                        dash = true;
                        player.vX = 1000*player.direction;
                    }
                    if (jump) {
                        dashCount++;
                    }
                }
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
    public void mouseClicked(MouseEvent e) 
    {
        // if help button is clicked.
        if (menu && (e.getX() >= buttonX && e.getX() <= buttonX+buttonW) && (e.getY() >= helpButtonY && e.getY() <= helpButtonY+buttonH)) {
            help = true;
        } 

        // if exit button is clicked.
        if (menu && (e.getX() >= buttonX && e.getX() <= buttonX+buttonW) && (e.getY() >= exitButtonY && e.getY() <= exitButtonY+buttonH)) {
            System.exit(10);
        } 
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && !menu) { player.state = 2; } // left click
        if (e.getButton() == MouseEvent.BUTTON3 && !menu) { player.state = 3; } // right click
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
