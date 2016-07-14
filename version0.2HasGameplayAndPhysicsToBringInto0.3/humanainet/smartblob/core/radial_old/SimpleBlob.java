package humanainet.smartblob.core.radial_old;

import java.util.Arrays;

public class SimpleBlob implements Smartblob{
	
	//TODO "I think I need to cache the x and y positions and speeds, so can add to speeds when point crosses perimeter the same way as in curvesmartblob, but somehow the radius and radiusSpeed arrays and vars about this object as a whole need to control those x and y positions and speeds. Maybe its more accurate to say info should flow back and forth between them. This creates same problem as in curvesmartblob physics calculating slow. So maybe I need to actually do it fully by radius. A point can maybe be rotated before comparing to a nonmoving form of a smartblob to collision detect against it, for all points in another smartblob that may intersect now."
	
	public final int angles;
	
	public float x, y, xSpeed, ySpeed, angle, angleSpeed;
	
	/** my radius per angle */
	public final float radius[];
	
	/** speed my radius is changing, per angle */
	public final float radiusSpeed[];
	
	/** from all angles, distances to closest object, accurate including radius of whats seen */
	public final float distantSee[];
	
	/** How much pull (or negative for push) on other smartblobs (their center position)
	as in distantSee, but writing to speed of see instead of just reading whats seen.
	To keep things simple, its only on the whole smartblob and not on its individual parts.
	Many smartblobs can be seen and pulled on at once in different amounts through this array.
	*/
	public final float distantPull[];
	
	public SimpleBlob(int angles){
		this.angles = angles;
		radius = new float[angles];
		Arrays.fill(radius, 1);
		radiusSpeed = new float[angles];
		distantSee = new float[angles];
		distantPull = new float[angles];
	}

	public float radius(float angle){
		throw new RuntimeException("TODO interpolate, considering angle of this smartblob");
	}

	public float radiusSpeed(float angle){
		throw new RuntimeException("TODO interpolate, considering angle of this smartblob");
	}
	
	//"TODO can x y positions and speeds of surface points (per angle) be cached somehow? Or is it faster to compute sine each time? Can cache of 2d affine be used instead of sine for them all?"
	

}
