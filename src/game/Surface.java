package game;

import java.awt.*;

public interface Surface
{
    public void repaint();
    public void putPixel(int x, int y, Color color);
}
