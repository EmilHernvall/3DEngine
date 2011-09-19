package game;

public class LightSource
{
    public Vector3D position;
    public double intensity;

    public LightSource(Vector3D position, double intensity)
    {
        this.position = position;
        this.intensity = intensity;
    }
}
