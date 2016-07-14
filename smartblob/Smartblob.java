/** Ben F Rayfield offers Smartblob opensource GNU GPL 2+ */
package smartblob;

import java.awt.Color;

import humanaicore.common.Rand;

public class Smartblob implements GameOb{
	
	//public final long id;
	
	/** number of surface corners, equals number of surface lines */
	public final int size;
	
	/** targetDistance[centriPoint][surfacePoint] */
	public final float targetDistance[][];
	
	/** target radius of centri points, enforced by targetDistnance[][] */
	public final float skeletonRadius;
	
	/** the circles the surface is made of,
	with rotated rectangle between tangent lines between each 2 adjacent circles
	*/
	public final Circle corner[];
	
	/** line[x] is between corner[x] and corner[(x+1)%size] */
	public final Pole pole[];
	
	protected float targetPoleLen;
	
	/** centri points */
	public final Circle skeleton[];
	
	protected double cachedWhen;
	
	/** Includes Circle corners and therefore Poles between them, but not Circle skeleton.
	Update with this.refresh(double)
	*/
	public final Circle cacheCenter;
	
	/** derived from angles of each vector of skeleton from center, average those, get angle from that */
	public double cachedAngle;
	
	protected Color color = Color.white;
	public Color color(){ return color; }
	public void setColor(Color c){ color = c; }
	
	public Smartblob(Circle whereAndSize){
		//this(64, whereAndSize);
		this(32, whereAndSize);
		//this(16, whereAndSize);
	}
	
	public Smartblob(int corners, Circle whereAndSize){
		this(corners, (float)(2*Math.PI*whereAndSize.pr/corners*.4f), 7, whereAndSize, whereAndSize.pr*2f);
		//this(corners, (float)(2*Math.PI*whereAndSize.pr/corners*.1f), 7, whereAndSize, whereAndSize.pr*2.1f);
	}
	
	protected final double startSurfaceRadius;
	
	/** Centri must be at least 3 and is good for physics if odd, especially if prime, and default is 7.
	Centri should be much less than corners. Default corners is 64.
	*/
	public Smartblob(int corners, float radiusPerCircle, int centri, Circle whereAndSize, float skeletonRadius){
		size = corners;
		this.skeletonRadius = skeletonRadius;
		if(centri < 3) throw new RuntimeException("centri="+centri);
		float blobCenterToCornerCenter = whereAndSize.pr-radiusPerCircle;
		this.startSurfaceRadius = blobCenterToCornerCenter;
		if(blobCenterToCornerCenter <= 0) throw new RuntimeException("blobCenterToCornerCenter="+blobCenterToCornerCenter);
		if(skeletonRadius < whereAndSize.pr/16) throw new RuntimeException(
			"skeleton/centri radius should be around 1.5-3 times bigger"
			+" than the max distance from center of corners, but is: "+skeletonRadius);
		this.cacheCenter = new Circle(whereAndSize);
		corner = new Circle[size];
		pole = new Pole[size];
		skeleton = new Circle[centri];
		for(int i=0; i<size; i++){
			double angle = 2*Math.PI*i/size;
			float r = blobCenterToCornerCenter;
			//r *= .8+.2*Math.sin(7*angle);
			//r *= .6+.2*Math.sin(7*angle);
			float py = whereAndSize.py+r*(float)Math.sin(angle);
			float px = whereAndSize.px+r*(float)Math.cos(angle);
			Circle c = new Circle(this, i, true, true, true, py, px, radiusPerCircle, 0, 0);
			double sinFraction = .5+.5*Math.sin(angle);
			float bright = 1-.6f*(float)(1-Math.pow(1-sinFraction, 7));
			
			c.setColor(new Color(bright*.2f, bright*.4f, bright));
			corner[i] = c;
		}
		for(int i=0; i<size; i++){
			Pole p = new Pole(corner[i], corner[(i+1)%size]);
			p.setColor(corner[i].color());
			pole[i] = p;
		}
		for(int i=0; i<centri; i++){
			double angle = 2*Math.PI*i/centri;
			float py = whereAndSize.py+skeletonRadius*(float)Math.sin(angle);
			float px = whereAndSize.px+skeletonRadius*(float)Math.cos(angle);
			skeleton[i] = new Circle(this, size+i, false, false, false, py, px, 5, 0, 0);
		}
		targetDistance = new float[centri][size];
		setTargetDistancesToObservedDistances();
		
		/*
		//TODO remove this testing code for changing speeds
		for(int i=0; i<size; i++){
			corner[i].sy = .2f*(float)Rand.strongRand.nextGaussian();
			corner[i].sx = .2f*(float)Rand.strongRand.nextGaussian();
		}*/
		
	}

	public float pr(){ return cacheCenter.pr; }
	
	public float px(){ return cacheCenter.px; }
	
	public float py(){ return cacheCenter.py; }
	
	public float sx(){ return cacheCenter.sx; }

	public float sy(){ return cacheCenter.sy; }

	/** updates Circle cacheCenter and double cachedAngle */
	public void refresh(double cacheTime){
		if(cachedWhen < cacheTime){
			float sumPx=0, sumPy=0, sumSx=0, sumSy=0;
			for(int i=0; i<size; i++){
				Circle c = corner[i];
				sumPy += c.py;
				sumPx += c.px;
				sumSy += c.sy;
				sumSx += c.sx;
			}
			cacheCenter.px = sumPx/size;
			cacheCenter.py = sumPy/size;
			cacheCenter.sx = sumSx/size;
			cacheCenter.sy = sumSy/size;
			float maxR = 0;
			for(int i=0; i<size; i++){
				Circle c = corner[i];
				float dy = c.px-cacheCenter.px;
				float dx = c.px-cacheCenter.px;
				float r = (float)Math.sqrt(dx*dx+dy*dy)+c.pr;
				maxR = Math.max(maxR, r);
			}
			cacheCenter.pr = maxR;
			cachedWhen = cacheTime;
			
			float sumRotatedDy=0, sumRotatedDx=0;
			for(int k=0; k<skeleton.length; k++){
				//Measure skeleton from constant angles spread evenly around circle
				//so all rotatedDy will be about equal, and same for all rotatedDx.
				double fromAngle = -2*Math.PI*k/skeleton.length;
				Circle skel = skeleton[k];
				float dy = skel.py-cacheCenter.py;
				float dx = skel.px-cacheCenter.px;
				float fromY0 = (float)Math.sin(fromAngle);
				float fromX0 = (float)Math.cos(fromAngle);
				//perpendicular
				float fromY1 = fromX0;
				float fromX1 = -fromY0;
				//affineTransform
				float rotatedDy = dy*fromX1 + dx*fromX0;
				float rotatedDx = dy*fromY1 + dx*fromY0;
				//if(k==0) System.out.println("k="+k+" dy="+dy+" dx="+dx+" rotatedDy="+rotatedDy+" rotatedDx="+rotatedDx);
				sumRotatedDy += rotatedDy;
				sumRotatedDx += rotatedDx;
				//if(k == 2){
					//sumRotatedDy += dy;
					//sumRotatedDx += dx;
				//	sumRotatedDy += rotatedDy;
				//	sumRotatedDx += rotatedDx;
				//}
			}
			if(sumRotatedDy == 0 && sumRotatedDx == 0){
				cachedAngle = 0;
			}else{
				double len = Math.sqrt(sumRotatedDy*sumRotatedDy + sumRotatedDx*sumRotatedDx);
				double normY = sumRotatedDy/len;
				double normX = sumRotatedDx/len;
				double c = Math.acos(normX);
				cachedAngle = normY<0 ? 2*Math.PI-c : c;
				cachedAngle = 2*Math.PI-cachedAngle + Math.PI/2; //start pointing right
			}
		}
	}
	
	protected void setTargetDistancesToObservedDistances(){
		for(int k=0; k<skeleton.length; k++){
			final Circle skel = skeleton[k];
			for(int s=0; s<size; s++){
				targetDistance[k][s] = Util.centerDistance(skel, corner[s]);
			}
		}
	}
	
	public void decayTowardTargetPositionRelToAngle(int index, double y, double x, double decay){
		double maxSurfaceRadius = startSurfaceRadius;
		Circle c = corner[index];
		c.targetRelY = (float)(c.targetRelY*(1-decay) + decay*y);
		c.targetRelX = (float)(c.targetRelX*(1-decay) + decay*x);
		for(int k=0; k<skeleton.length; k++){
			double angle = 2*Math.PI*k/skeleton.length;
			double skelY = skeletonRadius*Math.sin(angle);
			double skelX = skeletonRadius*Math.cos(angle);
			//FIXME Should c.targetRelY and X already be scaled by maxSurfaceRadius
			//or should they be bifractions as they are now?
			double cornerY = c.targetRelY*maxSurfaceRadius;
			double cornerX = c.targetRelX*maxSurfaceRadius;
			double dy = skelY-cornerY;
			double dx = skelX-cornerX;
			targetDistance[k][index] = (float)Math.sqrt(dy*dy + dx*dx);
		}
	}

}
