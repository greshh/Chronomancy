public class Player extends Character 
{
    public Player(double width, double height, double x, double y, double vX, double vY, Hitbox hitbox) {
        super(width, height, x, y, vX, vY, hitbox);
    } 

    /*
     * STATES;
     * 0 idle, 1 walk, 2 running, 3 dash, 4 jump/land, 5 light attack;
     */
}
