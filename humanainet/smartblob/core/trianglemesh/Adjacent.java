/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.trianglemesh;

import java.util.ArrayList;
import java.util.List;

public abstract class Adjacent{
	
	//TODO to allow lines to be added as distance constraints between points not in the same triangle,
	//convert these arrays to lists?
	
	/** starts full of nulls. Size is for all adjacent to fit.
	TODO first 2 adjacent corners are in same layer.
	*/
	public final List<MovCorner> adjacentCorners = new ArrayList();
	
	public final List<MovLine> adjacentLines = new ArrayList();
	
	/** starts full of nulls. Size is for all adjacent to fit. */
	public final List<MovTri> adjacentTris = new ArrayList();
	
	/** optionally, MovLines that are not between 2 adjacent corners.
	I'm going to use this for keeping the surface of radialsmartblob straighter
	so it cant fold on itself. These will skip an outer MovCorner (every 2)
	so combined with adjacent MovCorners (every 1) the distance constraints
	should be strong enough to have many points on surface and it be stable.
	<br><br>
	This is only for between surface points that are not adjacent but are at most n hops away.
	I'm moving others to centriSurfaceLines.
	*/
	public final List<MovLine> farLines = new ArrayList();
	
	/** REMOVED BECAUSE: Do this in farLines which is defined as any MovLines not between adjacent corners.
	<br><br>
	optionally, at least 3 (triangle is simplest shape that works) MovLines between
	this point and each of the 3 MovCorner of the center triangle
	of the centrismartblob, which are used to quickly move forces between all the surface points at once.
	*
	public final List<MovLine> centriLines[];
	*/
	public final List<MovLine> centriSurfaceLines = new ArrayList();
	
	//FIXME "TODO Is farLines and centriLines confusing the physics looping over adjacent MovLines? As long as LayeredZigzag.allLines() returns all such lines, the physics will work since its not using these datastructs except at time of creating the datastructs. Its done that way to avoid double computing MovLines which have 2 MovCorners each."
	
	/** fills in the adjacentCorners and adjacentTris arrays which start as containing nulls */
	public abstract void connectAdjacent();

	/* Dont need to know array sizes this early since I changed them to Lists
	public Adjacent(int adjacentTris, int adjacentLines, int adjacentCorners, int farLines, int centriLines){
		this.adjacentTris = new MovTri[adjacentTris];
		this.adjacentLines = new MovLine[adjacentLines];
		this.adjacentCorners = new MovCorner[adjacentCorners];
		this.farLines = new MovLine[farLines];
		this.centriLines = new MovLine[centriLines];
	}
	*/

}
