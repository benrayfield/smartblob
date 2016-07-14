/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.brain.brains;
import java.util.Random;

import humanainet.smartblob.core.brain.Brain;

/** A smartblob brain that instead of thinking it forms into simple shapes defined by sum of moving sine waves. */
public class NFreqRotater implements Brain{
	
	public final int size;
	public final int size(){ return size; }
	
	protected final int freqs[];
	protected final double phaseSpeeds[];
	
	protected double timeSimulated;
	
	/** size is array size and number of freqs and must be a power of 2 for Cooley Tukey FFT.
	<br><br>
	TODO rewrite Cooley Tukey FFT to work in a single array of double instead of creating more
	array and Complex number objects which is slower.
	*/
	public NFreqRotater(int size, int freqs[], double phaseSpeeds[]){
		if(freqs.length != phaseSpeeds.length) throw new RuntimeException("The arrays must be equal size");
		this.size = size;
		this.freqs = freqs.clone();
		this.phaseSpeeds = phaseSpeeds.clone();
	}
	
	/** range 0 to 1 */
	public double amplitudeAt(double angle){
		double sum = 0;
		for(int i=0; i<freqs.length; i++){
		//for(double freq : freqs){
			double freq = freqs[i];
			double phaseOffset = phaseSpeeds[i]*timeSimulated;
			sum += Math.sin(phaseOffset+angle*freq);
		}
		double bifraction = sum/freqs.length; //range -1 to 1
		return .5+.5*bifraction;
	}

	public void think(double io[], double secondsSinceLastCall, Random rand){
		timeSimulated += secondsSinceLastCall;
		if(size != io.length) throw new RuntimeException(size+" == size != io.length == "+io.length);
		//Ignore input and write amplitudes
		double mult = 2*Math.PI/size;
		for(int i=0; i<size; i++){
			double angle = mult*i;
			io[i] = amplitudeAt(angle);
		}
	}

}
