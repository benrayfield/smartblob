/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.physics.changetds;
import java.util.List;
import java.util.Random;

import humanainet.smartblob.core.brain.Brain;
import humanainet.smartblob.core.physics.ChangeSpeed;
import humanainet.smartblob.core.physics.Muscle;
import humanainet.smartblob.core.physics.muscle.LineMuscle;
import humanainet.smartblob.core.trianglemesh.SmartblobTri;

/** A substitute for MusclesFeelAndReact that changes MovLine.targetDistance */
@Deprecated //2016-2-6 I'm hooking Muscle into Centriblob.setTargetDistance and Centriblob.observeDistance
public class BrainSetsMuscleTarget implements ChangeSpeed{
	
	//TODO this should probably be a ChangeTargetDistance
	
	public final Random rand;
	
	public BrainSetsMuscleTarget(Random rand){
		this.rand = rand;
	}
	
	public void changeSpeed(SmartblobTri blob, float secondsSinceLastCall){
		Brain brain = blob.brain();
		if(brain == null) return;
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
		float decay = .0001f; //change this slower than smartblobs bounce on eachother
		for(int i=0; i<size; i++){
			float thinkToMuscle = (float) io[i]; //0 to 1
			//muscles.get(i).pushToward(thinkToMuscle, force);
			LineMuscle m = (LineMuscle) muscles.get(i);
			float target = m.line.startDistance*(float)(io[i]*m.scaleMult);
			//TODO change this slower than smartblobs bounce on eachother
			m.line.targetDistance = m.line.targetDistance*(1-decay) + decay*target;
		}
	}

}
