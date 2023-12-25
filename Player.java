public class Player extends Character 
{
    public Player(double width, double height, double x, double y, double vX, double vY, Hitbox hitbox) {
        super(width, height, x, y, vX, vY, hitbox);
    } 

    /*
     * STATES;
     * 0 idle, 1 running, 2 dash, 3 jump/land, 4 light sword attack, 5 heavy sword attack;
     */
}
