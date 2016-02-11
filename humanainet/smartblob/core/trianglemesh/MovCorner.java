/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.trianglemesh;

/** TODO similar to TriData.
Moving. Maybe I'll put distance constraints here between the up to 6 adjacent TriData
or up to 6 adjacent CornerData. */
public class MovCorner extends Adjacent{
	
	public final SmartblobTri smartblob;
	
	public final CornerName corner;
	
	public float y, x, speedX, speedY;
	
	/** TODO this is a new var as of 2016-2-4 and is not used by all code that affects physics. TODO */
	public float mass = 1;
	
	/** addtoX and addToY are similar to speed vars in that position is updated using them
	at the same time as speed vars (counts as isUpdatingSpeed) but are different in that they
	add directly to position and then are set to 0, as a 1 time thing during bounce calculations.
	*/
	public float addToX, addToY;
	
	public float addToSpeedX, addToSpeedY;
	
	/** This may be ignored, since TriData color is the main thing thats drawn.
	This would be drawn as a small circle or single pixel.
	<br><br>
	As int, see definition of color in DrawTri. TODO should each corner be drawn as potentially different color?
	*/
	public int colorARGB;
	//public Color colorOrNull;
	
	/** This constructor is only for the 3 inner MovCorner of a Centriblob.
	FIXME? (probably will have to)
	should any of its (as an Adjacent) arrays know about the surface points (MovCorner),
	or should those always be found from the MovCorners themselves?
	*
	public MovCorner(LayeredZigzag smartblob, CornerName corner){
		//super(0, 2, 2, 0, 0);
		this.smartblob = smartblob;
		this.corner = corner;
	}*/
	
	public final float defaultSpringTightnessIfCreateLines;
	
	public MovCorner(LayeredZigzag smartblob, CornerName corner/*, boolean edge, int centrilines*/, float defaultSpringTightnessIfCreateLines){
		//super(edge?3:6, edge?4:6, edge?4:6, 2, centrilines);
		this.smartblob = smartblob;
		this.corner = corner;
		this.defaultSpringTightnessIfCreateLines = defaultSpringTightnessIfCreateLines;
	}
	
	public final boolean connectFarLines = false;
	
	public void connectAdjacent(){
		final int lay = corner.layer;
		final int pt = corner.point;
		if(!(smartblob instanceof LayeredZigzag)) throw new RuntimeException(
			"TODO how to efficiently generalize "+SmartblobTri.class.getName()
			+" interface so we dont have to check if its a "+LayeredZigzag.class.getName());
		final LayeredZigzag smartblob = (LayeredZigzag) this.smartblob;
		final int laySiz = smartblob.layerSize;
		
		//TODO pointers to TriData
			
		//TODO pointers to CornerData
		boolean isLowestLayer = corner.layer == 0;
		boolean isHighestLayer = corner.layer == smartblob.layers-1;
		adjacentCorners.add(smartblob.corners[lay][(pt+1)%laySiz]);
		adjacentCorners.add(smartblob.corners[lay][(pt-1+laySiz)%laySiz]);
		boolean layerIsOdd = (corner.layer&1)==1;
		int highPInOtherLayer = layerIsOdd ? (pt+1)%laySiz : pt;
		if(!isLowestLayer){
			adjacentCorners.add(smartblob.corners[lay-1][(highPInOtherLayer)%laySiz]);
			adjacentCorners.add(smartblob.corners[lay-1][(highPInOtherLayer-1+laySiz)%laySiz]);
		}
		if(!isHighestLayer){
			adjacentCorners.add(smartblob.corners[lay+1][(highPInOtherLayer)%laySiz]);
			adjacentCorners.add(smartblob.corners[lay+1][(highPInOtherLayer-1+laySiz)%laySiz]);
		}
		/*
		//if(adjacentCorners.length == 6){ //6 adjacentCorners, all other 4
		if(!isLowestLayer && !isHighestLayer){
			adjacentCorners.add(smartblob.corners[lay-1][(highPInOtherLayer)%laySiz]);
			adjacentCorners.add(smartblob.corners[lay-1][(highPInOtherLayer-1+laySiz)%laySiz]);
			adjacentCorners.add(smartblob.corners[lay+1][(highPInOtherLayer)%laySiz]);
			adjacentCorners.add(smartblob.corners[lay+1][(highPInOtherLayer-1+laySiz)%laySiz]);
		//}else if(corner.layer == 0){ //4 adjacentCorners, other 2 are at higher layer
		}else if(isLowestLayer){
			adjacentCorners.add(smartblob.corners[lay+1][(highPInOtherLayer)%laySiz]);
			adjacentCorners.add(smartblob.corners[lay+1][(highPInOtherLayer-1+laySiz)%laySiz]);
		}else{ //4 adjacentCorners, other 2 are at lower layer
			//isHighestLayer
			adjacentCorners.add(smartblob.corners[lay-1][(highPInOtherLayer)%laySiz]);
			adjacentCorners.add(smartblob.corners[lay-1][(highPInOtherLayer-1+laySiz)%laySiz]);
		}*/
		
		//TODO pointers to LineData
		//for(int i=0; i<adjacentCorners.size(); i++){
		for(MovCorner c : adjacentCorners){
			//matches as key when either corner does this
			//LineName line = new LineName(corner, adjacentCorners.get(i).corner);
			LineName line = new LineName(corner, c.corner);
			adjacentLines.add(smartblob.lineData(line, defaultSpringTightnessIfCreateLines));
		}
		
		if(connectFarLines && corner.layer > 0){ //FIXME this doesnt appear to affect centriblob
			int cornersOver = 2;
			CornerName upCornerName = smartblob.corners[lay][(corner.point+cornersOver)%laySiz].corner;
			CornerName downCornerName = smartblob.corners[lay][(corner.point-cornersOver+laySiz)%laySiz].corner;
			LineName upLineName = new LineName(corner, upCornerName);
			LineName downLineName = new LineName(corner, downCornerName);
			MovLine upLine = smartblob.lineData(upLineName, defaultSpringTightnessIfCreateLines);
			MovLine downLine = smartblob.lineData(downLineName, defaultSpringTightnessIfCreateLines);
			farLines.add(downLine);
			farLines.add(upLine);
		}
	}
	
	public String toString(){
		return corner+"_x"+x+"_y"+y;
	}

}
