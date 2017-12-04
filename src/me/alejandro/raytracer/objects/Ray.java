package me.alejandro.raytracer.objects;

public class Ray {

    private Coordinate coord;
    private Vector vec;

    public Ray() {
        coord = new Coordinate(0, 0, 0);
        vec = new Vector(0, 0, 0);
    }

    public Ray(Coordinate coord, Vector vec) {
        this.coord = coord;
        this.vec = vec;
    }

    public Coordinate getCoordinate() {
        return coord;
    }

    public void setCoordinate(Coordinate coord) {
        this.coord = coord;
    }

    public Vector getVector() {
        return vec;
    }

    public void setVector(Vector vec) {
        this.vec = vec;
    }

    /*public boolean intersectCheck(Ray otherRay) {
        double b_facingZ1 = coord.getY();
        double b_facingZ2 = otherRay.getCoordinate().getY();
        double b_facingY1 = coord.getZ();
        double b_facingY2 = otherRay.getCoordinate().getZ();
        double b_facingX1 = coord.getX();
        double b_facingX2 = otherRay.getCoordinate().getX();
        double mx_facingZ1 = vec.getY() / vec.getX();
        double mx_facingZ2 = otherRay.getVector().getY() / otherRay.getVector().getX();
        double mx_facingY1 = vec.getZ() / vec.getX();
        double mx_facingY2 = otherRay.getVector().getZ() / otherRay.getVector().getX();
        double mx_facingX1 = vec.getY() / vec.getZ();
        double mx_facingX2 = otherRay.getVector().getY() / otherRay.getVector().getZ();

        //check 2d intersections one axis at a time...
        double intersectX_facingZ = (b_facingZ2 - b_facingZ1) / (mx_facingZ1 - mx_facingZ2); //facing parallel to Z axis (intercept at X coordinate)
        if((b_facingY2 - b_facingY1) / (mx_facingY1 - mx_facingY2) != intersectX_facingZ) return false; //facing parallel to Y axis
        double intersectY_facingZ = mx_facingZ1 * intersectX_facingZ + b_facingZ1; //facing parallel to Z axis (intercept at Y coordinate)
        double intersectZ_facingX = (b_facingY2 - b_facingZ1) / (mx_facingY1 - mx_facingZ2); //facing parallel to X axis (intercept at Z coordinate)
        if(mx_facingX1 * intersectZ_facingX + b_facingX1 != intersectY_facingZ) return false; //facing parallel to X axis

        return true;



        if(intersectX == Double.POSITIVE_INFINITY || intersectX == Double.NEGATIVE_INFINITY) System.out.println("Parallel lines");
        else if(mx_y1 == mx_y2 && b1 == b2) System.out.println("Coinciding lines");
        else System.out.println("Intersect at (" + intersectX + ", " + intersectY + ")");
    }

    public Coordinate intersectLocation(Ray otherRay) {

    }*/
}
