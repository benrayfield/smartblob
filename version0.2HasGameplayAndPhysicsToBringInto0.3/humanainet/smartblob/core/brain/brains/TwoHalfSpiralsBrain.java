/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.brain.brains;
import java.util.Random;

import humanainet.smartblob.core.brain.Brain;

public class TwoHalfSpiralsBrain implements Brain{
	
	public final int size;
	public final int size(){ return size; }
	
	public TwoHalfSpiralsBrain(int size){
		this.size = size;
	}

	public void think(double io[], double secondsSinceLastCall, Random rand){
		if(size != io.length) throw new RuntimeException(size+" == size != io.length == "+io.length);
		for(int i=0; i<size; i++){
			double fraction = (double)i/(size-1);
			io[i] = 2*Math.abs(.5-fraction);
		}
	}

}
