import java.awt.image.BufferedImage;

public class Character {
    BufferedImage spritesheet;
    boolean isHit;
    double height, width;
    double x, y; // coordinates from top-left corner, excluding xPush;
    double vX, vY;
    double timer, duration;
    int currentFrame;
    int direction; // =0 if neutral, <0 if right, >0 if left;
    int state; 
    int hp;
    Hitbox hitbox;

    public Character(double width, double height, double x, double y, double vX, double vY, Hitbox hitbox) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.vX = vX;
        this.vY = vY;
        this.hitbox = hitbox;
        direction = 0;
        state = 0;
        hp = 100;
        timer = 0;
        duration = 0;
        currentFrame = 0;
        isHit = false;
    }
}
