/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.physics.changespeeds;
import humanainet.smartblob.core.trianglemesh.LayeredZigzag;
import humanainet.smartblob.core.trianglemesh.MovCorner;
import humanainet.smartblob.core.trianglemesh.MovLine;

/** RelativeFriction moves speed, not like acceleration but as a fluid.
Moves (part of?) speed between adjacent corner/point of same triangle.
Moves only up to some maximum speed per second.
<br><br>
TODO? The part of speed it moves is only the part along the line between
pairs of corner/point. Or, should it be both x and y speed equally? 
<br><br> 
This is relative friction instead of just friction because
it doesnt slow an object's total movement
but will slow its vibration and less slow its rotation.
*/
public class RelativeFriction extends AbstractChangeSpeedLZ{
	
	public float maxSpeedToMovePerSecondInEachLine;
	
	public RelativeFriction(float maxSpeedToMovePerSecondInEachLine){
		this.maxSpeedToMovePerSecondInEachLine = maxSpeedToMovePerSecondInEachLine;
	}

	public void changeSpeed(LayeredZigzag z, float secondsSinceLastCall){
		float maxSpeedToMoveInEachLine = maxSpeedToMovePerSecondInEachLine*secondsSinceLastCall;
		for(MovLine line : z.allLineDatas()){
			moveSpeedBetweenLineEnds(line, maxSpeedToMoveInEachLine);
		}
	}
	
	public void moveSpeedBetweenLineEnds(MovLine line, float maxSpeedToMove){
		//FIXME this should be done at all MovLine at once so no direction is preferred,
		//which I've seen in slight rotation of curvblobs on screen, otherwise unexplained.
		MovCorner a = line.adjacentCorners.get(0), b = line.adjacentCorners.get(1);
		float ddy = b.speedY - a.speedY;
		float ddx = b.speedX - a.speedX;
		double ddRadiusSquared = ddy*ddy + ddx*ddx;
		if(ddRadiusSquared == 0) return;
		float ddRadius = (float)Math.sqrt(ddRadiusSquared);
		float moveHowMuchSpeed = Math.min(ddRadius/2, maxSpeedToMove);
		float mult = moveHowMuchSpeed/ddRadius;
		float moveY = ddy*mult, moveX = ddx*mult;
		//System.out.println("moveY="+moveY+" moveX="+moveX);
		/*b.speedY -= moveY;
		a.speedY += moveY;
		b.speedX -= moveX;
		a.speedX += moveX;
		*/
		b.addToSpeedY -= moveY/b.mass;
		a.addToSpeedY += moveY/a.mass;
		b.addToSpeedX -= moveX/b.mass;
		a.addToSpeedX += moveX/a.mass;
		
	}

}