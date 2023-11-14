import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class emnity extends GameEngine {

    public static void main(String args[]) {
        createGame(new emnity(), 60);
    }

    /* --- INTERACT FUNCTIONS --- */
    boolean jump, left, right;
    boolean accelerate;

    long leftTime, rightTime;

    @Override
    public void init() {
        leftTime = 0;
        rightTime = 0;

        jump = false;
        left = false;
        right = false;
    }

    @Override
    public void update(double dt) {
       //..
    }

    @Override
    public void paintComponent() {
        // DEBUG
        if (left && accelerate) { changeBackgroundColor(red);}
        else if (left && !accelerate) { changeBackgroundColor(orange); }
        else if (right && !accelerate) { changeBackgroundColor(blue); }
        else if (right && accelerate) { changeBackgroundColor(purple); }
        else { changeBackgroundColor(black); }
        clearBackground(width(), height());
    }
    
    @Override
    public void keyPressed(KeyEvent e) 
    {
        // updating left key bool - if LEFT is double-pressed (within 500ms), player is accelerated.
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (getTime()-leftTime < 200) {
                accelerate = true;
            } else {
                accelerate = false;
            }
            left = true;
            leftTime = getTime();
        }  

        // updating right key bool - if RIGHT is double-pressed (within 500ms), player is accelerated.
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) { 
            if (getTime()-rightTime < 200) {
                accelerate = true;
            } else {
                accelerate = false;
            }
            right = true;
            rightTime = getTime();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) { left = false; }  
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) { right = false; }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}
}