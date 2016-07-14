/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.trianglemesh;

/** Mutable data about a specific triangle in a specific polygon mesh
of shape the same as LayeredZigzag.
<br><br>
Moving. The first 2 triangles are in the same trianglelayer
(different layers but same distance outward because of inward/outward boolean).
The other is either 1 layer higher or lower.
This array is either size 2 (if edge) or 3.
All adjacent Tri are opposite of inward/outward.
*/
public class MovTri extends Adjacent{
	
	public final SmartblobTri smartblob;
	
	public final TriName tri;
	
	public float volume;
	
	/** Volume mostly flows between adjacent triangles so total volume is conserved
	and it helps to flow force through the smartblob.
	*/
	public float targetVolume;
	
	//protected Shape cachedShape;
	//"TODO use int x[] and y[] instead of Shape? In LayeredZigzag outer shape? At least to replace TriData.cachedShape, or actually let that be replaced by each TriData knowing its 3 CornerData and let the display code get it from there."
	
	//protected long cachedInWhatCycle = -1;
	
	/** If null, use the default color depending on tri.inward and the LayeredZigzag.
	This can be used to display pressure, selection by mouse, or other things that happen on screen.
	*/
	//public Color colorOrNull;
	public int colorARGB = 0x7f7f7f7f;
	
	public final float defaultSpringTightnessIfCreateLines;
	
	/** If edge, there are 2 adjacent Tri, else 3.
	Those TriData are filled in later by caller, so that array contains nulls until then.
	<br><br>
	defaultSpringTightnessIfCreateLines is only used if this is the first time the MovLines are created
	and stored in the LayeredZigzag.
	*/
	public MovTri(LayeredZigzag smartblob, TriName tri/*, boolean edge*/, float defaultSpringTightnessIfCreateLines){
		//super(edge?2:3, 3, 3, 0, 0);
		this.smartblob = smartblob;
		this.tri = tri;
		//adjacentTris = new TriData[edge ? 2 : 3];
		this.defaultSpringTightnessIfCreateLines = defaultSpringTightnessIfCreateLines;
	}


	/** First LineData in TriData is at same layer as first 2 corners */
	public void connectAdjacent(){
		if(!(smartblob instanceof LayeredZigzag)) throw new RuntimeException(
			"TODO how to efficiently generalize "+SmartblobTri.class.getName()
			+" interface so we dont have to check if its a "+LayeredZigzag.class.getName());
		final LayeredZigzag smartblob = (LayeredZigzag) this.smartblob;
			
		//TODO pointers to TriData
		
		//pointers to CornerData works
		adjacentCorners.add(smartblob.corners[tri.layer][tri.point]);
		adjacentCorners.add(smartblob.corners[tri.layer][(tri.point+1)%smartblob.layerSize]);
		boolean layerIsOdd = (tri.layer&1)==1;
		int pInOtherLayer = layerIsOdd ? (tri.point+1)%smartblob.layerSize : tri.point;
		adjacentCorners.add(
			tri.inward
				? smartblob.corners[tri.layer-1][pInOtherLayer]
				: smartblob.corners[tri.layer+1][pInOtherLayer]
		);
		
		//float normal
		//"TODO what was I thinking here when I wrote 'float normal'?"
			
			
		//TODO pointers to LineData
		//Do in same order as corners. 0_1, 1_2, and 2_0.
		CornerName a = adjacentCorners.get(0).corner, b = adjacentCorners.get(1).corner, c = adjacentCorners.get(2).corner;
		adjacentLines.add(smartblob.lineData(new LineName(a, b), defaultSpringTightnessIfCreateLines));
		adjacentLines.add(smartblob.lineData(new LineName(b, c), defaultSpringTightnessIfCreateLines));
		adjacentLines.add(smartblob.lineData(new LineName(c, a), defaultSpringTightnessIfCreateLines));
	}
	
	//"TODO qnSmartblobLineObject"

}
