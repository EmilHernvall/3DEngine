package game;

import java.awt.*;

public class Vertex
{
    public Vector3D a, b, c;
    public Color color;
    
    public Vertex(Vector3D a, Vector3D b, Vector3D c, Color color)
    {
        this.a = a;
        this.b = b;
        this.c = c;
        this.color = color;
    }
    
    public Vertex(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, Color color)
    {
        this.a = new Vector3D(x1, y1, z1);
        this.b = new Vector3D(x2, y2, z2);
        this.c = new Vector3D(x3, y3, z3);
        this.color = color;
    }
    
    public double centroid()
    {
        return (a.y + b.y + c.y) / 3.0;
    }
}
