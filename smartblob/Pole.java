/** Ben F Rayfield offers Smartblob opensource GNU GPL 2+ */
package smartblob;

import java.awt.Color;

/** between 2 Circle, a rotated rectangle between the 2 tangent lines that touch both circles */
public class Pole implements GameOb{
	
	/** c is lower index (or max if wrap between them) than d in smartblob, if its in a smartblob */
	public final Circle c, d;
	
	protected double refreshedWhen;
	
	protected float cachePr;
	
	protected Color color = Color.white;
	public Color color(){ return color; }
	public void setColor(Color c){ color = c; }
	
	public Pole(Circle c, Circle d){
		this.c = c;
		this.d = d;
	}

	public float pr(){
		return cachePr;
	}

	public float px(){
		return (c.px+d.px)/2;
	}

	public float py(){
		return (c.py+d.py)/2;
	}

	public float sx(){
		return (c.sx+d.sx)/2;
	}

	public float sy(){
		return (c.sy+d.sy)/2;
	}

	/** refreshes pr. Does not refresh the Circles because they are leafs and store data literally */
	public void refresh(double cacheTime){
		if(refreshedWhen < cacheTime){
			float dx = d.px-c.px;
			float dy = d.py-c.py;
			cachePr = (float)Math.sqrt(dx*dx+dy*dy)/2 + c.pr + d.pr;
			refreshedWhen = cacheTime;
		}
	}

}