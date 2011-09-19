package game;

import java.util.*;
import java.util.List;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GamePane extends JPanel
{
    private int width, height;
    private Point3D camera, observer;
    private List<Vertex> vertices;
    private float[] zBuffer;
    
    private float rotX = 0.0f;
    private float rotZ = 0.0f;

    public GamePane(int width, int height)
    {
        super();
        
        this.width = width;
        this.height = height;
        this.zBuffer = new float[width*height];
        this.vertices = new ArrayList<Vertex>();
        
        this.observer = new Point3D(0, 0, -5);
    }
    
    public int getWidth()
    {
        return width;
    }
    
    public int getHeight()
    {
        return height;
    }
    
    public void setCamera(Point3D camera)
    {
        this.camera = camera;
    }
    
    public Point3D getCamera()
    {
        return camera;
    }
    
    public float getRotX()
    {
        return rotX;
    }
    
    public float getRotZ()
    {
        return rotZ;
    }
    
    public void setRotX(float v)
    {
        this.rotX = v;
    }
    
    public void setRotZ(float v)
    {
        this.rotZ = v;
    }
    
    public void addCube(Point3D a, Point3D b)
    {
        // surfaces parallell to the xy plane
        // (a.x, a.y, a.z) - (b.x, b.y, a.z)
        vertices.add(new Vertex(a.x, a.y, a.z,  b.x, a.y, a.z,  a.x, b.y, a.z));
        vertices.add(new Vertex(b.x, b.y, a.z,  b.x, a.y, a.z,  a.x, b.y, a.z));
        
        // (a.x, a.y, b.z) - (b.x, b.y, b.z)
        vertices.add(new Vertex(a.x, a.y, b.z,  b.x, a.y, b.z,  a.x, b.y, b.z));
        vertices.add(new Vertex(b.x, b.y, b.z,  b.x, a.y, b.z,  a.x, b.y, b.z));
        
        // surfaces parallell to xz plane
        // (a.x, a.y, a.z) - (b.x, a.y, b.z)
        vertices.add(new Vertex(a.x, a.y, a.z,  a.x, a.y, b.z,  b.x, a.y, a.z));
        vertices.add(new Vertex(b.x, a.y, b.z,  a.x, a.y, b.z,  b.x, a.y, a.z));
        
        // (a.x, b.y, a.z) - (b.x, b.y, b.z)
        vertices.add(new Vertex(a.x, b.y, a.z,  a.x, b.y, b.z,  b.x, b.y, a.z));
        vertices.add(new Vertex(b.x, b.y, b.z,  a.x, b.y, b.z,  b.x, b.y, a.z));
        
        // surfaces parallell to yz plane
        // (a.x, a.y, a.z) - (a.x, b.x, b.z)
        vertices.add(new Vertex(a.x, a.y, a.z,  a.x, a.y, b.z,  a.x, b.y, a.z));
        vertices.add(new Vertex(a.x, b.y, b.z,  a.x, a.y, b.z,  a.x, b.y, a.z));
        
        // (b.x, a.y, a.z) - (b.x, b.x, b.z)
        vertices.add(new Vertex(b.x, a.y, a.z,  b.x, a.y, b.z,  b.x, b.y, a.z));
        vertices.add(new Vertex(b.x, b.y, b.z,  b.x, a.y, b.z,  b.x, b.y, a.z));
    }
    
    public void addVertex(Vertex v)
    {
        vertices.add(v);
    }
    
    private void resetZBuffer()
    {
        for (int i = 0; i < zBuffer.length; i++) {
            zBuffer[i] = Float.MAX_VALUE;
        }
    }
    
    @Override
    public void paint(Graphics graphics)
    {
        resetZBuffer();
    
        Graphics2D g = (Graphics2D)graphics;
        
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        g.setColor(Color.GREEN);
        for (Vertex v : vertices) {
            drawLine(g, v.a, v.b);
            drawLine(g, v.b, v.c);
            drawLine(g, v.a, v.c);
        }
    }
    
    private void drawLine(Graphics2D g, Point3D a, Point3D b)
    {
        // change coordinates to camera position
        a = a.sub(camera);
        b = b.sub(camera);
        
        // apply rotation
        a = a.rotZ(rotZ).rotX(rotX);
        b = b.rotZ(rotZ).rotX(rotX);
        
        // apply perspective
        int d = 500;
        
        int x1 = (int)(a.x * d/a.y) + width / 2;
        int y1 = (int)(a.z * d/a.y) + height / 2;
        
        int x2 = (int)(b.x * d/b.y) + width / 2;
        int y2 = (int)(b.z * d/b.y) + height / 2;
        
        g.drawLine(x1, y1, x2, y2);
    }
}
