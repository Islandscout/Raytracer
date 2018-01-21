package me.alejandro.raytracer.engine;

import me.alejandro.raytracer.entities.Lamp;
import me.alejandro.raytracer.entities.Model;
import me.alejandro.raytracer.objects.kdtree.Tree;

import java.util.ArrayList;

public class Scene {

    public ArrayList<Model> models = new ArrayList<>();
    public ArrayList<Lamp> lamps = new ArrayList<>();

}
