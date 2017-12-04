package me.alejandro.raytracer.objects;

public class ScreenCoordinate {
	
	private double x;
	private double y;
	
	public ScreenCoordinate(double x, double y) {
		this.x = x;
		this.y = y;
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
	
	public void multiply(double multiplier) {
		this.x *= multiplier;
		this.y *= multiplier;
	}
}
