package game;

public class Plane
{
    public Vector3D n;
    public double d;
    
    public Plane(Vector3D n, double d)
    {
        this.n = n;
        this.d = d;
    }
    
    public Plane(Vector3D a, Vector3D b, Vector3D c)
    {
        Vector3D first = b.sub(a);
        Vector3D second = c.sub(a);
        
        n = first.cross(second).norm();
        d = n.dot(a);
    }
    
    public double distance(Vector3D a)
    {
        return a.dot(n) - d;
    }
}
