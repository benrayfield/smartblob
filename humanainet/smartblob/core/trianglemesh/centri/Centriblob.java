/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.trianglemesh.centri;
import static humanaicore.common.CommonFuncs.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import humanainet.smartblob.core.brain.Brain;
import humanainet.smartblob.core.physics.Muscle;
import humanainet.smartblob.core.physics.muscle.MuscleFactory;
import humanainet.smartblob.core.physics.muscle.factories.CentriMuscleFactory;
import humanainet.smartblob.core.trianglemesh.CornerName;
import humanainet.smartblob.core.trianglemesh.LayeredZigzag;
import humanainet.smartblob.core.trianglemesh.LineName;
import humanainet.smartblob.core.trianglemesh.MovCorner;
import humanainet.smartblob.core.trianglemesh.MovLine;
import humanainet.smartblob.core.util.CentriblobUtil;
import humanainet.smartblob.core.util.CurvblobUtil;

public class Centriblob extends LayeredZigzag{
	
	/** the 3 center corners which all surface points/corners have distance constraints between *
	public final MovCorner centriCorners[] = new MovCorner[3];
	*/
	public final List<MovCorner> centriCorners = new ArrayList();
	
	/** only the 3 lines between centriCorners. Others are found in surface points as Adjacent.centriLines[] *
	public final MovLine centriLines[] = new MovLine[3];
	*/
	public final List<MovLine> centriLines = new ArrayList();
	
	public static final boolean doPhysicsOnAdjacentSurfacePoints = false; //experimental
	
	public final float centerRadius;
	
	/** nonbacked means you cant modify this and expect it to affect physics.
	This is written by the code that modifies multiple MovLine.targetRadius
	(for each centri MovCorner to the same surface MovCorner).
	This is read by CentriMuscle (for example) which needs to know
	what targetDistance for that angle was last set.
	*/
	protected final float nonbackedTargetRadiusAtAngle[];
	
	public Centriblob(int team, boolean isIgnoreCollisions, Brain brain, int howManyCentricorners, float centerRadius, int angles, float centerY, float centerX, float radius){
		super(team, isIgnoreCollisions, brain, 2, angles, centerY, centerX, radius);
		nonbackedTargetRadiusAtAngle = new float[angles];
		//arbitrary radius estimate, not accurate until Muscles read and write a few times
		Arrays.fill(nonbackedTargetRadiusAtAngle, radius);
		this.centerRadius = centerRadius;
		//Now all MovCorner, MovLine, and MovTri are created except the 3 centri near center and
		//MovLines between pairs of them and between each of them and each surface MovCorner.
		float defaultSpringTightnessBetweenPairOfCentriCorners =
			CentriblobUtil.defaultSpringTightnessForBetweenPairOfCentriCorners(this);
		double circle = 2*Math.PI;
		//float centriCornerMass = CentriblobUtil.defaultMassForCentriCorner(this);
		float centriCornerMass = CentriblobUtil.defaultMassForCentriCorner(howManyCentricorners, angles);
		for(int i=0; i<howManyCentricorners; i++){
			//Its normally against the rules to have negative layer,
			//but its not a part of any triangles drawn on screen (except during testing),
			//just the Adjacent.farLines. Its only for distance constraints.
			CornerName c = new CornerName(-1, i);
			MovCorner centricorner = new MovCorner(this, c, defaultSpringTightnessBetweenPairOfCentriCorners);
			double angle = circle*i/howManyCentricorners;
			centricorner.y = centerY + centerRadius*(float)Math.sin(angle);
			centricorner.x = centerX + centerRadius*(float)Math.cos(angle);
			centricorner.mass = centriCornerMass;
			centriCorners.add(centricorner);
			allMovCorners.add(centricorner);
		}
		for(int i=0; i<howManyCentricorners; i++){
			CornerName cx = centriCorners.get(i).corner;
			CornerName cy = centriCorners.get((i+1)%howManyCentricorners).corner;
			//lineData func only uses springTightnessInCentri if its a new MovLine.
			centriLines.add(lineData(new LineName(cx, cy), defaultSpringTightnessBetweenPairOfCentriCorners));
			//centriLines[i].springTightness = 0; //FIXME testing
			//"TODO use CentriblobUtil.distanceBetweenCenterPoints(centerRadius)"
			//redundant: centriLines[i].targetDistance = centerRadius*CentriblobUtil.distanceBetweenCenterPointsRatioF;
		}
		
		//TODO create a different func for this
		float tightnessBetweenAdjacentSurfacePoints = CentriblobUtil.defaultSpringTightnessForBetweenCentriCornerAndSurfaceCorner(this);
		
		float minRadius = radius/2, maxRadius = radius;
		MuscleFactory m = new CentriMuscleFactory(minRadius, maxRadius);
		Muscle muscles[] = m.newMuscles(this, m.maxMuscles(this));
		mutableMuscles().addAll(Arrays.asList(muscles));
		
		for(MovLine line : allLineDatas()){
			//tell new MovLines (other than whats in superclass) about their MovCorners
			//since they only know about CornerName which is an address in any LayeredZigzag of a MovCorner.
			line.connectAdjacent();
			//line.springTightness = 0; //FIXME testing
			//if(line.adjacentCorners.get(0).corner.layer == -1 ^ line.adjacentCorners.get(1).corner.layer == -1){
			MovCorner ca = line.adjacentCorners.get(0);
			MovCorner cb = line.adjacentCorners.get(1);
			/*if(ca.corner.layer == -1 && ca.corner.point == 0 && cb.corner.layer == 1 && cb.corner.point == 0){
			//if(line.adjacentCorners.get(0).corner.layer == -1 ^ line.adjacentCorners.get(1).corner.layer == -1){
			if(line.adjacentCorners.get(0).corner.layer == -1 ^ line.adjacentCorners.get(1).corner.layer == -1){
				System.out.println("centri distanceConstraint line: "+line);
				line.springTightness = 1; //FIXME testing
			}*/
			/*if(ca.corner.layer == -1 && ca.corner.point == 0 && cb.corner.layer == 1 && cb.corner.point == 0){
				//cb.speedX += 12.1;
				cb.speedY += 115;
			}*/
			if(doPhysicsOnAdjacentSurfacePoints){
				int lastLayer = layers-1;
				if(ca.corner.layer == lastLayer && cb.corner.layer == lastLayer){
					line.springTightness = tightnessBetweenAdjacentSurfacePoints; //FIXME testing
				}
			}
		}
		//for(MovLine line : allLineDatas()){
		//	line.startDistance = line.targetDistance = line.distance();
		//	log("line startDist="+line.startDistance+" dist="+line.distance());
		//}
		updateStartDistances();
		setTargetDistancesToStartDistances();
		log("centriblob constructed: "+this);
		
		//FIXME "TODO call lineData for each combination of surface point with each of centriCorners"
		//FIXME "TODO where will the logic be done on each set of 3 LinearInterpolate1Vars per 1 surface point?"
		//FIXME "I want the centri MovCorners and MovLines to know about all their adjacent centri related parts"
		
		//FIXME "TODO where do the Adjacent.farLines get connected to centri, and centri corners to eachother? This is the counterpart to Adjacent.connectAdjacent() which is called after MovCorner, MovLine, and MovTri are created."
	}
	
	public MovCorner getMovCorner(CornerName address){
		return address.layer==-1 ? centriCorners.get(address.point) : super.getMovCorner(address);
	}
	
	public void move(float secondsSinceLastCall){
		super.move(secondsSinceLastCall);
		for(MovCorner cd : centriCorners){
			cd.y += cd.speedY*secondsSinceLastCall + cd.addToY;
			cd.x += cd.speedX*secondsSinceLastCall + cd.addToX;
			cd.addToY = 0;
			cd.addToX = 0;
		}
	}
	
	public void fromYXPointAccelerateYX(float fromY, float fromX, float addToSpeedY, float addToSpeedX){
		System.out.println("centriblob accel");
	}
	
	/** observeRadius reads actual positions. setTargetRadius changes spring constraints. Use them together.
	<br><br>
	angle refers to 1 of the outer MovCorner.
	TODO use LinearInterpolate1Var in MovLine to cache this calculation so it can be set as percentile by SmartblobBrain.
	*/
	public void setTargetRadius(int angle, float newTargetRadius){
		//System.out.println("setTargetRadius angle="+angle+" r="+newTargetRadius);
		//calculate "correct" points, as in theory but not where they actually are, to calculate correct distances
		//so vibrations of where they actually are dont build up into new constraints.
		MovCorner surfaceCorner = corners[corners.length-1][angle];
		int howManyCentricorners = centriCorners.size();
		double circle = 2*Math.PI;
		for(int i=0; i<howManyCentricorners; i++){ //which centri point
			MovCorner centriCorner = centriCorners.get(i);
			double centriAngle = circle*i/howManyCentricorners;
			double correctCentriY = centerRadius*Math.sin(centriAngle);
			double correctCentriX = centerRadius*Math.cos(centriAngle);
			double correctPointAngle = (angle*2*Math.PI/layerSize());
			double correctPointY = newTargetRadius*Math.sin(correctPointAngle);
			double correctPointX = newTargetRadius*Math.cos(correctPointAngle);
			double dy = correctPointY-correctCentriY;
			double dx = correctPointX-correctCentriX;
			double targetDist = Math.sqrt(dy*dy + dx*dx);
			//TODO this should be somewhere in MovCorner.farLines
			MovLine line = lineData(new LineName(surfaceCorner.corner,centriCorner.corner), 0);
			line.targetDistance = (float) targetDist;
		}
		nonbackedTargetRadiusAtAngle[angle] = newTargetRadius;
	}
	
	/** observeRadius reads actual positions. setTargetRadius changes spring constraints. Use them together.
	<br><br>
	This depends on MoveLayer0ToCenterOfCentri having already moved all layer0 MovCorners
	to average position of centri MovCorners.
	<br><br>
	FIXME This also depends on all radius being balanced so that is actually the center of gravity,
	but if this rule is violated it will only be unbalanced a little, so TODO that code later.
	*/
	public float observeRadius(int angle){
		MovCorner center = corners[0][0]; //MoveLayer0ToCenterOfCentri moves all layer0 to cent
		MovCorner surfaceCorner = corners[corners.length-1][angle];
		return CurvblobUtil.distance(center, surfaceCorner);
	}
	
	/** Returns the last param of setTargetRadius func for that angle.
	<br><br>
	In the MovLines, its actually stored separately in as many different MovLines as centri MovCorners.
	That could be calculated using that many LinearInterpolate1Var, but its faster to cache it.
	*/
	public float observeTargetRadius(int angle){
		return nonbackedTargetRadiusAtAngle[angle];
	}

}