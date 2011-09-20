package game;

import java.util.*;
import java.util.List;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SwingSurface extends JPanel implements Surface
{
    private GraphicsEngine engine;
    private Graphics2D g;

    public SwingSurface(GraphicsEngine engine)
    {
        this.engine = engine;
    }
    
    @Override
    public void putPixel(int x, int y, Color color)
    {
        g.setColor(color);
        g.fillRect(x, y, 1, 1);
    }
    
    @Override
    public void paint(Graphics graphics)
    {
        this.g = (Graphics2D)graphics;
        
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        engine.drawScene();
    }
}
