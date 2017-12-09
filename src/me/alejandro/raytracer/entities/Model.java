package me.alejandro.raytracer.entities;

import java.util.ArrayList;

import me.alejandro.raytracer.objects.*;

public class Model implements Cloneable {

	private Coordinate coordinate;
	public ArrayList<Triangle> triangles = new ArrayList<Triangle>();
	private Material material;
	private String name;
	private AABB aabb;
	
	public Model(double x, double y, double z, ArrayList<Triangle> triangles, Material material, String name) {
		this.coordinate = new Coordinate(x, y, z);
		this.triangles = triangles;
		this.material = material;
		this.name = name;
		calculateAABB();
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public AABB getAabb() {
		return aabb;
	}

	public Model clone() {
		try {
			return (Model) super.clone();
		}
		catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void calculateAABB() {
		double xMin = Double.MAX_VALUE;
		double yMin = Double.MAX_VALUE;
		double zMin = Double.MAX_VALUE;
		double xMax = -Double.MAX_VALUE;
		double yMax = -Double.MAX_VALUE;
		double zMax = -Double.MAX_VALUE;
		for(Triangle triangle : triangles) {
			if(triangle.getCoord0().getX() < xMin) xMin = triangle.getCoord0().getX();
			if(triangle.getCoord0().getY() < yMin) yMin = triangle.getCoord0().getY();
			if(triangle.getCoord0().getZ() < zMin) zMin = triangle.getCoord0().getZ();
			if(triangle.getCoord0().getX() > xMax) xMax = triangle.getCoord0().getX();
			if(triangle.getCoord0().getY() > yMax) yMax = triangle.getCoord0().getY();
			if(triangle.getCoord0().getZ() > zMax) zMax = triangle.getCoord0().getZ();

			if(triangle.getCoord1().getX() < xMin) xMin = triangle.getCoord1().getX();
			if(triangle.getCoord1().getY() < yMin) yMin = triangle.getCoord1().getY();
			if(triangle.getCoord1().getZ() < zMin) zMin = triangle.getCoord1().getZ();
			if(triangle.getCoord1().getX() > xMax) xMax = triangle.getCoord1().getX();
			if(triangle.getCoord1().getY() > yMax) yMax = triangle.getCoord1().getY();
			if(triangle.getCoord1().getZ() > zMax) zMax = triangle.getCoord1().getZ();

			if(triangle.getCoord2().getX() < xMin) xMin = triangle.getCoord2().getX();
			if(triangle.getCoord2().getY() < yMin) yMin = triangle.getCoord2().getY();
			if(triangle.getCoord2().getZ() < zMin) zMin = triangle.getCoord2().getZ();
			if(triangle.getCoord2().getX() > xMax) xMax = triangle.getCoord2().getX();
			if(triangle.getCoord2().getY() > yMax) yMax = triangle.getCoord2().getY();
			if(triangle.getCoord2().getZ() > zMax) zMax = triangle.getCoord2().getZ();
		}
		aabb = new AABB(new Coordinate(xMin, yMin, zMin), new Coordinate(xMax, yMax, zMax));
	}

    public void translate(Vector vector) {
		coordinate.setX(coordinate.getX() + vector.getX());
		coordinate.setY(coordinate.getY() + vector.getY());
		coordinate.setZ(coordinate.getZ() + vector.getZ());
        for(Triangle triangle : triangles) {
            triangle.setCoord0(new Coordinate(triangle.getCoord0().getX() + vector.getX(), triangle.getCoord0().getY() + vector.getY(), triangle.getCoord0().getZ() + vector.getZ()));
            triangle.setCoord1(new Coordinate(triangle.getCoord1().getX() + vector.getX(), triangle.getCoord1().getY() + vector.getY(), triangle.getCoord1().getZ() + vector.getZ()));
            triangle.setCoord2(new Coordinate(triangle.getCoord2().getX() + vector.getX(), triangle.getCoord2().getY() + vector.getY(), triangle.getCoord2().getZ() + vector.getZ()));
        	triangle.setMiddle(new Coordinate(triangle.getMiddle().getX() + vector.getX(), triangle.getMiddle().getY() + vector.getY(), triangle.getMiddle().getZ() + vector.getZ()));
        }
        calculateAABB();
	}

	public void translateOrigin(Vector vector) {
		coordinate.setX(coordinate.getX() + vector.getX());
		coordinate.setY(coordinate.getY() + vector.getY());
		coordinate.setZ(coordinate.getZ() + vector.getZ());
	}

	//this is broken; it scales from 0, 0, 0. it should scale from origin of model
	public void scale(double scalar) {
		for(Triangle triangle : triangles) {
			triangle.setCoord0(new Coordinate(triangle.getCoord0().getX() * scalar, triangle.getCoord0().getY() * scalar, triangle.getCoord0().getZ() * scalar));
			triangle.setCoord1(new Coordinate(triangle.getCoord1().getX() * scalar, triangle.getCoord1().getY() * scalar, triangle.getCoord1().getZ() * scalar));
			triangle.setCoord2(new Coordinate(triangle.getCoord2().getX() * scalar, triangle.getCoord2().getY() * scalar, triangle.getCoord2().getZ() * scalar));
		}
	}

	//work on this later

	public void rotateZ(double degrees) {
		for(Triangle triangle : triangles) {
			double x_prime = triangle.getCoord0().getX() * Math.cos(Math.toRadians(degrees)) - triangle.getCoord0().getY() * Math.sin(Math.toRadians(degrees));
			double y_prime = triangle.getCoord0().getX() * Math.sin(Math.toRadians(degrees)) + triangle.getCoord0().getY() * Math.cos(Math.toRadians(degrees));
			triangle.setCoord0(new Coordinate(x_prime, y_prime, triangle.getCoord0().getZ()));

			x_prime = triangle.getCoord1().getX() * Math.cos(Math.toRadians(degrees)) - triangle.getCoord1().getY() * Math.sin(Math.toRadians(degrees));
			y_prime = triangle.getCoord1().getX() * Math.sin(Math.toRadians(degrees)) + triangle.getCoord1().getY() * Math.cos(Math.toRadians(degrees));
			triangle.setCoord1(new Coordinate(x_prime, y_prime, triangle.getCoord1().getZ()));

			x_prime = triangle.getCoord2().getX() * Math.cos(Math.toRadians(degrees)) - triangle.getCoord2().getY() * Math.sin(Math.toRadians(degrees));
			y_prime = triangle.getCoord2().getX() * Math.sin(Math.toRadians(degrees)) + triangle.getCoord2().getY() * Math.cos(Math.toRadians(degrees));
			triangle.setCoord2(new Coordinate(x_prime, y_prime, triangle.getCoord2().getZ()));

			triangle.getNormal0().setX(triangle.getNormal0().getX() * Math.cos(Math.toRadians(degrees)) - triangle.getNormal0().getY() * Math.sin(Math.toRadians(degrees)));
			triangle.getNormal0().setY(triangle.getNormal0().getX() * Math.sin(Math.toRadians(degrees)) + triangle.getNormal0().getY() * Math.cos(Math.toRadians(degrees)));
			//TODO: remember to rotate the other vertecies in the triangle

			calculateAABB();
		}
	}


}
