import java.awt.image.BufferedImage;

public class Character {
    BufferedImage spritesheet;
    double height, width;
    double x, y;
    double vX, vY;
    int direction; // =0 if neutral, <0 if right, >0 if left;
    int hp;

    public Character(double width, double height, double x, double y, double vX, double xY) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.vX = vX;
        this.vY = vY;
        direction = 0;
        hp = 100;
    }
}
