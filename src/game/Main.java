package game;

import java.awt.event.*;
import javax.swing.*;

public class Main implements KeyListener
{
    private JFrame frame;
    private GamePane game;

    public Main()
    {
    }

    public void run()
    {
        game = new GamePane(500, 500);
        game.setCamera(new Point3D(250, -500, 250));
        game.addCube(new Point3D(100, 100, 100), new Point3D(200, 200, 200));
        game.addCube(new Point3D(300, 100, 100), new Point3D(400, 200, 200));
        game.addCube(new Point3D(100, 100, 300), new Point3D(200, 200, 400));
        game.addCube(new Point3D(300, 100, 300), new Point3D(400, 200, 400));
        
        game.addCube(new Point3D(200, 200, 200), new Point3D(300, 300, 300));
    
        frame = new JFrame();
        frame.setResizable(false);
        frame.setTitle("Game");
        frame.setSize(game.getWidth(), game.getHeight());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.addKeyListener(this);
        
        frame.add(game);
    }
    
    @Override
    public void keyPressed(KeyEvent e)
    {
        Point3D camera = game.getCamera();
        int d = 5;
        Point3D movement = null;
        float d2 = 0.1f;
        switch (e.getKeyCode()) {
            // position
            case KeyEvent.VK_W:
                movement = new Point3D(0, d, 0);
                break;
            case KeyEvent.VK_S:
                movement = new Point3D(0, -d, 0);
                break;
            case KeyEvent.VK_A:
                movement = new Point3D(-d, 0, 0);
                break;
            case KeyEvent.VK_D:
                movement = new Point3D(d, 0, 0);
                break;
            case KeyEvent.VK_SHIFT:
                movement = new Point3D(0, 0, d);
                break;
            case KeyEvent.VK_SPACE:
                movement = new Point3D(0, 0, -d);
                break;
                
            // rotation
            case KeyEvent.VK_DOWN:
                game.setRotX(game.getRotX() - d2);
                break;
            case KeyEvent.VK_UP:
                game.setRotX(game.getRotX() + d2);
                break;
            case KeyEvent.VK_RIGHT:
                game.setRotZ(game.getRotZ() + d2);
                break;
            case KeyEvent.VK_LEFT:
                game.setRotZ(game.getRotZ() - d2);
                break;
        }
        
        if (movement != null) {
            movement = movement.rotX(-game.getRotX()).rotZ(-game.getRotZ());
            game.setCamera(camera.add(movement));
        }
        
        game.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
    }

    public static void main(String[] args)
    {
        Main main = new Main();
        main.run();
    }
}
