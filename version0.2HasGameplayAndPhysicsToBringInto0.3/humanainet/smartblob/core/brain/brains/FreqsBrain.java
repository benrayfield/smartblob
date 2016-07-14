/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.brain.brains;
import java.util.Random;

import humanainet.smartblob.core.brain.Brain;

/** A smartblob brain that instead of thinking it uses fourier math to match certain frequencies
and based on some parameters (TODO) adjusts them.
*/
public class FreqsBrain implements Brain{
	
	public final int size;
	public final int size(){ return size; }
	
	/** size is array size and number of freqs and must be a power of 2 for Cooley Tukey FFT.
	<br><br>
	TODO rewrite Cooley Tukey FFT to work in a single array of double instead of creating more
	array and Complex number objects which is slower.
	*/
	public FreqsBrain(int size){
		boolean sizeIsPowerOf2 = (size&(size-1)) == 0;
		if(!sizeIsPowerOf2) throw new RuntimeException("Size is not a power of 2: "+size);
		this.size = size;
	}

	public void think(double io[], double secondsSinceLastCall, Random rand){
		if(size != io.length) throw new RuntimeException(size+" == size != io.length == "+io.length);
		for(int i=0; i<size; i++) io[i] = io[i]*2-1; //range -1 to 1, averaging 0 if was .5
		throw new RuntimeException("TODO use CooleyTukeyFFT");
	}

}
