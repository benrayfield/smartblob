/** Ben F Rayfield offers Smartblob opensource GNU GPL 2+ */
package smartblob;

/** immutable */
public interface AccelField{
	
	//public double heightAtX(double x);

	//public double heightAtYX(double y, double x);
	
	//public double heightAtZYX(double z, double y, double x);
	
	/** reads position and return into same array */
	public void getAccelAt(float yx[]);
	
	public AccelField mult(float f);
	
}