package me.alejandro.raytracer.objects;

public class Triangle implements Cloneable {
	
	private Coordinate coord0;
	private Coordinate coord1;
	private Coordinate coord2;
	private Coordinate middle;
	private Coordinate texCoord0;
	private Coordinate texCoord1;
	private Coordinate texCoord2;
	private Vector normal;
	private Vector normal0;
	private Vector normal1;
	private Vector normal2;

	public Triangle(Coordinate coord0, Coordinate coord1, Coordinate coord2, Vector normal0, Vector normal1, Vector normal2, Coordinate texCoord0, Coordinate texCoord1, Coordinate texCoord2) {
		this.coord0 = coord0.clone();
		this.coord1 = coord1.clone();
		this.coord2 = coord2.clone();

		this.middle = new Coordinate((coord0.getX() + coord1.getX() + coord2.getX()) / 3, (coord0.getY() + coord1.getY() + coord2.getY()) / 3, (coord0.getZ() + coord1.getZ() + coord2.getZ()) / 3);

		this.normal0 = normal0.clone();
		this.normal1 = normal1.clone();
		this.normal2 = normal2.clone();
		this.normal0.normalize();
		this.normal1.normalize();
		this.normal2.normalize();

		this.normal = this.normal0.clone();
		this.normal.add(this.normal1);
		this.normal.add(this.normal2);
		this.normal.normalize();

		this.texCoord0 = texCoord0;
		this.texCoord1 = texCoord1;
		this.texCoord2 = texCoord2;
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

	public Vector getNormal() {
		return normal;
	}

	public Vector getNormal0() {
		return normal0;
	}

	public Vector getNormal1() {
		return normal1;
	}

	public Vector getNormal2() {
		return normal2;
	}

	public Coordinate getMiddle() {
		return middle;
	}

	public void setMiddle(Coordinate middle) {
		this.middle = middle;
	}

	public Coordinate getTexCoord0() {
		return texCoord0;
	}

	public Coordinate getTexCoord1() {
		return texCoord1;
	}

	public Coordinate getTexCoord2() {
		return texCoord2;
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
