package me.alejandro.raytracer.utils;

import me.alejandro.raytracer.objects.AABB;
import me.alejandro.raytracer.objects.Coordinate;
import me.alejandro.raytracer.objects.Triangle;
import me.alejandro.raytracer.objects.Vector;

public final class Geometry {

    /*public static boolean rayIntersectsSphere(Vector rayOrigin, Vector rayVector, Triangle inTriangle) {

    }*/

    /*public static Coordinate rayIntersectSpherePoint(Vector rayOrigin, Vector rayVector, Triangle inTriangle) {

    }*/

    //skidded from Moller Trumbore's ray-triangle-intersection algorithm
    public static boolean rayIntersectsTriangle(Vector rayOrigin, Vector rayVector, Triangle inTriangle) {
        Vector rayVect = new Vector(rayVector);
        double EPSILON = 0.0000001;
        Vector vertex0 = inTriangle.getCoord0().toVector();
        Vector vertex1 = inTriangle.getCoord1().toVector();
        Vector vertex2 = inTriangle.getCoord2().toVector();
        Vector edge1, edge2, h, s, q;
        double a,f,u,v;
        edge1 = new Vector(vertex1.getX() - vertex0.getX(), vertex1.getY() - vertex0.getY(), vertex1.getZ() - vertex0.getZ());
        edge2 = new Vector(vertex2.getX() - vertex0.getX(), vertex2.getY() - vertex0.getY(), vertex2.getZ() - vertex0.getZ());
        h = rayVect.crossProduct(edge2);
        a = edge1.dotProduct(h);
        if (a > -EPSILON && a < EPSILON) return false;
        f = 1/a;
        s = new Vector(rayOrigin.getX() - vertex0.getX(), rayOrigin.getY() - vertex0.getY(), rayOrigin.getZ() - vertex0.getZ());
        u = f * (s.dotProduct(h));
        if (u < 0.0 || u > 1.0) return false;
        q = s.crossProduct(edge1);
        v = f * rayVect.dotProduct(q);
        if (v < 0.0 || u + v > 1.0) return false;
        // At this stage we can compute t to find out where the intersection point is on the line.
        double t = f * edge2.dotProduct(q);
        return (t > EPSILON);
    }

    //big thanks to this thread: https://bukkit.org/threads/check-if-vector-goes-through-certain-area.393647/
    public static boolean rayIntersectsAABB(AABB aabb, Coordinate origin, Vector ray) {
        Vector mini = aabb.getMinimum().toVector();
        Vector maxi = aabb.getMaximum().toVector();

        double t1 = ((mini.getX() - origin.getX()) / ray.getX());
        double t2 = ((maxi.getX() - origin.getX()) / ray.getX());
        double t3 = ((mini.getY() - origin.getY()) / ray.getY());
        double t4 = ((maxi.getY() - origin.getY()) / ray.getY());
        double t5 = ((mini.getZ() - origin.getZ()) / ray.getZ());
        double t6 = ((maxi.getZ() - origin.getZ()) / ray.getZ());

        double tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        double tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        //If the ray intersects the AABB, but the AABB is behind the ray
        if (tmax < 0) {
            return false;
        }

        //If the ray doesn't intersect the AABB.
        if (tmin > tmax) {
            return false;
        }
        return true;
    }

    public static boolean cameraRayIntersectsAABB(AABB aabb, Vector ray) {
        Vector mini = aabb.getMinimum().toVector();
        Vector maxi = aabb.getMaximum().toVector();

        double t1 = (mini.getX() / ray.getX());
        double t2 = (maxi.getX() / ray.getX());
        double t3 = (mini.getY() / ray.getY());
        double t4 = (maxi.getY() / ray.getY());
        double t5 = (mini.getZ() / ray.getZ());
        double t6 = (maxi.getZ() / ray.getZ());

        double tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        double tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        //If the ray intersects the AABB, but the AABB is behind the ray
        if (tmax < 0) return false;

        //If the ray doesn't intersect the AABB.
        return !(tmin > tmax);
    }
}
