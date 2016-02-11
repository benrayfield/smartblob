/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.physics.changespeeds;
import humanaicore.common.MathUtil;
import humanainet.smartblob.core.physics.ChangeSpeed;
import humanainet.smartblob.core.trianglemesh.LayeredZigzag;
import humanainet.smartblob.core.trianglemesh.MovCorner;
import humanainet.smartblob.core.trianglemesh.MovLine;
import humanainet.smartblob.core.trianglemesh.SmartblobTri;

/** Acceleration is proportional to difference between actual distance and targetDistance,
minus springDampening as the equation says at https://en.wikipedia.org/wiki/Damping
is a force proportional to speed between the 2 ends of the spring.
*/
public class AccelerateLinearlyFromDistanceConstraintsWithSpringDampening extends AbstractChangeSpeedLZ{
	
	public float springTightnessMult, springDampen;
	
	public AccelerateLinearlyFromDistanceConstraintsWithSpringDampening(float springTightnessMult, float springDampenMult){
		this.springTightnessMult = springTightnessMult;
		this.springDampen = springDampenMult;
	}
	
	public void changeSpeed(LayeredZigzag blob, float secondsSinceLastCall){
		for(MovLine ld : blob.allLineDatas()){
			if(ld.springTightness == 0){
				//This is an optimization, not a behavior change
				//(except for no spring dampening, so TODO make that proportional?),
				//because force is multiplied by springTightness
				continue;
			}
			MovCorner a = ld.adjacentCorners.get(0), b = ld.adjacentCorners.get(1);
			float dy = b.y - a.y;
			float dx = b.x - a.x;
			float distance = (float)Math.sqrt(dx*dx+dy*dy);
			if(distance == 0) continue;
			float normDy = dy/distance;
			float normDx = dx/distance;
			float dSpeedY = b.speedY - a.speedY;
			float dSpeedX = b.speedX - a.speedX;
			float springEndsVelocityTowardEachother = dSpeedY*normDy + dSpeedX*normDx; //dotProd
			float springDampenForce = -springDampen*springEndsVelocityTowardEachother;
			//FIXME If springDampen is more than 1, it would move the other direction, in theory,
			//but why isnt it doing that? Why does a springDampen of 1.5 or sometimes 5 work?
			//FIXME TODO springDampenForce must only slow the spring, not accelerate it the opposite direction
			float massSumOfSpringEnds = a.mass+b.mass; //mass that spring dampening is slowing
			float springDampenForceAsMomentum = springDampenForce*massSumOfSpringEnds;
			float wantToAddToDistance = ld.targetDistance-distance; //positive or negative
			float springTightness = springTightnessMult*ld.springTightness;
			
			//TODO should springTightness be relative to mass (multiply by massSumOfSpringEnds below)?
			//Or should the same tightness between more mass vibrate slower (dont multiply)?
			
			float springForceAsMomentum = wantToAddToDistance*springTightness*massSumOfSpringEnds;
			float addToMomentum = springForceAsMomentum + springDampenForceAsMomentum;
			float addToDMomentum = secondsSinceLastCall*addToMomentum;
			float addToMomentumY = normDy*addToDMomentum;
			float addToMomentumX = normDx*addToDMomentum;
			b.addToSpeedY += addToMomentumY/b.mass;
			a.addToSpeedY -= addToMomentumY/a.mass;
			b.addToSpeedX += addToMomentumX/b.mass;
			a.addToSpeedX -= addToMomentumX/a.mass;
		}
	}

}