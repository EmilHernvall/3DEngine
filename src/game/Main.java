package game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Main implements KeyListener, ActionListener
{
    private JFrame frame;
    private GraphicsEngine engine;
    
    private boolean[] keyState = new boolean[256];

    public Main()
    {
        for (int i = 0; i < 0; i++) {
            keyState[i] = false;
        }
    }

    public void run()
    {
        engine = new GraphicsEngine(500, 500);
        
        SwingSurface surface = new SwingSurface(engine);
        engine.setSurface(surface);
        
        //engine.setCamera(new Vector3D(250, -500, 250));
        engine.setCamera(new Vector3D(709.40, 758.20, 8.01));
        engine.setRotX(-0.35f);
        engine.setRotZ(-2.55f);
        
        //game.addLightSource(new LightSource(new Vector3D(100, -250, 250), 1.0));
        engine.addLightSource(new LightSource(new Vector3D(250, -500, 250), 10.0));
        
        engine.addCube(new Vector3D(0, 0, 300), new Vector3D(200, 200, 500), Color.BLUE);
        engine.addCube(new Vector3D(300, 0, 300), new Vector3D(500, 200, 500), Color.YELLOW);
        
        engine.addSphere(new Vector3D(100, 100, 100), 100.0, 51, Color.RED);
        engine.addSphere(new Vector3D(400, 100, 100), 100.0, 51, Color.GREEN);
    
        frame = new JFrame();
        frame.setResizable(false);
        frame.setTitle("Game");
        frame.setSize(engine.getWidth(), engine.getHeight());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.addKeyListener(this);
        
        frame.add(surface);
        
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
        Vector3D camera = engine.getCamera();
        
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
            engine.setRotX(engine.getRotX() - d2);
        }
        if (keyState[KeyEvent.VK_UP]) {
            engine.setRotX(engine.getRotX() + d2);
        }
        if (keyState[KeyEvent.VK_RIGHT]) {
            engine.setRotZ(engine.getRotZ() + d2);
        }
        if (keyState[KeyEvent.VK_LEFT]) {
            engine.setRotZ(engine.getRotZ() - d2);
        }
        
        if (movement != null) {
            movement = movement.rotX(-engine.getRotX()).rotZ(-engine.getRotZ());
            camera = camera.add(movement);
            System.out.println("Camera position: " + camera + ", Camera angle: " + engine.getRotX() + ", " + engine.getRotZ());
            engine.setCamera(camera);
        }
        
        engine.triggerRedraw();
    }

    public static void main(String[] args)
    {
        Main main = new Main();
        main.run();
    }
}
