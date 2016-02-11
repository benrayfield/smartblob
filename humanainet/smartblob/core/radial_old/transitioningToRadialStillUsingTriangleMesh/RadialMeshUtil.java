package humanainet.smartblob.core.radial_old.transitioningToRadialStillUsingTriangleMesh;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import humanaicore.common.Rand;
import humanainet.smartblob.core.brain.Brain;
import humanainet.smartblob.core.brain.brains.NFreqRotater;
import humanainet.smartblob.core.brain.brains.SpiralBrain;
import humanainet.smartblob.core.brain.brains.TwoHalfSpiralsBrain;
import humanainet.smartblob.core.physics.ChangeSpeed;
import humanainet.smartblob.core.physics.SmartblobSim;
import humanainet.smartblob.core.physics.changespeeds.AccelerateLinearlyFromDistanceConstraints;
import humanainet.smartblob.core.physics.changespeeds.Friction;
import humanainet.smartblob.core.physics.changespeeds.HoldCenterTogether;
import humanainet.smartblob.core.physics.changespeeds.MusclesFeelAndReact;
import humanainet.smartblob.core.physics.changespeeds.RelativeFriction;
import humanainet.smartblob.core.physics.changetds.BrainSetsMuscleTarget;
import humanainet.smartblob.core.physics.globalchangespeeds.BounceOnSimpleWall;
import humanainet.smartblob.core.physics.globalchangespeeds.CollisionsChangeSpeed;
import humanainet.smartblob.core.physics.globalchangespeeds.GravityAsGlobalChangeSpeed;
import humanainet.smartblob.core.physics.muscle.MuscleFactory;
import humanainet.smartblob.core.physics.muscle.factories.LineMuscleFactory;
import humanainet.smartblob.core.physics.muscle.factories.RadialLineMuscleFactory;
import humanainet.smartblob.core.trianglemesh.LayeredZigzag;
import humanainet.ui.core.ColorUtil;

/** I'm planning to create a simpler smartblob design where everything is radial,
so triangles arent calculated directly, and physics is much faster.
But in the transition to that I'm using the inner radial part of smartblob
physics which technically already does radial but inefficiently
because it also allows vibration along perimeter.
Radial physics would have angle and angleSpeed vars for the whole smartblob.
*/
public class RadialMeshUtil{
	
	//"Change MovLine.targetDistance (a few times slower than bouncing) instead of Brain adding to speed"
	
	//"TODO connect muscles differently in radial because pairs of lines overlap. Define some lines as being copied to other lines so they arent computed. Create a ChangeSpeed to do it."
	//"Dont need a ChangeSpeed for the pairs of overlapping MovLine if only 1 in each pair has a Muscle, since they share a MovPoint on the surface, and all the center points are set equal"
	
	//TODO some of these options were copied from triangle based smartblob code
	//and should be taken apart and rebuilt in this new radial code,
	//only keeping the simpler parts needed for radial. Keep the original
	//triangle code in its own javapackage since its useful for
	//more advanced things, but from now on most smartblobs will be radial
	//and can at times pull on eachother from a distance
	//(along with seeing eachother) as a substitute for being able
	//to curve enough to grab eachother with radial cant.
	//They will still grab eachother by that pulling and will
	//still shape together on the smaller radial curves where they touch.
	
	public static LayeredZigzag smartblobExample(){
		
		int layers = 2; //radial has only the center point and 1 layer outward
		//int layerSize = 64;
		int layerSize = 32;
		
		//MuscleFactory muscleFactory = new LineMuscleFactory();
		MuscleFactory muscleFactory = new RadialLineMuscleFactory();

		//Brain brain = new NFreqRotater(muscleFactory.maxMuscles(layers, layerSize), new int[]{3}, new double[]{0});
		//Brain brain = new SpiralBrain(muscleFactory.maxMuscles(layers, layerSize));
		Brain brain = new TwoHalfSpiralsBrain(muscleFactory.maxMuscles(layers, layerSize));
		//Brain brain = null;
		
		LayeredZigzag blob = new LayeredZigzag(0, false, brain, layers, layerSize, 100, 100, 40);
		if(muscleFactory != null){
			blob.mutableMuscles().addAll(Arrays.asList( muscleFactory.newMuscles(blob, muscleFactory.maxMuscles(blob)) ));
		}
		//LayeredZigzag blob = new LayeredZigzag(brain, muscleFactory, layers, layerSize, 100, 100, 60);
		
		blob.mutablePhysics().addAll(defaultChangeSpeeds);
		
		//inner circle of smartblob has no outward facing triangles so alternate color of inward.
		int colorCenterEven = ColorUtil.color(.8f, .8f, .8f);
		int colorCenterOdd = ColorUtil.color(.1f, .1f, .9f);
		for(int p=0; p<blob.layerSize; p++){
			blob.trianglesInward[1][p].colorARGB = ((p&1)==1) ? colorCenterOdd : colorCenterEven;
		}
		
		return blob;
	}
	
	public static List<ChangeSpeed> defaultChangeSpeeds = Collections.unmodifiableList(Arrays.<ChangeSpeed>asList(
		new AccelerateLinearlyFromDistanceConstraints(100f),
		new Friction(15f),
		new RelativeFriction(.5f),
		new HoldCenterTogether(),
		//new MusclesFeelAndReact(60000, Rand.strongRand)
		new BrainSetsMuscleTarget(Rand.strongRand)
	));
	
	/** Add smartblobs to it later */
	public static SmartblobSim newSimWithDefaultOptions(){
		return new SmartblobSim(
			//new Gravity(1.5f),
			//new Gravity(3000),
			
				
			//new Gravity(200),
				
			//new Gravity(700),
			//new Gravity(1000),
			//new Gravity(30),
			new GravityAsGlobalChangeSpeed(150),
			new CollisionsChangeSpeed(.1f),
			new BounceOnSimpleWall(0, true, false),
			new BounceOnSimpleWall(0, false, false),
			new BounceOnSimpleWall(500, true, true),
			new BounceOnSimpleWall(500, false, true)
		);
	}
	

}
