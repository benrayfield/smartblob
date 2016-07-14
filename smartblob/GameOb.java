/** Ben F Rayfield offers Smartblob opensource GNU GPL 2+ */
package smartblob;

import java.awt.Color;

/** A game object */
public interface GameOb{
	
	/** position of radius (instead of its speed, which at least in this version of the software is constant */
	public float pr();
	
	/** position of x */
	public float px();
	
	/** position of y */
	public float py();
	
	/** speed of x */
	public float sx();
	
	/** speed of y */
	public float sy();
	
	public Color color();
	public void setColor(Color c);

	//public double refreshedWhen();
	
	//public float centerOfGrav
	
	/** if last refresh was before cacheTime, refresh again, which updates
	radius (to bounding radius), x, y, xSpeed, ySpeed, etc, if is made of multiple GameObs,
	*/
	public void refresh(double cacheTime);

}
