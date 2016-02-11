/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.physics.muscle.factories;
import humanainet.smartblob.core.physics.Muscle;
import humanainet.smartblob.core.physics.muscle.LineMuscle;
import humanainet.smartblob.core.physics.muscle.MuscleFactory;
import humanainet.smartblob.core.trianglemesh.LayeredZigzag;
import humanainet.smartblob.core.trianglemesh.LineName;
import humanainet.smartblob.core.trianglemesh.MovCorner;
import humanainet.smartblob.core.trianglemesh.MovLine;
import humanainet.smartblob.core.trianglemesh.centri.Centriblob;
import humanainet.smartblob.core.util.CentriblobUtil;

public class CentriMuscleFactory implements MuscleFactory{
	
	public final float minRadius, maxRadius;
	
	public CentriMuscleFactory(float minRadius, float maxRadius){
		this.minRadius = minRadius;
		this.maxRadius = maxRadius;
	}
	
	/** Creates the MovLine between surface points and centri, by calling LayeredZigzag.lineData */
	public Muscle[] newMuscles(LayeredZigzag smartblob, int quantity){
		//if(1<2) throw new RuntimeException("TODO this isnt used yet. using Centriblob.setTargetRadius until get it working. Then will cache with LinearInterpolate1Var if thats faster than the low memory uses of sine and sqrt");
		if(!(smartblob instanceof Centriblob)) throw new RuntimeException(
			"Not a "+Centriblob.class.getName()+": "+smartblob);
		int max = maxMuscles(smartblob);
		if(quantity < 1 || max < quantity) throw new RuntimeException(
			"quantity="+quantity+" min=1 maxMuscles="+max);
		Centriblob cblob = (Centriblob) smartblob;
		int lastLayer = cblob.layers-1;
		int siz = Math.min(quantity, cblob.layerSize);
		Muscle centrimuscles[] = new Muscle[siz];
		
		int howManyCentricorners = cblob.centriCorners.size();
		float springTightnessCentriToSurface =
			CentriblobUtil.defaultSpringTightnessForBetweenCentriCornerAndSurfaceCorner(cblob);
		for(int i=0; i<siz; i++){
			MovCorner surfacePoint = cblob.corners[lastLayer][i];
			//Muscle childs[] = new Muscle[howManyCentricorners];
			for(int c=0; c<howManyCentricorners; c++){
				//MovCorner centriPoint = cblob.centriCorners[c];
				MovCorner centriPoint = cblob.centriCorners.get(c);
				//IMPORTANT: Without this, Centriblob cant change radius at any int angle:
				//lineData func also puts the MovLine in LayeredZigzag.allLineDatas()
				//lineData func only uses springTightnessInCentri if its a new MovLine.
				MovLine line = cblob.lineData(
					new LineName(centriPoint.corner, surfacePoint.corner), springTightnessCentriToSurface);
				//childs[c] = new LineMuscle(line, true);
			}
			centrimuscles[i] = new CentriMuscle(cblob, i, minRadius, maxRadius);
		}
		return centrimuscles;
	}
	
	public int maxMuscles(LayeredZigzag smartblob){
		return maxMuscles(smartblob.layers, smartblob.layerSize);
	}
	
	/** Only the last layer gets any muscles. Each surface point has either 1 or 3 muscles TODO...
	TODO should it be 1 muscle that controls 3 child muscles (to each corner of centri)
	or should those 3 be visible to the SmartblobBrain as returned here?
	I'm going with only 1 per surface point, though that may change. I'll see how it works.
	*/
	public int maxMuscles(int layers, int layerSize){
		return layerSize;
	}

}
