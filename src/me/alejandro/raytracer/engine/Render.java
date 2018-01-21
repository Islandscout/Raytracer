package me.alejandro.raytracer.engine;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import me.alejandro.raytracer.Main;
import me.alejandro.raytracer.entities.Lamp;
import me.alejandro.raytracer.entities.Model;
import me.alejandro.raytracer.objects.AABB;
import me.alejandro.raytracer.objects.Coordinate;
import me.alejandro.raytracer.objects.Triangle;
import me.alejandro.raytracer.objects.Vector;
import me.alejandro.raytracer.objects.kdtree.Tree;
import me.alejandro.raytracer.utils.Debug;

public class Render {

    //TODO: Clean up this mess!

    private final static int SSAA = 4; //recommended to disable. This increases render time by A LOT when enabled.

    private final static boolean DIFFUSE = true; //lambert diffuse
    private final static boolean PHONG_INTERPOLATION = true; //smooth shading
    private final static boolean TEXTURES = false;

    private final static int INDIRECT_SAMPLES = 4;

    private final static boolean SHADOWS = true;
    private final static boolean SHADOW_FIX = true; //fixes shadow termination artifact on smooth surfaces. May cause issues on non-closed geometry.
    //private final static int SOFT_SHADOW = false;

    private final static boolean REFLECTIONS = true; //issue: wont reflect a component color if there is no direct light source for the color.
    private final static int REFLECTION_BOUNCES = 1;

    private final static boolean PHONG_SPECULARITY = true;

    private final static boolean DEPTH_OF_FIELD = true;
    private final static double DEPTH_OF_FIELD_SIZE = 0.05;
    private final static double DEPTH_OF_FIELD_FOCUS_DISTANCE = 2D;

    private final static Color BACKGROUND_COLOR = new Color(50, 50, 50);

    private Coordinate currentBarycentric = new Coordinate(0, 0, 0);
    private Vector cameraPosition = new Vector(0, 0, 0); //DO NOT TOUCH
    private Scene scene;
    private ThreadLocalRandom random = ThreadLocalRandom.current();

    public Render(Scene scene) {
        this.scene = scene;
    }

    public Color getColor(double x, double y) {
        int red = 0;
        int green = 0;
        int blue = 0;
        for(int i = 0; i < SSAA || i == 0; i++) {

            Vector ray; //make a ray that shoots from the camera
            if(SSAA != 0) ray = new Vector(x + random.nextDouble() / (Main.WIDTH / 2), y + random.nextDouble() / (Main.HEIGHT / 2), 1);
            else ray = new Vector(x, y, 1);

            double offsetX;
            double offsetY;
            if(DEPTH_OF_FIELD) {
                offsetX = (random.nextDouble() - 0.5) * DEPTH_OF_FIELD_SIZE * 2;
                offsetY = (random.nextDouble() - 0.5) * DEPTH_OF_FIELD_SIZE * 2;
                ray.setX(-offsetX/DEPTH_OF_FIELD_FOCUS_DISTANCE + ray.getX());
                ray.setY(-offsetY/DEPTH_OF_FIELD_FOCUS_DISTANCE + ray.getY());
            }

            Triangle triangle = null;
            Coordinate intersectFirst = null;
            Coordinate barycentric = null;
            Model model = null;

            //choose closest triangle in pixel
            //start by looping through every model
            double intersectDistance = Double.MAX_VALUE;
            for(Model loopModel : scene.models) {

                //for this model, load triangles based on whether the acceleration tree is enabled or not
                List<Triangle> triangles;
                if(Main.RAYTREE_SIZE > 0) { //much, much faster renders. gotta go fast
                    triangles = new ArrayList<>();
                    if(DEPTH_OF_FIELD) loopModel.getTree().reconstruct(ray, new Coordinate(offsetX, offsetY, 0), triangles);
                    else loopModel.getTree().reconstructFromCamRay(ray, triangles);
                }
                else {
                    //slower, but not incredibly slow
                    //ignore objects with a bounding box that is not intersected by camera ray. This saves a LOT of render time.
                    if(DEPTH_OF_FIELD ? !rayIntersectsAABB(loopModel.getAabb(), new Coordinate(offsetX, offsetY, 0), ray) : !cameraRayIntersectsAABB(loopModel.getAabb(), ray)) {
                        //red += 50; //debugging
                        continue;
                    }
                }

                //now, loop through every triangle loaded into list
                for (Triangle loopTriangle : Main.RAYTREE_SIZE > 0 ? triangles : loopModel.triangles) {
                    Coordinate loopIntersect = DEPTH_OF_FIELD ? rayIntersectsTriangle(new Coordinate(offsetX, offsetY, 0).toVector(), ray, loopTriangle) : rayIntersectsTriangle(cameraPosition, ray, loopTriangle); //check intersection
                    if (loopIntersect != null) {
                        if (loopIntersect.getZ() < intersectDistance) { //Z testing
                            intersectDistance = loopIntersect.getZ();
                            triangle = loopTriangle; //the closest triangle is now chosen
                            intersectFirst = loopIntersect; //this is where the ray intersects with the triangle
                            model = loopModel; //the model of the closest triangle is now chosen
                            barycentric = new Coordinate(currentBarycentric); //barycentric coordinates of triangle used for smooth phong shading and texture mapping
                        }
                    }
                }
            }

            //if something was found, then...
            if(triangle != null) {

                //jeez...
                //interpolate normal vector based on intersection point on triangle for smooth shading
                Vector interpolatedNormal;
                if(PHONG_INTERPOLATION) {
                    Vector vertexNormal0 = new Vector(triangle.getNormal0());
                    vertexNormal0.multiply(barycentric.getZ());
                    Vector vertexNormal1 = new Vector(triangle.getNormal1());
                    vertexNormal1.multiply(barycentric.getX());
                    Vector vertexNormal2 = new Vector(triangle.getNormal2());
                    vertexNormal2.multiply(barycentric.getY());
                    interpolatedNormal = vertexNormal0;
                    interpolatedNormal.add(vertexNormal1);
                    interpolatedNormal.add(vertexNormal2);
                    interpolatedNormal.normalize();
                }
                else {
                    interpolatedNormal = triangle.getNormal();
                }

                //calculate direct lighting
                Vector camera = new Vector(0 - intersectFirst.getX(), 0 - intersectFirst.getY(), 0 - intersectFirst.getZ());
                camera.normalize();
                boolean lightRayHitSomething;
                for(Lamp loopLamp : scene.lamps) {
                    lightRayHitSomething = false;
                    Vector lampVectorNotNormalized = new Vector(loopLamp.getCoordinate().getX() - intersectFirst.getX(), loopLamp.getCoordinate().getY() - intersectFirst.getY(), loopLamp.getCoordinate().getZ() - intersectFirst.getZ());
                    Vector lampVector = new Vector(lampVectorNotNormalized);
                    lampVector.normalize();

                    //test for shadows
                    if(SHADOWS) {
                        //for(int i1 = 0; i1 < SOFT_SHADOW_SAMPLES; i1++) {
                        for(Model loopModel : scene.models) {

                            List<Triangle> triangles;
                            if(Main.RAYTREE_SIZE > 0) {
                                triangles = new ArrayList<>();
                                loopModel.getTree().reconstruct(lampVectorNotNormalized, intersectFirst, triangles);
                            }
                            else {
                                if(!rayIntersectsAABB(loopModel.getAabb(), intersectFirst, lampVectorNotNormalized)) {
                                    //red += 50; //debug
                                    continue;
                                }
                            }
                            for(Triangle loopTriangle : Main.RAYTREE_SIZE > 0 ? triangles : loopModel.triangles) {
                                Coordinate intersectLightPath = rayIntersectsTriangle(intersectFirst.toVector(), lampVectorNotNormalized, loopTriangle);
                                if(intersectLightPath != null && intersectFirst.distanceSquared(loopLamp.getCoordinate()) > intersectLightPath.distanceSquared(loopLamp.getCoordinate())) {
                                    if(SHADOW_FIX){
                                        if(model == loopModel && lampVector.angleRadians(loopTriangle.getNormal()) >= 1.5708) {
                                            lightRayHitSomething = true;
                                            break;
                                        }
                                        else if (model != loopModel) {
                                            lightRayHitSomething = true;
                                            break;
                                        }
                                    }
                                    else {
                                        lightRayHitSomething = true;
                                        break;
                                    }
                                }
                            }
                            if(lightRayHitSomething) break;
                        }
                        //}
                    }
                    if(!lightRayHitSomething && DIFFUSE) { //if there isn't something in the way of the light, then...
                        double lightIntensity = Math.cos(interpolatedNormal.angleRadians(lampVectorNotNormalized));
                        if(lightIntensity < 0) continue;
                        double lightDistance = Math.pow(lampVectorNotNormalized.length(), 2) + 1; //power of 2 because we must follow inverse square law
                        red += (int) ((1 - model.getMaterial().getReflectiveness()) * (lightIntensity * (loopLamp.getColor().getRed() * model.getMaterial().getColor().getRed() / 255) * loopLamp.getIntensity()) / lightDistance);
                        green += (int) ((1 - model.getMaterial().getReflectiveness()) * (lightIntensity * (loopLamp.getColor().getGreen() * model.getMaterial().getColor().getGreen() / 255) * loopLamp.getIntensity()) / lightDistance);
                        blue += (int) ((1 - model.getMaterial().getReflectiveness()) * (lightIntensity * (loopLamp.getColor().getBlue() * model.getMaterial().getColor().getBlue() / 255) * loopLamp.getIntensity()) / lightDistance);
                    }

                    //calculate specularity
                    if (PHONG_SPECULARITY && !lightRayHitSomething) {
                        Vector specular = new Vector(interpolatedNormal);

                        specular.add(lampVector);
                        specular.add(interpolatedNormal);

                        specular.normalize();
                        specular.multiply(2 * camera.dotProduct(interpolatedNormal));
                        specular.subtract(camera);
                        double specular_amount = Math.cos(specular.angleRadians(lampVectorNotNormalized));

                        if(specular_amount < 0) specular_amount = 0;
                        specular_amount = (Math.pow(specular_amount, model.getMaterial().getSpecularHardness()) / (Math.pow(lampVectorNotNormalized.length(), 2) + 1)) * model.getMaterial().getSpecularIntensity(); //hardness and intensity
                        red += (int) (specular_amount * (loopLamp.getColor().getRed() * model.getMaterial().getSpecularColor().getRed() / 255) * loopLamp.getIntensity());
                        green += (int) (specular_amount * (loopLamp.getColor().getGreen() * model.getMaterial().getSpecularColor().getGreen() / 255) * loopLamp.getIntensity());
                        blue += (int) (specular_amount * (loopLamp.getColor().getBlue() * model.getMaterial().getSpecularColor().getBlue() / 255) * loopLamp.getIntensity());
                    }
                }

                //calculate indirect lighting (very very slow)
                //1 bounce & shadows on the indirect object do not effect bounced ray
                double redIndirect = 0;
                double greenIndirect = 0;
                double blueIndirect = 0;
                for(int indirectSample = 0; indirectSample < INDIRECT_SAMPLES; indirectSample ++) {
                    Vector indirectVector = new Vector(interpolatedNormal);
                    Vector randomVector = new Vector((random.nextDouble() - 0.5)*2, (random.nextDouble() - 0.5)*2, (random.nextDouble() - 0.5)*2);
                    randomVector.normalize();
                    indirectVector.add(randomVector);
                    Coordinate currentIndirectBounce = null;
                    double distanceSquared = Double.MAX_VALUE;
                    Triangle indirectTriangle = null;
                    Model indirectModel = null;
                    for(Model loopModel : scene.models) {

                        List<Triangle> triangles;
                        if(Main.RAYTREE_SIZE > 0) {
                            triangles = new ArrayList<>();
                            loopModel.getTree().reconstruct(indirectVector, intersectFirst, triangles);
                        }
                        else {
                            if(!rayIntersectsAABB(loopModel.getAabb(), intersectFirst, indirectVector)) {
                                //red += 50; //debug
                                continue;
                            }
                        }

                        for(Triangle loopTriangle : Main.RAYTREE_SIZE > 0 ? triangles : loopModel.triangles) {
                            Coordinate intersectRayPath = rayIntersectsTriangle(intersectFirst.toVector(), indirectVector, loopTriangle);
                            if(intersectRayPath != null && intersectFirst.distanceSquared(intersectRayPath) < distanceSquared) {
                                distanceSquared = intersectFirst.distanceSquared(intersectRayPath);
                                indirectTriangle = loopTriangle;
                                currentIndirectBounce = intersectRayPath;
                                indirectModel = loopModel;
                            }
                        }
                    }
                    if(currentIndirectBounce == null) {
                        redIndirect += ((BACKGROUND_COLOR.getRed() * model.getMaterial().getColor().getRed()) / 255 / (double) INDIRECT_SAMPLES);
                        greenIndirect += ((BACKGROUND_COLOR.getGreen() * model.getMaterial().getColor().getGreen()) / 255 / (double) INDIRECT_SAMPLES);
                        blueIndirect += ((BACKGROUND_COLOR.getBlue() * model.getMaterial().getColor().getBlue()) / 255 / (double) INDIRECT_SAMPLES);
                        continue;
                    }
                    for(Lamp indirectLoopLamp : scene.lamps) {
                        Vector indirectLampVectorNotNormalized = new Vector(indirectLoopLamp.getCoordinate().getX() - currentIndirectBounce.getX(), indirectLoopLamp.getCoordinate().getY() - currentIndirectBounce.getY(), indirectLoopLamp.getCoordinate().getZ() - currentIndirectBounce.getZ());
                        Vector indirectLampVector = new Vector(indirectLampVectorNotNormalized);
                        indirectLampVector.normalize();
                        double lightIntensity = Math.cos(indirectTriangle.getNormal().angleRadians(indirectLampVectorNotNormalized));
                        if(lightIntensity < 0) continue;
                        double lightDistance = Math.pow(indirectLampVectorNotNormalized.length(), 2) + 1; //power of 2 because we must follow inverse square law
                        redIndirect += (((lightIntensity * (indirectLoopLamp.getColor().getRed() * indirectModel.getMaterial().getColor().getRed() * model.getMaterial().getColor().getRed() / 65025) * indirectLoopLamp.getIntensity()) / lightDistance) / INDIRECT_SAMPLES);
                        greenIndirect += (((lightIntensity * (indirectLoopLamp.getColor().getGreen() * indirectModel.getMaterial().getColor().getGreen() * model.getMaterial().getColor().getGreen() / 65025) * indirectLoopLamp.getIntensity()) / lightDistance) / INDIRECT_SAMPLES);
                        blueIndirect += (((lightIntensity * (indirectLoopLamp.getColor().getBlue() * indirectModel.getMaterial().getColor().getBlue() * model.getMaterial().getColor().getBlue() / 65025) * indirectLoopLamp.getIntensity()) / lightDistance) / INDIRECT_SAMPLES);
                    }
                }
                red += redIndirect;
                green += greenIndirect;
                blue += blueIndirect;

                //calculate reflections (issues with this)
                if(REFLECTIONS && model.getMaterial().getReflectiveness() > 0) {
                    Coordinate currentBounce = null;
                    for (int i1 = 0; i1 < REFLECTION_BOUNCES; i1++) {
                        Vector reflection = interpolatedNormal.clone();
                        reflection.multiply(2 * camera.dotProduct(interpolatedNormal));
                        reflection.subtract(camera);
                        double triangleDistance = Double.MAX_VALUE;
                        Triangle chosenTriangle = null;
                        Model chosenModel = null;
                        for(Model loopModel : scene.models) {

                            List<Triangle> triangles;
                            if(Main.RAYTREE_SIZE > 0) {
                                triangles = new ArrayList<>();
                                loopModel.getTree().reconstruct(reflection, intersectFirst, triangles);
                            }
                            else {
                                if(!rayIntersectsAABB(loopModel.getAabb(), intersectFirst, reflection)) {
                                    //red += 50; //debug
                                    continue;
                                }
                            }

                            for(Triangle loopTriangle : Main.RAYTREE_SIZE > 0 ? triangles : loopModel.triangles) {
                                Coordinate intersectLightPath = rayIntersectsTriangle(intersectFirst.toVector(), reflection, loopTriangle);
                                if (intersectLightPath != null) {
                                    double checkDistance = intersectLightPath.distanceSquared(intersectFirst);
                                    if(checkDistance < triangleDistance) {
                                        triangleDistance = checkDistance;
                                        chosenTriangle = loopTriangle;
                                        chosenModel = loopModel;
                                        currentBounce = intersectLightPath;
                                    }
                                    //double fresnel = Math.cos(camera.angleRadians(triangle.getNormal()));
                                }
                            }
                        }

                        if(chosenTriangle != null) {
                            for(Lamp loopLamp : scene.lamps) {
                                Vector toLamp = new Vector(loopLamp.getCoordinate().getX() - currentBounce.getX(), loopLamp.getCoordinate().getY() - currentBounce.getY(), loopLamp.getCoordinate().getZ() - currentBounce.getZ());
                                double lightIntensity = Math.cos(chosenTriangle.getNormal().angleRadians(toLamp));
                                if(lightIntensity < 0) continue;
                                double lightDistance = Math.pow(toLamp.length(), 2) + 1;
                                red += (int) ((lightIntensity * loopLamp.getIntensity() * (loopLamp.getColor().getRed() * chosenModel.getMaterial().getColor().getRed() * model.getMaterial().getColor().getRed() * model.getMaterial().getReflectiveness() / 65025)) / lightDistance) / (REFLECTION_BOUNCES);
                                green += (int) ((lightIntensity * loopLamp.getIntensity() * (loopLamp.getColor().getGreen() * chosenModel.getMaterial().getColor().getGreen() * model.getMaterial().getColor().getGreen() * model.getMaterial().getReflectiveness() / 65025)) / lightDistance) / (REFLECTION_BOUNCES);
                                blue += (int) ((lightIntensity * loopLamp.getIntensity() * (loopLamp.getColor().getBlue() * chosenModel.getMaterial().getColor().getBlue() * model.getMaterial().getColor().getBlue() * model.getMaterial().getReflectiveness() / 65025)) / lightDistance) / (REFLECTION_BOUNCES);
                            }

                        }
                        else {
                            red += (int) (BACKGROUND_COLOR.getRed() * model.getMaterial().getReflectiveness()) / (REFLECTION_BOUNCES);
                            green += (int) (BACKGROUND_COLOR.getGreen() * model.getMaterial().getReflectiveness()) / (REFLECTION_BOUNCES);
                            blue += (int) (BACKGROUND_COLOR.getBlue() * model.getMaterial().getReflectiveness()) / (REFLECTION_BOUNCES);
                        }
                    }
                }
                if(TEXTURES) { //many thanks to this person: https://computergraphics.stackexchange.com/questions/1866/how-to-map-square-texture-to-triangle
                    int u = (int) (((barycentric.getZ() * triangle.getTexCoord0().getX()) + (barycentric.getX() * triangle.getTexCoord1().getX()) + (barycentric.getY() * triangle.getTexCoord2().getX())) * (model.getMaterial().getTexture().getWidth() - 1));
                    int v = (int) (((barycentric.getZ() * triangle.getTexCoord0().getY()) + (barycentric.getX() * triangle.getTexCoord1().getY()) + (barycentric.getY() * triangle.getTexCoord2().getY())) * (model.getMaterial().getTexture().getHeight() - 1));
                    Color textureColor = new Color(model.getMaterial().getTexture().getRGB(u, v));
                    red = (red * textureColor.getRed()) / 255;
                    green = (green * textureColor.getGreen()) / 255;
                    blue = (blue * textureColor.getBlue()) / 255;
                }
            }
            else { //if nothing was found, fill pixel with background color
                red += BACKGROUND_COLOR.getRed();
                green += BACKGROUND_COLOR.getGreen();
                blue += BACKGROUND_COLOR.getBlue();
            }
        }
        red /= SSAA > 0 ? SSAA : 1;
        green /= SSAA > 0 ? SSAA : 1;
        blue /= SSAA > 0 ? SSAA : 1;
        if(red > 255) red = 255;
        if(green > 255) green = 255;
        if(blue > 255) blue = 255;
        if(red < 0) red = 0;
        if(green < 0) green = 0;
        if(blue < 0) blue = 0;
        return new Color(red, green, blue);
    }

    //skidded from Moller Trumbore's ray-triangle-intersection algorithm
    private Coordinate rayIntersectsTriangle(Vector rayOrigin, Vector rayVector, Triangle inTriangle) {
        Vector rayVect = new Vector(rayVector);
        double EPSILON = 0.0000001;
        Vector vertex0 = inTriangle.getCoord0().toVector();
        Vector vertex1 = inTriangle.getCoord1().toVector();
        Vector vertex2 = inTriangle.getCoord2().toVector();
        Vector edge1, edge2, h, s, q;
        double a,f,u,v;
        edge1 = new Vector(vertex1.getX() - vertex0.getX(), vertex1.getY() - vertex0.getY(), vertex1.getZ() - vertex0.getZ());
        edge2 = new Vector(vertex2.getX() - vertex0.getX(), vertex2.getY() - vertex0.getY(), vertex2.getZ() - vertex0.getZ());
        h = rayVect.crossProduct(edge2);
        a = edge1.dotProduct(h);
        if (a > -EPSILON && a < EPSILON) return null;
        f = 1/a;
        s = new Vector(rayOrigin.getX() - vertex0.getX(), rayOrigin.getY() - vertex0.getY(), rayOrigin.getZ() - vertex0.getZ());
        u = f * (s.dotProduct(h));
        if (u < 0.0 || u > 1.0) return null;
        q = s.crossProduct(edge1);
        v = f * rayVect.dotProduct(q);
        if (v < 0.0 || u + v > 1.0) return null;
        // At this stage we can compute t to find out where the intersection point is on the line.
        double t = f * edge2.dotProduct(q);
        if (t > EPSILON) { //ray intersection
            rayVect.multiply(t);
            currentBarycentric.setX(u);
            currentBarycentric.setY(v);
            currentBarycentric.setZ(1-u-v);
            return new Coordinate(rayOrigin.getX() + rayVect.getX(), rayOrigin.getY() + rayVect.getY(), rayOrigin.getZ() + rayVect.getZ());
        }
        else return null; // This means that there is a line intersection but not a ray intersection.
    }

    //big thanks to this thread: https://bukkit.org/threads/check-if-vector-goes-through-certain-area.393647/
    private boolean rayIntersectsAABB(AABB aabb, Coordinate origin, Vector ray) {
        Vector mini = aabb.getMinimum().toVector();
        Vector maxi = aabb.getMaximum().toVector();

        double t1 = ((mini.getX() - origin.getX()) / ray.getX());
        double t2 = ((maxi.getX() - origin.getX()) / ray.getX());
        double t3 = ((mini.getY() - origin.getY()) / ray.getY());
        double t4 = ((maxi.getY() - origin.getY()) / ray.getY());
        double t5 = ((mini.getZ() - origin.getZ()) / ray.getZ());
        double t6 = ((maxi.getZ() - origin.getZ()) / ray.getZ());

        double tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        double tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        //If the ray intersects the AABB, but the AABB is behind the ray
        if (tmax < 0) {
            return false;
        }

        //If the ray doesn't intersect the AABB.
        if (tmin > tmax) {
            return false;
        }
        return true;
    }

    private boolean cameraRayIntersectsAABB(AABB aabb, Vector ray) {
        Vector mini = aabb.getMinimum().toVector();
        Vector maxi = aabb.getMaximum().toVector();

        double t1 = (mini.getX() / ray.getX());
        double t2 = (maxi.getX() / ray.getX());
        double t3 = (mini.getY() / ray.getY());
        double t4 = (maxi.getY() / ray.getY());
        double t5 = (mini.getZ() / ray.getZ());
        double t6 = (maxi.getZ() / ray.getZ());

        double tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        double tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        //If the ray intersects the AABB, but the AABB is behind the ray
        if (tmax < 0) return false;

        //If the ray doesn't intersect the AABB.
        return !(tmin > tmax);
    }

    //this is a joke, lol
    /*
    private boolean cameraRayInBoundsOfModel(Vector vec, Model model) {
        if(model.getAabb().getMaximum().getZ() <= 0 && model.getAabb().getMinimum().getZ() <= 0) return false; //you'll never see the object behind you, so why test for intersection?
        if(model.getAabb().getMinimum().getZ() <= 0) return true; //handle AABB that is at 0 or negative Z. Prevents division by zero and other logic/geometric problems.

        //project box as a 2d rectangle on screen (very painful)
        double xDivisorMax = model.getAabb().getMaximum().getX() / model.getAabb().getMinimum().getZ() > model.getAabb().getMaximum().getX() / model.getAabb().getMaximum().getZ() ? model.getAabb().getMinimum().getZ() : model.getAabb().getMaximum().getZ();
        double yDivisorMax = model.getAabb().getMaximum().getY() / model.getAabb().getMinimum().getZ() > model.getAabb().getMaximum().getY() / model.getAabb().getMaximum().getZ() ? model.getAabb().getMinimum().getZ() : model.getAabb().getMaximum().getZ();
        double yDivisorMin = model.getAabb().getMinimum().getY() / model.getAabb().getMinimum().getZ() < model.getAabb().getMinimum().getY() / model.getAabb().getMaximum().getZ() ? model.getAabb().getMinimum().getZ() : model.getAabb().getMaximum().getZ();
        double xDivisorMin = model.getAabb().getMinimum().getX() / model.getAabb().getMinimum().getZ() < model.getAabb().getMinimum().getX() / model.getAabb().getMaximum().getZ() ? model.getAabb().getMinimum().getZ() : model.getAabb().getMaximum().getZ();
        if(vec.getY() > model.getAabb().getMaximum().getY() / yDivisorMax || vec.getY() < model.getAabb().getMinimum().getY() / yDivisorMin) return false;
        if(vec.getX() > model.getAabb().getMaximum().getX() / xDivisorMax || vec.getX() < model.getAabb().getMinimum().getX() / xDivisorMin) return false;
        return true;
    }
    */
}
