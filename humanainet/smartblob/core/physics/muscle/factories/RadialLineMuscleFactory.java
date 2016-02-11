/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.physics.muscle.factories;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import humanainet.smartblob.core.physics.Muscle;
import humanainet.smartblob.core.physics.muscle.LineMuscle;
import humanainet.smartblob.core.physics.muscle.MuscleFactory;
import humanainet.smartblob.core.trianglemesh.CornerName;
import humanainet.smartblob.core.trianglemesh.LayeredZigzag;
import humanainet.smartblob.core.trianglemesh.LineName;
import humanainet.smartblob.core.trianglemesh.MovLine;

/** Since pairs of MovLine overlap outward from center (between layers 0 and 1),
only the first in each pair are used with a Muscle.
*/
public class RadialLineMuscleFactory implements MuscleFactory{
	
	/** When LineData are excluded because their startDistance is 0, this epsilon is used for roundoff detection */ 
	protected float epsilon = .00001f;
	
	/** Used in calculating the range of distances each LineMuscle works with (and uses max above that) */
	protected float scaleMult = LineMuscle.defaultScaleMult;
	
	/** TODO is this a good order? Any consistent order is ok. */
	public static final Comparator<MovLine> compareMovLines = new Comparator<MovLine>(){
		public int compare(MovLine x, MovLine y){
			return -compareLineNamesByHighCornerFirst.compare(x.line, y.line);
		}
	};
	
	public static final Comparator<LineName> compareLineNamesByHighCornerFirst = new Comparator<LineName>(){
		public int compare(LineName x, LineName y){
			int compare = x.cornerHigh.compareTo(y.cornerHigh);
			if(compare < 0) return -1;
			if(compare > 0) return 1;
			return x.cornerLow.compareTo(y.cornerLow);
		}
	};
	
	/** Around the edges */
	public Muscle[] newMuscles(LayeredZigzag smartblob, int quantity){
		Muscle m[] = new Muscle[smartblob.layerSize()];
		for(int i=0; i<m.length; i++){
			MovLine line = smartblob.zigzag(1, 2*i);
			m[i] = new LineMuscle(line, scaleMult, true);
		}
		return m;
	}

	public int maxMuscles(LayeredZigzag smartblob){
		return smartblob.layerSize();
	}
	
	public int maxMuscles(int layers, int layerSize){
		return layerSize;
	}

}
