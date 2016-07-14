/** Ben F Rayfield offers Smartblob opensource GNU GPL 2+ */
package smartblob;

import java.awt.Color;

public class Circle implements GameOb{
	
	//public final long id;
	
	/** position of y */
	public float py;
	
	/** position of x */
	public float px;
	
	/** position of radius */
	public float pr;
	
	/** position of mass *
	public float pm = 1;
	*/
	
	/** speed of y */
	public float sy;
	
	/** speed of x */
	public float sx;
	
	public float pyAdd;
	
	public float pxAdd;
	
	/** add to sy at end of physics cycle */
	public float syAdd;
	
	/** add to sx at end of physics cycle */
	public float sxAdd;
	
	/** If this is a corner of a smartblob, relative coordinate by distance constraints.
	This changes gradually so when game controls come in, especially from webcam
	which can be jumpy, the smartblob shape changes smooth. Else vibration builds up.
	*/
	public float targetRelX, targetRelY;
	
	public final boolean collisionDetect;
	
	public final Smartblob blobOrNull;
	
	/** -1 if blobOrNull==null */
	public final int indexInBlob;
	
	/** Other than pushedByGravity, If collisionDetect and not canBePushed, then the other gameObject gets the whole force */
	public final boolean canBePushed;
	
	public final boolean pushedByGravity;
	
	protected Color color = Color.white;
	public Color color(){ return color; }
	public void setColor(Color c){ color = c; }
	
	/** all start as 0 *
	public Circle(){}
	*/
	
	public Circle(Smartblob blob, int indexInBlob, boolean doCollisions, boolean canBePushed, boolean pushedByGravity, float py, float px, float pr, float sy, float sx){
		this.blobOrNull = blob;
		this.indexInBlob = indexInBlob;
		this.collisionDetect = doCollisions;
		this.canBePushed = canBePushed;
		this.pushedByGravity = pushedByGravity;
		this.py = py;
		this.px = px;
		this.pr = pr;
		this.sy = sy;
		this.sx = sx;
	}
	
	public Circle(Circle copyMe){
		this(copyMe.blobOrNull, copyMe.indexInBlob, copyMe.collisionDetect, copyMe.canBePushed, copyMe.pushedByGravity,
			copyMe.py, copyMe.px, copyMe.pr, copyMe.sy, copyMe.sx);
		//this.pm = copyMe.pm;
		this.syAdd = copyMe.syAdd;
		this.sxAdd = copyMe.sxAdd;
	}
	
	public float py(){ return py; }

	public float px(){ return px; }
	
	public float pr(){ return pr; }
	
	public float sy(){ return sy; }

	public float sx(){ return sx; }

	public void refresh(double cacheTime){}
	
	/** update position based on speed and parameter duration */
	public void syncSpeedAndMove(float secondsSinceLastCall){
		sy += syAdd;
		sx += sxAdd;
		//rotate sy += sxAdd;
		//rotate sx -= syAdd;
		syAdd = 0;
		sxAdd = 0;
		py += pyAdd + sy*secondsSinceLastCall;
		px += pxAdd + sx*secondsSinceLastCall;
		pyAdd = 0;
		pxAdd = 0;
	}
	
	public float distanceSq(Circle c){
		float dx = c.px-px, dy = c.py-py;
		return dx*dx+dy*dy;
	}
	
	public float distance(Circle c){
		return (float)Math.sqrt(distanceSq(c));
	}
	
	public void bounceAgainstWhileIgnoringRadiusSpeed(float y, float x){
		float dy = y-py; //here to (y,x)
		float dx = x-px;
		float len = (float)Math.sqrt(dx*dx + dy*dy);
		if(len == 0) return;
		float normDx = dx/len;
		float normDy = dy/len;
		float dot = sx*normDx + sy*normDy;
		//TODO should this consider difference in speeds? It cant since its bouncing against a constant point
		if(dot > 0){ //moving toward (y,x)
			syAdd -= normDy*dot;
			sxAdd -= normDx*dot;
		}
	}

}