package game;

import java.util.*;
import java.util.List;

import javax.swing.*;
import java.awt.*;

public class DebugWindow extends JFrame
{
    GraphicsEngine engine;
    double scale = 0.2;
    int d = 50;

    public DebugWindow(GraphicsEngine engine)
    {
        super();
        
        this.engine = engine;
        
        setResizable(false);
        setTitle("Debug");
        setSize(engine.getWidth(), engine.getHeight());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    public int transformX(double x)
    {
        return (int)(scale*x + engine.width/2);
    }
    
    public int transformY(double y)
    {
        return (int)(engine.height/2 - scale*y);
    }
    
    @Override
    public void paint(Graphics graphics)
    {
        Graphics2D g = (Graphics2D)graphics;
        
        List<Polygon> renderSet = new ArrayList<Polygon>();
        Collections.sort(renderSet, new Comparator<Polygon>() {
                public int compare(Polygon a, Polygon b) {
                    double d = a.centroidY() - b.centroidY();
                    return (int)d;
                }
            });
        
        renderSet.addAll(engine.polygons);
        
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, engine.width, engine.height);
        
        for (Polygon p : renderSet) {
            int[] x = { transformX(p.a.x), transformX(p.b.x), transformX(p.c.x) };
            int[] y = { transformY(p.a.y), transformY(p.b.y), transformY(p.c.y) };
            
            g.setColor(p.color);
            g.drawPolygon(x, y, 3);
        }
        
        Vector3D camera = engine.camera;
        int cameraX = transformX(camera.x);
        int cameraY = transformY(camera.y);
        
        g.fillArc(cameraX - 5, cameraY - 5, 10, 10, 0, 360);
        
        Vector3D cameraDir = new Vector3D(0, 1, 0);
        cameraDir = cameraDir.rotX(-engine.rotX).rotZ(-engine.rotZ).norm();
        
        Vector3D cameraTo = camera.add(cameraDir.mul(40));
        int cameraToX = transformX(cameraTo.x);
        int cameraToY = transformY(cameraTo.y);
        
        g.drawLine(cameraX, cameraY, cameraToX, cameraToY);
        
        Vector3D up = new Vector3D(0, 0, 1);
        Vector3D right = new Vector3D(1, 0, 0);
        up = up.rotX(-engine.rotX).rotZ(-engine.rotZ).norm();
        right = right.rotX(-engine.rotX).rotZ(-engine.rotZ).norm();
        
        double nearDist = engine.focalLength;
        double farDist = 2*nearDist;
        
        double farWidth = engine.width * farDist/nearDist;
        double farHeight = engine.height * farDist/nearDist;
        
        Vector3D nearCentroid = camera.add(cameraDir.mul(nearDist));
        Vector3D farCentroid = camera.add(cameraDir.mul(farDist));
        
        Vector3D ntl = nearCentroid.add(up.mul(engine.height/2.0)).add(right.mul(-engine.width/2.0));
        Vector3D ntr = nearCentroid.add(up.mul(engine.height/2.0)).add(right.mul(engine.width/2.0));
        Vector3D ftl = farCentroid.add(up.mul(farHeight/2.0)).add(right.mul(-farWidth/2.0));
        Vector3D ftr = farCentroid.add(up.mul(farHeight/2.0)).add(right.mul(farWidth/2.0));
        
        drawPoint(g, nearCentroid);
        drawPoint(g, farCentroid);
        
        drawPoint(g, ntl);
        drawPoint(g, ntr);
        drawPoint(g, ftl);
        drawPoint(g, ftr);
        
        //System.out.println("debug: " + renderSet.size() + " polygons");
    }
    
    private void drawPoint(Graphics2D g, Vector3D v)
    {
        int x = transformX(v.x);
        int y = transformY(v.y);
        
        g.fillArc(x - 5, y - 5, 10, 10, 0, 360);
    }
}
