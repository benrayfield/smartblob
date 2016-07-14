/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.physics.changespeeds;
import java.util.List;
import java.util.Random;

import humanainet.smartblob.core.brain.Brain;
import humanainet.smartblob.core.physics.ChangeSpeed;
import humanainet.smartblob.core.physics.Muscle;
import humanainet.smartblob.core.trianglemesh.SmartblobTri;

/** A ChangeSpeed that copies numbers from Muscles to Brain, thinks,
then copies numbers from Brain back to Muscles so they react to what they just felt.
<br><br>
Muscles can be for pushing/pulling at distance, but they can also be any senses
like (TODO) vision of a smartblob in all directions from its center or surface
or vectors of natural language. Anything you can hook a vector based AI to.
Probably for Smartblobs it will be just the distance and vision.
<br><br>
TODO An alternative to MusclesFeelAndReact being a ChangeSpeed is
to change MovLine.targetDistance
*/
public class MusclesFeelAndReact implements ChangeSpeed{
	
	public float forceMult;
	
	public final Random rand;
	
	/** Muscle.pushToward(pushTowardWhere, fractionOfASecond*forceMult) */
	public MusclesFeelAndReact(float forceMult, Random rand){
		this.forceMult = forceMult;
		this.rand = rand;
	}
	
	public void changeSpeed(SmartblobTri blob, float secondsSinceLastCall){
		//System.out.println("MusclesFeelAndReact blob="+blob+" secs="+secondsSinceLastCall);
		float force = secondsSinceLastCall*forceMult;
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
		for(int i=0; i<size; i++){
			float thinkToMuscle = (float) io[i]; //0 to 1
			muscles.get(i).pushToward(thinkToMuscle, force);
		}
	}

}