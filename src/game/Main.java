package game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Main implements KeyListener, ActionListener
{
    private JFrame frame;
    private GamePane game;
    
    private boolean[] keyState = new boolean[256];

    public Main()
    {
        for (int i = 0; i < 0; i++) {
            keyState[i] = false;
        }
    }

    public void run()
    {
        game = new GamePane(500, 500);
        
        game.setCamera(new Vector3D(250, -500, 250));
        
        //game.addLightSource(new LightSource(new Vector3D(100, -250, 250), 1.0));
        game.addLightSource(new LightSource(new Vector3D(250, -250, 500), 1.0));
        
        game.addCube(new Vector3D(100, 100, 100), new Vector3D(200, 200, 200), Color.RED);
        game.addCube(new Vector3D(300, 100, 100), new Vector3D(400, 200, 200), Color.GREEN);
        game.addCube(new Vector3D(100, 100, 300), new Vector3D(200, 200, 400), Color.BLUE);
        game.addCube(new Vector3D(300, 100, 300), new Vector3D(400, 200, 400), Color.YELLOW);
        game.addCube(new Vector3D(200, 200, 200), new Vector3D(300, 300, 300), Color.MAGENTA);
        game.addCube(new Vector3D(200, 0, 200), new Vector3D(300, 100, 300), Color.WHITE);
    
        frame = new JFrame();
        frame.setResizable(false);
        frame.setTitle("Game");
        frame.setSize(game.getWidth(), game.getHeight());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.addKeyListener(this);
        
        frame.add(game);
        
        Timer timer = new Timer(1000/24, this);
        timer.start();
    }
    
    @Override
    public void keyPressed(KeyEvent e)
    {
        keyState[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        keyState[e.getKeyCode()] = false;
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        Vector3D camera = game.getCamera();
        
        int d = 10;
        float d2 = 0.05f;
        
        Vector3D partial = null, movement = null;
        
        // position
        if (keyState[KeyEvent.VK_W]) {
            partial = new Vector3D(0, d, 0);
            movement = partial.add(movement);
        }
        if (keyState[KeyEvent.VK_S]) {
            partial = new Vector3D(0, -d, 0);
            movement = partial.add(movement);
        }
        if (keyState[KeyEvent.VK_A]) {
            partial = new Vector3D(-d, 0, 0);
            movement = partial.add(movement);
        }
        if (keyState[KeyEvent.VK_D]) {
            partial = new Vector3D(d, 0, 0);
            movement = partial.add(movement);
        }
        if (keyState[KeyEvent.VK_SHIFT]) {
            partial = new Vector3D(0, 0, d);
            movement = partial.add(movement);
        }
        if (keyState[KeyEvent.VK_SPACE]) {
            partial = new Vector3D(0, 0, -d);
            movement = partial.add(movement);
        }
            
        // rotation
        if (keyState[KeyEvent.VK_DOWN]) {
            game.setRotX(game.getRotX() - d2);
        }
        if (keyState[KeyEvent.VK_UP]) {
            game.setRotX(game.getRotX() + d2);
        }
        if (keyState[KeyEvent.VK_RIGHT]) {
            game.setRotZ(game.getRotZ() + d2);
        }
        if (keyState[KeyEvent.VK_LEFT]) {
            game.setRotZ(game.getRotZ() - d2);
        }
        
        if (movement != null) {
            movement = movement.rotX(-game.getRotX()).rotZ(-game.getRotZ());
            game.setCamera(camera.add(movement));
        }
        
        game.repaint();
    }

    public static void main(String[] args)
    {
        Main main = new Main();
        main.run();
    }
}
