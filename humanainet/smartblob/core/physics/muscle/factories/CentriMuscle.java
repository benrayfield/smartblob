/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.physics.muscle.factories;
import humanaicore.common.Rand;
import humanainet.smartblob.core.physics.Muscle;
import humanainet.smartblob.core.physics.muscle.LineMuscle;
import humanainet.smartblob.core.trianglemesh.centri.Centriblob;

public class CentriMuscle implements Muscle{
	
	public final Centriblob cblob;
	public final int angle;
	public final float minRadius, maxRadius;
	
	public CentriMuscle(Centriblob cblob, int angle, float minRadius, float maxRadius){
		this.cblob = cblob;
		this.angle = angle;
		this.minRadius = minRadius;
		this.maxRadius = maxRadius;
	}
	
	//TODO should push targetRadius toward observed radius (as read() returns), but that code shouldnt be in this class.
	//Maybe it should go in SmartblobBrain or a wrapper of it.
	
	public float read(){
		float radius = cblob.observeRadius(angle);
		if(radius < minRadius) return 0;
		if(maxRadius < radius) return 1;
		return (radius-minRadius)/(maxRadius-minRadius);
	}
	
	public void pushToward(float writeFraction, float force){
		//TODO (do this in Centriblob.setTargetRadius func etc, not here):
		//push average of transformed side of LinearInterpolate1Var toward writeFraction
		if(writeFraction < 0 || 1 < writeFraction) throw new RuntimeException("writeFraction="+writeFraction);
		if(force < 0 || 1 < force) throw new RuntimeException("force="+force+" but must be a fraction, as its a decay var.");
		float observedTargetRadius = cblob.observeTargetRadius(angle);
		float targetTargetRadius = minRadius + (maxRadius-minRadius)*writeFraction;
		float newTargetRadius = targetTargetRadius*force + (1-force)*observedTargetRadius;
		cblob.setTargetRadius(angle, newTargetRadius);
	}

}