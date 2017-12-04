package me.alejandro.raytracer.objects;

import me.alejandro.raytracer.utils.EasyReader;

public class MTLLoader implements Cloneable {

    private EasyReader reader;
    private Material material;

    public MTLLoader(String fileName) {
        String path = getClass().getClassLoader().getResource("me/alejandro/raytracer/res/" + fileName).toString();
        path = path.substring(6);
        reader = new EasyReader(path);
    }

    public void loadMaterial() {
        String line = reader.readLine();

    }

    public MTLLoader clone() {
        try {
            return (MTLLoader) super.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Material getMaterial() {
        return material;
    }
}
