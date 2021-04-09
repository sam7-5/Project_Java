package geometries;

import primitives.Point3D;
import primitives.Vector;

import java.beans.VetoableChangeListener;

/**
 * class plane with 2 different constructors first one with 3 points and the second
 * with a point and a vector
 */
public class Plane implements Geometry {
    private Point3D q0;
    private Vector normal;

    public Plane(Point3D q1, Point3D q2, Point3D q3) {
        this.q0 = q1;

        Vector U = q2.subtract(q1);
        Vector V = q3.subtract(q1);
        Vector N = U.crossProduct(V);
        N.normalize();
        this.normal = N;
    }

    public Plane(Point3D q0, Vector normal) {
        this.q0 = q0;
        this.normal = normal.normalized();
    }

    public Point3D getQ0() {
        return q0;
    }


    public Vector getNormal() {
        return normal;
    }

    public String toString() {
        return "Plane{" +
                "q0=" + q0 +
                ", normal=" + normal +
                '}';
    }

    @Override
    public Vector getNormal(Point3D point3D) {
        return normal;
    }
}
