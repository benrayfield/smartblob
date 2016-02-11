/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.trianglemesh;

/** A name of a corner. These CornerNamess are equal if their layer and point equal.
The moving version of this is MovCorner which has one of these as its parameter
and is shared by up to 6 triangles, a point with mutable x and y.
*/
public final class CornerName implements Comparable<CornerName>{
	
	public final int layer, point;
	
	protected final int hash;
	public int hashCode(){ return hash; }
	
	public CornerName(int layer, int point){
		this.layer = layer;
		this.point = point;
		//int h = point*3;
		//h += layer<<15;
		//hash = h;
		hash = (layer<<15)-point;
	}
	
	public boolean equals(Object o){
		if(o == this) return true;
		if(!(o instanceof CornerName)) return false;
		CornerName t = (CornerName)o;
		return point==t.point && layer==t.layer;
	}

	public int compareTo(CornerName c){
		if(layer < c.layer) return -1;
		if(layer > c.layer) return 1;
		if(point < c.point) return -1;
		if(point > c.point) return 1;
		return 0;
	}
	
	public String toString(){
		return "c_"+layer+"_"+point;
	}

}
