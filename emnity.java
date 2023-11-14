import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class emnity extends GameEngine {

    public static void main(String args[]) {
        createGame(new emnity(), 60);
    }

    /* --- INTERACT FUNCTIONS --- */
    boolean jump, left, right;

    @Override
    public void init() {
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
        changeBackgroundColor(black);
        clearBackground(width(), height());
    }
    
    @Override
    public void keyPressed(KeyEvent e) 
    {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) { left = true; }  
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) { right = true; }
        // if (e.getKeyCode() == KeyEvent.VK_SPACE) { jump = true; }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) { left = false; }  
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) { right = false; }
        // if (e.getKeyCode() == KeyEvent.VK_SPACE) { jump = false; }
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