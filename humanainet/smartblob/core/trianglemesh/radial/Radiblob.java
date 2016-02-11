/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.trianglemesh.radial;
import java.util.Arrays;

import humanaicore.common.CoreUtil;
import humanainet.smartblob.core.brain.Brain;
import humanainet.smartblob.core.physics.muscle.MuscleFactory;
import humanainet.smartblob.core.trianglemesh.LayeredZigzag;
import humanainet.smartblob.core.trianglemesh.MovCorner;
import humanainet.smartblob.core.util.RadiblobUtil;

/** Radial Smartblob.
Movement and speed are done in x y and radius, both position and speed of those.
Collisions are measured only by x y positions and speeds of surface points,
but they update only the radial vars fo the radialsmartblob as a whole.
Surface points are also used for drawing, but at the core
its a radial coordinate system, so radiusToYx is used but not yxToRadius.
<br><br>
OLD... For radialsmartblob,
after collisions of a LayeredZigzag have added to speeds of all surface points,
there is a step of holding them all to equally spread angles around a circle
and various radius,
and then copying back to the LayeredZigzag point positions/speeds.
It alternates between these 2 steps:
surface points in x and y vs radius per angle.
Theres also angle and x and y positions of the whole smartblob,
and speeds of those, in this radial view.
*/
public class Radiblob extends LayeredZigzag{
	
	public final float radiusPerAngle[];
	
	/** index in radiusPerAngle (times angleMult) is rotated by angle */
	public float blobAngle=.3f, blobAngleSpeed;
	
	public float blobY, blobX, blobYSpeed, blobXSpeed;
	
	//protected float ratioOfAngleToXYAccel = .5f;
	protected float ratioOfAngleToXYAccel = 1.0f;
	//protected float ratioOfAngleToXYAccel = 1.5f;
	
	public Radiblob(int team, boolean isIgnoreCollisions, Brain brain, MuscleFactory muscleFactory, int angles, float centerY, float centerX, float radius){
		super(team, isIgnoreCollisions, brain, 2, angles, centerY, centerX, radius);
		if(muscleFactory != null){
			mutableMuscles().addAll(Arrays.asList( muscleFactory.newMuscles(this, muscleFactory.maxMuscles(this)) ));
		}
		radiusPerAngle = new float[angles];
		Arrays.fill(radiusPerAngle, radius);
		float freq = 7;
		float minRadius = radius*.8f, maxRadius = radius;
		float aveRadius = (minRadius+maxRadius)/2;
		float halfRadiusRange = (maxRadius-minRadius)/2;
		for(int i=0; i<angles; i++) radiusPerAngle[i] =
			aveRadius + halfRadiusRange*(float)Math.sin(freq*2*Math.PI*i/angles);
	}
	
	public void setCenterToAvePositionOfSurfacePoints(){
		float sumY = 0, sumX = 0;
		float sumYSpeed = 0, sumXSpeed = 0;
		for(int i=0; i<layerSize; i++){
			MovCorner c = corners[1][i];
			sumY += c.y;
			sumX += c.x;
			sumYSpeed += c.speedY;
			sumXSpeed += c.speedX;
		}
		float aveY = sumY/layerSize, aveX = sumX/layerSize;
		float aveYSpeed = aveY/layerSize, aveXSpeed = aveX/layerSize;
		/*for(int i=0; i<layerSize; i++){
			MovCorner c = corners[0][i];
			c.y = aveY;
			c.x = aveX;
		}*/
		blobY = aveY;
		blobX = aveX;
		blobYSpeed = aveYSpeed;
		blobXSpeed = aveXSpeed;
	}
	
	/** READ y and x positions of surface points then WRITE radius at each angle (and angle of this smartblob as a whole) *
	protected void yxToRadius(){
		if(!isIgnorePhysics()){
			//setCenterToAvePositionOfSurfacePoints();
			//TODO
		}
	}*/
	
	/** READ radius at each angle (and angle of this smartblob as a whole) then WRITE y and x positions of surface points */
	public void radiusToYx(){
		if(!isIgnorePhysics()){
			float angleMult = (float)(2*Math.PI/layerSize);
			for(int i=0; i<layerSize; i++){
				MovCorner c = corners[0][i];
				c.y = blobY;
				c.x = blobX;
				c.speedY = blobYSpeed;
				c.speedX = blobXSpeed;
			}
			for(int i=0; i<layerSize; i++){
				float r = radiusPerAngle[i];
				float a = blobAngle+angleMult*i;
				MovCorner c = corners[1][i];
				//TODO use MovCorner.addToX and addToY?
				c.y = blobY+r*(float)Math.sin(a);
				c.x = blobX+r*(float)Math.cos(a);
				float epsilonTime = .001f;
				//FIXME handle each radiusSpeed here
				//TODO optimize by computing some of these sums outside the loop
				float yAfterEpsilonTime = blobY+blobYSpeed*epsilonTime + r*(float)Math.sin(a+blobAngleSpeed*epsilonTime);
				float xAfterEpsilonTime = blobX+blobXSpeed*epsilonTime + r*(float)Math.cos(a+blobAngleSpeed*epsilonTime);
				//"TODO set speeds of surface points"
				//"For each surface point, compute 2 points, where it is now and where its going epsilon time ahead, and compute speed from that."
				float dy = yAfterEpsilonTime-c.y;
				float dx = xAfterEpsilonTime-c.x;
				c.speedY = dy/epsilonTime;
				c.speedX = dx/epsilonTime;
			}
		}
	}
	
	public float displayVectorX, displayVectorY;
	
	/** Uses radial only as a middle step. Updates all MovCorner's positions and speeds. */
	public void fromYXPointAccelerateYX(float fromY, float fromX, float addToSpeedY, float addToSpeedX){
		float maxRadius = maxRadius();
		/*
		//System.out.println("TODO (radialsmartblob) accel. from: "+fromY+" "+fromX+" direction: "+addToSpeedY+" "+addToSpeedX);
		//Use blobAngleSpeed as 1-to-1 interchangible with x and y speed
		//Cog is Center Of Gravity
		float yCog = centerOfGravityY();
		float xCog = centerOfGravityX(); 
		float dyToCog = yCog-fromY;
		float dxToCog = xCog-fromX;
		float distToCog = (float)Math.sqrt(dyToCog*dyToCog + dxToCog*dxToCog);
		if(distToCog == 0){ //all x y accel
			blobYSpeed += addToSpeedY;
			blobXSpeed += addToSpeedX;
		}else{
			float yToCogNorm = dyToCog/distToCog;
			float xToCogNorm = dxToCog/distToCog;
			float sizeOfAccelToCog = addToSpeedY*yToCogNorm + addToSpeedX*xToCogNorm; //dotProd
			float yAccelToCog = sizeOfAccelToCog*yToCogNorm; //part of the vector thats toward/away from COG
			float xAccelToCog = sizeOfAccelToCog*xToCogNorm;
			float yAccelTangent = addToSpeedY-yAccelToCog; //tangent part of vector, thats not toward/away from COG
			float xAccelTangent = addToSpeedX-xAccelToCog;
			//this.displayVectorY = yAccelTangent;
			//this.displayVectorX = xAccelTangent;
			float angleOfPoint = (float)angleOfYX(dyToCog, dxToCog);
			float angleOfChangedPoint = (float)angleOfYX(dyToCog+yAccelTangent, dxToCog+xAccelTangent);
			float angleDiff = angleOfChangedPoint-angleOfPoint;
			if(angleDiff > Math.PI) angleDiff -= 2*Math.PI;
			else if(angleDiff < -Math.PI) angleDiff += 2*Math.PI;
			blobAngleSpeed -= angleDiff*ratioOfAngleToXYAccel;
			blobYSpeed += yAccelToCog;
			blobXSpeed += xAccelToCog;
		
		}		
		radiusToYx();
		*/
		
		float yCog = centerOfGravityY();
		float xCog = centerOfGravityX(); 
		float dyToCog = yCog-fromY;
		float dxToCog = xCog-fromX;
		float distToCog = (float)Math.sqrt(dyToCog*dyToCog + dxToCog*dxToCog);
		if(distToCog == 0){ //all x y accel
			blobYSpeed += addToSpeedY;
			blobXSpeed += addToSpeedX;
		}else{
			//use tangent to center of gravity (COG) norm vector first, to calculate angle acceleration,
			//and whatevers left is x and y acceleration
			//float yToCogNorm = dyToCog/distToCog;
			//float xToCogNorm = dxToCog/distToCog;
			double unitTangentYX[] = new double[2];
			getUnitTangentVector(unitTangentYX, dyToCog, dxToCog);
			//double testAngle = CoreUtil.time();
			//displayVectorY = (float)Math.sin(testAngle);//(float)unitTangentYX[0];
			//displayVectorX = (float)Math.cos(testAngle);//(float)unitTangentYX[1];
			displayVectorY = (float)unitTangentYX[0];
			displayVectorX = (float)unitTangentYX[1];
			float dotWithTangent = (float)(unitTangentYX[0]*addToSpeedY + unitTangentYX[1]*addToSpeedX);
			//float scaleBasedOnRadius = 70/distToCog; //I dont think this is the right math but its often closer
			//float scaleBasedOnRadius = maxRadius/distToCog; //I dont think this is the right math but its often closer
			float scaleBasedOnRadius = .5f*maxRadius/distToCog; //I dont think this is the right math but its often closer
			float tanMult = dotWithTangent*scaleBasedOnRadius;
			float yAccelTangent = (float)(unitTangentYX[0]*tanMult);
			float xAccelTangent = (float)(unitTangentYX[1]*tanMult);
			float angleOfPoint = (float)angleOfYX(dyToCog, dxToCog);
			float angleOfChangedPoint = (float)angleOfYX(dyToCog+yAccelTangent, dxToCog+xAccelTangent);
			float angleDiff = angleOfChangedPoint-angleOfPoint;
			if(angleDiff > Math.PI) angleDiff -= 2*Math.PI;
			else if(angleDiff < -Math.PI) angleDiff += 2*Math.PI;
			blobAngleSpeed -= angleDiff*ratioOfAngleToXYAccel;
			float yAccelRemaining = addToSpeedY-yAccelTangent;
			float xAccelRemaining = addToSpeedX-xAccelTangent;
			blobYSpeed += yAccelRemaining;
			blobXSpeed += xAccelRemaining;
		
		}		
		radiusToYx();
	}
	
	public void move(float secondsSinceLastCall){
		onStartUpdatePositions(); //not accurate, since may update both positions and speeds
		//RadialsmartblobUtil.doPolarAndYXPositionsAndSpeedsInSomeCombination(
		//	this, secondsSinceLastCall);
		blobY += blobYSpeed*secondsSinceLastCall;
		blobX += blobXSpeed*secondsSinceLastCall;
		blobAngle += blobAngleSpeed*secondsSinceLastCall;
		radiusToYx();
		onEndUpdatePositions(); //not accurate, since may have updated both positions and speeds
	}
	
	public static double angleOfYX(double y, double x){
		double len = Math.sqrt(y*y+x*x);
		if(len == 0) return 0;
		double yNorm = y/len;
		double xNorm = x/len;
		double angle = Math.acos(xNorm);
		if(y < 0) angle = 2*Math.PI-angle; //mirror
		return angle;
	}
	
	/** TODO optimize. This is wasteful and could be calculated much faster by
	not using sine/cosine and angleOfYX and sqrt.
	*/
	public static void getUnitTangentVector(double getTangentYX[], double y, double x){
		double len = Math.sqrt(y*y + x*x);
		if(len == 0) throw new RuntimeException("x and y are 0");
		y /= len; //unit length
		x /= len;
		double angle = angleOfYX(y,x);
		double epsilon = .000001;
		double angle2 = angle+epsilon;
		double y2 = Math.sin(angle2);
		double x2 = Math.cos(angle2);
		double dy = y2-y;
		double dx = x2-x;
		double len3 = Math.sqrt(dy*dy + dx*dx);
		getTangentYX[0] = dy/len3;
		getTangentYX[1] = dx/len3;
	}
	
	public static void main(String args[]){
		for(double a=0; a<2*Math.PI; a+=.1){
			double y = Math.sin(a);
			double x = Math.cos(a);
			double a2 = angleOfYX(y,x);
			System.out.println("angle2="+a2+" angle="+a+" y="+y+" x="+x);
		}
	}

}
