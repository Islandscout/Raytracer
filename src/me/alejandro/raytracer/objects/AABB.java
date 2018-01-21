package me.alejandro.raytracer.objects;

public class AABB {

    private Coordinate minimum;
    private Coordinate maximum;

    public AABB(Coordinate minimum, Coordinate maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public Coordinate getMinimum() {
        return minimum;
    }

    public void setMinimum(Coordinate minimum) {
        this.minimum = minimum;
    }

    public Coordinate getMaximum() {
        return maximum;
    }

    public void setMaximum(Coordinate maximum) {
        this.maximum = maximum;
    }

    public double getVolume() {
        return (maximum.getX() - minimum.getX()) * (maximum.getY() - minimum.getY()) * (maximum.getZ() - minimum.getZ());
    }
}
