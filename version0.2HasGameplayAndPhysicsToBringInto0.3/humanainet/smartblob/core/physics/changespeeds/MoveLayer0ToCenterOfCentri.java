/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.physics.changespeeds;
import java.util.List;
import java.util.Random;

import humanainet.smartblob.core.brain.Brain;
import humanainet.smartblob.core.physics.ChangeSpeed;
import humanainet.smartblob.core.physics.Muscle;
import humanainet.smartblob.core.physics.changetds.ChangeTargetDist;
import humanainet.smartblob.core.trianglemesh.MovCorner;
import humanainet.smartblob.core.trianglemesh.SmartblobTri;
import humanainet.smartblob.core.trianglemesh.centri.Centriblob;

/** Ignores everything except Centriblob.
Moves all MovCorner at layer0 to the average position of the centri's 3 corners.
<br><br>
HoldCenterTogether is for Curveblob. MoveLayer0ToCenterOfCentri is for Centriblob.
<br><br>
FIXME changing position is not a ChangeSpeed, and it makes things potentially depend on order
or may cause other problems. I need to separate these or rename ChangeSpeed to ChangeSpeedAndOrPos.
*/
public class MoveLayer0ToCenterOfCentri implements ChangeSpeed{

	public void changeSpeed(SmartblobTri blob, float secondsSinceLastCall){
		if(!(blob instanceof Centriblob)) return;
		Centriblob cblob = (Centriblob) blob;
		int siz = cblob.centriCorners.size();
		float aveY = 0, aveX = 0, aveYSpeed = 0, aveXSpeed = 0;
		for(int c=0; c<siz; c++){
			MovCorner mc = cblob.centriCorners.get(c);
			aveY += mc.y;
			aveX += mc.x;
			aveYSpeed += mc.speedY;
			aveXSpeed += mc.speedX;
		}
		//FIXME TODO? Should this consider mass of centri points could be different? In practice they never are.
		aveY /= siz;
		aveX /= siz;
		aveYSpeed /= siz;
		aveXSpeed /= siz;
		//float aveY = (cblob.centriCorners[0].y + cblob.centriCorners[1].y + cblob.centriCorners[2].y)/3;
		//float aveX = (cblob.centriCorners[0].x + cblob.centriCorners[1].x + cblob.centriCorners[2].x)/3;
		//float aveYSpeed = (cblob.centriCorners[0].speedY + cblob.centriCorners[1].speedY + cblob.centriCorners[2].speedY)/3;
		//float aveXSpeed = (cblob.centriCorners[0].speedX + cblob.centriCorners[1].speedX + cblob.centriCorners[2].speedX)/3;
		final MovCorner layer0[] = cblob.corners[0];
		for(int c=0; c<cblob.layerSize(); c++){
			layer0[c].y = aveY;
			layer0[c].x = aveX;
			layer0[c].speedY = aveYSpeed;
			layer0[c].speedX = aveXSpeed;
		}
	}

}