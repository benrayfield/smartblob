package humanainet.smartblob.core.radial_old;

/** Simpler than SmartblobTri, this has only radius at each angle so it cant bend as flexibly.
It can also see outward at all such angles and put force on what it sees to push or pull.
That means there are 2 statsysVar per angle: my radius, and what I see/pull outward.
*/
public interface Smartblob{
	
	public float radius(float angle);
	
	public float radiusSpeed(float angle);
	
	/* TODO
	public double centerX();
	
	public double centerY();
	
	public double speedX();
	
	public double speedY();
	
	"TODO probably will need some optimization like MovTri so can keep track of movements and how they see and push on eachother at specific points."
	
	"benfrayfieldResearch.smartblob16BitAddressing: There will be at most 2^16 smartblobs in memory at once, and each will have 2^16 possible angles as linear interpolated between a powerOf2 number of surface points controlled by statsys. An int names a specific smartblob and angle on it. A long names a specific smartblob and angle from it pointing at a specific surface point (by its angle) on a specific other smartblob. These do not say what the radius or speed or positions are, just that they are pointing at eachother that way. ... This is what I'm going to do... I wrote this first, so organize these details... Could refer to specific point on a specific smartblob by naming each smartblob by 32 bits and naming angle on it by 32 bits. Or could leave some of those bits off to reserve most of that range for other things, and it would still be enough. May also want some of those ranges to refer to myRadius or specific values of it and maybe the radius I see and specific values of it, but probably 2 numbers, 1 to refer to specific smartblob, and other to refer to angle on it, will be simple and work very well."
	*/

}
