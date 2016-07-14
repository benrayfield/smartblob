/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.physics.changespeeds;
import humanaicore.common.MathUtil;
import humanainet.smartblob.core.physics.ChangeSpeed;
import humanainet.smartblob.core.trianglemesh.LayeredZigzag;
import humanainet.smartblob.core.trianglemesh.MovCorner;
import humanainet.smartblob.core.trianglemesh.MovLine;
import humanainet.smartblob.core.trianglemesh.SmartblobTri;

/** Acceleration is proportional to difference between actual distance and targetDistance */
public class AccelerateBySigmoidOfFromDistanceConstraints extends AbstractChangeSpeedLZ{
	
	public float mult, multParamOfSigmoid;
	
	public AccelerateBySigmoidOfFromDistanceConstraints(float multSpringTightness){
		this(multSpringTightness, 15);
	}
	
	public AccelerateBySigmoidOfFromDistanceConstraints(float multSpringTightness, float multParamOfSigmoid){
		this.mult = multSpringTightness;
		this.multParamOfSigmoid = multParamOfSigmoid;
	}
	
	public void changeSpeed(LayeredZigzag blob, float secondsSinceLastCall){
		for(MovLine ld : blob.allLineDatas()){
			if(ld.springTightness == 0){
				//This is an optimization, not a behavior change, because force is multiplied by springTightness
				continue;
			}
			MovCorner a = ld.adjacentCorners.get(0), b = ld.adjacentCorners.get(1);
			float dy = b.y - a.y;
			float dx = b.x - a.x;
			float distance = (float)Math.sqrt(dx*dx+dy*dy);
			if(distance == 0) continue;
			float wantToAddToDistance = ld.targetDistance-distance; //positive or negative
			//wantToAddToDistance = (float)(2*MathUtil.sigmoid(5*wantToAddToDistance/ld.targetDistance)-1)*ld.targetDistance;
			wantToAddToDistance = (float)(2*MathUtil.sigmoid(multParamOfSigmoid*wantToAddToDistance/ld.targetDistance)-1);
			float normDy = dy/distance, normDx = dx/distance;
			float addToEachMomentum = secondsSinceLastCall*wantToAddToDistance*mult*ld.springTightness;
			float addToMomentumY = normDy*addToEachMomentum;
			float addToMomentumX = normDx*addToEachMomentum;
			b.addToSpeedY += addToMomentumY/b.mass;
			a.addToSpeedY -= addToMomentumY/a.mass;
			b.addToSpeedX += addToMomentumX/b.mass;
			a.addToSpeedX -= addToMomentumX/a.mass;
		}
	}

}