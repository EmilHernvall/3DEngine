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
}
