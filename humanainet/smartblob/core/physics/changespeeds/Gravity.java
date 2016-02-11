/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.physics.changespeeds;
import humanainet.smartblob.core.physics.ChangeSpeed;
import humanainet.smartblob.core.trianglemesh.LayeredZigzag;
import humanainet.smartblob.core.trianglemesh.MovCorner;
import humanainet.smartblob.core.trianglemesh.MovLine;
import humanainet.smartblob.core.trianglemesh.SmartblobTri;

public class Gravity implements ChangeSpeed{
	
	public float accelerateY;
	
	public Gravity(float accelerateY){
		this.accelerateY = accelerateY;
	}

	public void changeSpeed(SmartblobTri blob, float secondsSinceLastCall){
		float addToAllSpeeds = accelerateY*secondsSinceLastCall;
		for(MovCorner c : blob.allMovCorners()){
			c.addToSpeedY += addToAllSpeeds;
		}
	}

}