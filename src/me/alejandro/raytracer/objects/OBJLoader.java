package me.alejandro.raytracer.objects;

import me.alejandro.raytracer.entities.Model;
import me.alejandro.raytracer.utils.EasyReader;

import javax.jws.WebParam;
import java.net.URL;
import java.util.ArrayList;

public class OBJLoader implements Cloneable {

    private EasyReader reader;
    private Model model;

    public OBJLoader(String fileName) {
        String path = "resources/" + fileName;
        reader = new EasyReader(path); //might as well use EasyReader. I am not going to make my own file reader.
        if (reader.bad())
        {
            System.err.println("Can't open \"" + path + "\"");
            System.exit(1);
        }
    }

    public void loadModel() {
        String line = reader.readLine();
        ArrayList<Triangle> triangles = new ArrayList<>();
        ArrayList<Coordinate> verticies = new ArrayList<>();
        ArrayList<Coordinate> textureCoords = new ArrayList<>();
        ArrayList<Vector> normals = new ArrayList<>();
        String name = "Untitled";

        while (line != null) {
            if(line.equals("")) {
                line = (reader.readLine());
                continue;
            }
            if(line.charAt(0) == 'v' && line.charAt(1) == 'n') { //vertex normal
                String[] parts = line.split(" ");
                normals.add(new Vector(Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3])));
            }
            else if(line.charAt(0) == 'v' && line.charAt(1) == 't') { //texture coordinates
                String[] parts = line.split(" ");
                textureCoords.add(new Coordinate(Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), 0));
            }
            else if(line.charAt(0) == 'v' && line.charAt(1) == ' ') { //vertex coordinates
                String[] parts = line.split(" ");
                verticies.add(new Coordinate(Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3])));
            }
            else if(line.charAt(0) == 'o' && line.charAt(1) == ' ') { //object name
                name = line.substring(2);
            }
            else if(line.charAt(0) == 'f' && line.charAt(1) == ' ') { //define triangle
                int face_verticies[] = new int[3];
                int face_normals[] = new int[3];
                int texture_coordinates[] = new int[3];
                String[] parts = line.split(" ");
                for(int i = 1; i < parts.length; i ++) {
                    String[] moreParts = parts[i].split("/");
                    face_verticies[i - 1] = Integer.parseInt(moreParts[0]) - 1;
                    if(!moreParts[1].equals("")) texture_coordinates[i - 1] = Integer.parseInt(moreParts[1]) - 1;
                    face_normals[i - 1] = Integer.parseInt(moreParts[2]) - 1;
                }
                if(textureCoords.size() == 0) textureCoords.add(new Coordinate(0, 0, 0));
                triangles.add(new Triangle(verticies.get(face_verticies[0]), verticies.get(face_verticies[1]), verticies.get(face_verticies[2]), normals.get(face_normals[0]), normals.get(face_normals[1]), normals.get(face_normals[2]), textureCoords.get(texture_coordinates[0]), textureCoords.get(texture_coordinates[1]), textureCoords.get(texture_coordinates[2])));
            }
            line = reader.readLine();
        }
        //TODO: Automatically fetch texture from MTL file and apply it to material.
        model = new Model(0, 0, 0, triangles, new Material(), name);
        System.out.println("Loaded " + name + ", containing " + verticies.size() + " vertices and " + triangles.size() + " triangles.");
    }

    public OBJLoader clone() {
        try {
            return (OBJLoader) super.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Model getModel() {
        return model;
    }
}
