package geometries;

import java.util.LinkedList;
import java.util.List;
import primitives.*;
import static primitives.Util.*;

/**
 * Polygon class represents two-dimensional polygon in 3D Cartesian coordinate
 * system
 * 
 * @author Dan
 */
public class Polygon extends Geometry {
	/**
	 * List of polygon's vertices
	 */
	protected List<Point3D> vertices;
	/**
	 * Associated plane in which the polygon lays
	 */
	protected Plane plane;

	/**
	 * Polygon constructor based on vertices list. The list must be ordered by edge
	 * path. The polygon must be convex.
	 * 
	 * @param vertices list of vertices according to their order by edge path
	 * @throws IllegalArgumentException in any case of illegal combination of
	 *                                  vertices:
	 *                                  <ul>
	 *                                  <li>Less than 3 vertices</li>
	 *                                  <li>Consequent vertices are in the same
	 *                                  point
	 *                                  <li>The vertices are not in the same
	 *                                  plane</li>
	 *                                  <li>The order of vertices is not according
	 *                                  to edge path</li>
	 *                                  <li>Three consequent vertices lay in the
	 *                                  same line (180&#176; angle between two
	 *                                  consequent edges)
	 *                                  <li>The polygon is concave (not convex)</li>
	 *                                  </ul>
	 */
	public Polygon(Point3D... vertices) {
		if (vertices.length < 3)
			throw new IllegalArgumentException("A polygon can't have less than 3 vertices");
		this.vertices = List.of(vertices);
		// Generate the plane according to the first three vertices and associate the
		// polygon with this plane.
		// The plane holds the invariant normal (orthogonal unit) vector to the polygon
		plane = new Plane(vertices[0], vertices[1], vertices[2]);
		//calculate the minimum and maximum of coordinates X,Y,Z of the polygon
		Xmin = MAX;
		Ymin = MAX;
		Zmin = MAX;
		Xmax = MIN;
		Ymax = MIN;
		Zmax = MIN;

		for (Point3D p : vertices) {
			double xPoint = p.getX();
			double yPoint = p.getY();
			double zPoint = p.getZ();

			if (Xmin > xPoint)  Xmin = xPoint;
			if (Ymin > yPoint)  Ymin = yPoint;
			if (Zmin > zPoint)  Zmin = zPoint;

			if (Xmax < xPoint)  Xmax = xPoint;
			if (Ymax < yPoint)  Ymax = yPoint;
			if (Zmax < zPoint)  Zmax = zPoint;
		}

		if (vertices.length == 3)
			return; // no need for more tests for a Triangle

		Vector n = plane.getNormal();

		// Subtracting any subsequent points will throw an IllegalArgumentException
		// because of Zero Vector if they are in the same point
		Vector edge1 = vertices[vertices.length - 1].subtract(vertices[vertices.length - 2]);
		Vector edge2 = vertices[0].subtract(vertices[vertices.length - 1]);

		// Cross Product of any subsequent edges will throw an IllegalArgumentException
		// because of Zero Vector if they connect three vertices that lay in the same
		// line.
		// Generate the direction of the polygon according to the angle between last and
		// first edge being less than 180 deg. It is hold by the sign of its dot product
		// with
		// the normal. If all the rest consequent edges will generate the same sign -
		// the
		// polygon is convex ("kamur" in Hebrew).
		boolean positive = edge1.crossProduct(edge2).dotProduct(n) > 0;
		for (int i = 1; i < vertices.length; ++i) {
			// Test that the point is in the same plane as calculated originally
			if (!isZero(vertices[i].subtract(vertices[0]).dotProduct(n)))
				throw new IllegalArgumentException("All vertices of a polygon must lay in the same plane");
			// Test the consequent edges have
			edge1 = edge2;
			edge2 = vertices[i].subtract(vertices[i - 1]);
			if (positive != (edge1.crossProduct(edge2).dotProduct(n) > 0))
				throw new IllegalArgumentException("All vertices must be ordered and the polygon must be convex");
		}
	}

	@Override
	public Vector getNormal(Point3D point) {
		return plane.getNormal();
	}

//	@Override
//	public List<GeoPoint> findGeoIntersections tichonit(Ray ray ,double maxDistance) {
//		List<GeoPoint> planeIntersections = plane.findGeoIntersections(ray , maxDistance);
//
//		if (planeIntersections == null) {
//			return null;
//		}
//
//		Point3D P0 = ray.getP0();
//		Vector v = ray.getDir();
//
//		double vn=0;
//		for (int i =0;i<vertices.size();++i){
//
//			Vector v1=vertices.get(i).subtract(P0);
//
//			Vector v2;
//			if(i==vertices.size()-1){
//				v2=vertices.get(0).subtract(P0);
//			}
//			else{
//				v2=vertices.get(i+1).subtract(P0);
//			}
//
//			Vector n = v1.crossProduct(v2).normalize();
//			double vni = alignZero(v.dotProduct(n));
//
//			if(i==0){
//				vn = vni;
//			}
//
//			if(vni==0|| vn<0&&vni>0||vn>0&&vni<0){
//				return null;
//			}
//		}
//		planeIntersections.get(0).geometry=this;
//		return planeIntersections;
//
//	}

	public List<GeoPoint> findGeoIntersections (Ray ray ,double maxDistance) {
		List<Vector> vectors=new LinkedList<>();
		for(Point3D p:vertices){
			vectors.add(p.subtract(ray.getP0()));
		}

		List<Vector > normals=new LinkedList<>();
		for(int i=0;i< vectors.size()-1;i++){
			normals.add((vectors.get(i).crossProduct(vectors.get(i+1))).normalize());
		}
		normals.add((vectors.get(vectors.size()-1).crossProduct(vectors.get(0))).normalize());

		boolean Negative =true;
		boolean Positive=true;

		for(Vector n:normals){
			if(alignZero(n.dotProduct(ray.getDir()))>=0){
				Negative=false;
			}
			if(alignZero(n.dotProduct(ray.getDir()))<=0){
				Positive=false;
			}
		}

		List<GeoPoint>result= plane.findGeoIntersections(ray, maxDistance);
		if(result!=null){
			result.get(0).geometry=this;
		}

		if(Negative||Positive){
			return result;
		}
		return null;
	}




	//	@Override
//	public List<GeoPoint> findGeoIntersections(Ray ray ,double maxDistance) {
//		List<GeoPoint> result = plane.findIntersections(ray,maxDistance);
//
//		if (result == null) {
//			return null;
//		}
//
//		Point3D P0 = ray.getP0();
//		Vector v = ray.getDir();
//
//		Point3D P1 = vertices.get(1);
//		Point3D P2 = vertices.get(0);
//
//		Vector v1 = P1.subtract(P0);
//		Vector v2 = P2.subtract(P0);
//
//		double sign = alignZero(v.dotProduct(v1.crossProduct(v2)));
//
//		if (isZero(sign)) {
//			return null;
//		}
//
//		boolean positive = sign > 0;
//
//		//iterate through all vertices of the polygon
//		for (int i = vertices.size() - 1; i > 0; --i)  {
//			v1 = v2;
//			v2 = vertices.get(i).subtract(P0);
//
//			sign = alignZero(v.dotProduct(v1.crossProduct(v2)));
//			if (isZero(sign)) {
//				return null;
//			}
//
//			if (positive != (sign > 0)) {
//				return null;
//			}
//		}
//		return List.of(new GeoPoint(this,result.get(0))) ;
//		//return result;
//
//	}

}
