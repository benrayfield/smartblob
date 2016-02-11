/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.physics.changespeeds;
import humanainet.smartblob.core.physics.ChangeSpeed;
import humanainet.smartblob.core.trianglemesh.LayeredZigzag;
import humanainet.smartblob.core.trianglemesh.MovCorner;
import humanainet.smartblob.core.trianglemesh.MovLine;
import humanainet.smartblob.core.trianglemesh.SmartblobTri;

public class Friction extends AbstractChangeSpeedLZ{
	
	public float friction;
	
	public Friction(float friction){
		this.friction = friction;
	}
	
	public void changeSpeed(LayeredZigzag blob, float secondsSinceLastCall){
		float subtractFromSpeed = friction*secondsSinceLastCall;
		//for(int layer=0; layer<blob.layers; layer++){
			//for(int p=0; p<blob.layerSize; p++){
			for(MovCorner cd : blob.allMovCorners()){
				//MovCorner cd = blob.corners[layer][p];
				float speed = (float)Math.sqrt(cd.speedY*cd.speedY + cd.speedX*cd.speedX);
				float newSpeed = speed-subtractFromSpeed;
				if(newSpeed <= 0){
					cd.addToSpeedY -= cd.speedY;
					cd.addToSpeedX -= cd.speedX;
				}else{
					float mult = newSpeed/speed;
					float newSpeedY = cd.speedY*mult;
					float newSpeedX = cd.speedX*mult; 
					cd.addToSpeedY += newSpeedY-cd.speedY;
					cd.addToSpeedX += newSpeedX-cd.speedX;
				}
			}
		//}
	}

}