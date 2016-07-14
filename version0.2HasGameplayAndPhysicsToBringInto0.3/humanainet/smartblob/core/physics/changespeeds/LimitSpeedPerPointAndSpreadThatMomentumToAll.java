/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.physics.changespeeds;
import java.util.List;

import humanainet.smartblob.core.physics.ChangeSpeed;
import humanainet.smartblob.core.trianglemesh.LayeredZigzag;
import humanainet.smartblob.core.trianglemesh.MovCorner;
import humanainet.smartblob.core.trianglemesh.MovLine;
import humanainet.smartblob.core.trianglemesh.SmartblobTri;

public class LimitSpeedPerPointAndSpreadThatMomentumToAll implements ChangeSpeed{
	
	public float approxMaxSpeed;
	
	public LimitSpeedPerPointAndSpreadThatMomentumToAll(float approxMaxSpeed){
		this.approxMaxSpeed = approxMaxSpeed;
	}

	public void changeSpeed(SmartblobTri blob, float secondsSinceLastCall){
		float maxSq = approxMaxSpeed*approxMaxSpeed;
		List<MovCorner> corners = blob.allMovCorners();
		float yMomentumAdded = 0;
		float xMomentumAdded = 0;
		float totalMass = 0;
		//TODO lots of code in this func needs optimizing. Move math out of loops
		for(MovCorner c : corners){
			totalMass += c.mass;
			float dy = c.speedY, dx = c.speedX;
			float speedSq = dy*dy + dx*dx;
			if(speedSq <= maxSq) continue;
			//speed can never be 0 here
			float speed = (float)Math.sqrt(speedSq);
			float speedToRemove = speed-approxMaxSpeed;
			//new speed is approxMaxSpeed
			float mult = approxMaxSpeed/speed;
			float newYSpeed = c.speedY*mult;
			float newXSpeed = c.speedX*mult;
			float addToYSpeed = newYSpeed-c.speedY;
			float addToXSpeed = newXSpeed-c.speedX;
			yMomentumAdded += addToYSpeed*c.mass;
			xMomentumAdded += addToXSpeed*c.mass;
			c.speedY += addToYSpeed;
			c.speedX += addToXSpeed;
		}
		for(MovCorner c : corners){
			float iAmFractionOfTotalMass = c.mass/totalMass;
			float yMomentumToAdd = -yMomentumAdded*iAmFractionOfTotalMass;
			float xMomentumToAdd = -xMomentumAdded*iAmFractionOfTotalMass;
			c.speedY += yMomentumToAdd/c.mass;
			c.speedX += xMomentumToAdd/c.mass;
		}
	}

}