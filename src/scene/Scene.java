package scene;

import elements.AmbientLight;
import geometries.Geometries;
import primitives.Color;

public class Scene {


    public Geometries geometries;
    public Color background;
    public AmbientLight ambientLight;

    private final String name;

    public Scene(String name) {
        this.name = name;
    }

    //chaining methods
    public Scene setBackground(Color background) {
        this.background = background;
        return this;
    }

    public Scene setAmbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    public Scene setGeometries(Geometries geometries) {
        this.geometries = geometries;
        return this;
    }
}