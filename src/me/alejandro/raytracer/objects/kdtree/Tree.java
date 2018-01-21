package me.alejandro.raytracer.objects.kdtree;

import me.alejandro.raytracer.Main;
import me.alejandro.raytracer.entities.Model;
import me.alejandro.raytracer.objects.AABB;
import me.alejandro.raytracer.objects.Coordinate;
import me.alejandro.raytracer.objects.Triangle;
import me.alejandro.raytracer.objects.Vector;
import me.alejandro.raytracer.utils.Debug;
import me.alejandro.raytracer.utils.Geometry;

import java.util.ArrayList;
import java.util.List;

public class Tree {

    //This sort of mess I'm doing is probably frowned upon...

    private List<Triangle> theseTriangles = new ArrayList<>();
    private Node a;
    private Node b;
    private int treeNodes;
    private AABB aabb;
    private boolean leaf = false;

    public Tree(Model model) {
        build(model);
    }

    private void build(Model model) {
        aabb = model.getAabb();
        if(model.triangles.size() <= Main.RAYTREE_SIZE) {
            theseTriangles.addAll(model.triangles);
            leaf = true;
            return;
        }

        double x = model.getAabb().getMaximum().getX() - model.getAabb().getMinimum().getX();
        double y = model.getAabb().getMaximum().getY() - model.getAabb().getMinimum().getY();
        double z = model.getAabb().getMaximum().getZ() - model.getAabb().getMinimum().getZ();

        //intellij doesn't like assigning values to a byte using the ternary operator. incompatible types... int to a byte... blah blah shut up.
        byte orientation;
        if(x > y && x > z) {
            orientation = 0;
        }
        else if(y > x && y > z) {
            orientation = 1;
        }
        else orientation = 2;

        //omg...
        List<Triangle> aTriangles = new ArrayList<>();
        List<Triangle> bTriangles = new ArrayList<>();
        AABB left;
        AABB right;
        if(orientation == 0) {
            double mid = model.getAabb().getMinimum().getX() + (x / 2);
            Coordinate max = new Coordinate(model.getAabb().getMaximum());
            max.setX(model.getAabb().getMaximum().getX() - (x / 2));
            Coordinate min = new Coordinate(model.getAabb().getMinimum());
            min.setX(mid);
            left = new AABB(new Coordinate(model.getAabb().getMinimum()), max);
            right = new AABB(min, new Coordinate(model.getAabb().getMaximum()));
            for(Triangle loopTriangle : model.triangles) {
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
            double mid = model.getAabb().getMinimum().getY() + (y / 2);
            Coordinate max = new Coordinate(model.getAabb().getMaximum());
            max.setY(model.getAabb().getMaximum().getY() - (y / 2));
            Coordinate min = new Coordinate(model.getAabb().getMinimum());
            min.setY(mid);
            left = new AABB(new Coordinate(model.getAabb().getMinimum()), max);
            right = new AABB(min, new Coordinate(model.getAabb().getMaximum()));
            for(Triangle loopTriangle : model.triangles) {
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
            double mid = model.getAabb().getMinimum().getZ() + (z / 2);
            Coordinate max = new Coordinate(model.getAabb().getMaximum());
            max.setZ(model.getAabb().getMaximum().getZ() - (z / 2));
            Coordinate min = new Coordinate(model.getAabb().getMinimum());
            min.setZ(mid);
            left = new AABB(new Coordinate(model.getAabb().getMinimum()), max);
            right = new AABB(min, new Coordinate(model.getAabb().getMaximum()));
            for(Triangle loopTriangle : model.triangles) {
                if(loopTriangle.getCoord0().getZ() < mid && loopTriangle.getCoord1().getZ() < mid && loopTriangle.getCoord2().getZ() < mid) {
                    aTriangles.add(loopTriangle);
                }
                else if(loopTriangle.getCoord0().getZ() >= mid && loopTriangle.getCoord1().getZ() >= mid && loopTriangle.getCoord2().getZ() >= mid) {
                    bTriangles.add(loopTriangle);
                }
                else theseTriangles.add(loopTriangle);
            }
        }

        if(aTriangles.size() > Main.RAYTREE_SIZE && bTriangles.size() > Main.RAYTREE_SIZE) {
            aTriangles.addAll(theseTriangles);
            bTriangles.addAll(theseTriangles);
            theseTriangles.clear();
        }

        if(aTriangles.size() > 0) a = new Node(aTriangles, orientation, left, this);
        if(bTriangles.size() > 0) b = new Node(bTriangles, orientation, right, this);
    }

    public void reconstruct(Vector ray, Coordinate origin, List<Triangle> triangleBucket) {
        if(Geometry.rayIntersectsAABB(aabb, origin, ray)) {
            triangleBucket.addAll(theseTriangles);
            if(a != null) a.reconstruct(ray, origin, triangleBucket);
            if(b != null) b.reconstruct(ray, origin, triangleBucket);
        }
    }

    //same as above, but a little more optimized
    public void reconstructFromCamRay(Vector ray, List<Triangle> triangleBucket) {
        if(Geometry.cameraRayIntersectsAABB(aabb,  ray)) {
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

    public int getNodeNumber() {
        return treeNodes;
    }

    public void setTreeNodes(int treeNodes) {
        this.treeNodes = treeNodes;
    }

    public List<Triangle> getTriangles() {
        return theseTriangles;
    }

    public AABB getAabb() {
        return aabb;
    }
}
