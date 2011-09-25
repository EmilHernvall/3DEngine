package game;

import java.awt.Color;

public class SphereGenerator
{
    private GraphicsEngine engine;

    public SphereGenerator(GraphicsEngine engine)
    {
        this.engine = engine;
    }

    public void createSphere(Vector3D center, double radius, int steps, Color color)
    {
        for (int t = 0; t < steps; t++) {
            for (int s = 0; s < steps; s++) {
                double theta1 = 2*Math.PI*t/steps;
                double theta2 = 2*Math.PI*(t+1)/steps;
                double phi1 = 2*Math.PI*s/steps;
                double phi2 = 2*Math.PI*(s+1)/steps;
                
                double x11 = center.x + radius*Math.sin(theta1)*Math.cos(phi1);
                double y11 = center.y + radius*Math.sin(theta1)*Math.sin(phi1);
                
                double x12 = center.x + radius*Math.sin(theta1)*Math.cos(phi2);
                double y12 = center.y + radius*Math.sin(theta1)*Math.sin(phi2);
                
                double z1 = center.z + radius*Math.cos(theta1);
                
                double x21 = center.x + radius*Math.sin(theta2)*Math.cos(phi1);
                double y21 = center.y + radius*Math.sin(theta2)*Math.sin(phi1);
                
                double x22 = center.x + radius*Math.sin(theta2)*Math.cos(phi2);
                double y22 = center.y + radius*Math.sin(theta2)*Math.sin(phi2);
                
                double z2 = center.z + radius*Math.cos(theta2);
                
                engine.addPolygon(new Polygon(x11, y11, z1,  x12, y12, z1,  x21, y21, z2,  color));
                engine.addPolygon(new Polygon(x22, y22, z2,  x21, y21, z2,  x12, y12, z1,  color));
            }
        }
    }
}
