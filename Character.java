import java.awt.image.BufferedImage;

public class Character {
    BufferedImage spritesheet;
    double height, width;
    double x, y;
    double vX, vY;
    int hp;

    public Character(double width, double height, double x, double y, double vX, double xY) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.vX = vX;
        this.vY = vY;
        hp = 100;
    }
}
