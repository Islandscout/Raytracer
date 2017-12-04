package me.alejandro.raytracer.utils;

import me.alejandro.raytracer.Main;
import me.alejandro.raytracer.objects.Coordinate;
import me.alejandro.raytracer.objects.ScreenCoordinate;

public class PerspectiveTransform {
	
	public static ScreenCoordinate transformToScreen(double x, double y, double z) {
		ScreenCoordinate coord = new ScreenCoordinate(x/z, y/z);
		coord = fovFix(coord);
		return coord;
	}
	
	public static ScreenCoordinate transformToScreen(Coordinate precoord) {
		ScreenCoordinate coord = new ScreenCoordinate(precoord.getX() / precoord.getZ(), precoord.getY() / precoord.getZ());
		coord = fovFix(coord);
		return coord;
	}
	
	private static ScreenCoordinate fovFix(ScreenCoordinate coord) {
		coord.multiply(1/Math.tan(Main.FOV/2));
		return coord;
	}

}
