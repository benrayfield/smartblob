/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.util;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import humanaicore.common.Rand;
import humanainet.smartblob.core.brain.Brain;
import humanainet.smartblob.core.physics.ChangeSpeed;
import humanainet.smartblob.core.physics.SmartblobSim;
import humanainet.smartblob.core.physics.changespeeds.AccelerateLinearlyFromDistanceConstraints;
import humanainet.smartblob.core.physics.changespeeds.Friction;
import humanainet.smartblob.core.physics.changespeeds.HoldCenterTogether;
import humanainet.smartblob.core.physics.changespeeds.MusclesFeelAndReact;
import humanainet.smartblob.core.physics.changespeeds.RadialsmartblobMultFriction;
import humanainet.smartblob.core.physics.changespeeds.RelativeFriction;
import humanainet.smartblob.core.physics.globalchangespeeds.BounceOnSimpleWall;
import humanainet.smartblob.core.physics.globalchangespeeds.CollisionsChangeSpeed;
import humanainet.smartblob.core.physics.globalchangespeeds.GravityAsGlobalChangeSpeed;
import humanainet.smartblob.core.physics.globalchangespeeds.RadialCollisionsChangeSpeed;
import humanainet.smartblob.core.physics.muscle.factories.LineMuscleFactory;
import humanainet.smartblob.core.trianglemesh.LayeredZigzag;
import humanainet.smartblob.core.trianglemesh.MovCorner;
import humanainet.smartblob.core.trianglemesh.SmartblobTri;
import humanainet.smartblob.core.trianglemesh.radial.Radiblob;
import humanainet.ui.core.ColorUtil;

public class RadiblobUtil{
	private RadiblobUtil(){}
	
	//"TODO rewrite defaultChangeSpeeds for radialsmartblob since it was copied from code for curvesmartblob"
	public static List<ChangeSpeed> defaultChangeSpeeds = Collections.unmodifiableList(Arrays.<ChangeSpeed>asList(
		//new AccelerateLinearlyFromDistanceConstraints(.01f),
		
		//new AccelerateLinearlyFromDistanceConstraints(5f),
		//new Friction(3f)//,
			
		//new Friction(23f)//,
		//new Friction(24423f)//,
		
		//new Friction(15f)//,
		//new RelativeFriction(.5f)//,
		new RadialsmartblobMultFriction(.08f)
		//new HoldCenterTogether()
		//new MusclesFeelAndReact(60000, Rand.strongRand)
	));
	
	public static Radiblob wavegear(
			int team, boolean isIgnoreCollisions, Brain brain, float y, float x, float minRadius, float maxRadius, int angles, int frequency){
		Radiblob blob = new Radiblob(
			team, isIgnoreCollisions, brain, new LineMuscleFactory(), angles, y, x, maxRadius);
		/*for(int p=0; p<angles; p++){
			MovCorner c = blob.corners[0][p];
			c.y = y;
			c.x = x;
		}*/
		blob.blobY = y;
		blob.blobX = x;
		float radiusRange = maxRadius-minRadius;
		float halfRadiusRange = radiusRange/2;
		float aveRadius = (maxRadius+minRadius)/2;
		for(int p=0; p<angles; p++){
			//MovCorner c = blob.corners[1][p];
			float angle = (float)(2*Math.PI*p/angles);
			/*float recurseAngle = angle*frequency;
			float radius = minRadius + radiusRange*(.5f+.5f*(float)Math.sin(recurseAngle));
			c.y = (float)(y + radius*Math.sin(angle));
			c.x = (float)(x + radius*Math.cos(angle));
			*/
			blob.radiusPerAngle[p] =
				aveRadius + halfRadiusRange*(float)Math.sin(frequency*2*Math.PI*p/angles);
			blob.trianglesInward[1][p].colorARGB = ColorUtil.color(0f, 0f, 1f);
		}
		blob.updateStartDistances();
		blob.setTargetDistancesToStartDistances();
		blob.mutablePhysics().addAll(defaultChangeSpeeds);
		return blob;
	}
	
	public static SmartblobSim newSimWithDefaultOptions(){
		//"TODO rewrite this for radialsmartblob, since it was copied from curvesmartblob code"
		return new SmartblobSim(
			//new Gravity(1.5f),
			//new Gravity(3000),
			
				
			//new Gravity(200),
				
			new GravityAsGlobalChangeSpeed(700),
			//new Gravity(1000),
			//new Gravity(30),
			//new Gravity(230),
			//new CollisionsChangeSpeed(.1f),
			new RadialCollisionsChangeSpeed(.1f),
			//new CollisionsChangeSpeed(223.5f),
			new BounceOnSimpleWall(0, true, false),
			new BounceOnSimpleWall(0, false, false),
			new BounceOnSimpleWall(900, true, true),
			new BounceOnSimpleWall(900, false, true)
		);
	}
	
	public static SmartblobTri newCursor(int team){
		int angles = 16;
		float radius = 20;
		//float radius = 70;
		Radiblob blob = new Radiblob(team, true, null, null, angles, radius, radius, radius);
		//start empty by default. blob.mutablePhysics().clear();
		return blob;
	}
	
	/** An alternative to CurvesmartblobUtil.moveAll. TODO merge them later after this radialsmartblob reserach is working.
	This ignores all that are not Radialsmartblob
	*
	public static void doPolarAndYXPositionsAndSpeedsInSomeCombination(SmartblobSim sim, float secondsThisTime){
		SmartblobTri blobArray[];
		synchronized(sim.smartblobs){
			blobArray = sim.smartblobs.toArray(new SmartblobTri[0]);
		}
		
		"TODO do onStartUpdateSpeeds for start/end positions/speeds apply in RadialSmartblob? Write the code first, and see what order things are done. Then make sure locking is done right."
		
		"We start with collision and other forces already put into all MovCorners"
		"Then translate that to radial datastruct and delete the data at MovCorners"
		"Should movement be done only in terms of radial datastructs? Or should it translate back to MovCorner first?"
		"Translate radial to MovCorner, and use only MovCorner in the next step."
		"Do collision detection and force updates on MovCorners, and loop around to start of process."
		
		"One thing I'm sure of is this can be done individually per smartblob."
		
		//
		for(SmartblobTri blob : blobArray){
			blob.onStartUpdatePositions();
			if(blob instanceof Radialsmartblob){
				TODO
				//move((Radialsmartblob)blob, secondsThisTime);
			}else{
				System.out.println(SmartblobTri.class.getName()+" type unknown: "+blob.getClass().getName());
			}
			blob.onEndUpdatePositions();
		}

		//
		for(SmartblobTri blob : blobArray){
			blob.onStartUpdatePositions();
			if(blob instanceof Radialsmartblob){
				TODO
				//move((Radialsmartblob)blob, secondsThisTime);
			}else{
				System.out.println(SmartblobTri.class.getName()+" type unknown: "+blob.getClass().getName());
			}
			blob.onEndUpdatePositions();
		}
		
		
		TODO
	}*/
	
	/*public static void doPolarAndYXPositionsAndSpeedsInSomeCombination(Radialsmartblob blob, float secondsThisTime){
		//CurvesmartblobUtil.move(blob, secondsThisTime);
		//blob.yxToRadius();
		blob.blobY += blob.blobYSpeed*secondsThisTime;
		blob.blobX += blob.blobXSpeed*secondsThisTime;
		blob.blobAngle += blob.blobAngleSpeed*secondsThisTime;
		blob.radiusToYx();
	}*/

}