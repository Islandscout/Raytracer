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
        String path = getClass().getClassLoader().getResource("me/alejandro/raytracer/res/" + fileName).toString();
        path = path.substring(6); //there really needs to be a better way of doing this
        reader = new EasyReader(path); //might as well use EasyReader. I am not going to make my own file reader.
    }

    public void loadModel() {
        String line = reader.readLine();
        ArrayList<Triangle> triangles = new ArrayList<>();
        ArrayList<Coordinate> verticies = new ArrayList<>();
        ArrayList<Integer> indicies = new ArrayList<>();
        ArrayList<Vector> normals = new ArrayList<>();
        String name = "Untitled";

        while (line != null) {
            if(line.equals("")) {
                line = (reader.readLine());
                continue;
            }
            if(line.charAt(0) == 'v' && line.charAt(1) == 'n') {
                String[] parts = line.split(" ");
                normals.add(new Vector(Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3])));
            }
            else if(line.charAt(0) == 'v' && line.charAt(1) == ' ') {
                String[] parts = line.split(" ");
                verticies.add(new Coordinate(Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3])));
            }
            else if(line.charAt(0) == 'o' && line.charAt(1) == ' ') {
                name = line.substring(2);
            }
            else if(line.charAt(0) == 'f' && line.charAt(1) == ' ') {
                int face_verticies[] = new int[1024]; //might have to make this a bit bigger
                int face_normals[] = new int[1024]; //may need to replace this array with an int
                String[] parts = line.split(" ");
                for(int i = 1; i < parts.length; i ++) {
                    String[] moreParts = parts[i].split("/");
                    face_verticies[i - 1] = Integer.parseInt(moreParts[0]) - 1;
                    face_normals[i - 1] = Integer.parseInt(moreParts[2]) - 1;
                }
                triangles.add(new Triangle(verticies.get(face_verticies[0]), verticies.get(face_verticies[1]), verticies.get(face_verticies[2]), normals.get(face_normals[0])));
            }
            line = reader.readLine();
        }
        //System.out.println(triangles.get(0).getCoord0().getX() + " " + triangles.get(0).getCoord0().getY() + " " + triangles.get(0).getCoord0().getZ());
        model = new Model(0, 0, 0, triangles, new Material(), name);
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
