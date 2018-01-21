package me.alejandro.raytracer.objects.kdtree;

import me.alejandro.raytracer.Main;
import me.alejandro.raytracer.objects.AABB;
import me.alejandro.raytracer.objects.Coordinate;
import me.alejandro.raytracer.objects.Triangle;
import me.alejandro.raytracer.objects.Vector;
import me.alejandro.raytracer.utils.Geometry;

import java.util.ArrayList;
import java.util.List;

class Node {

    private Node a;
    private Node b;
    private List<Triangle> theseTriangles = new ArrayList<>();
    private byte orientation;
    private AABB aabb;
    private Tree tree;
    private boolean leaf = false;

    Node(List<Triangle> parentTriangles, byte orientation, AABB aabb, Tree tree) {
        this.orientation = orientation; //0, 1, 2 -> x, y, z
        this.aabb = aabb;
        this.tree = tree;
        this.tree.setTreeNodes(this.tree.getNodeNumber() + 1);
        organize(parentTriangles);
    }

    private void organize(List<Triangle> triangles) {
        //Call it a day if there are too few triangles to split
        if(triangles.size() <= Main.RAYTREE_SIZE) {
            theseTriangles.addAll(triangles);
            leaf = true;
            return;
        }

        double x = aabb.getMaximum().getX() - aabb.getMinimum().getX();
        double y = aabb.getMaximum().getY() - aabb.getMinimum().getY();
        double z = aabb.getMaximum().getZ() - aabb.getMinimum().getZ();

        orientation++;
        if(orientation > 2) orientation = 0;

        List<Triangle> aTriangles = new ArrayList<>();
        List<Triangle> bTriangles = new ArrayList<>();
        AABB left;
        AABB right;
        if(orientation == 0) {
            double mid = aabb.getMinimum().getX() + (x / 2);
            Coordinate max = new Coordinate(aabb.getMaximum());
            max.setX(aabb.getMaximum().getX() - (x / 2));
            Coordinate min = new Coordinate(aabb.getMinimum());
            min.setX(mid);
            left = new AABB(new Coordinate(aabb.getMinimum()), max);
            right = new AABB(min, new Coordinate(aabb.getMaximum()));
            for(Triangle loopTriangle : triangles) {
                if(loopTriangle.getCoord0().getX() < mid && loopTriangle.getCoord1().getX() < mid && loopTriangle.getCoord2().getX() < mid) {
                    aTriangles.add(loopTriangle);
                }
                else if(loopTriangle.getCoord0().getX() >= mid && loopTriangle.getCoord1().getX() >= mid && loopTriangle.getCoord2().getX() >= mid) {
                    bTriangles.add(loopTriangle);
                }
                else theseTriangles.add(loopTriangle);
            }
        }
        else if(orientation == 1) {
            double mid = aabb.getMinimum().getY() + (y / 2);
            Coordinate max = new Coordinate(aabb.getMaximum());
            max.setY(aabb.getMaximum().getY() - (y / 2));
            Coordinate min = new Coordinate(aabb.getMinimum());
            min.setY(mid);
            left = new AABB(new Coordinate(aabb.getMinimum()), max);
            right = new AABB(min, new Coordinate(aabb.getMaximum()));
            for(Triangle loopTriangle : triangles) {
                if(loopTriangle.getCoord0().getY() < mid && loopTriangle.getCoord1().getY() < mid && loopTriangle.getCoord2().getY() < mid) {
                    aTriangles.add(loopTriangle);
                }
                else if(loopTriangle.getCoord0().getY() >= mid && loopTriangle.getCoord1().getY() >= mid && loopTriangle.getCoord2().getY() >= mid) {
                    bTriangles.add(loopTriangle);
                }
                else theseTriangles.add(loopTriangle);
            }
        }
        else {
            double mid = aabb.getMinimum().getZ() + (z / 2);
            Coordinate max = new Coordinate(aabb.getMaximum());
            max.setZ(aabb.getMaximum().getZ() - (z / 2));
            Coordinate min = new Coordinate(aabb.getMinimum());
            min.setZ(mid);
            left = new AABB(new Coordinate(aabb.getMinimum()), max);
            right = new AABB(min, new Coordinate(aabb.getMaximum()));
            for(Triangle loopTriangle : triangles) {
                if(loopTriangle.getCoord0().getZ() < mid && loopTriangle.getCoord1().getZ() < mid && loopTriangle.getCoord2().getZ() < mid) {
                    aTriangles.add(loopTriangle);
                }
                else if(loopTriangle.getCoord0().getZ() >= mid && loopTriangle.getCoord1().getZ() >= mid && loopTriangle.getCoord2().getZ() >= mid) {
                    bTriangles.add(loopTriangle);
                }
                else theseTriangles.add(loopTriangle);
            }
        }

        //Check if it makes sense to make new nodes. If you can't split the triangles anymore, call it a day.
        if(aTriangles.size() > Main.RAYTREE_SIZE && bTriangles.size() > Main.RAYTREE_SIZE) {
            aTriangles.addAll(theseTriangles);
            bTriangles.addAll(theseTriangles);
            theseTriangles.clear();
        }

        if(aTriangles.size() > 0) a = new Node(aTriangles, orientation, left, tree);
        if(bTriangles.size() > 0) b = new Node(bTriangles, orientation, right, tree);
    }

    void reconstruct(Vector ray, Coordinate origin, List<Triangle> triangleBucket) {
        if(Geometry.rayIntersectsAABB(aabb, origin, ray)) {
            triangleBucket.addAll(theseTriangles);
            if(a != null) a.reconstruct(ray, origin, triangleBucket);
            if(b != null) b.reconstruct(ray, origin, triangleBucket);
        }
    }

    void reconstructFromCamRay(Vector ray, List<Triangle> triangleBucket) {
        if(Geometry.cameraRayIntersectsAABB(aabb, ray)) {
            triangleBucket.addAll(theseTriangles);
            if(a != null) a.reconstructFromCamRay(ray, triangleBucket);
            if(b != null) b.reconstructFromCamRay(ray, triangleBucket);
        }
    }

    public Node getA() {
        return a;
    }

    public Node getB() {
        return b;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public List<Triangle> getTriangles() {
        return theseTriangles;
    }

    public AABB getAabb() {
        return aabb;
    }
}
