package me.alejandro.raytracer.objects;

public class Vector implements Cloneable {
	
	private double x;
	private double y;
	private double z;
	
	public Vector() {
		this.x = 0D;
		this.y = 0D;
		this.z = 0D;
	}
	
	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector(Vector vector) {
		this.x = vector.getX();
		this.y = vector.getY();
		this.z = vector.getZ();
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

	public Vector clone() {
		try {
			return (Vector) super.clone();
		}
		catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void normalize() {
		double distance = Math.sqrt(x*x+y*y+z*z);
		x /= distance;
		y /= distance;
		z /= distance;
	}

	public double length() {
		return Math.sqrt(x*x+y*y+z*z);
	}

	public double lengthSquared() {
	    return x*x+y*y+z*z;
    }

	public double angleDegrees(Vector otherVector) {
	    double dot = dotProduct(otherVector);
	    double lengthThis = length();
	    double lengthThat = otherVector.length();
	    return Math.toDegrees(Math.acos(dot / (lengthThis * lengthThat)));
    }

    public double angleRadians(Vector otherVector) {
        double dot = dotProduct(otherVector);
        double lengthThis = length();
        double lengthThat = otherVector.length();
        return Math.acos(dot / (lengthThis * lengthThat));
    }

    public double dotProduct(Vector otherVector) {
	    return (x * otherVector.getX()) + (y * otherVector.getY()) + (z * otherVector.getZ());
    }

    public Vector crossProduct(Vector otherVector) {
		double x = this.y*otherVector.getZ() - this.z*otherVector.getY();
		double y = this.z*otherVector.getX() - this.x*otherVector.getZ();
		double z = this.x*otherVector.getY() - this.y*otherVector.getX();
		return new Vector(x, y, z);
	}

	public void multiply(double scalar) {
		this.x *= scalar;
		this.y *= scalar;
		this.z *= scalar;
	}

	public void add(Vector otherVector) {
		this.x += otherVector.getX();
		this.y += otherVector.getY();
		this.z += otherVector.getZ();
	}

	public void subtract(Vector otherVector) {
		this.x -= otherVector.getX();
		this.y -= otherVector.getY();
		this.z -= otherVector.getZ();
	}

}
