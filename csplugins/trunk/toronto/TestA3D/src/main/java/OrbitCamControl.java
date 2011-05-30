import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Camera;

public class OrbitCamControl {

    private Camera camera;
    
    private Vector3 position = new Vector3();
    private Vector3 target = new Vector3();
    private Vector3 upAxis = new Vector3();
    private Vector3 worldUpAxis = new Vector3();
    
    private double zoomSpeed = 0.01;
    private double horizontalRotateSpeed = 0.01;
    private double verticalRotateSpeed = 0.01;
    
    private double distance = 10;
    private double minDistance = 2;
    private double maxDistance = 25;
    
    public OrbitCamControl(Camera camera, Vector3 target, Vector3 worldUpAxis) {
    	this.target = target;
    	this.camera = camera;
    	this.worldUpAxis = worldUpAxis;
    	
    	upAxis.set(camera.getLocation());
    }
    
    private void rotateHorizontal(double angle) {
    	//camera.
    }
    
}