/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.physics.globalchangespeeds;
import humanainet.smartblob.core.physics.GlobalChangeSpeed;
import humanainet.smartblob.core.physics.SmartblobSim;
import humanainet.smartblob.core.trianglemesh.LayeredZigzag;
import humanainet.smartblob.core.trianglemesh.MovCorner;
import humanainet.smartblob.core.trianglemesh.SmartblobTri;
import humanainet.smartblob.core.trianglemesh.radial.Radiblob;

/** Subtracts from vertical speed continuously
(actually adds since positive is down in java graphics) of all CornerData
<br><br>
TODO whyDoesGravityDestabilizeCentriblobWithSpringDampening? Is it a thread error
or something similar maybe with order of updating speeds and positions?
I'm creating a ChangeSpeed version of Gravity thats individual to each smartblob.
*/
@Deprecated //see comment about whyDoesGravityDestabilizeCentriblobWithSpringDampening
public class GravityAsGlobalChangeSpeed implements GlobalChangeSpeed{
	
	public float acceleration;
	
	public GravityAsGlobalChangeSpeed(float acceleration){
		this.acceleration = acceleration;
	}
	
	public void globalChangeSpeed(SmartblobSim sim, float secondsSinceLastCall){
		boolean downIsPositive = true; //in java graphics down is positive y
		float amount = secondsSinceLastCall*acceleration;
		float addToSpeed = downIsPositive ? amount : -amount;
		SmartblobTri blobArray[];
		synchronized(sim.smartblobs){
			blobArray = sim.smartblobs.toArray(new SmartblobTri[0]);
		}
		for(SmartblobTri blob : blobArray){
			if(!blob.isIgnorePhysics()){
				if(blob instanceof LayeredZigzag){
					if(blob instanceof Radiblob){
						((Radiblob)blob).blobYSpeed += addToSpeed;
					}else{
						addToAllMovCornerYSpeeds(blob, addToSpeed);
					}
				}else{
					System.out.println(SmartblobTri.class.getName()+" type unknown: "+blob.getClass().getName());
				}
			}
		}
	}
	
	public static void addToAllMovCornerYSpeeds(SmartblobTri z, float addToAllSpeeds){
		/*for(MovCorner layerOfCorners[] : z.corners){
			for(MovCorner cd : layerOfCorners){
				cd.speedY += addToAllSpeeds;
			}
		}*/
		for(MovCorner c : z.allMovCorners()){
			c.addToSpeedY += addToAllSpeeds;
		}
		
	}

}
