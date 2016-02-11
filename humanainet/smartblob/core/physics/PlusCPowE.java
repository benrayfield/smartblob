package humanainet.smartblob.core.physics;

/** TODO part of bobagaForceFunc.
A very general kind is sum of c*distance^e for pairs of constants c and e.
A common e is -2 meaning inverseDistanceSquared. -1 is inverseDistance.
Can also be fractional exponents.
*/
public class PlusCPowE{
	
	public final double c, e;
	
	public PlusCPowE(double c, double e){
		this.c = c;
		this.e = e;
	}
	
	//TODO equals and hashcode funcs

}
