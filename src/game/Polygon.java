package game;

import java.awt.*;

public class Polygon
{
    public Vector3D a, b, c;
    public Color color;
    public double aIntensity, bIntensity, cIntensity;
    public Vector3D centroid;
    public double radius;
    
    public Polygon(Vector3D a, Vector3D b, Vector3D c, Color color)
    {
        this.a = a;
        this.b = b;
        this.c = c;
        this.color = color;
        this.aIntensity = 0.0;
        this.bIntensity = 0.0;
        this.cIntensity = 0.0;
        this.centroid = new Vector3D(
                (a.x + b.x + c.x) / 3.0,
                (a.y + b.y + c.y) / 3.0,
                (a.z + b.z + c.z) / 3.0
            );
        this.radius = maxDistance(centroid);
    }
    
    public Polygon(double x1, double y1, double z1, double x2, double y2, double z2, 
        double x3, double y3, double z3, Color color)
    {
        this(new Vector3D(x1, y1, z1), 
            new Vector3D(x2, y2, z2), 
            new Vector3D(x3, y3, z3), 
            color);
    }

    public double centroidX()
    {
        return centroid.x;
    }
    
    public double centroidY()
    {
        return centroid.y;
    }
    
    public double centroidZ()
    {
        return centroid.z;
    }
    
    public Vector3D centroid3D()
    {
        return centroid;
    }
    
    public double maxDistance(Vector3D d)
    {
        double first = a.sub(d).abs();
        double second = b.sub(d).abs();
        double third = c.sub(d).abs();
        
        return Math.max(first, Math.max(second, third));
    }
    
    public Vector3D normal()
    {
        Vector3D first = b.sub(a);
        Vector3D second = c.sub(a);
        
        return first.cross(second).norm();
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Vector3D)) {
            return false;
        }
        
        Polygon pol = (Polygon)obj;
        
        return a.equals(pol.a) && b.equals(pol.b) && c.equals(pol.c);
    }
    
    @Override
    public String toString()
    {
        return a + ", " + b + ", " + c;
    }
}
