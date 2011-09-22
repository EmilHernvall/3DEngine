package game;

import java.util.*;
import java.util.List;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GraphicsEngine
{
    private Surface surface;

    private int width, height;
    private Vector3D camera, observer;
    private List<LightSource> lightsources;
    private List<Polygon> polygons;
    private Map<Vector3D, List<Polygon>> vertices;
    private float[] zBuffer;
    
    private float rotX = 0.0f;
    private float rotZ = 0.0f;
    
    private double ambientIntensity = 1.0;
    private int focalLength = 500;

    public GraphicsEngine(int width, int height)
    {
        this.width = width;
        this.height = height;
        
        this.zBuffer = new float[width*height];
        
        this.lightsources = new ArrayList<LightSource>();
        this.polygons = new ArrayList<Polygon>();
        this.vertices = new HashMap<Vector3D, List<Polygon>>();
        
        this.observer = new Vector3D(0, 0, -5);
    }
    
    public void setSurface(Surface surface)
    {
        this.surface = surface;
    }
    
    public Surface getSurface()
    {
        return surface;
    }
    
    public int getWidth()
    {
        return width;
    }
    
    public int getHeight()
    {
        return height;
    }
    
    public void setCamera(Vector3D camera)
    {
        this.camera = camera;
    }
    
    public Vector3D getCamera()
    {
        return camera;
    }
    
    public float getRotX()
    {
        return rotX;
    }
    
    public float getRotZ()
    {
        return rotZ;
    }
    
    public void setRotX(float v)
    {
        this.rotX = v;
    }
    
    public void setRotZ(float v)
    {
        this.rotZ = v;
    }
    
    public void addLightSource(LightSource l)
    {
        lightsources.add(l);
    }
    
    public void addPolygon(Polygon p)
    {
        polygons.add(p);
        addVertice(p.a, p);
        addVertice(p.b, p);
        addVertice(p.c, p);
    }
    
    private void addVertice(Vector3D edge, Polygon p)
    {
        List<Polygon> edges = vertices.get(edge);
        if (edges == null) {
            edges = new ArrayList<Polygon>();
            vertices.put(edge, edges);
        }
        edges.add(p);
    }
    
    public void addCube(Vector3D a, Vector3D b, Color color)
    {
        // surfaces parallell to the xy plane
        // (a.x, a.y, a.z) - (b.x, b.y, a.z)
        addPolygon(new Polygon(a.x, a.y, a.z,  b.x, a.y, a.z,  a.x, b.y, a.z, -1.0, color));
        addPolygon(new Polygon(b.x, b.y, a.z,  b.x, a.y, a.z,  a.x, b.y, a.z, 1.0, color));
        
        // (a.x, a.y, b.z) - (b.x, b.y, b.z)
        addPolygon(new Polygon(a.x, a.y, b.z,  b.x, a.y, b.z,  a.x, b.y, b.z, 1.0, color));
        addPolygon(new Polygon(b.x, b.y, b.z,  b.x, a.y, b.z,  a.x, b.y, b.z, -1.0, color));
        
        // surfaces parallell to xz plane
        // (a.x, a.y, a.z) - (b.x, a.y, b.z)
        addPolygon(new Polygon(a.x, a.y, a.z,  a.x, a.y, b.z,  b.x, a.y, a.z, -1.0, color));
        addPolygon(new Polygon(b.x, a.y, b.z,  a.x, a.y, b.z,  b.x, a.y, a.z, 1.0, color));
        
        // (a.x, b.y, a.z) - (b.x, b.y, b.z)
        addPolygon(new Polygon(a.x, b.y, a.z,  a.x, b.y, b.z,  b.x, b.y, a.z, 1.0, color));
        addPolygon(new Polygon(b.x, b.y, b.z,  a.x, b.y, b.z,  b.x, b.y, a.z, -1.0, color));
        
        // surfaces parallell to yz plane
        // (a.x, a.y, a.z) - (a.x, b.x, b.z)
        addPolygon(new Polygon(a.x, a.y, a.z,  a.x, a.y, b.z,  a.x, b.y, a.z, 1.0, color));
        addPolygon(new Polygon(a.x, b.y, b.z,  a.x, a.y, b.z,  a.x, b.y, a.z, -1.0, color));
        
        // (b.x, a.y, a.z) - (b.x, b.x, b.z)
        addPolygon(new Polygon(b.x, a.y, a.z,  b.x, a.y, b.z,  b.x, b.y, a.z, -1.0, color));
        addPolygon(new Polygon(b.x, b.y, b.z,  b.x, a.y, b.z,  b.x, b.y, a.z, 1.0, color));
    }
    
    public void addSphere(Vector3D center, double radius, int steps, Color color)
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
                
                addPolygon(new Polygon(x11, y11, z1,  x12, y12, z1,  x21, y21, z2,  1.0, color));
                addPolygon(new Polygon(x22, y22, z2,  x12, y12, z1,  x21, y21, z2,  -1.0, color));
            }
        }
    }
    
    public void triggerRedraw()
    {
        surface.repaint();
    }
    
    private void resetZBuffer()
    {
        for (int i = 0; i < zBuffer.length; i++) {
            zBuffer[i] = Float.MAX_VALUE;
        }
    }
    
    private double calcIntensity(Vector3D v)
    {
        // average the normals of all edges that intersect at
        // this vertice (gouraud shading)
        List<Polygon> polygons = vertices.get(v);
        Vector3D normal = null;
        for (Polygon p : polygons) {
            Vector3D n = p.normal();
            if (Double.isNaN(n.x)) {
                continue;
            }
            
            if (normal == null) {
                normal = n;
            } else {
                normal = normal.add(n);
            }
        }
        normal = normal.mul(1.0/polygons.size());
    
        double intensity = ambientIntensity;
        for (LightSource l : lightsources) {
            Vector3D rel = v.sub(l.position);
            
            double i = normal.dot(rel);
            if (!Double.isNaN(i) && i > 0) {
                intensity += i * l.intensity;
            }
        }
        
        return Math.abs(intensity);
    }
    
    public void drawScene()
    {
        long time = System.currentTimeMillis();
        
        resetZBuffer();
        
        List<Polygon> renderList = new ArrayList<Polygon>();
            
        double intensityNorm = 0.0;
        for (Polygon v : polygons) {
        
            // hide polygons facing away from the camera
            Vector3D normal = v.normal();
            Vector3D rel = camera.sub(v.a);
            double d = normal.dot(rel);
            if (d < 0) {
                continue;
            }
        
            // calculate color
            double aIntensity = calcIntensity(v.a);
            intensityNorm = Math.max(intensityNorm, aIntensity);
            double bIntensity = calcIntensity(v.b);
            intensityNorm = Math.max(intensityNorm, bIntensity);
            double cIntensity = calcIntensity(v.c);
            intensityNorm = Math.max(intensityNorm, cIntensity);
            
            // change coordinates to camera position
            Vector3D a = v.a.sub(camera);
            Vector3D b = v.b.sub(camera);
            Vector3D c = v.c.sub(camera);
            
            // apply rotation
            a = a.rotZ(rotZ).rotX(rotX);
            b = b.rotZ(rotZ).rotX(rotX);
            c = c.rotZ(rotZ).rotX(rotX);
            
            Polygon p = new Polygon(a, b, c, v.dir, v.color);
            p.aIntensity = aIntensity;
            p.bIntensity = bIntensity;
            p.cIntensity = cIntensity;
            renderList.add(p);
        }
        
        // normalize colors and draw
        int drawn = 0;
        for (Polygon v : renderList) {
            v.aIntensity /= intensityNorm;
            v.bIntensity /= intensityNorm;
            v.cIntensity /= intensityNorm;
            
            //System.out.println(v.aIntensity + ", " + v.bIntensity + ", " + v.cIntensity);
                
            if (drawPolygon(v)) {
                drawn++;
            }
        }
        
        time = System.currentTimeMillis() - time;
        
        System.out.println("drew " + drawn + " polygons in " + time + " ms.");
    }
    
    private boolean drawPolygon(Polygon v)
    {
        Vector3D a = v.a;
        Vector3D b = v.b;
        Vector3D c = v.c;
        
        // hide surfaces behind the camera
        if (a.y <= 1 || b.y <= 1 || c.y < 1) {
            return false;
        }
        
        // apply perspective
        int x1 = (int)(a.x * focalLength/a.y) + width / 2;
        int y1 = (int)(a.z * focalLength/a.y) + height / 2;
        int z1 = (int)a.y;
        double i1 = v.aIntensity;
        
        int x2 = (int)(b.x * focalLength/b.y) + width / 2;
        int y2 = (int)(b.z * focalLength/b.y) + height / 2;
        int z2 = (int)b.y;
        double i2 = v.bIntensity;
        
        int x3 = (int)(c.x * focalLength/c.y) + width / 2;
        int y3 = (int)(c.z * focalLength/c.y) + height / 2;
        int z3 = (int)c.y;
        double i3 = v.cIntensity;
        
        if (
                ((x1 < 0 || x1 > width) && (y1 < 0 || y1 > height)) ||
                ((x2 < 0 || x2 > width) && (y2 < 0 || y2 > height)) || 
                ((x3 < 0 || x3 > width) && (y3 < 0 || y3 > height))
            ) {
            
            return false;
        }
        
        // draw triangle
        int tmp;
        double tmp2;
        if (y1 > y2) { 
            tmp = x1; x1 = x2; x2 = tmp;
            tmp = y1; y1 = y2; y2 = tmp;
            tmp = z1; z1 = z2; z2 = tmp;
            tmp2 = i1; i1 = i2; i2 = tmp2;
        }
        if (y1 > y3) { 
            tmp = x1; x1 = x3; x3 = tmp;
            tmp = y1; y1 = y3; y3 = tmp;
            tmp = z1; z1 = z3; z3 = tmp;
            tmp2 = i1; i1 = i3; i3 = tmp2;
        }
        if (y2 > y3) {
            tmp = x2; x2 = x3; x3 = tmp;
            tmp = y2; y2 = y3; y3 = tmp;
            tmp = z2; z2 = z3; z3 = tmp;
            tmp2 = i2; i2 = i3; i3 = tmp2;
        }
        
        double[] x12 = MathUtils.linearInterpolation(y1, x1, y2, x2);
        double[] x23 = MathUtils.linearInterpolation(y2, x2, y3, x3);
        double[] x13 = MathUtils.linearInterpolation(y1, x1, y3, x3);
        
        double[] z12 = MathUtils.linearInterpolation(y1, z1, y2, z2);
        double[] z23 = MathUtils.linearInterpolation(y2, z2, y3, z3);
        double[] z13 = MathUtils.linearInterpolation(y1, z1, y3, z3);
        
        double[] i12 = MathUtils.linearInterpolation(y1, i1, y2, i2);
        double[] i23 = MathUtils.linearInterpolation(y2, i2, y3, i3);
        double[] i13 = MathUtils.linearInterpolation(y1, i1, y3, i3);

        for (int y = y1; y < y2; y++) {
            drawSegment(
                    (int)x12[y-y1], y, (int)z12[y-y1], i12[y-y1],
                    (int)x13[y-y1], y, (int)z13[y-y1], i13[y-y1],
                    v.color
                );
        }

        for (int y = y2; y < y3; y++) {
            drawSegment(
                    (int)x23[y-y2], y, (int)z23[y-y2], i23[y-y2],
                    (int)x13[y-y1], y, (int)z13[y-y1], i13[y-y1],
                    v.color
                );
        }
        
        return true;
    }
    
    private void drawSegment(int x0, int y0, int z0, double i0, int x1, int y1, int z1, double i1, Color color)
    {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        int tmp;
        double tmp2;
        if (dx > dy) {
            if (x0 > x1) {
                tmp = x0; x0 = x1; x1 = tmp;
                tmp = y0; y0 = y1; y1 = tmp;
                tmp2 = i0; i0 = i1; i1 = tmp2;
            }

            double[] y_values = MathUtils.linearInterpolation(x0, y0, x1, y1);
            double[] z_values = MathUtils.linearInterpolation(x0, z0, x1, z1);
            double[] i_values = MathUtils.linearInterpolation(x0, i0, x1, i1);
            for (int x = x0; x < x1; x++) {
                putPixel(x, (int)y_values[x-x0], (float)z_values[x-x0], color, i_values[x-x0]);
            }
        }
        else {
            if (y0 > y1) {
                tmp = x0; x0 = x1; x1 = tmp;
                tmp = y0; y0 = y1; y1 = tmp;
                tmp2 = i0; i0 = i1; i1 = tmp2;
            }

            double[] x_values = MathUtils.linearInterpolation(y0, x0, y1, x1);
            double[] z_values = MathUtils.linearInterpolation(y0, z0, y1, z1);
            double[] i_values = MathUtils.linearInterpolation(y0, i0, y1, i1);
            for (int y = y0; y < y1; y++) {
                putPixel((int)x_values[y-y0], y, (float)z_values[y-y0], color, i_values[y-y0]);
            }
        }
    }
    
    private void putPixel(int x, int y, float z, Color color, double intensity)
    {
        if (x < 0 || x >= width) {
            return;
        }
        if (y < 0 || y >= height) {
            return;
        }
    
        if (z < zBuffer[width*y + x]) {
            Color newColor = new Color(
                    (int)(color.getRed() * intensity),
                    (int)(color.getGreen() * intensity),
                    (int)(color.getBlue() * intensity)
                );

            surface.putPixel(x, y, newColor);
            zBuffer[width*y + x] = z;
        }
    }
}
