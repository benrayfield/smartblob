/** Ben F Rayfield offers HumanAiCore opensource GNU LGPL */
package humanaicore.math;

/* An object that has an interpolate(x) function that returns y,
where y is 1 of the outputNumbers if x is 1 of the inputNumbers,
or y is between 2 adjacent outputNumbers if x is between 2 adjacent inputNumbers.
inputNumbers must be sorted ascending, but outputNumbers can be any numbers.
The arrays must be the same size.
Does not modify or copy the arrays. Uses them directly.
If interpolate(x) and x < inputNumbers[0] || inputNumbers[inputNumbers.length-1] < x,
then that last number is used.
*/
public class LinearInterpolate1Var{
	
	protected final double inputNumbers[], outputNumbers[];
	
	public LinearInterpolate1Var(double inputNumbers[], double outputNumbers[]){ //TODO swap parameter order since outputs are normally first
		this.inputNumbers = inputNumbers; //x
		this.outputNumbers = outputNumbers; //y
	}
	
	public double interpolate(double x){
		int lowIndex = 0;
		int highIndex = inputNumbers.length-1;
		double lowX = inputNumbers[lowIndex]; //below known range
		double highX = inputNumbers[highIndex]; //above known range
		double lowY = outputNumbers[lowIndex];
		double highY = outputNumbers[highIndex];
		if(x < lowX) return lowY;
		if(highX < x) return highY;
		for(int i=0; i<30; i++){ //Should end before this. Avoid infinite loops.
			int midIndex = (lowIndex+highIndex)/2;
			double midX = inputNumbers[midIndex];
			double midY = outputNumbers[midIndex];
			if(x == midX) return midY; //TODO optimize. This happens rarely.
			if(x < midX){
				if(lowIndex+1 == midIndex){
					double rangeX = midX - lowX;
					double fractionX = (x-lowX)/rangeX;
					return lowY*(1-fractionX) + fractionX*midY;
				}else{
					highIndex = midIndex;
					highX = midX;
					highY = midY;
				}
			}else{
				if(midIndex+1 == highIndex){
					double rangeX = highX - midX;
					double fractionX = (x-midX)/rangeX;
					return midY*(1-fractionX) + fractionX*highY;
				}else{
					lowIndex = midIndex;
					lowX = midX;
					lowY = midY;
				}
			}
		}
		//nlmi.err('interpolate('+x+') function ended wrong (or would not end): this_node='+nlmi.toLongString(this_node));
		return Double.NaN;
	}

}
