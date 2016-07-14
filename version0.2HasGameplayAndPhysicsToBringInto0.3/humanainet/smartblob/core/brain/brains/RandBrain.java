/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.brain.brains;
import java.util.Random;

import humanainet.smartblob.core.brain.Brain;

/** A smartblob brain that instead of thinking outputs random numbers */
public class RandBrain implements Brain{
	
	public final int size;
	public final int size(){ return size; }
	
	public RandBrain(int size){
		this.size = size;
	}
	
	public void think(double io[], double secondsSinceLastCall, Random rand){
		for(int i=0; i<size; i++) io[i] = rand.nextDouble();
	}

}
