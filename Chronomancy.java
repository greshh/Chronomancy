import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Chronomancy extends GameEngine {

    public static void main(String args[]) {
        createGame(new Chronomancy(), 60);
    }

    boolean space, left, right, down, shift, q;
    boolean leftClick, rightClick;
    boolean jump, dash, attack;

    int jumpCount, dashCount;

    double xPush; // the amount the frame gets shifted by as a result of the player moving out of screen.
    final double frameXR = mWidth-(mWidth/8); // the right x-coordinate where the frame starts moving.
    final double frameXL = mWidth/8; // the left x-coordinate where the frame starts moving.

    /* --- EDIT THESE VALUES AS NEEDED TO CHANGE MOVEMENT --- */
    final static int FPS = 60; // frames per second;
    final double GROUND = mHeight-50.0; // y-coordinate of ground.
    final double GRAVITY = 40.0;
    final double HORIZONTAL_ACCELERATION = 60.0;
    final double HORIZONTAL_DECELERATION = 60.0;
    final double GROUND_POUND_ACCELERATION = 100.0;
    final double MAX_VERTICAL_VELOCITY = -800;
    final double MAX_HORIZONTAL_VELOCITY = 800; // without dash;
  
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
        platforms.add(new Platform(900, 400, platformWidth, platformHeight));
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
    double stepsX, stepsY;

    ArrayList<Item> inventory = new ArrayList<>();

    // normal sprites.
    BufferedImage idleImage = (BufferedImage)(loadImage("sprites/idle.png"));
    BufferedImage[] walkImage = new BufferedImage[2];
    BufferedImage[] runImage = new BufferedImage[3];
    BufferedImage jumpImage = (BufferedImage)(loadImage("sprites/jump.png"));
    BufferedImage landImage = (BufferedImage)(loadImage("sprites/land.png"));
    BufferedImage[] dashImage = new BufferedImage[2];
    BufferedImage stopImage = (BufferedImage)(loadImage("sprites/stop.png")); // when slowing down after arrow key has been released.

    // attack sprites
    BufferedImage[] dLightStartImage = new BufferedImage[12]; // on ground, holding down arrow key and attacking
    BufferedImage[] dLightHitImage = new BufferedImage[6];
    BufferedImage[] dLightMissImage = new BufferedImage[8];
    BufferedImage[] sLightImage = new BufferedImage[24]; // on ground, holding left/right arrow key and attacking
    BufferedImage[] sAirImage = new BufferedImage[20]; // in air, holding left/right arrow key and attacking

    double playerv2X; // derivative of velocity - acceleration.
    
    // checking collisions for platforms.
    public boolean checkCollision(Platform p) {
        if (player.hitbox.y < GROUND && (player.hitbox.y <= p.y+p.height && player.hitbox.y+player.hitbox.height >= p.y 
            && player.hitbox.x <= p.x+p.width+xPush && player.hitbox.x+player.hitbox.width >= p.x+xPush)) {
            return true;
        } else { 
            return false; 
        }
    }

    public boolean checkCollision(Item item) {
        if ((player.hitbox.y < item.y+item.height) 
                && (player.hitbox.y+player.hitbox.height > item.y)
                && (player.hitbox.x < item.x+item.width+xPush)
                && (player.hitbox.x+player.hitbox.width > item.x+xPush)) {
            return true;
        } else {
            return false;
        }
    }

    // checking collisions for enemies.
    public boolean checkCollision(Character enemy) {
        if ((player.hitbox.y < enemy.hitbox.y+enemy.hitbox.height) 
                && (player.hitbox.y+player.hitbox.height > enemy.hitbox.y) 
                && (player.hitbox.x < enemy.hitbox.x+enemy.hitbox.width+xPush) 
                && (player.hitbox.x+player.hitbox.width > enemy.hitbox.x+xPush)) {
            return true;
        } else { 
            return false; 
        }
    }

    public boolean isHitting() {
        for (Enemy e:enemies) {
            if (checkCollision(e)) { 
                return true;
            }
        }
        return false;
    }

    public void updatePlayer(double dt) 
    {
        // HORIZONTAL MOVEMENT;

        /* update horizontal acceleration. */
        playerv2X = player.vX/dt; // TO BE FIXED KLDFKLSDJF

        stepsX = Math.abs(player.vX);
        /* this code makes the player move until the object is collided, instead of going inside the object and moving out like it was before. */
        for (int i = (int)stepsX; i > 0; i--) {
            lastvalue = player.hitbox.x;
            player.hitbox.x += player.vX/stepsX*dt;

            // detecting for collisions;
            for (Platform p:platforms) {
                if (checkCollision(p) == true) {
                    player.hitbox.x = lastvalue;
                    player.vX = 0;
                }
            }
        }

        /* update player position according to position of the player. */
        if (player.direction >= 0) {
            player.x = player.hitbox.x+player.hitbox.width+60.0;
        } else {
            player.x = player.hitbox.x+player.hitbox.width+65.0;
        }

        if (player.hitbox.x+player.hitbox.width > frameXR) { 
            xPush+= (frameXR-player.hitbox.x-player.hitbox.width);
            player.hitbox.x = frameXR-player.hitbox.width;
            player.x = frameXR+60.0;
        } 
        if (player.hitbox.x < frameXL) {
            if (xPush < 0) { xPush+= (frameXL-player.hitbox.x); }
            player.hitbox.x = frameXL;
            player.x = frameXL+player.width-65.0;
        }

        // VERTICAL MOVEMENT;

        stepsY = Math.abs(player.vY);
        for (int i = (int)stepsY; i > 0; i--) {
            lastvalue = player.hitbox.y;
            player.hitbox.y += player.vY/stepsY*dt;
            
            for (Platform p:platforms) {
                if (checkCollision(p) == true) {
                    player.hitbox.y = lastvalue;
                    if (player.vY > 0) {
                        jump = false;
                        jumpCount = 0;
                        dashCount = 0;
                    }
                    player.vY = 0;
                }
            }
        } 

        if (player.hitbox.y+player.hitbox.height < GROUND) { 
            player.vY+= GRAVITY; 
        } else {
            player.hitbox.y = GROUND-player.hitbox.height;
            jump = false;
            jumpCount = 0;
            dashCount = 0;
            player.vY = 0.0;
        }

        /* update player position according to position of the player. */
        player.y = player.hitbox.y+player.hitbox.height; 

        
        /* if the player runs out of the screen, it stays at the edge and the scene moves by xPush. */
        if (player.x-player.width <= 0 && xPush == 0) {
            player.x = player.width;
        }

        if (dash) {
            if ((left && player.direction > 0) || (right && player.direction < 0)) { 
                dash = false;
                player.state = 0;
                player.vX = 0.0;
            }
        }

        if (left && !attack && !dash) { 
            player.direction = -1;
            if (player.vX >= -400.0) {
                if (player.state != 1) { 
                    player.timer = 0; 
                    player.currentFrame = 0;
                }
                player.state = 1;
            } else {
                if (player.state != 2) { 
                    player.timer = 0; 
                    player.currentFrame = 0;
                }
                player.state = 2;
            }
            if (player.vX > 0) { player.vX-= HORIZONTAL_ACCELERATION + HORIZONTAL_DECELERATION; } 
            if (player.vX > -MAX_HORIZONTAL_VELOCITY && ((Player)player).attackState != 2) { 
                player.vX-= HORIZONTAL_ACCELERATION; 
            } 
            if (player.vX < -MAX_HORIZONTAL_VELOCITY && !dash) { player.vX = -MAX_HORIZONTAL_VELOCITY; }
        }
        if (right && !attack && !dash) { 
            player.direction = 1;
            if (player.vX <= 400.0) {
                if (player.state != 1) { 
                    player.timer = 0; 
                    player.currentFrame = 0;
                }
                player.state = 1;
            } else {
                if (player.state != 2) { 
                    player.timer = 0; 
                    player.currentFrame = 0;
                }
                player.state = 2;
            }
            if (player.vX < 0) { player.vX+= HORIZONTAL_ACCELERATION + HORIZONTAL_DECELERATION; } 
            if (player.vX < MAX_HORIZONTAL_VELOCITY && ((Player)player).attackState != 2) { 
                player.vX+= HORIZONTAL_ACCELERATION; 
            } 
            if (player.vX > MAX_HORIZONTAL_VELOCITY && !dash) { player.vX = MAX_HORIZONTAL_VELOCITY; }
        }
        if (!left && !right && !dash) { 
            if (player.vX < 0 && player.direction < 0) { player.vX+= HORIZONTAL_DECELERATION; } // left;
            if (player.vX > 0 && player.direction > 0) { player.vX-= HORIZONTAL_DECELERATION; } // right;
            // if (((player.vX < 0 && player.direction > 0) || (player.vX > 0 && player.direction < 0))) { // after backDash, facing one way, going the other;
            //     player.state = 2; 
            //     player.vX = 0;
            // } 
            if (player.vX > -40 && player.vX < 40) { player.vX = 0; }
            if (player.vX == 0 && !attack) { 
                if (player.state != 0) { 
                    player.timer = 0; 
                    player.currentFrame = 0;
                }
                player.state = 0; 
            } // change state to idle if not doing anything;
        }

        if (jump && !attack && ((Player)player).attackState != 3) { 
            if (player.state != 4) { 
                player.timer = 0; 
                player.currentFrame = 0;
            }
            player.state = 4; 
        }
        
        if (down) { player.vY+= GROUND_POUND_ACCELERATION; }

        if (dash) {
            if (!attack) {
                if (player.state != 3) {
                    player.timer = 0;
                    player.currentFrame = 0;
                }
            }
            player.state = 3;
            if (dash) {
                if (player.direction < 0) { // left
                    if (player.vX >= -MAX_HORIZONTAL_VELOCITY) {
                        dash = false;
                    } else {
                        player.vX += 50.0;
                    }
                } else if (player.direction > 0) { // right
                    if (player.vX <= MAX_HORIZONTAL_VELOCITY) {
                        dash = false;
                    } else {
                        player.vX -= 50.0;
                    }
                }
            }
        }

        if (((Player)player).attackState == 2) { // sLight;
            if (player.currentFrame < 8 || player.currentFrame >= 18) {
                player.vX = 0.0;
            } else if (player.currentFrame >= 16) {
                if (player.direction < 0) {  // left;
                    player.vX += 50.0;
                } else if (player.direction > 0) { // right;
                    player.vX -= 50.0;
                }
            } else {
                if (player.direction < 0) { // left;
                    player.vX -= 150.0;
                } else if (player.direction > 0) { // right;
                    player.vX += 150.0;
                }
            } 
        } else if (((Player)player).attackState == 3) { // sAir;
            if (player.currentFrame < 8 || player.currentFrame >= 14) {
                player.vX = 0.0;
            } else if (player.currentFrame >= 12) {
                if (player.direction < 0) {  // left;
                    player.vX += 50.0;
                } else if (player.direction > 0) { // right;
                    player.vX -= 50.0;
                }
            } else {
                if (player.direction < 0) { // left;
                    player.vX -= 150.0;
                } else if (player.direction > 0) { // right;
                    player.vX += 150.0;
                }
            } 
        }

        /* changing state to light attack on mouse click. */
        if (leftClick) { 
            if ((!jump && down) || (!jump && (left || right)) || (jump && (left || right))) { // before nair is created.
                if (!jump && down) {
                    ((Player)player).attackState = 1; // dLight;
                } else if (!jump && (left || right)) {
                    ((Player)player).attackState = 2; // sLight;
                } else if (jump && (left || right)) {
                    ((Player)player).attackState = 3; // sAir;
                }
                if (player.state != 5) { 
                    player.timer = 0; 
                }
                player.state = 5;
                attack = true;
            }
        }

        if (!attack) { ((Player)player).attackState = 0; }

        // UPDATING SPRITES.
        player.timer += dt;
        switch (player.state) {
            case 1: // walking;
                player.duration = 0.9;
                player.currentFrame = (int)Math.floor(((player.timer%player.duration)/player.duration)*2);
            case 2: // running;
                player.duration = 0.3;
                player.currentFrame = (int)Math.floor(((player.timer%player.duration)/player.duration)*3);
                break;
            case 5: // light attack;
                if (((Player)player).attackState == 1) { // dLight;
                    if (!player.isAttacking && attack) { // dLightStart
                        if (player.currentFrame >= 11) {
                            player.isAttacking = true;
                            player.timer = 0;
                        }
                        player.duration = 0.3;
                        player.currentFrame = (int)Math.floor(((player.timer%player.duration)/player.duration)*12);
                    } 
                    if (player.isAttacking && !isHitting() && attack) { // dLightMiss
                        if (player.currentFrame >= 7) {
                            player.isAttacking = false;
                            player.timer = 0;
                            attack = false;
                            ((Player)player).attackState = 0;
                        }
                        player.duration = 0.3;
                        player.currentFrame = (int)Math.floor(((player.timer%player.duration)/player.duration)*8);
                    }
                    if (player.isAttacking && isHitting() && attack) { // dLightMiss
                        if (player.currentFrame >= 5) {
                            player.isAttacking = false;
                            player.timer = 0;
                            attack = false;
                            ((Player)player).attackState = 0;
                        }
                        player.duration = 0.3;
                        player.currentFrame = (int)Math.floor(((player.timer % player.duration)/player.duration)*6);
                    }
                } else if (((Player)player).attackState == 2) { // sLight;
                    if (player.currentFrame >= 23) {
                        player.isAttacking = false;
                        player.timer = 0;
                        attack = false;
                        ((Player)player).attackState = 0;
                    }
                    player.duration = 0.7;
                    player.currentFrame = (int)Math.floor(((player.timer % player.duration)/player.duration)*24);
                } else if (((Player)player).attackState == 3) { // sAir;
                    if (player.currentFrame >= 19) {
                        player.isAttacking = false;
                        player.timer = 0;
                        attack = false;
                        ((Player)player).attackState = 0;
                    }
                    player.duration = 0.7;
                    player.currentFrame = (int)Math.floor(((player.timer % player.duration)/player.duration)*20);
                }
                //player.currentFrame = 0; // DEBUG FOR TESTING IF CURRENT FRAME CODE ISN'T WORKING YET
                break;
            default:
                player.currentFrame = 0;
                player.timer = 0;
                player.duration = 0;
        }
    }

    public void drawPlayer() {
        changeColor(white);
        /* draws the player according to its state (listed) and then is determined by which direction the player was/is facing. */
        switch (player.state) {
            case 0: // idle;
                if (player.direction >= 0) { // facing right;
                    drawImage(idleImage, (player.x-player.width), (player.y-player.height), player.width, player.height); 
                } else { // left;
                    drawImage(idleImage, player.x, (player.y-player.height), -player.width, player.height);
                }
                break;
            case 1: // walk;
                if (player.currentFrame >= 2) { player.currentFrame = 0; }
                if (player.direction >= 0) { // facing right;
                    drawImage(walkImage[player.currentFrame], (player.x-player.width), (player.y-player.height), player.width, player.height);
                } else { // facing left;
                    drawImage(walkImage[player.currentFrame], player.x, (player.y-player.height), -player.width, player.height);
                }
                break;
            case 2: // run;
                if (player.direction >= 0) { // facing right or idle;
                    if (right) {
                        drawImage(runImage[player.currentFrame], (player.x-player.width), (player.y-player.height), player.width, player.height);
                    } else {
                        drawImage(stopImage, (player.x-player.width), (player.y-player.height), player.width, player.height);
                    }
                } else { // facing left;
                    if (left) {
                        drawImage(runImage[player.currentFrame], player.x, (player.y-player.height), -player.width, player.height);
                    } else {
                        drawImage(stopImage, player.x, (player.y-player.height), -player.width, player.height);
                    }
                }
                break;
            case 3: // dash;
                if (player.direction >= 0) {
                    drawImage(dashImage[0], (player.x-player.width), (player.y-player.height), player.width, player.height);
                } else {
                    drawImage(dashImage[0], player.x, (player.y-player.height), -player.width, player.height);
                }
                break;
            case 4: // jump/land;
            /* if the player is jumping up, jump.png is used. else, jumping downwards = land.png. */
                if (player.direction >= 0) {
                    if (player.vY < 0) {
                        drawImage(jumpImage, (player.x-player.width), (player.y-player.height), player.width, player.height);
                    } else {
                        drawImage(landImage, (player.x-player.width), (player.y-player.height), player.width, player.height);
                    }
                } else {
                    if (player.vY < 0) {
                        drawImage(jumpImage, player.x, (player.y-player.height), -player.width, player.height);
                    } else {
                        drawImage(landImage, player.x, (player.y-player.height), -player.width, player.height);
                    }
                }
                break;
            case 5:
                if (((Player)player).attackState == 1) { // dLight;
                    if (!player.isAttacking) {
                        if (player.direction >= 0) {
                            drawImage(dLightStartImage[player.currentFrame], (player.x-player.width), (player.y-player.height), player.width, player.height);
                        } else {
                            drawImage(dLightStartImage[player.currentFrame], player.x, (player.y-player.height), -player.width, player.height);
                        } 
                    } else if (player.isAttacking && isHitting()) {
                        if (player.direction >= 0) {
                            drawImage(dLightHitImage[player.currentFrame], (player.x-player.width), (player.y-player.height), player.width, player.height);
                        } else {
                            drawImage(dLightHitImage[player.currentFrame], player.x, (player.y-player.height), -player.width, player.height);
                        } 
                    } else if (player.isAttacking && !isHitting()) {
                        if (player.direction >= 0) {
                            drawImage(dLightMissImage[player.currentFrame], (player.x-player.width), (player.y-player.height), player.width, player.height);
                        } else {
                            drawImage(dLightMissImage[player.currentFrame], player.x, (player.y-player.height), -player.width, player.height);
                        } 
                    }
                } else if (((Player)player).attackState == 2) { // sLightImage;
                    if (player.direction >= 0) {
                        drawImage(sLightImage[player.currentFrame], (player.x-player.width), (player.y-player.height), player.width, player.height);
                    } else {
                        drawImage(sLightImage[player.currentFrame], player.x, (player.y-player.height), -player.width, player.height);
                    }
                } else if (((Player)player).attackState == 3) { // sAirImage;
                    if (player.direction >= 0) {
                        drawImage(sAirImage[player.currentFrame], (player.x-player.width), (player.y-player.height), player.width, player.height);
                    } else {
                        drawImage(sAirImage[player.currentFrame], player.x, (player.y-player.height), -player.width, player.height);
                    }
                }
                break;
            default:
                break;
        }
        
        /* draw hitbox if debug. */
        if (debug) { drawRectangle(player.hitbox.x, player.hitbox.y, player.hitbox.width, player.hitbox.height); }
    }

    /* --- NPC --- */
    ArrayList<NPC> npcs = new ArrayList<>();

    double npcY = GROUND-150.0;

    public void initNPCs() {
        npcs.add(new NPC(30, 150, 1500.0, npcY, 0.0, 0.0, new Hitbox(30, 150, 1500.0, npcY)));
    }

    public void drawNPCs() {
        changeColor(blue);
        for (NPC n:npcs) {
            drawRectangle(n.x+xPush, n.y, n.width, n.height);
        }
    }

    /* --- ENEMY --- */
    ArrayList<Enemy> enemies = new ArrayList<>();

    double enemyY = GROUND-150.0;

    public void initEnemy() {
        enemies.add(new Enemy(30, 150, 1100.00, enemyY, 0.0, 0.0, 0.0, new Hitbox(1100.00,enemyY,30,150)));
        //enemies.add(new Moving(player.width, player.height, 300.00, enemyY, 75.00, 0, 2.0, 150.0, new Hitbox(0,0,0,0)));
        //enemies.add(new Following(player.width, player.height, 1500.00, enemyY, 200.00, 0));
    }

    public void drawEnemy() {
        changeColor(orange);
        for (Character e:enemies) {
            if (e.hp > 0) { 
                drawRectangle(e.x+xPush, e.y, e.width, e.height); 
            } else { e.hp = 0; } // ensures the enemy's health is not negative.
            if (debug) { 
                changeColor(white);
                drawRectangle(e.hitbox.x+xPush, e.hitbox.y, e.hitbox.width, e.hitbox.height);
            }
        }
    }

    public void updateEnemy(double dt, Enemy e) {
        if (e.getClass().getName() == "Moving") {
                // if the moving enemy reaches its maximum distance to the RIGHT, it waits for the waiting period before resuming.
                if (e.hitbox.x >= ((Moving)e).originalX+((Moving)e).maxDistance) {
                    if (((Moving)e).waitTime < ((Moving)e).waitPeriod) {
                        e.direction = 0;
                        ((Moving)e).waitTime+= dt;
                    } else {
                        ((Moving)e).waitTime = 0;
                        e.direction = -1;
                    }
                // if the moving enemy reaches its maximum distance to the LEFT, it waits for the waiting period before resuming.
                } else if (e.hitbox.x <= ((Moving)e).originalX-((Moving)e).maxDistance) {
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
        //     double distance = e.hitbox.x-player.hitbox.x;
        //     if (distance < 0) { 
        //         e.direction = 1;
        //     } else if (distance > 0) {
        //         e.direction = -1;
        //     }
        // }
        if (e.direction > 0) { e.hitbox.x+= e.vX*dt; }
        else if (e.direction < 0) { e.hitbox.x-= e.vX*dt; }
            
        // if (checkCollision(e)) {
        //     if (e.waitTime < e.waitPeriod) {
        //         e.waitTime+= dt;
        //     } else {
        //         player.hp-= 0.00000001;
        //         e.waitTime = 0;
        //         e.newWaitPeriod();
        //     }
        // }
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

    /* --- ITEM & INVENTORY --- */
    ArrayList<Item> availableItems = new ArrayList<>();

    public void initItems() {
        availableItems.add(new Item("Apple", "Yummy apple yum", 1000.0, 350.0, 20.0, 20.0));
        availableItems.add(new Item("Watch", "You need to tell the time?", 200.0, 350.0, 20.0, 20.0));
    }

    public void drawItems() {
        changeColor(green);
        for (Item a:availableItems) {
            drawCircle(a.x+xPush, a.y, a.width/2);
        }
    }

    public void updateItems(double dt) {
        boolean[] isCollected = new boolean[availableItems.size()];
        for (int i = 0; i < availableItems.size(); i++) {
            isCollected[i] = false;
        }

        for (int i = 0; i < availableItems.size(); i++) {
            Item a = availableItems.get(i);
            if (checkCollision(a)) {
                inventory.add(a);
                isCollected[i] = true;
            }
        }

        for (int i = 0; i < availableItems.size(); i++) {
            if (isCollected[i]) {
                availableItems.remove(i);
            }
        }
    }

    /* --- GAME --- */

    public void loadSprites() 
    {
        // PLAYER //

        dashImage[0] = (BufferedImage)(loadImage("sprites/dashf.png"));
        dashImage[1] = (BufferedImage)(loadImage("sprites/dashb.png"));

        walkImage[0] = (BufferedImage)(loadImage("sprites/walk1.png"));
        walkImage[1] = (BufferedImage)(loadImage("sprites/walk2.png"));

        runImage[0] = (BufferedImage)(loadImage("sprites/run1.png"));
        runImage[1] = (BufferedImage)(loadImage("sprites/run2.png"));
        runImage[2] = (BufferedImage)(loadImage("sprites/run3.png"));

        dLightStartImage[0] = (BufferedImage)(loadImage("sprites/dlight0001.png"));
        dLightStartImage[1] = dLightStartImage[0];
        dLightStartImage[2] = (BufferedImage)(loadImage("sprites/dlight0003.png"));
        dLightStartImage[3] = dLightStartImage[2];
        dLightStartImage[4] = dLightStartImage[2];
        dLightStartImage[5] = dLightStartImage[2];
        dLightStartImage[6] = (BufferedImage)(loadImage("sprites/dlight0007.png"));
        dLightStartImage[7] = dLightStartImage[6];
        dLightStartImage[8] = (BufferedImage)(loadImage("sprites/dlight0009.png"));
        dLightStartImage[9] = dLightStartImage[8];
        dLightStartImage[10] = (BufferedImage)(loadImage("sprites/dlight0011.png"));
        dLightStartImage[11] = dLightStartImage[10];

        dLightHitImage[0] = (BufferedImage)(loadImage("sprites/dLightHit0001.png"));
        dLightHitImage[1] = dLightHitImage[0];
        dLightHitImage[2] = (BufferedImage)(loadImage("sprites/dLightHit0003.png"));
        dLightHitImage[3] = dLightHitImage[2];
        dLightHitImage[4] = (BufferedImage)(loadImage("sprites/dLightHit0005.png"));
        dLightHitImage[5] = dLightHitImage[4];

        dLightMissImage[0] = (BufferedImage)(loadImage("sprites/dLightMiss0001.png"));
        dLightMissImage[1] = dLightMissImage[0];
        dLightMissImage[2] = dLightMissImage[0];
        dLightMissImage[3] = dLightMissImage[0];
        dLightMissImage[4] = (BufferedImage)(loadImage("sprites/dLightMiss0005.png"));
        dLightMissImage[5] = dLightMissImage[4];
        dLightMissImage[6] = (BufferedImage)(loadImage("sprites/dLightMiss0007.png"));
        dLightMissImage[7] = dLightMissImage[6];

        sAirImage[0] = (BufferedImage)(loadImage("sprites/sAir0001.png"));
        sAirImage[1] = sAirImage[0];
        sAirImage[2] = (BufferedImage)(loadImage("sprites/sAir0003.png"));
        sAirImage[3] = sAirImage[2];
        sAirImage[4] = (BufferedImage)(loadImage("sprites/sAir0005.png"));
        sAirImage[5] = sAirImage[4];
        sAirImage[6] = sAirImage[4];
        sAirImage[7] = sAirImage[4];
        sAirImage[8] = (BufferedImage)(loadImage("sprites/sAir0009.png"));
        sAirImage[9] = sAirImage[8];
        sAirImage[10] = (BufferedImage)(loadImage("sprites/sAir0011.png"));
        sAirImage[11] = sAirImage[10];
        sAirImage[12] = (BufferedImage)(loadImage("sprites/sAir0013.png"));
        sAirImage[13] = sAirImage[12];
        sAirImage[14] = (BufferedImage)(loadImage("sprites/sAir0015.png"));
        sAirImage[15] = sAirImage[14];
        sAirImage[16] = sAirImage[14];
        sAirImage[17] = sAirImage[14];
        sAirImage[18] = sAirImage[14];
        sAirImage[19] = sAirImage[14];

        sLightImage[0] = (BufferedImage)(loadImage("sprites/sLight0001.png"));
        sLightImage[1] = sLightImage[0];
        sLightImage[2] = (BufferedImage)(loadImage("sprites/sLight0003.png"));
        sLightImage[3] = sLightImage[2];
        sLightImage[4] = sLightImage[2];
        sLightImage[5] = sLightImage[2];
        sLightImage[6] = (BufferedImage)(loadImage("sprites/sLight0007.png"));
        sLightImage[7] = sLightImage[6];
        sLightImage[8] = (BufferedImage)(loadImage("sprites/sLight0009.png"));
        sLightImage[9] = sLightImage[8];
        sLightImage[10] = (BufferedImage)(loadImage("sprites/sLight0011.png"));
        sLightImage[11] = sLightImage[10];
        sLightImage[12] = (BufferedImage)(loadImage("sprites/sLight0013.png"));
        sLightImage[13] = sLightImage[12];
        sLightImage[14] = sLightImage[12];
        sLightImage[15] = sLightImage[12];
        sLightImage[16] = (BufferedImage)(loadImage("sprites/sLight0017.png"));
        sLightImage[17] = sLightImage[16];
        sLightImage[18] = (BufferedImage)(loadImage("sprites/sLight0019.png"));
        sLightImage[19] = sLightImage[18];
        sLightImage[20] = sLightImage[18];
        sLightImage[21] = sLightImage[18];
        sLightImage[22] = (BufferedImage)(loadImage("sprites/sLight0023.png"));
        sLightImage[23] = sLightImage[22];   
    }

    @Override
    public void init() 
    {
        loadSprites();

        space = false;
        left = false;
        right = false;
        down = false;
        shift = false;
        q = false;
        leftClick = false;
        rightClick = false;

        jump = false;
        dash = false;
        attack = false;
        jumpCount = 0;
        dashCount = 0;

        xPush = 0;

        debug = false;
        menu = false;

        player = new Player(150.0, 150.0, (double)(mWidth/2), GROUND, 0.0, 0.0, new Hitbox((double)(mWidth/2)-85.0,GROUND-135.0,25.0,135.0));
        // new Hitbox((double)(mWidth/2)-85.0,GROUND-135.0,25.0,135.0)

        player.timer = 0; // reset player sprite timer.
        
        initEnemy();
        initNPCs();
        initPlatforms();
        initItems();
    }

    @Override
    public void update(double dt) {
        if (!menu) { 
            updatePlayer(dt); 
            updateItems(dt);
            for (Enemy e:enemies) {
                updateEnemy(dt, e);
                // if (checkCollision(e) && !e.isHit && attack) { 
                //     e.isHit = true;
                //     if (leftClick) { e.hp-= 10; } // for light sword attack;
                //     if (rightClick) { e.hp-= 30; } // for heavy sword attack;
                //     if (q) { e.hp-= 50; } // for ultimate;
                // } else if (e.isHit && !q && !leftClick && !rightClick) {
                //     e.isHit = false;
                // }
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
        drawNPCs();
        drawHealthBar();
        drawItems();

        changeColor(white);
        drawLine(0, GROUND, mWidth, GROUND); // DEBUG;

        /* press TAB to see the debug menu */
        if (debug) {
            drawBoldText(5, 45, "player x: " + player.x + "");
            drawBoldText(5, 90, "GROUND: " + GROUND);
            drawBoldText(5, 135, "dash: " + dash);
            drawBoldText(5, 270, "shift: " + shift + "");
            drawBoldText(5, 315, "player.vX: " + player.vX + "");
            //drawBoldText(5, 360, "enemy 1: " + enemies.get(1).waitPeriod + "");
            if (menu) {
                changeColor(red);
                drawLine(mWidth/2, 0, mWidth/2, mHeight);
            }
        }

        changeColor(white);
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
                if ((jump && dashCount < 1) || !jump) {
                    if (!dash) {
                        dash = true;
                        if (player.direction != 0) {
                            player.vX = 1300 * player.direction;
                        } else {
                            player.vX = 1300;
                            player.direction = 1;
                        }
                    }
                    if (jump) {
                        dashCount++;
                    }
                }
            }
            shift = true; 
        }
        if (e.getKeyCode() == KeyEvent.VK_TAB) { debug = !debug; }  
        if (e.getKeyCode() == KeyEvent.VK_F) { leftClick = true; } // alternative key for attack (left click).
        // if (e.getKeyCode() == KeyEvent.VK_Q) {
        //     q = true;
        //     player.state = 5;
        // }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) { left = false; }  
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) { right = false; }
        if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) { down = false; }
        if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_W) { space = false; }
        if (e.getKeyCode() == KeyEvent.VK_SHIFT || e.getKeyCode() == KeyEvent.VK_L) { shift = false; }
        if (e.getKeyCode() == KeyEvent.VK_F) { leftClick = false; }
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
