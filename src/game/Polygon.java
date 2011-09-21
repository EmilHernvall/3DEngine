package game;

import java.awt.*;

public class Polygon
{
    public Vector3D a, b, c;
    public Color color;
    public double aIntensity, bIntensity, cIntensity;
    
    public Polygon(Vector3D a, Vector3D b, Vector3D c, Color color)
    {
        this.a = a;
        this.b = b;
        this.c = c;
        this.color = color;
        this.aIntensity = 0.0;
        this.bIntensity = 0.0;
        this.cIntensity = 0.0;        
    }
    
    public Polygon(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, Color color)
    {
        this.a = new Vector3D(x1, y1, z1);
        this.b = new Vector3D(x2, y2, z2);
        this.c = new Vector3D(x3, y3, z3);
        this.color = color;
        this.aIntensity = 0.0;
        this.bIntensity = 0.0;
        this.cIntensity = 0.0;
    }
    
    public double centroidY()
    {
        return (a.y + b.y + c.y) / 3.0;
    }
    
    public Vector3D centroid3D()
    {
        return new Vector3D(
                (a.x + b.x + c.x) / 3.0,
                (a.y + b.y + c.y) / 3.0,
                (a.z + b.z + c.z) / 3.0
            );
    }
    
    public Vector3D normal()
    {
        Vector3D first = b.sub(a);
        Vector3D second = c.sub(a);
        
        return first.cross(second).norm();
    }
    
    @Override
    public String toString()
    {
        return a + ", " + b + ", " + c;
    }
}
