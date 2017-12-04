package me.alejandro.raytracer.entities;

import me.alejandro.raytracer.objects.Coordinate;

import java.awt.*;

public class Lamp implements Cloneable {

    private Coordinate coordinate;
    private double intensity;
    private Color color;
    private LampType lampType;

    public Lamp(Coordinate coordinate, double intensity, Color color, LampType lampType) {
        this.coordinate = coordinate;
        this.intensity = intensity;
        this.color = color;
        this.lampType = lampType;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public LampType getLampType() {
        return lampType;
    }

    public void setLampType(LampType lampType) {
        this.lampType = lampType;
    }

    public Lamp clone() {
        try {
            return (Lamp) super.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
