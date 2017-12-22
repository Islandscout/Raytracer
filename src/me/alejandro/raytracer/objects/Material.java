package me.alejandro.raytracer.objects;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Material implements Cloneable {

    private Color color;
    private Color specularColor;
    private double specularIntensity;
    private int specularHardness;
    private double alpha;
    private double reflectiveness;
    private double gloss;
    private BufferedImage texture;

    public Material() {
        this.color = new Color(0, 0, 0);
        this.specularColor = new Color(0 , 0, 0);
        this.specularIntensity = 1;
        this.specularHardness = 50;
        this.alpha = 1D;
        this.reflectiveness = 0D;
        this.gloss = 1D;
        this.texture = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        this.texture.setRGB(0, 0, Color.WHITE.getRGB());
    }

    public Material(Color color, Color specularColor, double specularIntensity, int specularHardness, double alpha, double reflectiveness, double gloss) {
        this.color = color;
        this.specularColor = specularColor;
        this.specularIntensity = specularIntensity;
        this.specularHardness = specularHardness;
        this.alpha = alpha;
        this.reflectiveness = reflectiveness;
        this.gloss = gloss;
        this.texture = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        this.texture.setRGB(0, 0, Color.WHITE.getRGB());
    }

    public Material(Color color, Color specularColor, double specularIntensity, int specularHardness, double alpha, double reflectiveness, double gloss, BufferedImage texture) {
        this.color = color;
        this.specularColor = specularColor;
        this.specularIntensity = specularIntensity;
        this.specularHardness = specularHardness;
        this.alpha = alpha;
        this.reflectiveness = reflectiveness;
        this.gloss = gloss;
        this.texture = texture;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getReflectiveness() {
        return reflectiveness;
    }

    public void setReflectiveness(double reflectiveness) {
        this.reflectiveness = reflectiveness;
    }

    public double getGloss() {
        return gloss;
    }

    public void setGloss(double gloss) {
        this.gloss = gloss;
    }

    public Color getSpecularColor() {
        return specularColor;
    }

    public void setSpecularColor(Color specularColor) {
        this.specularColor = specularColor;
    }

    public double getSpecularIntensity() {
        return specularIntensity;
    }

    public void setSpecularIntensity(double specularIntensity) {
        this.specularIntensity = specularIntensity;
    }

    public int getSpecularHardness() {
        return specularHardness;
    }

    public void setSpecularHardness(int specularHardness) {
        this.specularHardness = specularHardness;
    }

    public BufferedImage getTexture() {
        return texture;
    }

    public void setTexture(BufferedImage texture) {
        this.texture = texture;
    }

    public Material clone() {
        try {
            return (Material) super.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
