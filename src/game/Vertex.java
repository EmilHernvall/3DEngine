package game;

import java.awt.*;

public class Vertex
{
    public Point3D a, b, c;
    
    public Vertex(Point3D a, Point3D b, Point3D c)
    {
        this.a = a;
        this.b = b;
        this.c = c;
    }
    
    public Vertex(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3)
    {
        this.a = new Point3D(x1, y1, z1);
        this.b = new Point3D(x2, y2, z2);
        this.c = new Point3D(x3, y3, z3);
    }
}
