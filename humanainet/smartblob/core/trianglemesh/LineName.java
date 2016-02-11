/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.trianglemesh;

/** TODO edit text. This is the nonmoving version, and MovLine takes it as a parameter.
OLD: Moving. Lines equal when their 2 low Corners equal and 2 high Corners equal.
*/
public final class LineName{
	
	/** low and high corners are ordered (or equal) by CornerName which are Comparable with eachother */
	public final CornerName cornerLow, cornerHigh;
	
	public final int hash;
	public int hashCode(){ return hash; }
	
	/** Either order works as they will be sorted */
	public LineName(CornerName x, CornerName y){
		int compare = x.compareTo(y);
		if(compare < 0){
			cornerLow = x;
			cornerHigh = y;
		}else{
			cornerLow = y;
			cornerHigh = x;
		}
		/*CornerName low, high;
		long xLong = (((long)x.layer)<<32) | x.point;
		long yLong = (((long)y.layer)<<32) | y.point;
		if(xLong < yLong){
			cornerLow = x;
			cornerHigh = y;
		}else{
			cornerLow = y;
			cornerHigh = x;
		}
		*/
		hash = cornerHigh.hash<<5 + cornerLow.hash;
	}
	
	public boolean equals(Object o){
		if(o == this) return true;
		if(!(o instanceof LineName)) return false;
		LineName line = (LineName)o;
		return cornerLow.equals(line.cornerLow) && cornerHigh.equals(line.cornerHigh);
	}

}
