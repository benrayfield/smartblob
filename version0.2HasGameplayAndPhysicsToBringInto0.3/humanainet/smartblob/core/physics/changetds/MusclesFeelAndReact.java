/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.physics.changetds;
import java.util.List;
import java.util.Random;

import humanainet.smartblob.core.brain.Brain;
import humanainet.smartblob.core.physics.ChangeSpeed;
import humanainet.smartblob.core.physics.Muscle;
import humanainet.smartblob.core.trianglemesh.SmartblobTri;

/** Same behaviors as humanainet.smartblob.core.physics.changespeeds.MusclesFeelAndReact */
@Deprecated //2016-2-6 I'm hooking Muscle into Centriblob.setTargetDistance and Centriblob.observeDistance
public class MusclesFeelAndReact implements ChangeTargetDist{
	
	public float forceMult;
	
	public final Random rand;
	
	/** Muscle.pushToward(pushTowardWhere, fractionOfASecond*forceMult) */
	public MusclesFeelAndReact(float forceMult, Random rand){
		this.forceMult = forceMult;
		this.rand = rand;
	}

	public void changeTargetDists(SmartblobTri blob, float secondsSinceLastCall){
		float force = secondsSinceLastCall*forceMult;
		Brain brain = blob.brain();
		List<Muscle> muscles = blob.mutableMuscles();
		int size = brain.size();
		if(size != muscles.size()) throw new RuntimeException(
			brain.size()+" == brain.size() != muscles.size() == "+muscles.size());
		//Brain internals arent counted in the size,
		//only the parts that interact with the outside world aka visible nodes,
		//in this case the Muscles.
		double io[] = new double[size];
		for(int i=0; i<size; i++){
			float feelFromMuscle = muscles.get(i).read(); //0 to 1
			io[i] = feelFromMuscle;
		}
		brain.think(io, secondsSinceLastCall, rand);
		for(int i=0; i<size; i++){
			float thinkToMuscle = (float) io[i]; //0 to 1
			throw new RuntimeException("muscles.get(i).setTargetDistance WAIT IT DOESNT HAVE THIS FUNCTION. Should it be done as fraction? Or as another Muscle?");
		}
		
	}

}