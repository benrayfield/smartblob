/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.physics.globalchangespeeds;
import humanaicore.common.time.Time;
import humanainet.smartblob.core.physics.GlobalChangeSpeed;
import humanainet.smartblob.core.physics.SmartblobSim;
import humanainet.smartblob.core.trianglemesh.LayeredZigzag;
import humanainet.smartblob.core.trianglemesh.MovCorner;
import humanainet.smartblob.core.trianglemesh.SmartblobTri;
import humanainet.smartblob.core.trianglemesh.radial.Radiblob;
import humanainet.ui.core.shapes.Rect;

public class BounceOnSimpleWall implements GlobalChangeSpeed{
	
	//TODO optimize collisions by checking boundingRectangle
	
	public float wallPosition;
	
	public final boolean verticalInsteadOfHorizontal;
	
	public final boolean maxInsteadOfMin;
	
	public BounceOnSimpleWall(float position, boolean verticalInsteadOfHorizontal, boolean maxInsteadOfMin){
		this.wallPosition = position;
		this.verticalInsteadOfHorizontal = verticalInsteadOfHorizontal;
		this.maxInsteadOfMin = maxInsteadOfMin;
		System.out.println(this);
	}
	
	public void globalChangeSpeed(SmartblobSim sim, float secondsSinceLastCall){
		SmartblobTri blobArray[];
		synchronized(sim.smartblobs){
			blobArray = sim.smartblobs.toArray(new SmartblobTri[0]);
		}
		for(SmartblobTri blob : blobArray){
			if(!blob.isIgnorePhysics()){
				Rect r = blob.boundingRectangle();
				//System.err.println("lock all uses of the smartblob because when click, some parts are resizing to up");
				if(anyPartIsPastThisWall(r)){
					if(blob instanceof LayeredZigzag){
						bounceSomePartsOnWall((LayeredZigzag)blob);
					}else{
						System.out.println(SmartblobTri.class.getName()+" type unknown: "+blob.getClass().getName());
					}
				}
			}
		}
	}
	
	public boolean anyPartIsPastThisWall(Rect r){
		if(verticalInsteadOfHorizontal){
			if(maxInsteadOfMin){ //max vertical
				return wallPosition <= r.y+r.height;
			}else{ //min vertical
				return r.y <= wallPosition;
			}
		}else{
			if(maxInsteadOfMin){ //max horizontal
				return wallPosition <= r.x+r.width;
			}else{ //min horizontal
				return r.x <= wallPosition;
			}
		}
	}
	
	public void bounceSomePartsOnWall(LayeredZigzag z){
		if(z instanceof Radiblob){
			bounceSomePartsOnWallRadial((Radiblob)z);
			return;
		}
		final float position = this.wallPosition;
		for(MovCorner layerOfCorners[] : z.corners){
			for(MovCorner cd : layerOfCorners){
				if(verticalInsteadOfHorizontal){
					if(maxInsteadOfMin){ //max vertical
						if(position < cd.y){ //bottom wall
							cd.speedY = -Math.abs(cd.speedY);
							cd.y = position; //FIXME causes energy to not be conserved so subtract it from energy gradually
							//float past = cd.y-position;
							//cd.addToY -= 2*past;
							//cd.y -= 2*past;
						}
					}else{ //min vertical
						if(cd.y < position){ //top wall
							cd.speedY = Math.abs(cd.speedY);
							cd.y = position; //FIXME causes energy to not be conserved so subtract it from energy gradually
							//TODO float past = position-cd.y;
							//TODO cd.addToY += 2*position;
						}
					}
				}else{
					if(maxInsteadOfMin){ //max horizontal
						if(position < cd.x){ //right wall
							cd.speedX = -Math.abs(cd.speedX);
							cd.x = position; //FIXME causes energy to not be conserved so subtract it from energy gradually
							//TODO float past = cd.x-position;
							//TODO cd.addToX -= 2*position;
						}
					}else{ //min horizontal
						if(cd.x < position){ //left wall
							cd.speedX = Math.abs(cd.speedX);
							cd.x = position; //FIXME causes energy to not be conserved so subtract it from energy gradually
							//TODO float past = position-cd.x;
							//TODO cd.addToX += 2*position;
						}
					}
				}
			}
		}
	}
	
	public void bounceSomePartsOnWallRadial(Radiblob r){
		//System.err.println("wall time="+Time.time());
		
		//"what if it hits 2+ places on a wall at once? If each mirrors the velocity based on surface point velocity, does that make it bounce 2+ times as much force as it should? Or does only the first change its velocity? What if the first pushes it sideways andOr rotation? Does it matter which is computed first? This is the simplest form of a problem that will happen between multiple blobs hitting eachother, sometimes multiple collisions at once on the same blob. I need to get this math straight, and test it by first bouncing with gravity and make sure it bounces the same height and acts like curvesmartblob bouncing, then add more blobs and have them bounce on eachother."
		//TODO
		try{
			final float position = this.wallPosition;
			for(MovCorner layerOfCorners[] : r.corners){
				for(MovCorner cd : layerOfCorners){
					if(verticalInsteadOfHorizontal){
						if(maxInsteadOfMin){ //max vertical
							//System.out.println("wall bottom");
							if(position < cd.y){ //bottom wall
								//System.out.println("wall bottom");
								//float positiveDiff = cd.y-position; //TODO use LayeredZigzag's minX maxX minY maxY funcs?
								float positiveDiff = r.maxY()-position;
								r.blobY -= positiveDiff;
								r.radiusToYx(); //TODO shouldnt this happen the same in fromYXPointAccelerateYX call below?
								//cd.speedY = -Math.abs(cd.speedY);
								r.fromYXPointAccelerateYX(cd.y, cd.x, -2*cd.speedY, 0);
								//cd.y = position; //FIXME causes energy to not be conserved so subtract it from energy gradually
							}
						}else{ //min vertical
							//System.out.println("wall top");
							if(cd.y < position){ //top wall
								//System.out.println("wall top");
								//float positiveDiff = position-cd.y; //TODO use LayeredZigzag's minX maxX minY maxY funcs?
								float positiveDiff = position-r.minY();
								r.blobY += positiveDiff;
								r.radiusToYx(); //TODO shouldnt this happen the same in fromYXPointAccelerateYX call below?
								//cd.speedY = Math.abs(cd.speedY);
								r.fromYXPointAccelerateYX(cd.y, cd.x, -2*cd.speedY, 0);
								//cd.y = position;
							}
						}
					}else{
						if(maxInsteadOfMin){ //max horizontal
							System.out.println("wall right");
							if(position < cd.x){ //right wall
								//System.out.println("wall right");
								//float positiveDiff = cd.x-position; //TODO use LayeredZigzag's minX maxX minY maxY funcs?
								float positiveDiff = r.maxX()-position;
								//System.out.println("rightwall diff="+positiveDiff);
								r.blobX -= positiveDiff;
								r.radiusToYx(); //TODO shouldnt this happen the same in fromYXPointAccelerateYX call below?
								//cd.speedX = -Math.abs(cd.speedX);
								r.fromYXPointAccelerateYX(cd.y, cd.x, 0, -2*cd.speedX);
								//cd.x = position; //FIXME causes energy to not be conserved so subtract it from energy gradually
							}
							
							/*
							//final float wall = position;
							final float wall = 500;
							//final float pos = position-100;
							//final float pos = position-20;
							//final float pos = position+20;
							//final float pos = 900;
							float maxX = r.maxX();
							float positiveDiff = maxX-wall;
							System.err.println("wall="+wall+" maxX="+maxX+" positiveDiff="+positiveDiff);
							r.blobX -= positiveDiff;
							r.radiusToYx();
							*/
							
						}else{ //min horizontal
							//System.out.println("wall left");
							if(cd.x < position){ //left wall
								//System.out.println("wall left");
								//float positiveDiff = position-cd.x; //TODO use LayeredZigzag's minX maxX minY maxY funcs?
								float positiveDiff = position-r.minX();
								r.blobX += positiveDiff;
								r.radiusToYx(); //TODO shouldnt this happen the same in fromYXPointAccelerateYX call below?
								//cd.speedX = Math.abs(cd.speedX);
								r.fromYXPointAccelerateYX(cd.y, cd.x, 0, -2*cd.speedX);
								//cd.x = position; //FIXME causes energy to not be conserved so subtract it from energy gradually
							}
						}
					}
				}
			}
		}catch(Throwable t){
			System.err.println(t.getMessage());
		}
		
		//TODO
	}
	
	public String toString(){
		if(verticalInsteadOfHorizontal) return (maxInsteadOfMin?"bottom":"top")+" wall "+wallPosition;
		return (maxInsteadOfMin?"right":"left")+" wall "+wallPosition;
	}

}
