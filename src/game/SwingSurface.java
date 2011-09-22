package game;

import java.util.*;
import java.util.List;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;

public class SwingSurface extends JPanel implements Surface
{
    private GraphicsEngine engine;
    private BufferedImage canvas = null;
    private WritableRaster raster = null;
    private float[] colorArr = new float[3];

    public SwingSurface(GraphicsEngine engine)
    {
        this.engine = engine;
    }
    
    @Override
    public void putPixel(int x, int y, Color color)
    {
        raster.setPixel(x, y, new int[] { color.getRed(), color.getGreen(), color.getBlue() });
    }
    
    @Override
    public void paint(Graphics graphics)
    {
        if (canvas == null) {
            GraphicsConfiguration conf = ((Graphics2D)graphics).getDeviceConfiguration();
            canvas = conf.createCompatibleImage(engine.getWidth(), engine.getHeight());    
            raster = canvas.getRaster();
        }
        
        Graphics2D g = null; 
        try { 
            g = canvas.createGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, engine.getWidth(), engine.getHeight());
        } finally {
            g.dispose();
        }
        
        engine.drawScene();
        graphics.drawImage(canvas, 0, 0, null);
    }
}
