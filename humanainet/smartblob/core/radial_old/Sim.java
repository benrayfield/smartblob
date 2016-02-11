package humanainet.smartblob.core.radial_old;

/** There will be at most 2^16 smartblobs in memory at once, and each will
have 2^16 possible angles as linear interpolated between a powerOf2 number
of surface points controlled by statsys. An int names a specific smartblob
and angle on it. A long names a specific smartblob and angle from it pointing
at a specific surface point (by its angle) on a specific other smartblob.
These do not say what the radius or speed or positions are, just that they
are pointing at eachother that way.
*/
public interface Sim{
	
	public Smartblob blob(short blobIndex);
	
	/** high 16 bits are blobIndex, and low 16 are angle on it, of another smartblob
	which is pointed at from that angle in the parameter smartblob and parameter angle.
	*/
	public int pointsAt(int fromBlobAndAngle);
	
	/** Each smartblob has a powerOf2 number of angles it literally computes
	radius and radiusSpeed and seeing outward. Between those its linear interpolated.
	Ususually there are far less of these literal angles than can be represented in short.
	*
	public short angles(short blobIndex);
	*/

}
