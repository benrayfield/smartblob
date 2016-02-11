/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.trianglemesh;
import humanainet.smartblob.core.brain.Brain;
import humanainet.smartblob.core.physics.ChangeSpeed;
import humanainet.smartblob.core.physics.Muscle;
import humanainet.smartblob.core.physics.changetds.ChangeTargetDist;
import humanainet.ui.core.shapes.Rect;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//import humanaicore.statsysinterface.Statsys;

/** A smartblob is a changing polygon which has a statsys observing and moving it
while the smartblob bounces on and is grabbed by other smartblobs.
The polygon can only change to another polygon with the same number of points.
<br><br>
Positions and speeds are never updated together as enforced by 4 funcs
which mark the start and end of updating positions and speeds.
*/
public interface SmartblobTri{
	
	//"TODO define outer border as existing java class or make new one?"
	
	//"TODO every Smartblob must tell its MovPoint, MovLine, and MovTri."
	
	/** Brains are mutable. Their size is immutable and matches size and order of muscles(). */
	public Brain brain();
	
	/** brain must have same size as mutableMuscles().size() as those are its inputs/outputs. */
	public void setBrain(Brain b);
	
	/** Mutable list of Muscle. Must be same size and order as vars in brain() when brain is used. */
	public List<Muscle> mutableMuscles();
	
	public int layers();
	
	public int layerSize();
	
	/** It could be positive charge vs negative charge, as opposites attract and same team repels,
	or 3 teams like rock paper scissors chasing eachother by attract/repel forces asymmetricly.
	There could be a team for mouse cursors, or a range of ints for cursors so everyone can have their own.
	*/
	public int team();
	
	/** Renaming isIgnoreCollisions to isIgnorePhysics, so cursor doesnt fall with gravity etc.
	A cursor may be a certain int or range of ints returned by team().
	It must return true for isIgnoreCollisions().
	Other things than cursors may ignore collisions, like things just to display certain places.
	*/
	public boolean isIgnorePhysics();
	
	/** This is an approximation of the shape which can actually be scalars.
	<br><br>
	Make sure to use 1 pixel bigger in width and height for bounding rectangle
	because scalar positions get rounded down. Or are they rounded either way?
	I'm creating boundingRectangle() for that. Use that instead of this Polygons rect.
	<br><br>
	OLD BUT PARTIALLY RELEVANT:
	For compatibility with shapes that have int positions,
	a shape may have scalar positions but they can never get close enough
	to eachother that any 2 points occupy the same pixel at int positions,
	except for the innermost layer which are all held to equal x and y.
	Violating this may result in errors where Polygon objects say
	you have not enclosed a well defined shape since it crosses itself.
	*
	public Shape shape();
	*/
	
	/** 1 pixel bigger in all directions than the Polygon's rectangle since its based on ints
	and I'm undecided what kind of rounding I'll end up using.
	TODO is this Rect cached until positions change? I think so, but make sure.
	*/
	public Rect boundingRectangle();
	
	/** Returns the TriData of the closest outer LineData to bounce on
	or null if no collision. It doesnt have to literally intersect that triangle,
	but it does have to be the best outer line to bounce on.
	<br><bre>
	Direction of bounce is away from the other point on the LineData's only TriData.
	<br><br>
	Closest outer LineData is defined as the LineData which contains,
	anywhere on that line between the 2 points,
	the closest point (on the line) to the given point.
	*/
	public MovTri findCollision(float y, float x);
	
	/** For half of equal and opposite force at angles when 2 smartblobs hit eachother with angled surfaces */
	public void fromYXPointAccelerateYX(float fromY, float fromX, float addToSpeedY, float addToSpeedX);
	
	/** Moves the positions based on speeds, of x y and radius,
	then updates positions and speeds of any MovCorner points as a nonbacked copy of that.
	*/
	public void move(float secondsSinceLastCall);
	
	/** The farthest point out from the centerOfGravity */
	public float maxRadius();
	
	/** From centerOfGravity:
	Use this with fromYXPointAccelerateYX to compute how much of an acceleration point and direction,
	in the sideways/angle part goes to changing the centerOfGravity and how much goes to rotation.
	*/
	public float aveRadiusByVolume();
	
	public float centerOfGravityX();
	public float centerOfGravityY();
	
	public void setCenterOfGravityYX(float y, float x);
	
	public void addToAllPositions(float addToEachY, float addToEachX, boolean addDirectly);
	
	/** Mutable list of physics ops that act on this smartblob. Add them to the list. */
	public List<ChangeSpeed> mutablePhysics();

	/** may be null *
	public ChangeTargetDist getChangeTD();
	
	public void setChangeTD(ChangeTargetDist changeTD);
	*/
	
	/** doubleAngle ranges 0 to 2*layerSize-1 because each point in a layer has 2 lines going down. */
	public MovLine zigzag(int highLayer, int doubleAngle);
	
	/** To deal with alternating angles in zigzag and depending on layer,
	this returns a number -1 or 1 (or for around(int,int) use 0) that should be multiplied
	by how Brain sees Muscles of those MovLine so it bends consistently to create
	convex vs concave shapes depending on doubleAngle param.
	When a positive signed MovLine shrinks, a near MoveLine thats negative should lengthen,
	but in practice they may move in any combination since learned movements will be more complex.
	*/
	public byte sign(int highLayer, int doubleAngle);
	
	/** angle ranges 0 to layerSize-1. If layer is the outermost, then these MovLine are perimeter. */
	public MovLine around(int layer, int angle);
	
	/** Between onStartUpdatePositions() and onEndUpdatePositions() */
	public boolean isUpdatingPositions();
	
	/** Between onStartUpdateSpeeds() and onEndUpdateSpeeds() */
	public boolean isUpdatingSpeeds();
	
	/** prepare to use ChangeTargetDist object *
	public void onStartUpdateTargetDistances();
	
	/** done using ChangeTargetDist object *
	public void onEndUpdateTargetDistances();
	*/
	
	/** Tells this Smartblob that speeds are being updated, maybe by external code */
	public void onStartUpdateSpeeds();
	
	public void onEndUpdateSpeeds();
	
	/** Tells this Smartblob that positions are being updated (based on speeds),
	maybe by external code.
	*/
	public void onStartUpdatePositions();
	
	/** This func includes updating boundingRectangle() and shape()
	or onStartUpdatePosition may mark the need for new Rectangle and Shape
	in each call of those funcs. It can only be cached after end and before start.
	*/
	public void onEndUpdatePositions();
	
	/** May remove this or replace it with an array later.
	If this layeredZigzag is a cursor, then this is normally 0 when main button is not down,
	or 1 when it is down. There could be levels between in future versions depending on
	if the game controller (or touchscreen or mouse or whatever button) can detect pressure.
	*/
	public float cursorScalar();
	
	public MovCorner getMovCorner(CornerName address);
	
	/** includes extra corners not displayed like the 3 corners of centri in centriblob */ 
	public List<MovCorner> allMovCorners(); 

}