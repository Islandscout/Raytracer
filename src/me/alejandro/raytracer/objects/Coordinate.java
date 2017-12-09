package me.alejandro.raytracer.objects;

public class Coordinate implements Cloneable {
	
	private double x;
	private double y;
	private double z;
	
	public Coordinate(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Coordinate(Coordinate coordinate) {
		this.x = coordinate.getX();
		this.y = coordinate.getY();
		this.z = coordinate.getZ();
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public Coordinate clone() {
		try {
			return (Coordinate) super.clone();
		}
		catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void multiply(double multiplier) {
		this.x *= multiplier;
		this.y *= multiplier;
		this.z *= multiplier;
	}

	public Vector toVector() {
		return new Vector(x, y, z);
	}
	
	public double distance(Coordinate otherCoordinate) {
		return Math.sqrt((this.x - otherCoordinate.getX())*(this.x - otherCoordinate.getX()) + (this.y - otherCoordinate.getY())*(this.y - otherCoordinate.getY()) + (this.z - otherCoordinate.getZ())*(this.z - otherCoordinate.getZ()));
	}

	public double distanceSquared(Coordinate otherCoordinate) {
		return (this.x - otherCoordinate.getX())*(this.x - otherCoordinate.getX()) + (this.y - otherCoordinate.getY())*(this.y - otherCoordinate.getY()) + (this.z - otherCoordinate.getZ())*(this.z - otherCoordinate.getZ());
	}

}
