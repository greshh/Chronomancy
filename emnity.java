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

    /* --- MENU --- */
    boolean debug;
    boolean menu;
    boolean help;

    final int buttonX = (mWidth/2)-110; // x-coordinate from top-left corner;
    final int buttonW = 220; // width;
    final int buttonH = 50; // height;

    // help button;
    int helpButtonY = (mHeight/2)-100;

    // exit button;
    int exitButtonY = mHeight/2-25; // y-coordinate from top-left corner;

    public void drawMenu() {
        changeColor(white);
        drawSolidRectangle(((mWidth/2)-150), ((mHeight/2)-200), 300, 400);
        changeColor(black);
        drawRectangle(buttonX, helpButtonY, buttonW, buttonH);
        drawBoldText((mWidth/2)-35, helpButtonY+35, "HELP");
        drawRectangle(buttonX, exitButtonY, buttonW, buttonH);
        drawBoldText((mWidth/2)-30, exitButtonY+35, "EXIT");
    }

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
    
    // checking collisions for platforms.
    public boolean checkCollision(Platform p) {
        if (player.y > GROUND 
            || (player.y > p.y && player.y-player.height < p.y+p.height 
            && player.x > p.x+xPush && player.x-player.width < p.x+xPush+p.width)) {
            return true;
        } else { 
            return false; 
        }
    }


    // checking collisions for enemies.
    public boolean checkCollision(Character enemy) {
        if ((player.y > enemy.y && player.y-player.height < enemy.y+enemy.height 
            && player.x > enemy.x+xPush && player.x-player.width < enemy.x+xPush+enemy.width)) {
            return true;
        } else { 
            return false; 
        }
    }

    public void updatePlayer(double dt) 
    {
        // HORIZONTAL MOVEMENT;
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
        changeColor(white);
        drawRectangle((player.x-player.width), (player.y-player.height), player.width, player.height);
    }

    /* --- ENEMY --- */
    ArrayList<Enemy> enemies = new ArrayList<>();

    double enemyY = (GROUND-70.0);

    public void initEnemy() {
        enemies.add(new Enemy(player.width, player.height, 1100.00, enemyY, 0, 0, 2.0));
        enemies.add(new Moving(player.width, player.height, 300.00, enemyY, 75.00, 0, 2.0, 150.0));
        //enemies.add(new Following(player.width, player.height, 1500.00, enemyY, 200.00, 0));
    }

    public void drawEnemy() {
        changeColor(white);
        for (Character e:enemies) {
            if (e.hp > 0) { 
                drawRectangle(e.x+xPush, e.y, e.width, e.height); 
            } else { e.hp = 0; }
        }
    }

    public void updateEnemy(double dt, Enemy e) {
        if (e.getClass().getName() == "Moving") {
                // if the moving enemy reaches its maximum distance to the RIGHT, it waits for the waiting period before resuming.
                if (e.x >= ((Moving)e).originalX+((Moving)e).maxDistance) {
                    if (((Moving)e).waitTime < ((Moving)e).waitPeriod) {
                        e.direction = 0;
                        ((Moving)e).waitTime+= dt;
                    } else {
                        ((Moving)e).waitTime = 0;
                        e.direction = -1;
                    }
                // if the moving enemy reaches its maximum distance to the LEFT, it waits for the waiting period before resuming.
                } else if (e.x <= ((Moving)e).originalX-((Moving)e).maxDistance) {
                    if (((Moving)e).waitTime < ((Moving)e).waitPeriod) {
                        e.direction = 0;
                        ((Moving)e).waitTime+= dt;
                    } else {
                        ((Moving)e).waitTime = 0;
                        e.direction = 1;
                    }
                }
            }
        // else if (e.getClass().getName() == "Following") {
        //     double distance = e.x-player.x;
        //     if (distance < 0) { 
        //         e.direction = 1;
        //     } else if (distance > 0) {
        //         e.direction = -1;
        //     }
        // }
        if (e.direction > 0) { e.x+= e.vX*dt; }
        else if (e.direction < 0) { e.x-= e.vX*dt; }
            
        if (checkCollision(e)) {
            if (e.waitTime < e.waitPeriod) {
                e.waitTime+= dt;
            } else {
                player.hp-= 0.00000001;
                e.waitTime = 0;
                e.newWaitPeriod();
            }
        }
    }

    /* --- HEALTH BAR --- */
    double healthBarW = (mWidth/2)-100.0;
    double healthBarH = 25;

    // currently only for player HP;
    public void drawHealthBar() { 
        changeColor(red);
        drawSolidRectangle(5, 60, (player.hp*0.01)*healthBarW, healthBarH);
        changeColor(white);
        drawRectangle(5, 60, healthBarW, healthBarH);
    }

    /* --- GAME --- */

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

        player = new Player(50.0, 70.0, (mWidth/2), GROUND, 0, 0);
        initEnemy();
        initPlatforms();
    }

    @Override
    public void update(double dt) {
        if (!menu) { 
            updatePlayer(dt); 
            for (Enemy e:enemies) {
                updateEnemy(dt, e);
                if (checkCollision(e) && !e.isHit && (q || leftClick || rightClick)) { 
                    e.isHit = true;
                    if (leftClick) { e.hp-= 10; } // for light sword attack;
                    if (rightClick) { e.hp-= 30; } // for heavy sword attack;
                    if (q) { e.hp-= 50; } // for ultimate;
                } else if (e.isHit && !q && !leftClick && !rightClick) {
                    e.isHit = false;
                }
            }
        }
    }

    @Override
    public void paintComponent() {
        changeBackgroundColor(black);
        clearBackground(width(), height());
        drawPlayer();
        drawPlatforms();
        drawEnemy();
        drawHealthBar();

        drawLine(0, GROUND, mWidth, GROUND); // DEBUG;

        /* press TAB to see the debug menu */
        if (debug) {
            drawBoldText(5, 45, "dashCount: " + dashCount + "");
            drawBoldText(5, 90, "xPush: " + xPush + "");
            // drawBoldText(5, 135, "platforms: " + platforms.size());
            drawBoldText(5, 180, "player.x: " + player.x + "");
            // drawBoldText(5, 225, "player.y: " + player.y + "");
            drawBoldText(5, 270, "dash? " + dash + "");
            drawBoldText(5, 315, "enemy 0: " + enemies.get(0).waitPeriod + "");
            drawBoldText(5, 360, "enemy 1: " + enemies.get(1).waitPeriod + "");
            if (menu) {
                changeColor(red);
                drawLine(mWidth/2, 0, mWidth/2, mHeight);
            }
        }

        changeColor(white);
        drawBoldText(mWidth-300, 45, "Player HP: " + player.hp);
        for (int i = 0; i < enemies.size(); i++) {
            if (enemies.get(i).x+xPush > 0 && enemies.get(i).x+enemies.get(i).width+xPush < mWidth && enemies.get(i).hp > 0) {
                drawBoldText(mWidth-300, 45+((i+1)*30), "Enemy " + i + " HP: " + enemies.get(i).hp);
            }
        }

        if (menu) { drawMenu(); }
    }
    
    /* --- ACTIONS --- */

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
        if (e.getButton() == MouseEvent.BUTTON1 && !menu) { leftClick = true; } // left click
        if (e.getButton() == MouseEvent.BUTTON3 && !menu) { rightClick = true; } // right click
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && !menu) { leftClick = false; } // left click
        if (e.getButton() == MouseEvent.BUTTON3 && !menu) { rightClick = false; } // right click
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}
}
