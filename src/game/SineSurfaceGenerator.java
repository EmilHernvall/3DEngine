package game;

import java.awt.Color;

public class SineSurfaceGenerator
{
    private GraphicsEngine engine;

    public SineSurfaceGenerator(GraphicsEngine engine)
    {
        this.engine = engine;
    }

    public void createSineFloor(Vector3D corner, double amplitude, double period, int steps, double scale, Color color)
    {
        for (int s = 0; s < steps; s++) {
            for (int t = 0; t < steps; t++) {
                double theta1 = 2*Math.PI*t/period;
                double theta2 = 2*Math.PI*(t+1)/period;
                double phi1 = 2*Math.PI*s/period;
                double phi2 = 2*Math.PI*(s+1)/period;
                
                double x1 = corner.x + t*scale;
                double y1 = corner.y + s*scale;
                double z11 = corner.z + amplitude*Math.sin(theta1)*Math.sin(phi1);
                double z12 = corner.z + amplitude*Math.sin(theta1)*Math.sin(phi2);
                
                double x2 = corner.x + (t + 1)*scale;
                double y2 = corner.y + (s + 1)*scale;
                double z21 = corner.z + amplitude*Math.sin(theta2)*Math.sin(phi1);
                double z22 = corner.z + amplitude*Math.sin(theta2)*Math.sin(phi2);
                
                engine.addPolygon(new Polygon(x1, y1, z11,  x1, y2, z12,  x2, y1, z21,  color));
                engine.addPolygon(new Polygon(x2, y2, z22,  x2, y1, z21,  x1, y2, z12,  color));
            }
        }
    }
}
