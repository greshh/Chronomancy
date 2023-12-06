public class Enemy extends Character 
{
    double waitTime; // current time since previous attack;
    double waitPeriod; // current period between attacks on player;
    double maxWaitPeriod; // maximum period between attacks on player;

    public Enemy(double width, double height, double x, double y, double vX, double vY, double maxWaitPeriod) {
        super(width, height, x, y, vX, vY);
        waitTime = 0;
        newWaitPeriod();
    }

    public void newWaitPeriod() { waitPeriod = Math.random()*maxWaitPeriod; }
}
