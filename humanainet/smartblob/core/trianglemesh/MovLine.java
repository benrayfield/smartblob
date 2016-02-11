/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.trianglemesh;

import humanaicore.math.LinearInterpolate1Var;

/** I'm undecided if I'll use this LineData class or if include these vars in CornerData.
TODO update comments. this class is now called MovLine and I'm planning to keep it.
<br><br>
Moving. benfrayfieldResearch.qnSmartblobLineObject says:
In context of the datastructs tested by smartblobTestAdjacentTrianglePointLine,
should smartblobLayersOfZigzagAroundRadial have line objects which would hold cached
distance and targetDistance between each pair of connected corner? Maybe they would
all be collected into a set and run once each, then do force calculations at corners
and maybe triangles. Fluid flows between adjacent triangles to hold total volume
constant, but it still has to be calculated at the corners after that.
The corners have to move.
*/
public class MovLine extends Adjacent{
	
	public final SmartblobTri smartblob;
	
	public final LineName line;
	
	//better to calculate this again each time since thats where dx and dy numbers are:
	//public float distance;
	
	public float targetDistance;
	
	/** Distance in the balanced angles and radius all CornerData of the smartblob started */
	public float startDistance;
	
	/** Higher is a stronger distanceConstraint.
	Some lines will have springTightness of 0 to avoid physics calculations (TODO check for that).
	Between layer0 and layer1, pairs of lines overlap because layer0's MovCorners all have
	the same position. Also, in Centriblob, the only distance constraints will be between
	the corners of centri (an equilateral triangle) and eachother,
	and between each of those corners and each surface point (MovCorner at highest layer).
	Centriblob may also have distanceConstraints between adjacent surface points,
	but I'll see if thats needed in further experiments.
	The 3 MovLine between the centri corners need the highest springTightness
	because all the surface points have distanceConstraints with them.
	*/
	public float springTightness;
	
	protected LinearInterpolate1Var forceCurveOrNull;
	public LinearInterpolate1Var getForceCurveOrNull(){
		return forceCurveOrNull;
	}
	public void setForceCurveOrNull(LinearInterpolate1Var forceCurveOrNull){
		this.forceCurveOrNull = forceCurveOrNull;
	}
	
	/** touching 1 or 2 triangles. Edge is true if both layers are first or last layer. */
	public MovLine(LayeredZigzag smartblob, LineName line, float springTightness/*, boolean edge, int centrilines*/){
		//super(edge?1:2, 0, 2, 0, centrilines);
		this.smartblob = smartblob;
		this.line = line;
		this.springTightness = springTightness;
	}
	
	/** a distanceConstraint not connected to any triangles, directly between 2 Corners/points *
	public LineData(LayeredZigzag smartblob, Line line){
		"TODO how would reverse pointers work back at this LineData since the others know their number of Lines already? Maybe its best not to do this as LineData or not at all, especially if the fewer constraints work on their own."
	}*/
	
	public void connectAdjacent(){
		if(!adjacentCorners.isEmpty()) return; //only run once
		if(!(smartblob instanceof LayeredZigzag)) throw new RuntimeException(
			"TODO how to efficiently generalize "+SmartblobTri.class.getName()
			+" interface so we dont have to check if its a "+LayeredZigzag.class.getName());
		final LayeredZigzag smartblob = (LayeredZigzag) this.smartblob;
		
		//TODO pointers to TriData
		
		//TODO pointers to LineData
					
		//Since Centriblob has CornerName.layer == -1 for the 3 corners of centri, use the function:
		adjacentCorners.add(smartblob.getMovCorner(line.cornerLow));
		adjacentCorners.add(smartblob.getMovCorner(line.cornerHigh));
		//adjacentCorners.add(smartblob.corners[line.cornerLow.layer][line.cornerLow.point]);
		//adjacentCorners.add(smartblob.corners[line.cornerHigh.layer][line.cornerHigh.point]);
	}
	
	public float distance(){
		MovCorner a = adjacentCorners.get(0), b = adjacentCorners.get(1);
		float dy = b.y - a.y;
		float dx = b.y - a.y;
		return (float)Math.sqrt(dy*dy + dx*dx);
	}
	
	public String toString(){
		return "[MovLine "+adjacentCorners.get(0)+" "+adjacentCorners.get(1)+" distance="+distance()+"]";
	}

}