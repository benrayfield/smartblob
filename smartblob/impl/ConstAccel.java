/** Ben F Rayfield offers Smartblob opensource GNU GPL 2+ */
package smartblob.impl;
import smartblob.AccelField;

public class ConstAccel implements AccelField{
	
	public final float accelY, accelX;
	
	public ConstAccel(float accelY, float accelX){
		this.accelY = accelY;
		this.accelX = accelX;
	}
	
	public void getAccelAt(float[] yx){
		yx[0] = accelY;
		yx[1] = accelX;
	}
	
	public AccelField mult(float mult){
		return new ConstAccel(accelY*mult, accelX*mult);
	}

}
