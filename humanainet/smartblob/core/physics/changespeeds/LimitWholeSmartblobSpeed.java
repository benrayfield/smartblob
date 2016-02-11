/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.physics.changespeeds;
import java.util.List;

import humanainet.smartblob.core.physics.ChangeSpeed;
import humanainet.smartblob.core.trianglemesh.LayeredZigzag;
import humanainet.smartblob.core.trianglemesh.MovCorner;
import humanainet.smartblob.core.trianglemesh.MovLine;
import humanainet.smartblob.core.trianglemesh.SmartblobTri;

/** Should be used at lower speed limit than individual points are limited
like in LimitWholeSmartblobSpeed, but in general its worse physics to
limit anything by speed in absolute space instead of relative to the smartblob
because it causes things like slowing in mid air when change shape fast.
Its a tradeoff to raise stability of vibrating points.
Higher speed limits act on the blobs less often.
*/
public class LimitWholeSmartblobSpeed implements ChangeSpeed{
	
	public float maxSpeed;
	
	public LimitWholeSmartblobSpeed(float maxSpeed){
		this.maxSpeed = maxSpeed;
	}

	public void changeSpeed(SmartblobTri blob, float secondsSinceLastCall){
		float maxSq = maxSpeed*maxSpeed;
		List<MovCorner> corners = blob.allMovCorners();
		float momentumY = 0, momentumX = 0, mass = 0;
		for(MovCorner c : corners){
			mass += c.mass;
			momentumY += c.speedY*c.mass;
			momentumX += c.speedX*c.mass;
		}
		float aveSpeedY = momentumY/mass;
		float aveSpeedX = momentumX/mass;
		float blobSpeedSq = aveSpeedY*aveSpeedY + aveSpeedX*aveSpeedX;
		if(blobSpeedSq <= maxSq) return;
		float blobSpeed = (float)Math.sqrt(blobSpeedSq);
		float newSpeed = maxSpeed; //TODO gradual decay of speed change?
		float mult = newSpeed/blobSpeed;
		float newAveSpeedY = aveSpeedY*mult;
		float newAveSpeedX = aveSpeedX*mult;
		float addToEachYSpeed = newAveSpeedY-aveSpeedY;
		float addToEachXSpeed = newAveSpeedX-aveSpeedX;
		for(MovCorner c : corners){
			c.addToSpeedY += addToEachYSpeed;
			c.addToSpeedX += addToEachXSpeed;
		}
	}

}