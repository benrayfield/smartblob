/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.physics.changespeeds;
import java.util.List;

import humanainet.smartblob.core.physics.ChangeSpeed;
import humanainet.smartblob.core.trianglemesh.LayeredZigzag;
import humanainet.smartblob.core.trianglemesh.MovCorner;
import humanainet.smartblob.core.trianglemesh.MovLine;
import humanainet.smartblob.core.trianglemesh.SmartblobTri;

public class GraduallyLimitSpeedOfPointsRelativeToAveSpeedOfSmartblob implements ChangeSpeed{
	
	public float maxRelSpeed, relSpeedFractionDecayPerSecondWhenAboveMax;
	
	public GraduallyLimitSpeedOfPointsRelativeToAveSpeedOfSmartblob(float maxRelSpeed, float relSpeedFractionDecayPerSecondWhenAboveMax){
		this.maxRelSpeed = maxRelSpeed;
		this.relSpeedFractionDecayPerSecondWhenAboveMax = relSpeedFractionDecayPerSecondWhenAboveMax;
	}

	public void changeSpeed(SmartblobTri blob, float secondsSinceLastCall){
		float relSpeedDecayWhenPastMax = Math.min(relSpeedFractionDecayPerSecondWhenAboveMax*secondsSinceLastCall, 1);
		//float relSpeedDecayWhenPastMax = relSpeedFractionDecayPerSecondWhenAboveMax*secondsSinceLastCall;
		float maxRelSpeedSq = maxRelSpeed*maxRelSpeed;
		List<MovCorner> corners = blob.allMovCorners();
		float totalMomentumY = 0, totalMomentumX = 0, totalMass = 0;
		for(MovCorner c : corners){
			totalMass += c.mass;
			totalMomentumY += c.speedY*c.mass;
			totalMomentumX += c.speedX*c.mass;
		}
		float aveSpeedY = totalMomentumY/totalMass;
		float aveSpeedX = totalMomentumX/totalMass;
		for(MovCorner c : corners){
			float relSpeedY = c.speedY-aveSpeedY;
			float relSpeedX = c.speedX-aveSpeedX;
			float relSpeedSq = relSpeedY*relSpeedY + relSpeedX*relSpeedX;
			if(relSpeedSq <= maxRelSpeedSq) continue;
			float relSpeed = (float)Math.sqrt(relSpeedSq);
			//float relSpeedToRemove = relSpeed-maxRelSpeed;
			float newRelSpeed = relSpeed*(1-relSpeedDecayWhenPastMax) + relSpeedDecayWhenPastMax*maxRelSpeed;
			if(c.corner.layer==1 && c.corner.point==0) System.out.println("newRelSpeed="+newRelSpeed);
			float multRelVelocityBy = newRelSpeed/relSpeed;
			float newSpeedY = aveSpeedY + relSpeedY*multRelVelocityBy;
			float newSpeedX = aveSpeedX + relSpeedX*multRelVelocityBy;
			c.addToSpeedY += newSpeedY-c.speedY;
			c.addToSpeedX += newSpeedX-c.speedX;
		}
	}

}