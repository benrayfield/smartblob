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

public class LineMuscleFactory implements MuscleFactory{
	
	//TODO TriMuscleFactory (for volume of triangles) and LineAndTriMuscleFactory (both kinds of muscles in same list)
	
	//TODO standardize the order of lines. Start with outer lines then go inward. But there are other choices.
	
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
	
	/** FIXME: order of MovLine must be consistent for same number of rings and angles of LayeredZigzag,
	but this will work as long as you dont save and load the smartblob or statsys controlling its brain.
	*
	public Muscle[] newMuscles(LayeredZigzag smartblob, int quantity){
		List<Muscle> muscles = new ArrayList();
		MovLine movLines[] = smartblob.allLineDatas();
		Arrays.sort(movLines, compareMovLines);
		for(MovLine line : movLines){
			if(muscles.size() == quantity) break;
			if(epsilon < line.startDistance){
				CornerName highCorner = line.adjacentCorners[1].corner;
				byte sign = smartblob.sign(highCorner.layer, highCorner.point);
				muscles.add(new LineMuscle(line,scaleMult,sign==1));
			}
		}
		if(muscles.size() < quantity) throw new RuntimeException(
			"Could only create "+muscles.size()+" muscles but you said you need "+quantity);
		return muscles.toArray(new Muscle[0]);
	}*/
	
	/** Around the edges */
	public Muscle[] newMuscles(LayeredZigzag smartblob, int quantity){
		Muscle m[] = new Muscle[smartblob.layerSize()*2];
		int topLayer = smartblob.layers()-1;
		for(int i=0; i<m.length; i++){
			byte sign = smartblob.sign(topLayer, i);
			MovLine line = smartblob.zigzag(topLayer, i);
			m[i] = new LineMuscle(line, scaleMult, sign==1);
		}
		return m;
	}

	/** Excludes LineData whose startDistance is less than epsilon */
	public int maxMuscles(LayeredZigzag smartblob){
		int nonzeroLines = 0;
		for(MovLine line : smartblob.allLineDatas()){
			if(epsilon < line.startDistance) nonzeroLines++;
		}
		return nonzeroLines;
	}
	
	public int maxMuscles(int layers, int layerSize){
		/*if(layers == 3 && layerSize == 12) return 72;
		if(layers == 4 && layerSize == 12) return 108;
		if(layers == 6 && layerSize == 12) return 180;
		if(layers == 3 && layerSize == 32) return 192;
		throw new RuntimeException("TODO calculate instead of manually coding the 1 case I'm trying for now.");
		*/
		return layerSize*2;
	}

}
