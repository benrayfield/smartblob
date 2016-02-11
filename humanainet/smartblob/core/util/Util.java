package humanainet.smartblob.core.util;

import java.util.Arrays;
import java.util.Random;

import humanainet.smartblob.core.trianglemesh.LayeredZigzag;
import humanainet.smartblob.core.trianglemesh.MovCorner;
import humanainet.smartblob.core.trianglemesh.SmartblobTri;
import humanainet.ui.core.shapes.Rect;

public class Util{
	private Util(){}
	
	/** Moves the ball randomly until its farther away from its closest ball than newMinDist. */
	public static void moveBallToRandomMoreEmptyPlace(Rect walls, SmartblobTri blob, SmartblobTri blobs[], float newMinDist, Random rand){
		float minDist = distanceToClosestOtherBall(blob, blobs);
		while(minDist < newMinDist){
			float minX = walls.x+blob.maxRadius();
			float maxX = walls.x+walls.width-blob.maxRadius();
			float minY = walls.y+blob.maxRadius();
			float maxY = walls.y+walls.height-blob.maxRadius();
			//ball.position[0] = minX+rand.nextDouble()*(maxX-minX);
			//ball.position[1] = minY+rand.nextDouble()*(maxY-minY);
			//blob.setCenterOfGravityYX(
			//	minY+rand.nextFloat()*(maxY-minY),
			//	minX+rand.nextFloat()*(maxX-minX));
			float newY = minX+rand.nextFloat()*(maxY-minY);
			float newX = minX+rand.nextFloat()*(maxX-minX);
			blob.addToAllPositions(newY-blob.centerOfGravityY(), newX-blob.centerOfGravityX(), true);
			minDist = distanceToClosestOtherBall(blob, blobs);
		}
		System.out.println("Done moving ball="+blob+" minDist="+minDist);
	}
	
	/** Distance is negative if balls overlap */
	public static float distanceToClosestOtherBall(SmartblobTri blob, SmartblobTri blobs[]){
		if(blobs.length == 1) return Float.MAX_VALUE;
		float minDist = Float.MAX_VALUE;
		for(int i=0; i<blobs.length; i++){
			if(blobs[i] != blob){
				SmartblobTri otherBlob = blobs[i];
				//double dx = blob.position[0]-otherBall.position[0];
				float dx = blob.centerOfGravityX()-otherBlob.centerOfGravityX();
				float dy = blob.centerOfGravityY()-otherBlob.centerOfGravityY();
				float dist = (float) Math.sqrt(dx*dx + dy*dy);
				dist -= blob.maxRadius()+otherBlob.maxRadius();
				//distance is negative if blobs (maxRadius) overlap
				minDist = Math.min(minDist, dist);
			}
		}
		//System.out.println("minDist="+minDist+" ball="+ball);
		return minDist;
	}
	
	/** adds dy to all points y speed, and dx to all points x speed */
	public static void accelerateYX(LayeredZigzag blob, float dy, float dx){
		System.out.println("accelerateYX dy"+dy+" dx"+dx);
		int layers = blob.layers();
		for(int layer=0; layer<layers; layer++){
			int layerSize = blob.layerSize();
			for(int p=0; p<layerSize; p++){
				MovCorner c = blob.corners[layer][p];
				c.speedY += dy;
				c.speedX += dx;
			}
		}
	}
	
	public static float[] xPoints(LayeredZigzag blob){
		MovCorner mc[] = blob.corners();
		float x[] = new float[mc.length];
		for(int i=0; i<x.length; i++){
			x[i] = mc[i].x;
		}
		Arrays.sort(x);
		return x;
	}

}
