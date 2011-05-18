package TestJME3;

import java.util.Random;

// import TestGraphics.DrawnNode;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.TangentBinormalGenerator;

/**
 * Sample 2 - How to use nodes as handles to manipulate objects in the scene
 * graph. You can rotate, translate, and scale objects by manipulating their
 * parent nodes. The Root Node is special: Only what is attached to the Root
 * Node appears in the scene.
 */
public class TestJME3 extends SimpleApplication {

	private final int NODE_COUNT = 5500;
	private final float LARGE_SPHERE_RADIUS = 3.0f;
	
    float angle;
    PointLight pl;
    Geometry lightMdl;

    public static void main(String[] args){
        TestJME3 app = new TestJME3();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // Geometry teapot = (Geometry) assetManager.loadModel("Models/Teapot/Teapot.obj");
        // TangentBinormalGenerator.generate(teapot.getMesh(), true);

        // teapot.setLocalScale(2f);
        
    	Sphere s = new Sphere(3, 2, 2);
        Geometry sphere = new Geometry ("Sphere", s);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");

        		//        mat.selectTechnique("GBuf");
        
        mat.setFloat("Shininess", 12);
        mat.setBoolean("UseMaterialColors", true);

//        mat.setTexture("ColorRamp", assetManager.loadTexture("Textures/ColorRamp/cloudy.png"));
//
//        mat.setBoolean("VTangent", true);
//        mat.setBoolean("Minnaert", true);
//        mat.setBoolean("WardIso", true);
//        mat.setBoolean("VertexLighting", true);
//        mat.setBoolean("LowQuality", true);
//        mat.setBoolean("HighQuality", true);

        mat.setColor("Ambient",  ColorRGBA.Black);
        mat.setColor("Diffuse",  ColorRGBA.Gray);
        mat.setColor("Specular", ColorRGBA.Gray);
        
        sphere.setMaterial(mat);
        
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-1, -1, -1).normalizeLocal());
        dl.setColor(ColorRGBA.Cyan);
        rootNode.addLight(dl);
        
        generateNodes();
    }

    private void generateNodes() {
		Random random = new Random();
		//random.setSeed(nodeSeed);
		// nodeSeed++;
		// 500 should be the default seed
		
		Sphere sphere = new Sphere(10, 10, 0.03f);;
		Geometry geometry;
		Material material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		
        material.setFloat("Shininess", 12);
        material.setBoolean("UseMaterialColors", true);

        material.setColor("Ambient",  ColorRGBA.Black);
        material.setColor("Diffuse",  ColorRGBA.Gray);
        material.setColor("Specular", ColorRGBA.Gray);
        
        material.setBoolean("LowQuality", true);
		
		float x, y, z;
		float radius = LARGE_SPHERE_RADIUS;
		
		for (int i = 0; i < NODE_COUNT; i++) {
			do {
				x = (float)(radius * 2 * random.nextFloat() - radius);
				y = (float)(radius * 2 * random.nextFloat() - radius);
				z = (float)(radius * 2 * random.nextFloat() - radius);
			} while (Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2) > Math.pow(radius, 2));
		
			geometry = new Geometry("Sphere", sphere);
			geometry.setMaterial(material);
			
			geometry.move(x, y, z);
			
			rootNode.attachChild(geometry);
		}
		
		//System.out.println("Last node float: " + random.nextFloat());
	}
    
    @Override
    public void simpleUpdate(float tpf){
//        cam.setLocation(new Vector3f(2.0632997f, 1.9493936f, 2.6885238f));
//        cam.setRotation(new Quaternion(-0.053555284f, 0.9407851f, -0.17754152f, -0.28378546f));

        // angle += tpf;
        // angle %= FastMath.TWO_PI;
        
        // pl.setPosition(new Vector3f(FastMath.cos(angle) * 2f, 0.5f, FastMath.sin(angle) * 2f));
        // lightMdl.setLocalTranslation(pl.getPosition());
    }
}