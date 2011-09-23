package game;

public class Vector3D
{
    public double x, y, z;

    public Vector3D(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector3D add(Vector3D b)
    {
        if (b == null) {
            return this;
        }
        
        return new Vector3D(x + b.x, y + b.y, z + b.z);
    }
    
    public Vector3D sub(Vector3D b)
    {
        if (b == null) {
            return this;
        }
    
        return new Vector3D(x - b.x, y - b.y, z - b.z);
    }
    
    public Vector3D mul(double c)
    {
        return new Vector3D(c*x, c*y, c*z);
    }
    
    public Vector3D rotX(float theta)
    {
        return new Vector3D(
                x,
                y * Math.cos(theta) - z * Math.sin(theta),
                y * Math.sin(theta) + z * Math.cos(theta)
            );
    }
    
    public Vector3D rotY(float theta)
    {
        return new Vector3D(
                x * Math.cos(theta) + z * Math.sin(theta),
                y,
                -x * Math.sin(theta) + z * Math.cos(theta)
            );
    }
    
    public Vector3D rotZ(float theta)
    {
        return new Vector3D(
                x * Math.cos(theta) - y * Math.sin(theta),
                x * Math.sin(theta) + y * Math.cos(theta),
                z
            );
    }
    
    public Vector3D cross(Vector3D b)
    {
        return new Vector3D(
                y*b.z - z*b.y,
                z*b.x - x*b.z,
                x*b.y - y*b.x
            );
    }
    
    public double dot(Vector3D b)
    {
        return x*b.x + y*b.y + z*b.z;
    }
    
    public double abs()
    {
        return Math.sqrt(x*x + y*y + z*z);
    }
    
    public Vector3D norm()
    {
        return mul(1.0/abs());
    }
    
    public double distance(Vector3D b)
    {
        Vector3D e = sub(b).norm();
        return (b.x - x)/e.x;
    }
    
    public boolean equals(Vector3D vec)
    {
        double eps = 0.0001;
        return 
            Math.abs(vec.x - x) < eps && 
            Math.abs(vec.y - y) < eps &&
            Math.abs(vec.z - z) < eps;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Vector3D)) {
            return false;
        }
        
        Vector3D b = (Vector3D)obj;
        
        return b.x == x && b.y == y && b.z == z;
    }
    
    @Override
    public int hashCode()
    {
        int code;
        long v;
        
        code = 17;
        
        v = Double.doubleToLongBits(x);
        code = 31*code + (int)(v ^ (v >> 32));
        
        v = Double.doubleToLongBits(y);
        code = 31*code + (int)(v ^ (v >> 32));
        
        v = Double.doubleToLongBits(z);
        code = 31*code + (int)(v ^ (v >> 32));
        
        return code;
    }
    
    @Override
    public String toString()
    {
        return String.format("<%.2f, %.2f, %.2f>", x, y, z);
    }
}
