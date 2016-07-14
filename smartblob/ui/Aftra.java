package smartblob.ui;

import java.util.Arrays;

/** affine transform of any number of dims */
public class Aftra{
	
	public final float axis[][];
	
	public final float centerIn[];
	
	public final float centerOut[];
	
	public final int dims;
	
	/** starts as identity matrix */
	public Aftra(int dims){
		this.dims = dims;
		this.axis = new float[dims][dims];
		this.centerIn = new float[dims];
		this.centerOut = new float[dims];
		for(int i=0; i<dims; i++){
			axis[i][i] = 1;
		}
	}
	
	/*TODO copy some code from forward1DimYX public void forward(float out[], float in[]){
		Arrays.fill(out, 0);
		for(int i=0; i<dims; i++){
			float ax[] = axis[i];
			for(int j=0; j<dims; j++){
				out[j] += ax[j]*in[i];
			}
			out[i] -= center[i];
		}
	}
	
	public float forward1Dim(int dim, float in[]){
		float sum = -center[dim];
		for(int i=0; i<dims; i++){
			sum += axis[i][dim]*in[i];
		}
		return sum;
	}*/
	
	/** only use this if dims==2 */
	public float forward1DimYX(int dim, float inY, float inX){
		return axis[0][dim]*(inY-centerIn[0]) + axis[1][dim]*(inX-centerIn[1]) + centerOut[dim];
	}
	
	/** Example use: for drawing circles at correct radius, if all axis are equal len */
	public float maxAxisLen(){
		float max = 0;
		for(int i=0; i<dims; i++){
			max = Math.max(max, axisLen(i));
		}
		return max;
	}
	
	/** Example use: for drawing ovals of correct size in each direction */
	public float axisLen(int dim){
		float sumOfSquares = 0;
		float ax[] = axis[dim];
		for(int i=0; i<dims; i++){
			sumOfSquares += ax[i]*ax[i];
		}
		return (float)Math.sqrt(sumOfSquares);
	}
	
	public void backward(float out[], float in[]){
		throw new RuntimeException("TODO is this math harder than forward?");
	}

}
