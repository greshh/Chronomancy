public class Moving extends Enemy 
{
    double originalX; // the origin of the enemy before moving;
    double maxDistance; // the distance (one side) where the enemy travels;
    double waitTime; // in seconds;
    double waitPeriod; // in seconds;

    public Moving(double width, double height, double x, double y, double vX, double vY, double maxWaitPeriod, double maxDistance, Hitbox hitbox) {
        super(width, height, x, y, vX, vY, maxWaitPeriod, hitbox);
        this.originalX = hitbox.x;
        this.maxDistance = maxDistance;
        direction = 1;
        waitTime = 0.0;
        waitPeriod = 2.0;
    }
}
