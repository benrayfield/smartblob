/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.physics.muscle;
import humanainet.smartblob.core.physics.Muscle;
import humanainet.smartblob.core.trianglemesh.MovCorner;
import humanainet.smartblob.core.trianglemesh.MovLine;

public class LineMuscle implements Muscle{
	
	public final MovLine line;
	
	public float scaleMult;
	
	public static final float defaultScaleMult = 2f;
	//public static final float defaultScaleMult = 3f;
	//public static final float defaultScaleMult = 2.5f;
	
	public final boolean sign;
	
	/** uses default scaleMult */
	public LineMuscle(MovLine line, boolean sign){
		this(line, defaultScaleMult, sign);
	}
	
	/** See defaultScaleMult for a reasonable number to try,
	but for more curve ability you may want to go a little higher.
	Since muslces use range 0 to 1, that is scaled into actual distances
	ranging 0 to line.startDistance*scaleMult and truncating to that max.
	*/
	public LineMuscle(MovLine line, float scaleMult, boolean sign){
		this.line = line;
		this.scaleMult = scaleMult;
		this.sign = sign;
	}
	
	public float read(){
		MovCorner a = line.adjacentCorners.get(0), b = line.adjacentCorners.get(1);
		float dy = b.y-a.y, dx = b.x-a.x;
		float distance = (float)Math.sqrt(dy*dy + dx*dx);
		float range = line.startDistance*scaleMult;
		if(range < distance) return 1;
		return distance/range; 
	}
	
	public void pushToward(float writeFraction, float force){
		if(!sign) writeFraction = 1-writeFraction;
		MovCorner a = line.adjacentCorners.get(0), b = line.adjacentCorners.get(1);
		float dy = b.y-a.y, dx = b.x-a.x;
		float distance = (float)Math.sqrt(dy*dy + dx*dx);
		if(distance == 0) throw new RuntimeException(
			"Distance is 0. Dont know what direction to accelerate");
		float range = line.startDistance*scaleMult;
		float targetDistance = writeFraction*range;
		float wantChangeDistance = targetDistance-distance;
		float push = force*wantChangeDistance;
		float normDy = dy/distance, normDx = dx/distance;
		float addToEachSpeed = push/2;
		float addToEachSpeedY = normDy*addToEachSpeed;
		float addToEachSpeedX = normDx*addToEachSpeed;
		//System.out.println("Add to sx "+addToEachSpeedX+" sy "+addToEachSpeedY+" line "+line);
		b.speedY += addToEachSpeedY;
		a.speedY -= addToEachSpeedY;
		b.speedX += addToEachSpeedX;
		a.speedX -= addToEachSpeedX;
	}

}