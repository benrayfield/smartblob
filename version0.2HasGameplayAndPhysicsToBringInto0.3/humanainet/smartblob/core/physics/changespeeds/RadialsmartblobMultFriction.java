/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.physics.changespeeds;
import humanainet.smartblob.core.trianglemesh.LayeredZigzag;
import humanainet.smartblob.core.trianglemesh.radial.Radiblob;

public class RadialsmartblobMultFriction extends AbstractChangeSpeedLZ{
	
	public float friction;
	
	public RadialsmartblobMultFriction(float friction){
		this.friction = friction;
	}
	
	public void changeSpeed(LayeredZigzag blob, float secondsSinceLastCall){
		secondsSinceLastCall = Math.max(0, Math.min(secondsSinceLastCall, .5f));
		float mult = 1-friction*secondsSinceLastCall;
		if(!(blob instanceof Radiblob)) return;
		Radiblob r = (Radiblob) blob;
		r.blobXSpeed *= mult;
		r.blobYSpeed *= mult;
		r.blobAngleSpeed *= mult;
	}

}