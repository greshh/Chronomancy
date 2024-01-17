public class Player extends Character 
{
    int attackState;

    public Player(double width, double height, double x, double y, double vX, double vY, Hitbox hitbox) {
        super(width, height, x, y, vX, vY, hitbox);
        attackState = 0;
    } 

    /*
     * STATES;
     * 0 idle, 1 walk, 2 running, 3 dash, 4 jump/land, 5 light attack;
     */

    /*
     * ATTACK STATES;
     * 0 not attacking, 1 dLight, 2 sLight, 3 sAir;
     */
}
