/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.physics.changespeeds;
import humanainet.smartblob.core.physics.ChangeSpeed;
import humanainet.smartblob.core.trianglemesh.LayeredZigzag;
import humanainet.smartblob.core.trianglemesh.MovCorner;
import humanainet.smartblob.core.trianglemesh.MovLine;
import humanainet.smartblob.core.trianglemesh.SmartblobTri;

/** does not conserve energy. Instead, truncates speed into the chosen range,
keeping the direction of that speed.
*/
public class LimitSpeedPerPoint implements ChangeSpeed{
	
	public float minSpeed, maxSpeed;
	
	public LimitSpeedPerPoint(float minSpeed, float maxSpeed){
		this.minSpeed = minSpeed;
		this.maxSpeed = maxSpeed;
	}

	public void changeSpeed(SmartblobTri blob, float secondsSinceLastCall){
		float minSq = minSpeed*minSpeed;
		float maxSq = maxSpeed*maxSpeed;
		for(MovCorner c : blob.allMovCorners()){
			float dy = c.speedY, dx = c.speedX;
			float speedSq = dy*dy + dx*dx;
			if(minSq <= speedSq && speedSq <= maxSq) continue;
			if(speedSq == 0) continue; //rare. it will be pushed on by springs and probably have nonzero speed next time.
			float speed = (float)Math.sqrt(speedSq);
			float newSpeed = Math.max(minSpeed, Math.min(speed, maxSpeed));
			float mult = newSpeed/speed;
			c.speedY *= mult;
			c.speedX *= mult;
		}
	}

}