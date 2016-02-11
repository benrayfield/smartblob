/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.physics.changespeeds;
import java.util.List;

import humanainet.smartblob.core.physics.ChangeSpeed;
import humanainet.smartblob.core.trianglemesh.LayeredZigzag;
import humanainet.smartblob.core.trianglemesh.MovCorner;
import humanainet.smartblob.core.trianglemesh.MovLine;
import humanainet.smartblob.core.trianglemesh.SmartblobTri;

/** does not conserve direction of momentum but does conserve total speed of all points,
which can be in opposite directions so it may convert speed to heat.
<br><br>
maxSpeed can be exceeded slightly since speed is spread to all points
after limiting it to maxSpeed in each. Whatever is removed from one speed is spread.
<br><br>
TODO check sparsedoppler energy norm, which holds sum of radius constant,
as compared to squared radius or sqrt etc.
That may lead to other kinds of norming to balance this.
*/
public class LimitSpeedPerPointAndSpreadThatSpeedToOtherPointsInTheirCurrentDirections implements ChangeSpeed{
	
	public float approxMaxSpeed;
	
	public LimitSpeedPerPointAndSpreadThatSpeedToOtherPointsInTheirCurrentDirections(float approxMaxSpeed){
		this.approxMaxSpeed = approxMaxSpeed;
	}

	public void changeSpeed(SmartblobTri blob, float secondsSinceLastCall){
		float maxSq = approxMaxSpeed*approxMaxSpeed;
		float momentumMagnitudeRemoved = 0;
		List<MovCorner> corners = blob.allMovCorners();
		for(MovCorner c : corners){
			float dy = c.speedY, dx = c.speedX;
			float speedSq = dy*dy + dx*dx;
			if(speedSq <= maxSq) continue;
			//speed can never be 0 here
			float speed = (float)Math.sqrt(speedSq);
			float speedToRemove = speed-approxMaxSpeed;
			momentumMagnitudeRemoved += speedToRemove*c.mass;
			//new speed is approxMaxSpeed
			float mult = approxMaxSpeed/speed;
			c.speedY *= mult;
			c.speedX *= mult;
		}
		if(momentumMagnitudeRemoved == 0) return; //momemtum hasnt changed because no points moved too fast
		//Hold total momentum magnitude constant, undoing the loss of momentum from max speed on points
		float momentumMagnitudeNow = 0;
		for(MovCorner c : corners){
			float dy = c.speedY, dx = c.speedX;
			float speed = (float)Math.sqrt(dy*dy + dx*dx);
			float momentum = speed*c.mass;
			momentumMagnitudeNow += momentum;
		}
		float newMomentumMagnitude = momentumMagnitudeNow+momentumMagnitudeRemoved;
		float multAllSpeedsBy = newMomentumMagnitude/momentumMagnitudeNow;
		for(MovCorner c : corners){
			c.speedY += multAllSpeedsBy;
			c.speedX += multAllSpeedsBy;
		}
	}

}