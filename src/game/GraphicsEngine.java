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
    private List<Vertex> vertices;
    private float[] zBuffer;
    
    private float rotX = 0.0f;
    private float rotZ = 0.0f;
    
    private int focalLength = 500;

    public GraphicsEngine(int width, int height)
    {
        this.width = width;
        this.height = height;
        
        this.zBuffer = new float[width*height];
        
        this.lightsources = new ArrayList<LightSource>();
        this.vertices = new ArrayList<Vertex>();
        
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
    
    public void addVertex(Vertex v)
    {
        vertices.add(v);
    }
    
    public void addCube(Vector3D a, Vector3D b, Color color)
    {
        // surfaces parallell to the xy plane
        // (a.x, a.y, a.z) - (b.x, b.y, a.z)
        vertices.add(new Vertex(a.x, a.y, a.z,  b.x, a.y, a.z,  a.x, b.y, a.z, color));
        vertices.add(new Vertex(b.x, b.y, a.z,  b.x, a.y, a.z,  a.x, b.y, a.z, color));
        
        // (a.x, a.y, b.z) - (b.x, b.y, b.z)
        vertices.add(new Vertex(a.x, a.y, b.z,  b.x, a.y, b.z,  a.x, b.y, b.z, color));
        vertices.add(new Vertex(b.x, b.y, b.z,  b.x, a.y, b.z,  a.x, b.y, b.z, color));
        
        // surfaces parallell to xz plane
        // (a.x, a.y, a.z) - (b.x, a.y, b.z)
        vertices.add(new Vertex(a.x, a.y, a.z,  a.x, a.y, b.z,  b.x, a.y, a.z, color));
        vertices.add(new Vertex(b.x, a.y, b.z,  a.x, a.y, b.z,  b.x, a.y, a.z, color));
        
        // (a.x, b.y, a.z) - (b.x, b.y, b.z)
        vertices.add(new Vertex(a.x, b.y, a.z,  a.x, b.y, b.z,  b.x, b.y, a.z, color));
        vertices.add(new Vertex(b.x, b.y, b.z,  a.x, b.y, b.z,  b.x, b.y, a.z, color));
        
        // surfaces parallell to yz plane
        // (a.x, a.y, a.z) - (a.x, b.x, b.z)
        vertices.add(new Vertex(a.x, a.y, a.z,  a.x, a.y, b.z,  a.x, b.y, a.z, color));
        vertices.add(new Vertex(a.x, b.y, b.z,  a.x, a.y, b.z,  a.x, b.y, a.z, color));
        
        // (b.x, a.y, a.z) - (b.x, b.x, b.z)
        vertices.add(new Vertex(b.x, a.y, a.z,  b.x, a.y, b.z,  b.x, b.y, a.z, color));
        vertices.add(new Vertex(b.x, b.y, b.z,  b.x, a.y, b.z,  b.x, b.y, a.z, color));
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
    
    public void drawScene()
    {
        resetZBuffer();
        
        List<Vertex> renderList = new ArrayList<Vertex>();
            
        double intensityNorm = 0.0;
        for (Vertex v : vertices) {
            // calculate color
            Vector3D first = v.b.sub(v.a);
            Vector3D second = v.c.sub(v.a);
            
            Vector3D normal = first.cross(second).norm();
            double intensity = 0.0;
            for (LightSource l : lightsources) {
                Vector3D rel = v.a.sub(l.position);
                intensity += normal.dot(rel) * l.intensity;
            }
            
            intensityNorm = Math.max(intensityNorm, intensity);
        
            // change coordinates to camera position
            Vector3D a = v.a.sub(camera);
            Vector3D b = v.b.sub(camera);
            Vector3D c = v.c.sub(camera);
            
            // apply rotation
            a = a.rotZ(rotZ).rotX(rotX);
            b = b.rotZ(rotZ).rotX(rotX);
            c = c.rotZ(rotZ).rotX(rotX);
            
            renderList.add(new Vertex(a, b, c, v.color, intensity));
        }
        
        for (Vertex v : renderList) {
            v.intensity /= intensityNorm;
            v.color = new Color(
                    colorBoundary(v.color.getRed() * v.intensity),
                    colorBoundary(v.color.getGreen() * v.intensity),
                    colorBoundary(v.color.getBlue() * v.intensity)
                );
                
            drawVertex(v);
        }
    }
    
    private int colorBoundary(double v)
    {
        if (v < 0) {
            return colorBoundary(-v);
        } else if (v > 0xFF) {
            return 0xFF;
        } else {
            return (int)v;
        }
    }
    
    private void drawVertex(Vertex v)
    {
        Vector3D a = v.a;
        Vector3D b = v.b;
        Vector3D c = v.c;
        
        // hide surfaces behind the camera
        if (a.y <= 1 || b.y <= 1 || c.y < 1) {
            return;
        }
        
        // apply perspective
        int x1 = (int)(a.x * focalLength/a.y) + width / 2;
        int y1 = (int)(a.z * focalLength/a.y) + height / 2;
        int z1 = (int)a.y;
        
        int x2 = (int)(b.x * focalLength/b.y) + width / 2;
        int y2 = (int)(b.z * focalLength/b.y) + height / 2;
        int z2 = (int)b.y;
        
        int x3 = (int)(c.x * focalLength/c.y) + width / 2;
        int y3 = (int)(c.z * focalLength/c.y) + height / 2;
        int z3 = (int)c.y;
        
        // draw triangle
        int tmp;
        if (y1 > y2) { 
            tmp = x1; x1 = x2; x2 = tmp;
            tmp = y1; y1 = y2; y2 = tmp;
            tmp = z1; z1 = z2; z2 = tmp;
        }
        if (y1 > y3) { 
            tmp = x1; x1 = x3; x3 = tmp;
            tmp = y1; y1 = y3; y3 = tmp;
            tmp = z1; z1 = z3; z3 = tmp;
        }
        if (y2 > y3) {
            tmp = x2; x2 = x3; x3 = tmp;
            tmp = y2; y2 = y3; y3 = tmp;
            tmp = z2; z2 = z3; z3 = tmp;
        }
        
        double[] x12 = linearInterpolation(y1, x1, y2, x2);
        double[] x23 = linearInterpolation(y2, x2, y3, x3);
        double[] x13 = linearInterpolation(y1, x1, y3, x3);
        
        double[] z12 = linearInterpolation(y1, z1, y2, z2);
        double[] z23 = linearInterpolation(y2, z2, y3, z3);
        double[] z13 = linearInterpolation(y1, z1, y3, z3);

        for (int y = y1; y < y2; y++) {
            drawSegment((int)x12[y-y1], y, (int)z12[y-y1], (int)x13[y-y1], y, (int)z13[y-y1], v.color);
        }

        for (int y = y2; y < y3; y++) {
            drawSegment((int)x23[y-y2], y, (int)z23[y-y2], (int)x13[y-y1], y, (int)z13[y-y1], v.color);
        }
    }
    
    private void drawSegment(int x0, int y0, int z0, int x1, int y1, int z1, Color color)
    {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        int tmp;
        if (dx > dy) {
            if (x0 > x1) {
                tmp = x0; x0 = x1; x1 = tmp;
                tmp = y0; y0 = y1; y1 = tmp;
            }

            double[] y_values = linearInterpolation(x0, y0, x1, y1);
            double[] z_values = linearInterpolation(x0, z0, x1, z1);
            for (int x = x0; x < x1; x++) {
                putPixel(x, (int)y_values[x-x0], (float)z_values[x-x0], color);
            }
        }
        else {
            if (y0 > y1) {
                tmp = x0; x0 = x1; x1 = tmp;
                tmp = y0; y0 = y1; y1 = tmp;
            }

            double[] x_values = linearInterpolation(y0, x0, y1, x1);
            double[] z_values = linearInterpolation(y0, z0, y1, z1);
            for (int y = y0; y < y1; y++) {
                putPixel((int)x_values[y-y0], y, (float)z_values[y-y0], color);
            }
        }
    }
    
    private void putPixel(int x, int y, float z, Color color)
    {
        if (x < 0 || x >= width) {
            return;
        }
        if (y < 0 || y >= height) {
            return;
        }
    
        if (z < zBuffer[width*y + x]) {
            surface.putPixel(x, y, color);
            zBuffer[width*y + x] = z;
        }
    }
    
    private double[] linearInterpolation(int t0, double f0, int t1, double f1)
    {
        int nSteps = Math.abs(t1 - t0);
        if (nSteps == 0) {
            return new double[] { f0 };
        } 
        
        double fSlope = (f1 - f0) / nSteps;
        
        double f = f0;
        double[] lValues = new double[nSteps];
        for (int i = 0; i < nSteps; i++) {
            lValues[i] = f;
            f += fSlope;
        }
        
        return lValues;
    }
}
