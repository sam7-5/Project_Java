package elements;

import primitives.Color;

public class AmbientLight {
    final private Color intensity;
    public AmbientLight(Color Ia, double Ka) {
        intensity = Ia.scale(Ka);
    }

    public Color getIntensity() {
        return intensity;
    }
}