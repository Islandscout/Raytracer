package me.alejandro.raytracer;

import me.alejandro.raytracer.engine.Render;
import me.alejandro.raytracer.engine.Scene;
import me.alejandro.raytracer.entities.Lamp;
import me.alejandro.raytracer.entities.LampType;
import me.alejandro.raytracer.entities.Model;
import me.alejandro.raytracer.objects.Coordinate;
import me.alejandro.raytracer.objects.OBJLoader;
import me.alejandro.raytracer.objects.Vector;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Main {
	
	public final static int WIDTH = 900;
	public final static int HEIGHT = 600;
	public final static int FOV = 90; //horizontal field of view
	public static double ASPECT_RATIO = HEIGHT / (double) WIDTH;
	public static double FOV_MULTIPLIER = Math.tan(Math.toRadians(FOV/2));
	private static BufferedImage buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	public static BufferedImage texture;
	
	/*
	   A simple raytracer in Java. A dream come true after 2 years.
	   August 4, 2017
	 */
	
	public static void main(String[] args) {

		try {
			texture = ImageIO.read(new File("tex.jpg"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		final JFrame frame = new JFrame("Render");
		final JLabel lbl = new JLabel();

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		OBJLoader loaderPlane = new OBJLoader("testPlane.obj");
		OBJLoader loaderSphere = new OBJLoader("sphere.obj");
		OBJLoader loaderCube = new OBJLoader("cube.obj");
		OBJLoader loaderCone = new OBJLoader("cone.obj");
		OBJLoader loaderTeapot = new OBJLoader("teapot.obj");
		loaderTeapot.loadModel();
		loaderPlane.loadModel();
		loaderSphere.loadModel();
		loaderCube.loadModel();
		loaderCone.loadModel();
		Model plane = loaderPlane.getModel().clone();
		Model sphere = loaderSphere.getModel().clone();
		Model cube = loaderCube.getModel().clone();
		Model cone = loaderCone.getModel().clone();
		Model teapot = loaderTeapot.getModel().clone();

		//plane.translate(new Vector(-1.5, -1, 5));
		plane.translate(new Vector(0, 0, 15));
		plane.getMaterial().setColor(Color.WHITE);
		plane.getMaterial().setSpecularColor(Color.WHITE);
		plane.getMaterial().setReflectiveness(0.3);
		plane.getMaterial().setSpecularHardness(50);

		sphere.translate(new Vector(-1.5, -1, 5));
		sphere.getMaterial().setColor(new Color(0, 0, 50));
		sphere.getMaterial().setSpecularColor(Color.BLUE);

		cube.translate(new Vector(-1.5, -1, 5));
		cube.getMaterial().setColor(Color.RED);
		cube.getMaterial().setSpecularColor(Color.WHITE);
		cube.getMaterial().setReflectiveness(0.3);

		cone.translate(new Vector(-1.5, -1, 6));
		cone.getMaterial().setColor(new Color(50, 255, 50));
		cone.getMaterial().setSpecularColor(new Color(50, 255, 50));
		cone.getMaterial().setReflectiveness(0.3);

		teapot.translate(new Vector(-1.5, -1, 5));
		//teapot.translate(new Vector(0, 0, 2.3));
		teapot.getMaterial().setColor(Color.WHITE);
		teapot.getMaterial().setSpecularHardness(200);
		teapot.getMaterial().setSpecularColor(Color.WHITE);

		//cone.rotateZ(-160);


        Scene scene = new Scene();
        scene.models.add(plane);
		//scene.models.add(sphere);
		//scene.models.add(cube);
		//scene.models.add(cone);
		//scene.models.add(teapot);

		Lamp lamp = new Lamp(new Coordinate(3, 0, 3.2), 10D, new Color(255, 255, 200), LampType.POINT);
		Lamp lamp1 = new Lamp(new Coordinate(-3, 0, 0), 7D, new Color(255, 255, 0), LampType.POINT);
		scene.lamps.add(lamp);
		scene.lamps.add(lamp1);

		Render render = new Render(scene);
		long timeElapsed = System.currentTimeMillis();
		for(int y = 0; y < HEIGHT; y++) {
			for(int x = 0; x < WIDTH; x++) {
				buffer.setRGB(x, y, render.getColor(imgToCartesianX(x), imgToCartesianY(y)).getRGB());
			}
			if(y % 10 == 0) {
				ImageIcon imgIcon = new ImageIcon(buffer);
				lbl.setIcon(imgIcon);
				frame.getContentPane().add(lbl, BorderLayout.CENTER);
				frame.pack();
				frame.setVisible(true);
			}
		}

		timeElapsed = System.currentTimeMillis() - timeElapsed;
		System.out.println("Elapsed render time: " + timeElapsed + "ms");

		ImageIcon imgIcon = new ImageIcon(buffer);
		lbl.setIcon(imgIcon);
		frame.getContentPane().add(lbl, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);

		System.out.println("Writing image to disk...");
		File image = new File("output.png");
		try {
			ImageIO.write(buffer, "PNG", image);
		}
		catch(Exception e) {
			System.out.println("Could not write image.");
		}
		System.out.println("Complete!");
	}
	
	private static double imgToCartesianX(double x) {
		return FOV_MULTIPLIER * ((x / (WIDTH - (WIDTH / 2))) - 1);
	}
	
	private static double imgToCartesianY(double y) {
		return ASPECT_RATIO * FOV_MULTIPLIER * (((y / (HEIGHT - (HEIGHT / 2))) - 1) * -1);
	}

}
