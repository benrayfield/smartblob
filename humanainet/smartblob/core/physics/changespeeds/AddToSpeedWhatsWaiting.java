/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.physics.changespeeds;
import java.util.List;

import humanainet.smartblob.core.physics.ChangeSpeed;
import humanainet.smartblob.core.trianglemesh.LayeredZigzag;
import humanainet.smartblob.core.trianglemesh.MovCorner;
import humanainet.smartblob.core.trianglemesh.MovLine;
import humanainet.smartblob.core.trianglemesh.SmartblobTri;

/** moves whats in addToSpeedY and addToSpeedX vars into speedY and speedX vars, for each MovCorner */
public class AddToSpeedWhatsWaiting implements ChangeSpeed{
	
	public void changeSpeed(SmartblobTri blob, float secondsSinceLastCall){
		for(MovCorner c : blob.allMovCorners()){
			c.speedY += c.addToSpeedY;
			c.addToSpeedY = 0;
			c.speedX += c.addToSpeedX;
			c.addToSpeedX = 0;
		}
	}

}