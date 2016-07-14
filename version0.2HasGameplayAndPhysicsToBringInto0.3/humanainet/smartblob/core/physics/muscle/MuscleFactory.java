/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.physics.muscle;
import humanainet.smartblob.core.physics.Muscle;
import humanainet.smartblob.core.trianglemesh.LayeredZigzag;

public interface MuscleFactory{
	
	/** Returns an immutable list of new muscles for that smartblob.
	If this MuscleFactory normally creates more muscles than that for that
	size or type of smartblob, then returns the first quantity of them.
	*/
	public Muscle[] newMuscles(LayeredZigzag smartblob, int quantity);
	
	//TODO Are both these maxMuscles functions needed,
	//since Radialblob and Centriblob both extend LayeredZigzag?
	
	/** Whats the max muscles that newMuscles(smartblob,int) can create at once? */
	public int maxMuscles(LayeredZigzag smartblob);
	
	/** radial has 2 layers (center point and surface). LayeredZigzag can have more. */
	public int maxMuscles(int layers, int layerSize);

}