package game;

public class MathUtils
{
    public static double[] linearInterpolation(int t0, double f0, int t1, double f1)
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
    
    private static Polygon[] clipOnePoint(Plane plane, Polygon p)
    {
        Vector3D[] inside = new Vector3D[2];
        double[] insideIntensity = new double[2];
        Vector3D[] newPoints = new Vector3D[2];
        Vector3D outside;
        double outsideIntensity;
    
        if (plane.distance(p.a) < 0) {
            outside = p.a;
            inside[0] = p.b;
            inside[1] = p.c;
            outsideIntensity = p.aIntensity;
            insideIntensity[0] = p.bIntensity;
            insideIntensity[1] = p.cIntensity;
        } else if (plane.distance(p.b) < 0) {
            outside = p.b;
            inside[0] = p.a;
            inside[1] = p.c;
            outsideIntensity = p.bIntensity;
            insideIntensity[0] = p.aIntensity;
            insideIntensity[1] = p.cIntensity;
        } else {
            outside = p.c;
            inside[0] = p.a;
            inside[1] = p.b;
            outsideIntensity = p.cIntensity;
            insideIntensity[0] = p.aIntensity;
            insideIntensity[1] = p.bIntensity;
        }
        
        newPoints[0] = plane.intersectLine(inside[0], outside);
        newPoints[1] = plane.intersectLine(inside[1], outside);
        
        Polygon[] result = new Polygon[2];
        
        result[0] = new Polygon(inside[0], inside[1], newPoints[0], p.color);
        result[0].aIntensity = insideIntensity[0];
        result[0].bIntensity = insideIntensity[1];
        result[0].cIntensity = interpolateIntensity(inside[0], insideIntensity[0], 
            outside, outsideIntensity, newPoints[0]);
            
        result[1] = new Polygon(inside[1], newPoints[0], newPoints[1], p.color);
        result[1].aIntensity = insideIntensity[1];
        result[1].bIntensity = interpolateIntensity(inside[0], insideIntensity[0], 
            outside, outsideIntensity, newPoints[0]);
        result[1].cIntensity = interpolateIntensity(inside[1], insideIntensity[1], 
            outside, outsideIntensity, newPoints[1]);
        
        fixNormal(result[0], p);
        fixNormal(result[1], p);
    
        return result;
    }
    
    private static Polygon clipTwoPoints(Plane plane, Polygon p)
    {
        Vector3D inside;
        double insideIntensity;
        Vector3D[] outside = new Vector3D[2];
        Vector3D[] newPoints = new Vector3D[2];
        double[] outsideIntensity = new double[2];
    
        if (plane.distance(p.a) > 0) {
            inside = p.a;
            outside[0] = p.b;
            outside[1] = p.c;
            insideIntensity = p.aIntensity;
            outsideIntensity[0] = p.bIntensity;
            outsideIntensity[1] = p.cIntensity;
        } else if (plane.distance(p.b) > 0) {
            inside = p.b;
            outside[0] = p.a;
            outside[1] = p.c;
            insideIntensity = p.bIntensity;
            outsideIntensity[0] = p.aIntensity;
            outsideIntensity[1] = p.cIntensity;
        } else {
            inside = p.c;
            outside[0] = p.a;
            outside[1] = p.b;
            insideIntensity = p.cIntensity;
            outsideIntensity[0] = p.aIntensity;
            outsideIntensity[1] = p.bIntensity;
        }
        
        newPoints[0] = plane.intersectLine(inside, outside[0]);
        newPoints[1] = plane.intersectLine(inside, outside[1]);
        
        Polygon newPolygon = new Polygon(inside, newPoints[0], newPoints[1], p.color);
        newPolygon.aIntensity = insideIntensity;
        newPolygon.bIntensity = interpolateIntensity(inside, insideIntensity, 
            outside[0], outsideIntensity[0], newPoints[0]);
        newPolygon.cIntensity = interpolateIntensity(inside, insideIntensity, 
            outside[1], outsideIntensity[1], newPoints[1]);
            
        fixNormal(newPolygon, p);
        
        return newPolygon;
    }
    
    public static Polygon[] clip(Plane plane, Polygon p)
    {
        int outside = 0;
        if (plane.distance(p.a) < 0) {
            outside++;
        }
        if (plane.distance(p.b) < 0) {
            outside++;
        }        
        if (plane.distance(p.c) < 0) {
            outside++;
        }
        
        switch (outside) {
            case 0: 
                return new Polygon[] { p };
            case 1: 
                return clipOnePoint(plane, p);
            case 2: 
                return new Polygon[] { clipTwoPoints(plane, p) };
            case 3: 
            default: 
                return null;
        }
    }
    
    private static void fixNormal(Polygon p, Polygon r)
    {
        Vector3D n1 = p.normal();
        Vector3D n2 = r.normal();
        
        if (!n1.equals(n2)) {
            Vector3D tmp;
            tmp = p.b;
            p.b = p.c;
            p.c = tmp;
            
            double tmp2;
            tmp2 = p.bIntensity;
            p.bIntensity = p.cIntensity;
            p.cIntensity = tmp2;
        }
    }
    
    private static double interpolateIntensity(Vector3D a, double aIntensity, 
        Vector3D b, double bIntensity, Vector3D newPoint)
    {
        double fullDistance = b.sub(a).abs();
        double newDistance = newPoint.sub(a).abs();
        
        double slope = (bIntensity - aIntensity) / fullDistance;
        
        return aIntensity + slope*newDistance;
    }
}
