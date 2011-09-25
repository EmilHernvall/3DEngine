package game;

import java.awt.Color;

public class CubeGenerator
{
    private GraphicsEngine engine;

    public CubeGenerator(GraphicsEngine engine)
    {
        this.engine = engine;
    }

    public void createCube(Vector3D a, Vector3D b, Color color)
    {
        // surfaces parallell to the xy plane
        // (a.x, a.y, a.z) - (b.x, b.y, a.z)
        engine.addPolygon(new Polygon(a.x, a.y, a.z,  a.x, b.y, a.z,  b.x, a.y, a.z, color));
        engine.addPolygon(new Polygon(b.x, b.y, a.z,  b.x, a.y, a.z,  a.x, b.y, a.z, color));
        
        // (a.x, a.y, b.z) - (b.x, b.y, b.z)
        engine.addPolygon(new Polygon(a.x, a.y, b.z,  b.x, a.y, b.z,  a.x, b.y, b.z, color));
        engine.addPolygon(new Polygon(b.x, b.y, b.z,  a.x, b.y, b.z,  b.x, a.y, b.z, color));
        
        // surfaces parallell to xz plane
        // (a.x, a.y, a.z) - (b.x, a.y, b.z)
        engine.addPolygon(new Polygon(a.x, a.y, a.z,  b.x, a.y, a.z,  a.x, a.y, b.z, color));
        engine.addPolygon(new Polygon(b.x, a.y, b.z,  a.x, a.y, b.z,  b.x, a.y, a.z, color));
        
        // (a.x, b.y, a.z) - (b.x, b.y, b.z)
        engine.addPolygon(new Polygon(a.x, b.y, a.z,  a.x, b.y, b.z,  b.x, b.y, a.z, color));
        engine.addPolygon(new Polygon(b.x, b.y, b.z,  b.x, b.y, a.z,  a.x, b.y, b.z, color));
        
        // surfaces parallell to yz plane
        // (a.x, a.y, a.z) - (a.x, b.x, b.z)
        engine.addPolygon(new Polygon(a.x, a.y, a.z,  a.x, a.y, b.z,  a.x, b.y, a.z, color));
        engine.addPolygon(new Polygon(a.x, b.y, b.z,  a.x, b.y, a.z,  a.x, a.y, b.z, color));
        
        // (b.x, a.y, a.z) - (b.x, b.x, b.z)
        engine.addPolygon(new Polygon(b.x, a.y, a.z,  b.x, b.y, a.z,  b.x, a.y, b.z, color));
        engine.addPolygon(new Polygon(b.x, b.y, b.z,  b.x, a.y, b.z,  b.x, b.y, a.z, color));
    }
}
