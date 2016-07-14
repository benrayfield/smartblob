package humanainet.smartblob.core.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import humanaicore.common.MathUtil;
import humanaicore.common.Rand;
import humanainet.smartblob.core.brain.Brain;
import humanainet.smartblob.core.physics.ChangeSpeed;
import humanainet.smartblob.core.physics.SmartblobSim;
import humanainet.smartblob.core.physics.changespeeds.AccelerateBySigmoidOfFromDistanceConstraints;
import humanainet.smartblob.core.physics.changespeeds.AccelerateConstantFromDistanceConstraints;
import humanainet.smartblob.core.physics.changespeeds.AccelerateLinearlyFromDistanceConstraints;
import humanainet.smartblob.core.physics.changespeeds.AccelerateLinearlyFromDistanceConstraintsWithSpringDampening;
import humanainet.smartblob.core.physics.changespeeds.AddToSpeedWhatsWaiting;
import humanainet.smartblob.core.physics.changespeeds.Friction;
import humanainet.smartblob.core.physics.changespeeds.HoldCenterTogether;
import humanainet.smartblob.core.physics.changespeeds.GraduallyLimitSpeedOfPointsRelativeToAveSpeedOfSmartblob;
import humanainet.smartblob.core.physics.changespeeds.Gravity;
import humanainet.smartblob.core.physics.changespeeds.LimitSpeedPerPoint;
import humanainet.smartblob.core.physics.changespeeds.LimitSpeedPerPointAndSpreadThatMomentumToAll;
import humanainet.smartblob.core.physics.changespeeds.LimitSpeedPerPointAndSpreadThatSpeedToOtherPointsInTheirCurrentDirections;
import humanainet.smartblob.core.physics.changespeeds.LimitWholeSmartblobSpeed;
import humanainet.smartblob.core.physics.changespeeds.MoveLayer0ToCenterOfCentri;
import humanainet.smartblob.core.physics.changespeeds.MusclesFeelAndReact;
import humanainet.smartblob.core.physics.changespeeds.RelativeFriction;
import humanainet.smartblob.core.physics.globalchangespeeds.BounceOnSimpleWall;
import humanainet.smartblob.core.physics.globalchangespeeds.CollisionsChangeSpeed;
import humanainet.smartblob.core.physics.muscle.factories.LineMuscleFactory;
import humanainet.smartblob.core.trianglemesh.LayeredZigzag;
import humanainet.smartblob.core.trianglemesh.MovCorner;
import humanainet.smartblob.core.trianglemesh.MovLine;
import humanainet.smartblob.core.trianglemesh.SmartblobTri;
import humanainet.smartblob.core.trianglemesh.centri.Centriblob;
import humanainet.smartblob.core.trianglemesh.radial.Radiblob;

public class CentriblobUtil{
	private CentriblobUtil(){}
	
	/*public static float defaultMassForCentriCorner(Centriblob cblob){
		int centriCorners = cblob.centriCorners.size();
		if(centriCorners == 0) throw new RuntimeException("Are you calling this before creating the centricorners?");
		int surfaceCorners = cblob.layerSize();
		return (float)surfaceCorners/centriCorners;
		//return 2;
		//return 1; //FIXME this should probably be surfaceCorners/centriCorners
		//return 100;
		//return .5f;
	}*/
	
	public static float defaultMassForCentriCorner(int centriCorners, int surfaceCorners){
		//return (float)surfaceCorners/centriCorners;
		return 1;
	}
	
	/** This is for some kinds of MovLine that are not drawn on screen and are just for distanceConstraints. */
	public static float defaultSpringTightnessForBetweenPairOfCentriCorners(Centriblob cblob){
		//Average square of heads-tails, for all possible flips, is exactly number of coins.
		//TODO Does that mean this should be multiplied or divided by 3 since theres 3 centri corners
		//but theres also 3 distanceConstraints, 1 between each of them and each surface point. 
		//return (float)Math.sqrt(cblob.layerSize);
		//FIXME TODO return (float)Math.sqrt(cblob.layerSize);
		//return 2;
		//return 1;
		return 0;
		//return 10;
	}
	
	/** This is for some kinds of MovLine that are not drawn on screen and are just for distanceConstraints. */
	public static float defaultSpringTightnessForBetweenCentriCornerAndSurfaceCorner(Centriblob cblob){
		return 1;
	}

	/** Unlike defaultSpringTightnessForBetweenPairOfCentriCorners
	and defaultSpringTightnessForBetweenCentriCornerAndSurfaceCorner,
	which are both for Adjacent.farLine kind of MovLines,
	this is for MovLines that are between MovCorners that are adjacent to eachother
	because they are part of at least 1 MovTri. These are the normal kind of MovLine
	and are part of whats drawn on screen. The others are just for distanceConstraints.
	<br><br>
	curvesmartblob/curvblob uses all its MovLine as distanceConstraints,
	but Radiblob and Centriblob calculate it different.
	*/
	public static float defaultSpringTightnessForLinesThatArePartOfNormalTriangles(LayeredZigzag blob){
		return (blob instanceof Centriblob) || (blob instanceof Radiblob) ? 0 : 1;
	}
	
	/** If radius of the corners of an equilateral triangle is centerRadius,
	whats the distance between corners?
	*
	public static float distanceBetweenCenterPoints(float centerRadius){
		return centerRadius*distanceBetweenCenterPointsRatioF;
	}*/
	
	//public static final double angle0 = 0, angle1 = Math.PI*2/3, angle2 = Math.PI*4/3;
	
	/*public static final double distanceBetweenCenterPointsRatio = MathUtil.vectorLengthDyDx(
		Math.sin(angle1) - Math.sin(angle0), //y of 2 points
		Math.cos(angle1) - Math.cos(angle0) //x of 2 points
	);
	
	public static final float distanceBetweenCenterPointsRatioF = (float) distanceBetweenCenterPointsRatio;
	*/
	
	
	public static List<ChangeSpeed> defaultChangeSpeeds = Collections.unmodifiableList(Arrays.<ChangeSpeed>asList(
		/*new AccelerateLinearlyFromDistanceConstraints(4f),
		new MoveLayer0ToCenterOfCentri(),
		new Friction(500f),
		new RelativeFriction(30)
		*/
			
		/*new AccelerateLinearlyFromDistanceConstraints(4f),
		new MoveLayer0ToCenterOfCentri(),
		new Friction(500f),
		//new RelativeFriction(30),
		new LimitSpeedPerPoint(0f,5000f)
		*/
			
		/*new AccelerateLinearlyFromDistanceConstraints(44f),
		new MoveLayer0ToCenterOfCentri(),
		new Friction(500f),
		new RelativeFriction(30),
		new LimitSpeedPerPoint(0f,5000f)
		*/
			
		/*new AccelerateLinearlyFromDistanceConstraints(552f),
		new MoveLayer0ToCenterOfCentri(),
		new Friction(500f),
		//new RelativeFriction(30),
		//new LimitSpeedPerPointAndSpreadThatMomentumToAll(3000f)
		new LimitSpeedPerPointAndSpreadThatSpeedToOtherPointsInTheirCurrentDirections(10000f)
		//new LimitSpeedPerPoint(0f,10000f)
		*/
			
		/*//new AccelerateLinearlyFromDistanceConstraints(1502f),
		new AccelerateLinearlyFromDistanceConstraints(502f),
		new MoveLayer0ToCenterOfCentri(),
		new Friction(100f),
		//new RelativeFriction(30),
		new LimitSpeedPerPointAndSpreadThatMomentumToAll(2000f)
		//new LimitSpeedPerPointAndSpreadThatSpeedToOtherPointsInTheirCurrentDirections(10000f)
		//new LimitSpeedPerPoint(0f,10000f)
		*/
		
		/*new AccelerateLinearlyFromDistanceConstraints(300f),
		new MoveLayer0ToCenterOfCentri(),
		new Friction(4f),
		new LimitSpeedOfPointsRelativeToAveSpeedOfSmartblob(22000f)
		*/
			
		/*new AccelerateLinearlyFromDistanceConstraints(130f),
		new MoveLayer0ToCenterOfCentri(),
		new Friction(40f),
		new LimitSpeedOfPointsRelativeToAveSpeedOfSmartblob(1500f, 3f)//,
		//new LimitSpeedPerPoint(0f,20000f)
		*/
		
		/*
		new AccelerateLinearlyFromDistanceConstraints(40f),
		new MoveLayer0ToCenterOfCentri(),
		new Friction(400f),
		//new LimitSpeedOfPointsRelativeToAveSpeedOfSmartblob(1500f, 10f)
		//new LimitSpeedOfPointsRelativeToAveSpeedOfSmartblob(0f, 5f)
		//new LimitSpeedOfPointsRelativeToAveSpeedOfSmartblob(5000f, 5f)
		//new LimitSpeedOfPointsRelativeToAveSpeedOfSmartblob(1500f, 6f),
		//new LimitSpeedOfPointsRelativeToAveSpeedOfSmartblob(10000f, 10f)
		new LimitSpeedOfPointsRelativeToAveSpeedOfSmartblob(3500f, 12f)
		//new LimitSpeedPerPoint(0f,20000f)
		*/
			
		/*new AccelerateLinearlyFromDistanceConstraints(20f),
		new MoveLayer0ToCenterOfCentri(),
		new Friction(30f),
		new GraduallyLimitSpeedOfPointsRelativeToAveSpeedOfSmartblob(3500f, 12f)
		*/
			
		/*
		//works great but too much vibrating
		new AccelerateLinearlyFromDistanceConstraints(5000f),
		new MoveLayer0ToCenterOfCentri(),
		new Friction(180f),
		new GraduallyLimitSpeedOfPointsRelativeToAveSpeedOfSmartblob(2000f, 10f),
		//new GraduallyLimitSpeedOfPointsRelativeToAveSpeedOfSmartblob(2000f, 10f),
		//new GraduallyLimitSpeedOfPointsRelativeToAveSpeedOfSmartblob(3000f, 10f),
		new LimitWholeSmartblobSpeed(3000f),
		new LimitSpeedPerPoint(0f,5000f)
		*/
		
		/*
		//new AddToSpeedWhatsWaiting(),
		new AccelerateBySigmoidOfFromDistanceConstraints(500000f, 20f),
		//new AccelerateLinearlyFromDistanceConstraints(5000f),
		new MoveLayer0ToCenterOfCentri(),
		//new AddToSpeedWhatsWaiting(),
		new Friction(80f),
		//new AddToSpeedWhatsWaiting(),
		//new GraduallyLimitSpeedOfPointsRelativeToAveSpeedOfSmartblob(0f, 1000000f),
		new GraduallyLimitSpeedOfPointsRelativeToAveSpeedOfSmartblob(2000f, 15f)
		//new GraduallyLimitSpeedOfPointsRelativeToAveSpeedOfSmartblob(4000f, 5f)
		//new GraduallyLimitSpeedOfPointsRelativeToAveSpeedOfSmartblob(4000f, 30f)
		//new AddToSpeedWhatsWaiting()
		//new GraduallyLimitSpeedOfPointsRelativeToAveSpeedOfSmartblob(2000f, 10f),
		//new GraduallyLimitSpeedOfPointsRelativeToAveSpeedOfSmartblob(3000f, 10f),
		//new LimitWholeSmartblobSpeed(3000f),
		//new LimitSpeedPerPoint(0f,1000f)
		*/
		
		/*
		new AccelerateLinearlyFromDistanceConstraintsWithSpringDampening(4500f, 1.5f),
		new MoveLayer0ToCenterOfCentri()
		*/
			
		new AccelerateLinearlyFromDistanceConstraintsWithSpringDampening(1000f, 1.5f),
		//new Gravity(300),
		new MoveLayer0ToCenterOfCentri()
		
	));
	
	/** Add smartblobs to it later */
	public static SmartblobSim newSimWithDefaultOptions(/*int howManyCursors*/){
		return new SmartblobSim(
			//new Gravity(1.5f),
			//new Gravity(10000),
			
			//new Gravity(3000),
				
			//new Gravity(2000),
			
				
			//new Gravity(200),
				
			//new Gravity(700),
			//new Gravity(200),
			//new Gravity(1000),
			//new Gravity(10),
			//new Gravity(13),
			//new Gravity(100),
			//new Gravity(30),
			//new Gravity(100),
			//new Gravity(200),
			//new CollisionsChangeSpeed(0f),
			new CollisionsChangeSpeed(.1f),
			//new CollisionsChangeSpeed(1.4f),
			//new CollisionsChangeSpeed(0f),
			new BounceOnSimpleWall(0, true, false),
			new BounceOnSimpleWall(0, false, false),
			new BounceOnSimpleWall(500, true, true),
			new BounceOnSimpleWall(500, false, true)
		);
	}
	
	public static Centriblob wavegear(Brain brain, float y, float x,
			int howManyCentricorners, float centerRadius,
			float outerMinRadius, float outerMaxRadius, int angles , float frequency){
		//LayeredZigzag blob = new LayeredZigzag(0, false, brain, new LineMuscleFactory(), layers, layerSize, 0, 0, 0);
		Centriblob blob = new Centriblob(0, false, brain, howManyCentricorners, centerRadius, angles, y, x, outerMaxRadius);
		int layers = 2;
		if(layers != blob.layers()) throw new RuntimeException("TODO I am planning to use many layers in centriblob, not for physics but all layers except the last are just for display of colored triangles. found layers="+layers);
		for(int layer=0; layer<layers; layer++){
			double fraction = (double)layer/(layers-1);
			boolean layerIsOdd = (layer&1)==1;
			for(int p=0; p<angles; p++){
				//TODO optimize by reversing order of these loops?
				MovCorner c = blob.corners[layer][p];
				double minRadius = fraction*outerMinRadius;
				double maxRadius = fraction*outerMaxRadius;
				double radiusRange = maxRadius-minRadius;
				double halfRadiusRange = radiusRange/2;
				double aveRadius = (minRadius+maxRadius)/2;
				double angle = (double)p/angles*2*Math.PI;
				if(layerIsOdd) angle += Math.PI/angles;
				double recurseAngle = angle*frequency;
				//double radius = minRadius + radiusRange*Math.sin(recurseAngle);
				double radius = aveRadius + halfRadiusRange*Math.sin(recurseAngle);
				c.y = (float)(y + radius*Math.sin(angle));
				c.x = (float)(x + radius*Math.cos(angle));
			}
		}
		blob.mutablePhysics().addAll(defaultChangeSpeeds);
		
		//whenever change blob shape and want it to try to hold that shape, these 2 lines:
		blob.updateStartDistances();
		blob.setTargetDistancesToStartDistances();
		
		/*for(MovLine line : blob.allLineDatas()){
			line.startDistance = line.targetDistance = line.distance();
			
		}*/
		return blob;
	}
	
	public static SmartblobTri newCursor(int team){
		int angles = 16;
		float radius = 20;
		//float radius = 70;
		Radiblob blob = new Radiblob(team, true, null, null, angles, radius, radius, radius);
		//start empty by default. blob.mutablePhysics().clear();
		return blob;
	}

}
