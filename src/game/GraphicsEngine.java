package game;

import java.util.*;
import java.util.List;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GraphicsEngine
{
    Surface surface;

    int width, height;
    Vector3D camera, observer;
    List<LightSource> lightsources;
    List<Polygon> polygons;
    Map<Vector3D, List<Polygon>> vertices;
    float[] zBuffer;
    
    float rotX = 0.0f;
    float rotZ = 0.0f;
    
    double ambientIntensity = 1.0;
    int focalLength = 500;

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
    
    public void setSurface(Surface surface) { this.surface = surface; }
    public Surface getSurface() { return surface; }
    
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    
    public Vector3D getCamera() { return camera; }
    public void setCamera(Vector3D camera) { this.camera = camera; }
    
    public float getRotX() { return rotX;  }
    public void setRotX(float v) { this.rotX = v; }

    public float getRotZ() { return rotZ; }
    public void setRotZ(float v) { this.rotZ = v; }
    
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
    
    public void preprocessScene()
    {
        double intensityNorm = 0.0;
        for (Polygon v : polygons) {
            // calculate color
            v.aIntensity = calcIntensity(v.a);
            intensityNorm = Math.max(intensityNorm, v.aIntensity);
            v.bIntensity = calcIntensity(v.b);
            intensityNorm = Math.max(intensityNorm, v.bIntensity);
            v.cIntensity = calcIntensity(v.c);
            intensityNorm = Math.max(intensityNorm, v.cIntensity);
        }
        
        // normalize intensities
        for (Polygon v : polygons) {
            v.aIntensity /= intensityNorm;
            v.bIntensity /= intensityNorm;
            v.cIntensity /= intensityNorm;
        }    
    }
    
    private List<Plane> createFrustum()
    {
        double nearDist = focalLength;
        double farDist = 4*focalLength;
        
        double farWidth = width * farDist/nearDist;
        double farHeight = height * farDist/nearDist;
        
        Vector3D cameraDir = new Vector3D(0, 1, 0);
        cameraDir = cameraDir.rotX(-rotX).rotZ(-rotZ).norm();
        
        Vector3D up = new Vector3D(0, 0, 1);
        up = up.rotX(-rotX).rotZ(-rotZ).norm();
        
        Vector3D right = new Vector3D(1, 0, 0);
        right = right.rotX(-rotX).rotZ(-rotZ).norm();
        
        Vector3D nearCentroid = camera.add(cameraDir.mul(nearDist));
        Vector3D farCentroid = camera.add(cameraDir.mul(farDist));
        
        Vector3D ntl = nearCentroid.add(up.mul(height/2.0)).add(right.mul(-width/2.0));
        Vector3D ntr = nearCentroid.add(up.mul(height/2.0)).add(right.mul(width/2.0));
        Vector3D nbl = nearCentroid.add(up.mul(-height/2.0)).add(right.mul(-width/2.0));
        Vector3D nbr = nearCentroid.add(up.mul(-height/2.0)).add(right.mul(width/2.0));
        
        Vector3D ftl = farCentroid.add(up.mul(farHeight/2.0)).add(right.mul(-farWidth/2.0));
        Vector3D ftr = farCentroid.add(up.mul(farHeight/2.0)).add(right.mul(farWidth/2.0));
        Vector3D fbl = farCentroid.add(up.mul(-farHeight/2.0)).add(right.mul(-farWidth/2.0));
        Vector3D fbr = farCentroid.add(up.mul(-farHeight/2.0)).add(right.mul(farWidth/2.0));
        
        List<Plane> frustum = new ArrayList<Plane>();
        
        frustum.add(new Plane(ntl, nbl, ftl)); // left plane
        frustum.add(new Plane(ntr, ftr, nbr)); // right plane
        frustum.add(new Plane(ntr, ntl, ftr)); // top plane
        frustum.add(new Plane(nbr, fbr, nbl)); // bottom plane
        //frustum.add(new Plane(nbl, ntl, nbr)); // near plane
        //frustum.add(new Plane(fbl, fbr, ftl)); // far plane
        
        return frustum;
    }
    
    public void drawScene()
    {
        long time = System.currentTimeMillis();
        
        resetZBuffer();
        
        List<Plane> frustum = createFrustum();
        
        int drawn = 0;
        List<Polygon> renderList = new ArrayList<Polygon>();
        for (Polygon p : polygons) {
            List<Polygon> all = new ArrayList<Polygon>();
            all.add(p);
            
            // hide polygons outside of the statum
            boolean visible = true;
            for (Plane plane : frustum) {
                
                List<Polygon> newPolygons = new ArrayList<Polygon>();
                for (Polygon current : all) {
                    double dist1 = plane.distance(current.a);
                    double dist2 = plane.distance(current.b);
                    double dist3 = plane.distance(current.c);
                    
                    // the polygon is at least partly outside of the frustum and needs
                    // clipping
                    if (dist1 < 0 || dist2 < 0 || dist3 < 0) {
                        Polygon[] clippedPolygons = MathUtils.clip(plane, current);
                        if (clippedPolygons == null) {
                            continue;
                        }
                        for (Polygon newPolygon : clippedPolygons) {
                            newPolygons.add(newPolygon);
                        }
                    }
                    // the polygon resides fully inside of the frustum
                    else {
                        newPolygons.add(current);
                    }
                }
                all = newPolygons;
            }

            renderList.addAll(all);
        }
        
        for (Polygon p : renderList) {
            if (drawPolygon(p)) {
                drawn++;
            }
        }
        
        time = System.currentTimeMillis() - time;
        
        System.out.println("drew " + drawn + " polygons in " + time + " ms (" + (1000/(time+1)) + " fps).");
    }
    
    private boolean drawPolygon(Polygon v)
    {
        // hide polygons facing away from the camera
        Vector3D normal = v.normal();
        Vector3D rel = camera.sub(v.a);
        double d = normal.dot(rel);
        if (d < 0) {
            return false;
        }
    
        // change coordinates to camera position
        Vector3D a = v.a.sub(camera);
        Vector3D b = v.b.sub(camera);
        Vector3D c = v.c.sub(camera);
        
        // apply rotation
        a = a.rotZ(rotZ).rotX(rotX);
        b = b.rotZ(rotZ).rotX(rotX);
        c = c.rotZ(rotZ).rotX(rotX);
        
        Polygon p = new Polygon(a, b, c, v.color);
        p.aIntensity = v.aIntensity;
        p.bIntensity = v.bIntensity;
        p.cIntensity = v.cIntensity;
        
        // hide surfaces behind the camera
        if (a.y <= 1 || b.y <= 1 || c.y < 1) {
            return false;
        }
        
        // apply perspective. x and y are the 2d coordinates here,
        // while z just indicates depth from now on.
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

        // the drawing is really done in 2d after the perspective transformation
        // but we interpolate the depth to be able to do z-buffering.
        double[] z12 = MathUtils.linearInterpolation(y1, z1, y2, z2);
        double[] z23 = MathUtils.linearInterpolation(y2, z2, y3, z3);
        double[] z13 = MathUtils.linearInterpolation(y1, z1, y3, z3);
        
        // this is the gouraud shading, which we do by interpolating the
        // intensities of each vertex across the triangle
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
