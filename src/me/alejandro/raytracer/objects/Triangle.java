package me.alejandro.raytracer.objects;

public class Triangle implements Cloneable {
	
	private Coordinate coord0;
	private Coordinate coord1;
	private Coordinate coord2;
	private Coordinate middle;
	//private Vector normal;
	private Vector normal0;
	private Vector normal1;
	private Vector normal2;

	public Triangle() {
		this.coord0 = new Coordinate(0, 0, 0);
		this.coord1 = new Coordinate(0, 0, 0);
		this.coord2 = new Coordinate(0, 0, 0);
		this.middle = new Coordinate(0, 0, 0);
		//this.normal = new Vector(0, 0, 0);
	}
	
	public Triangle(double x0, double y0, double z0, double x1, double y1, double z1, double x2, double y2, double z2) {
		this.coord0 = new Coordinate(x0, y0, z0);
		this.coord1 = new Coordinate(x1, y1, z1);
		this.coord2 = new Coordinate(z2, y2, z2);
		this.middle = new Coordinate((x0 + x1 + x2) / 3, (y0 + y1 + y2) / 3, (z0 + z1 + z2) / 3);
		//this.normal = this.coord0.toVector().crossProduct(this.coord1.toVector());
		//this.normal.normalize();
	}

	public Triangle(Coordinate coord0, Coordinate coord1, Coordinate coord2, Vector normal0, Vector normal1, Vector normal2) {
		this.coord0 = coord0;
		this.coord1 = coord1;
		this.coord2 = coord2;
		this.middle = new Coordinate((coord0.getX() + coord1.getX() + coord2.getX()) / 3, (coord0.getY() + coord1.getY() + coord2.getY()) / 3, (coord0.getZ() + coord1.getZ() + coord2.getZ()) / 3);
		//this.normal = normal0;
		this.normal0 = normal0;
		this.normal1 = normal1;
		this.normal2 = normal2;
		//this.normal.normalize();
		this.normal0.normalize();
		this.normal1.normalize();
		this.normal2.normalize();
	}

	public Triangle(Coordinate coord0, Coordinate coord1, Coordinate coord2, Vector normal) {
		this.coord0 = coord0;
		this.coord1 = coord1;
		this.coord2 = coord2;
		this.middle = new Coordinate((coord0.getX() + coord1.getX() + coord2.getX()) / 3, (coord0.getY() + coord1.getY() + coord2.getY()) / 3, (coord0.getZ() + coord1.getZ() + coord2.getZ()) / 3);
		//this.normal = normal;
		//this.normal.normalize();
	}

	public Coordinate getCoord0() {
		return coord0;
	}

	public void setCoord0(Coordinate coord0) {
		this.coord0 = coord0;
	}

	public Coordinate getCoord1() {
		return coord1;
	}

	public void setCoord1(Coordinate coord1) {
		this.coord1 = coord1;
	}

	public Coordinate getCoord2() {
		return coord2;
	}

	public void setCoord2(Coordinate coord2) {
		this.coord2 = coord2;
	}

	//public Vector getNormal() {
		//return normal;
	//}

	public Vector getNormal0() {
		return normal0;
	}

	public Vector getNormal1() {
		return normal1;
	}

	public Vector getNormal2() {
		return normal2;
	}

	//public void setNormal(Vector normal) {
		//this.normal = normal;
	//}

	public Coordinate getMiddle() {
		return middle;
	}

	public void setMiddle(Coordinate middle) {
		this.middle = middle;
	}

	public Triangle clone() {
		try {
			return (Triangle) super.clone();
		}
		catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
