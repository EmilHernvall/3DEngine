package game;

import java.util.*;
import java.util.List;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GamePane extends JPanel
{
    private int width, height;
    private Vector3D camera, observer;
    private List<Vertex> vertices;
    private float[] zBuffer;
    
    private float rotX = 0.0f;
    private float rotZ = 0.0f;
    
    private int focalLength = 500;

    public GamePane(int width, int height)
    {
        super();
        
        this.width = width;
        this.height = height;
        this.zBuffer = new float[width*height];
        this.vertices = new ArrayList<Vertex>();
        
        this.observer = new Vector3D(0, 0, -5);
    }
    
    public int getWidth()
    {
        return width;
    }
    
    public int getHeight()
    {
        return height;
    }
    
    public void setCamera(Vector3D camera)
    {
        this.camera = camera;
    }
    
    public Vector3D getCamera()
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
    
    public void addCube(Vector3D a, Vector3D b, Color color)
    {
        // surfaces parallell to the xy plane
        // (a.x, a.y, a.z) - (b.x, b.y, a.z)
        vertices.add(new Vertex(a.x, a.y, a.z,  b.x, a.y, a.z,  a.x, b.y, a.z, color));
        vertices.add(new Vertex(b.x, b.y, a.z,  b.x, a.y, a.z,  a.x, b.y, a.z, color));
        
        // (a.x, a.y, b.z) - (b.x, b.y, b.z)
        vertices.add(new Vertex(a.x, a.y, b.z,  b.x, a.y, b.z,  a.x, b.y, b.z, color));
        vertices.add(new Vertex(b.x, b.y, b.z,  b.x, a.y, b.z,  a.x, b.y, b.z, color));
        
        // surfaces parallell to xz plane
        // (a.x, a.y, a.z) - (b.x, a.y, b.z)
        vertices.add(new Vertex(a.x, a.y, a.z,  a.x, a.y, b.z,  b.x, a.y, a.z, color));
        vertices.add(new Vertex(b.x, a.y, b.z,  a.x, a.y, b.z,  b.x, a.y, a.z, color));
        
        // (a.x, b.y, a.z) - (b.x, b.y, b.z)
        vertices.add(new Vertex(a.x, b.y, a.z,  a.x, b.y, b.z,  b.x, b.y, a.z, color));
        vertices.add(new Vertex(b.x, b.y, b.z,  a.x, b.y, b.z,  b.x, b.y, a.z, color));
        
        // surfaces parallell to yz plane
        // (a.x, a.y, a.z) - (a.x, b.x, b.z)
        vertices.add(new Vertex(a.x, a.y, a.z,  a.x, a.y, b.z,  a.x, b.y, a.z, color));
        vertices.add(new Vertex(a.x, b.y, b.z,  a.x, a.y, b.z,  a.x, b.y, a.z, color));
        
        // (b.x, a.y, a.z) - (b.x, b.x, b.z)
        vertices.add(new Vertex(b.x, a.y, a.z,  b.x, a.y, b.z,  b.x, b.y, a.z, color));
        vertices.add(new Vertex(b.x, b.y, b.z,  b.x, a.y, b.z,  b.x, b.y, a.z, color));
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
        //resetZBuffer();
        
        Set<Vertex> renderList = new TreeSet<Vertex>(new Comparator<Vertex>() {
                public int compare(Vertex a, Vertex b) {
                    return a.centroid() - b.centroid() < 0 ? 1 : -1;
                }
            });
        for (Vertex v : vertices) {
            // change coordinates to camera position
            Vector3D a = v.a.sub(camera);
            Vector3D b = v.b.sub(camera);
            Vector3D c = v.c.sub(camera);
            
            // apply rotation
            a = a.rotZ(rotZ).rotX(rotX);
            b = b.rotZ(rotZ).rotX(rotX);
            c = c.rotZ(rotZ).rotX(rotX);
            
            renderList.add(new Vertex(a, b, c, v.color));
        }
    
        Graphics2D g = (Graphics2D)graphics;
        
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        for (Vertex v : renderList) {
            g.setColor(v.color);
            drawVertice(g, v);
        
            g.setColor(Color.GREEN);
            drawLine(g, v.a, v.b);
            drawLine(g, v.b, v.c);
            drawLine(g, v.a, v.c);
        }
    }
    
    private void drawLine(Graphics2D g, Vector3D a, Vector3D b)
    {
        // hide lines that are behind the camera
        if (a.y <= 1 || b.y <= 1) {
            return;
        }
        
        // apply perspective
        int x1 = (int)(a.x * focalLength/a.y) + width / 2;
        int y1 = (int)(a.z * focalLength/a.y) + height / 2;
        
        int x2 = (int)(b.x * focalLength/b.y) + width / 2;
        int y2 = (int)(b.z * focalLength/b.y) + height / 2;
        
        g.drawLine(x1, y1, x2, y2);
    }
    
    private void drawVertice(Graphics2D g, Vertex v)
    {
        Vector3D a = v.a;
        Vector3D b = v.b;
        Vector3D c = v.c;
        
        // hide surfaces behind the camera
        if (a.y <= 1 || b.y <= 1 || c.y < 1) {
            return;
        }
        
        // apply perspective
        int x1 = (int)(a.x * focalLength/a.y) + width / 2;
        int y1 = (int)(a.z * focalLength/a.y) + height / 2;
        
        int x2 = (int)(b.x * focalLength/b.y) + width / 2;
        int y2 = (int)(b.z * focalLength/b.y) + height / 2;
        
        int x3 = (int)(c.x * focalLength/c.y) + width / 2;
        int y3 = (int)(c.z * focalLength/c.y) + height / 2;
        
        // draw triangle
        g.fillPolygon(
                new int[] { x1, x2, x3 },
                new int[] { y1, y2, y3 },
                3
            );
    }
}
