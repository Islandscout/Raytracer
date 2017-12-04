package me.alejandro.raytracer;

import java.util.ArrayList;

import me.alejandro.raytracer.entities.Lamp;
import me.alejandro.raytracer.entities.Model;

public class Entities {
	
	private ArrayList<Model> models = new ArrayList<>();
	private ArrayList<Lamp> lamps = new ArrayList<>();

	public ArrayList<Model> getModels() {
		return models;
	}

	public void setModels(ArrayList<Model> models) {
		this.models = models;
	}

	public ArrayList<Lamp> getLamps() {
		return lamps;
	}

	public void setLamps(ArrayList<Lamp> lamps) {
		this.lamps = lamps;
	}
	
	
	

}
