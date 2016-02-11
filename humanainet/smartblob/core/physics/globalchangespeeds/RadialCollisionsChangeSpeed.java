/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.physics.globalchangespeeds;
import humanainet.smartblob.core.physics.GlobalChangeSpeed;
import humanainet.smartblob.core.physics.SmartblobSim;
import humanainet.smartblob.core.trianglemesh.LayeredZigzag;
import humanainet.smartblob.core.trianglemesh.MovCorner;
import humanainet.smartblob.core.trianglemesh.MovLine;
import humanainet.smartblob.core.trianglemesh.MovTri;
import humanainet.smartblob.core.trianglemesh.SmartblobTri;
import humanainet.smartblob.core.trianglemesh.radial.Radiblob;
import humanainet.smartblob.core.util.CurvblobUtil;
import humanainet.ui.core.shapes.Rect;

/** First all pairs of Smartblob.boundingRectangle() are checked for possible collision.
Then of those which may collide, more detailed checks are done. For all outer point
found to collide past a surface line, speeds of the 2 points on those lines are updated.
Positions are not updated, so this can be done in any order for the same result.
Actually order may affect it a little since speeds are kept the same in magnitude
along certain directions but set away from eachother.
*/
public class RadialCollisionsChangeSpeed implements GlobalChangeSpeed{
	
	//FIXME "This code copied from CollisionsChangeSpeed for curvesmartblob and needs to probably start over and do differently for radial, since the absval in direction of collision must apply to all points in the hardbodies not just the few directly involved"
	
	public float fractionOfPreventOverlap;
	
	public RadialCollisionsChangeSpeed(float fractionOfPreventOverlap){
		this.fractionOfPreventOverlap = fractionOfPreventOverlap;
	}
	
	public void globalChangeSpeed(SmartblobSim sim, float secondsSinceLastCall){
		SmartblobTri blobArray[];
		synchronized(sim.smartblobs){
			blobArray = sim.smartblobs.toArray(new SmartblobTri[0]);
		}
		for(int i=0; i<blobArray.length-1; i++){
			for(int j=i+1; j<blobArray.length; j++){ //for all pairs
				nextPair(blobArray[i], blobArray[j], fractionOfPreventOverlap);
			}
		}
	}
	
	public void nextPair(SmartblobTri a, SmartblobTri b, float fractionOfPreventOverlap){
		if(!a.isIgnorePhysics() && !b.isIgnorePhysics()){
			Rect ra = a.boundingRectangle();
			Rect rb = b.boundingRectangle();
			if(ra.intersects(rb)){
				//System.out.println("intersection: "+ra.intersection(rb));
				if(a instanceof Radiblob && b instanceof Radiblob){
					rectanglesIntersect((Radiblob)a, (Radiblob)b, fractionOfPreventOverlap);
				}else{
					System.out.println("At least 1 of 2 "+SmartblobTri.class.getName()
						+" type unknown: "+a.getClass().getName()
						+" and "+b.getClass().getName());
				}
			}
		}
	}
	
	
	/** Their bounding rectangles are known to intersect. The smartblobs may intersect. */
	public void rectanglesIntersect(Radiblob a, Radiblob b, float fractionOfPreventOverlap){
		//Consider only points in the intersection of their bounding rectangles.
		//TODO I'd like to only consider lines that intersect those rectangles,
		//but its easier for now to just check them all. Its small quantity.
		Rect intersect = a.boundingRectangle().intersection(b.boundingRectangle());

		handleIntersectsBetweenPointAndWeightedSumOfLine(intersect, a, b, fractionOfPreventOverlap);
		handleIntersectsBetweenPointAndWeightedSumOfLine(intersect, b, a, fractionOfPreventOverlap);
	}
	
	/** Rectangle intersect is the intersection of the 2 smartblobs bounding rectangles.
	Finds the closest outer triangle and does point intersection anywhere past its outer line.
	<br><br>
	fractionOfPreventOverlap is 1 to do this instantly and 0 to do it none:
	TODO smartblob collisions must not allow any overlap move them to eachothers borders if past,
	which solves the problem of smartblobs slowly sinking into eachother when pushed together by gravity,
	but this comes at a small cost of forces
	not being exactly conserved (since where did the change in position come from?)
	but can be compensated for by friction or, TODO in later version: keep track of this positions added and
	pay it back to the position later when not in such pressure. 
	*/
	protected void handleIntersectsBetweenPointAndWeightedSumOfLine(
			Rect intersect, Radiblob myPoints, Radiblob myLines, float fractionOfPreventOverlap){
		System.out.println("intersect blobs handleIntersectsBetweenPointAndWeightedSumOfLine");
		//"TODO in this func, find the part that spreads force between 3 MovCorner: 2 of a line and 1 that crossed that line. Change it to 2 equalAndOpposite fromYXPointAccelerateYX"
		int lastLayer = myPoints.layers-1;
		float getYX[] = new float[2];
		for(int p=0; p<myPoints.layerSize; p++){
			MovCorner point = myPoints.corners[lastLayer][p];
			if(intersect.containsYX(point.y, point.x)){
				//may have crossed an outer line in the other smartblob
				MovTri t = myLines.findCollision(point.y, point.x);
				if(t != null){ //collision found
					
					System.out.println("point.speeds Y "+point.speedY+" X "+point.speedX);
					//FIXME "Confirmed, by 2 blobs moving toward eachother, yet all colliding point.speed are 0"
					
					//Find point on infinite line its closest to, and absVal part of speed thats toward it
					CurvblobUtil.getClosestPointToInfiniteLine(getYX, t, point.y, point.x);
					//vector from [closest point on infinite line] to point.
					float vectorY = point.y-getYX[0];
					float vectorX = point.x-getYX[1];
					
					//smartblob collisions must not allow any overlap move them to eachothers borders if past
					point.addToY -= fractionOfPreventOverlap*vectorY;
					point.addToX -= fractionOfPreventOverlap*vectorX;
					
					float vectorLen = (float)Math.sqrt(vectorY*vectorY + vectorX*vectorX);
					if(vectorLen == 0){
						//Dont bounce if its only touching the line. Wait until crosses.
						continue;
					}
					
					MovCorner a = t.adjacentCorners.get(0), b = t.adjacentCorners.get(1);
					
					float aDy = getYX[0]-a.y, aDx = getYX[1]-a.x; 
					float distanceA = (float)Math.sqrt(aDy*aDy + aDx*aDx);
					float bDy = getYX[0]-b.y, bDx = getYX[1]-b.x; 
					float distanceB = (float)Math.sqrt(bDy*bDy + bDx*bDx);
					//TODO These fractions may be slightly above 1 and the other negative, but usually normal fractions.
					//By distance, they reverse their behavior when it goes past either end of line,
					//but that only happens when border of smartblob has negative curve, unlike a circle.
					float distanceSum = distanceA+distanceB;
					float fractionLineEndA = distanceA/distanceSum;
					float fractionLineEndB = 1-fractionLineEndA;
					
					//Speed of pointOnLine is a weightedSum of speeds at the line's ends.
					//TODO this is a little inaccurate if the point on the line segment is past either end.
					float speedYOfPointOnLine = a.speedY*fractionLineEndA + fractionLineEndB*b.speedY;
					float speedXOfPointOnLine = a.speedX*fractionLineEndA + fractionLineEndB*b.speedX;
					
					float ddy = point.speedY-speedYOfPointOnLine;
					float ddx = point.speedX-speedXOfPointOnLine;
					
					float normVY = vectorY/vectorLen;
					float normVX = vectorX/vectorLen;
					//"If vector is in same direction as third corner, flip it.
					//Since we already know theres a collision, always flip here.
					normVY = -normVY;
					normVX = -normVX;
					//Now (normVY,normVX) is length 1 and point outward from smartblob.
					//Get the part of speed vector aligned with normVector, then flip it.
					//TODO should that speed be difference between the 2 smartblobs at that point,
					//or do them separately? Doing them separately, at least for now.
					
					//float speedDotNorm = point.speedY*normVY + point.speedX*normVX;
					float speedDotNorm = ddy*normVY + ddx*normVX;
					
					//speedDotNorm *= 100; //FIXME remove this line. testing why it just slows down instead of bounces. Could it be need to multiply by number of points on surface? I dont think so since its calculated as whole objects.
					//TODO "Could it be that the speeds of the surface points are not updated and are stuck at 0?"
					
					//If speedDotNorm is positive, the smartblob is moving away (if the line was at rest)
					if(speedDotNorm < 0){
						
						
						//Like the BounceOnSimpleWall code, use absVal.
						//partOfSpeedVec is the part aligned with normVec (TODO opposite?)
						float partOfSpeedVecY = normVY*speedDotNorm;
						float partOfSpeedVecX = normVX*speedDotNorm;
						float addToSpeedY = -2*partOfSpeedVecY;
						float addToSpeedX = -2*partOfSpeedVecX;
						float addToEachSpeedY = addToSpeedY/2;
						float addToEachSpeedX = addToSpeedX/2;
						//point.speedY -= 2*partOfSpeedVecY;
						//point.speedX -= 2*partOfSpeedVecX;
						
						/*
						point.speedY += addToEachSpeedY;
						a.speedY -= fractionLineEndA*addToEachSpeedY;
						b.speedY -= fractionLineEndB*addToEachSpeedY;
						
						point.speedX += addToEachSpeedX;
						a.speedX -= fractionLineEndA*addToEachSpeedX;
						b.speedX -= fractionLineEndB*addToEachSpeedX;
						"Somewhere around here call blob.fromYXPointAccelerateYX 2 times as equalAndOppositeForce"
						*/
						myPoints.fromYXPointAccelerateYX(point.y, point.x, addToEachSpeedY, addToEachSpeedX);
						myLines.fromYXPointAccelerateYX(point.y, point.x, -addToEachSpeedY, -addToEachSpeedX);
						
						//TODO equal and opposite force, between this and weightedSum between 2 ends of line (even if it hangs past ends, just do something like 1.1*endA - .1*endB
						//"TODO use fractionLineEndA and fractionLineEndB"
						//TODO
					}
					
				}
				/*LineData lineData = myLines.findCollision(point.y, point.x);
				if(lineData != null){ //collision found
					throw new RuntimeException("TODO");
				}
				*/
			}
		}
	}

}
