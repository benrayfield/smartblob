/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.core.physics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import humanainet.smartblob.core.physics.changetds.ChangeTargetDist;
import humanainet.smartblob.core.physics.globalchangespeeds.BounceOnSimpleWall;
import humanainet.smartblob.core.trianglemesh.LayeredZigzag;
import humanainet.smartblob.core.trianglemesh.MovCorner;
import humanainet.smartblob.core.trianglemesh.MovTri;
import humanainet.smartblob.core.trianglemesh.SmartblobTri;
import humanainet.smartblob.core.trianglemesh.radial.Radiblob;
import humanainet.smartblob.core.util.CurvblobUtil;
import humanainet.smartblob.core.util.RadiblobUtil;
import humanainet.smartblob.core.util.Util;
import humanainet.ui.core.shapes.Rect;

/** Simulation of smartblob physics including collisions with eachother andOr walls
and maybe 1d heightmap from floor andOr other walls.
*/
public class SmartblobSim{
	
	//TODO use this same SmartblobSim class for curvesmartblob and radialsmartblob, with different options
	
	public final Set<SmartblobTri> smartblobs = new HashSet();
	
	boolean testAccelTowardMouse = true;
	
	/** for testing accelerate toward cursor in context of bobagaBallOverheats */
	protected SmartblobTri firstSmartblob;
	
	public final List<GlobalChangeSpeed> physicsParts;
	
	/** TODO bobagaBallOverheats and reshapeOverheats */
	protected final boolean accelerateFirstSmartblobTowardCursor = false;
	
	/** At least 1 cursor per player, by default exactly 1, and you can just think of
	multiple cursors per player as 2 players working together (maybe on the same SmartblobTri.team()).
	Cursors have true Smartblob.isIgnoreCollisions(), but not all isIgnoreCollisions are cursors.
	*/
	protected List<SmartblobTri> cursors = new ArrayList<SmartblobTri>();
	public SmartblobTri[] cursors(){
		return cursors.toArray(new SmartblobTri[0]);
	}
	public void addCursor(SmartblobTri cursor){
		if(!cursor.isIgnorePhysics()) throw new RuntimeException("Cursor must have isIgnoreCollisions: "+cursor);
		cursors.add(cursor);
		smartblobs.add(cursor);
	}
	
	protected Rect cachedDynamicBounds;
	/** update this with updateDynamicBoundsAtMostInsideThis func */
	public Rect getCachedDynamicBounds(){ return cachedDynamicBounds; }
	
	public SmartblobSim(GlobalChangeSpeed... physicsParts){
		this.physicsParts = new ArrayList(Arrays.asList(physicsParts));
	}
	
	/** The first was curvesmartblob, but later I limited it to radialsmartblob which is still a LayeredZigzag.
	In theory they're compatible, but for now I'm using this to turn off CurvesmartblobUtil.moveAll
	while I design a more complex similar process for radial physics having both polar and xy coordinates
	and alternating between them.
	TODO merge these after radialsmartblob is working, so both can be on screen and physics together.
	*
	public final boolean curvesmartblobMode = false;
	*/
	
	protected double secondsSimulated = 0;
	
	public void nextState(float secondsThisTime){
		
		secondsSimulated += secondsThisTime;
		
		//secondsThisTime = .001f; //FIXME
		
		
		SmartblobTri blobArray[];
		
		synchronized(smartblobs){
			blobArray = smartblobs.toArray(new SmartblobTri[0]);
		}
		
		/*if(secondsSimulated < 3){
			for(SmartblobTri blob : blobArray){
				blob.onStartUpdateSpeeds();
				for(MovCorner c : blob.allMovCorners()){
					//c.addToSpeedY = -c.speedY;
					//c.addToSpeedX = -c.speedX;
					c.speedY = 0;
					c.speedX = 0;
				}
				blob.onEndUpdateSpeeds();
			}
		}*/
		
		/*for(Smartblob blob : blobArray){
			blob.onStartUpdateTargetDistances();
		}
		for(Smartblob blob : blobArray){
			ChangeTargetDist changeTD = blob.getChangeTD();
			if(changeTD != null){
				changeTD.changeTargetDists(blob, secondsThisTime);
			}		}
		for(Smartblob blob : blobArray){
			blob.onEndUpdateTargetDistances();
		}*/
		
		for(SmartblobTri blob : blobArray){
			blob.onStartUpdateSpeeds();
		}
		for(GlobalChangeSpeed p : physicsParts){
			p.globalChangeSpeed(this, secondsThisTime);
		}
		if(testAccelTowardMouse){
			if(firstSmartblob == null && smartblobs.size()>0 && cursors.size()>0){
				for(SmartblobTri blob : smartblobs){
					if(!blob.isIgnorePhysics()){
						firstSmartblob = blob;
						break;
					}
				}
			}
			if(firstSmartblob != null){
				SmartblobTri cursor = cursors()[0];
				if(accelerateFirstSmartblobTowardCursor){
					//TODO merge duplicate code
					float toCursorY = cursor.centerOfGravityY()-firstSmartblob.centerOfGravityY();
					float toCursorX = cursor.centerOfGravityX()-firstSmartblob.centerOfGravityX();
					System.out.println("toCursorY="+toCursorY+" toCursorX="+toCursorX);
					float distSq = toCursorY*toCursorY + toCursorX*toCursorX;
					if(distSq > 0){
						float minDist = 50;
						distSq = Math.max(distSq, minDist*minDist);
						float dist = (float)Math.sqrt(distSq);
						float normDy = toCursorY/dist;
						float normDx = toCursorX/dist;
						System.out.println("normDy="+normDy+" normDx="+normDx);
						float mult = cursor.cursorScalar()*20000000f*secondsThisTime;
						float dy = mult*normDy/distSq;
						float dx = mult*normDx/distSq;
						Util.accelerateYX((LayeredZigzag)firstSmartblob, dy, dx);
					}
				}/*else{
					//Test fromYXPointAccelerateYX from mouse cursor,
					//before code for collisions between radialsmartblobs
					//
					//TODO merge duplicate code
					MovTri m = firstSmartblob.findCollision(mouseY, mouseX);
					"TODO should this be done in the ui?""
					Util.accelerateYX((LayeredZigzag)firstSmartblob, dy, dx);
				}*/
			}
		}
		for(SmartblobTri blob : blobArray){
			blob.onEndUpdateSpeeds();
		}
		
		//"Check for Radialsmartblob somewhere in this func, and call yxToRadius and radiusToYx, probably as a replacement for CurvesmartblobUtil.moveAll, or maybe inside it"
		//"Can it be done in moveAll? I'm not sure how this process fits together as it branches to 2 kinds of movement"
		
		//CurvesmartblobUtil.moveAll(this, secondsThisTime); //calls onStart*  and onEnd* *UpdatePositions
		
		for(SmartblobTri blob : blobArray){
			blob.onStartUpdateSpeeds();
		}
		for(SmartblobTri blob : blobArray){
			//blob.nextState(secondsSinceLastCall); //does all SmartblobPhysicsPart and updateShape and maybe more
			//TODO threads
			for(ChangeSpeed c : blob.mutablePhysics()){
				c.changeSpeed(blob, secondsThisTime);
			}
			blob.onEndUpdateSpeeds();
		}
		
		//CurvesmartblobUtil.moveAll(this, secondsThisTime);
		/*if(curvesmartblobMode){
			//calls onStart* and onEnd* *UpdatePositions
			CurvesmartblobUtil.moveAll(this, secondsThisTime);
		}else{
			//calls onStart* and onEnd* *UpdatePositions
			RadialsmartblobUtil.doPolarAndYXPositionsAndSpeedsInSomeCombination(this, secondsThisTime);
		}*/
		
		//SmartblobTri blobArray2[];
		//synchronized(smartblobs){
		//	blobArray = smartblobs.toArray(new SmartblobTri[0]);
		//}
		/*for(SmartblobTri blob : blobArray){
			blob.onStartUpdatePositions();
			//if(blob instanceof LayeredZigzag){
				CurvesmartblobUtil.move((LayeredZigzag)blob, secondsThisTime);
			//}else{
			//	System.out.println(SmartblobTri.class.getName()+" type unknown: "+blob.getClass().getName());
			//}
			blob.onEndUpdatePositions();
		}*/
	
		for(SmartblobTri blob : blobArray){
			for(MovCorner c : blob.allMovCorners()){
				c.speedX += c.addToSpeedX;
				c.addToSpeedX = 0;
				c.speedY += c.addToSpeedY;
				c.addToSpeedY = 0;
			}
			blob.move((float)secondsThisTime);
			
			/*if(blob instanceof LayeredZigzag){
				if(blob instanceof Radialsmartblob){

					//FIXME onStart and onEnd of things in this last step...
					blob.onStartUpdatePositions(); //not accurate, since may update both positions and speeds
					RadialsmartblobUtil.doPolarAndYXPositionsAndSpeedsInSomeCombination(
						(Radialsmartblob)blob, secondsThisTime);
					//CurvesmartblobUtil.move((LayeredZigzag)blob, secondsThisTime);
					blob.onEndUpdatePositions(); //not accurate, since may have updated both positions and speeds
					//blob.move(secondsSinceLastCall);
					
					TODO
				}else{ //curvesmartblob
					blob.onStartUpdatePositions();
					CurvesmartblobUtil.move((LayeredZigzag)blob, secondsThisTime);
					blob.onEndUpdatePositions();
				}
			}else{
				throw new RuntimeException("smartblob type unknown: "+blob.getClass());
			}*/
		}
	}
	
	/** If no wall is found in direction of left, right, up, andOr down, that part of maxBounds is used instead.
	Updates the dynamicBounds var to at most hardMaxbounds or usually what the BounceOnSimpleWall say inside that.
	*/
	public void updateDynamicBoundsAtMostInsideThis(Rect hardMaxBounds){
		//top of screen is 0. Increase is down.
		float top = hardMaxBounds.y;
		float bottom = hardMaxBounds.y+hardMaxBounds.height;
		float left = hardMaxBounds.x;
		float right = hardMaxBounds.x+hardMaxBounds.width;
		for(GlobalChangeSpeed physicsPart : physicsParts){
			if(physicsPart instanceof BounceOnSimpleWall){
				BounceOnSimpleWall wall = (BounceOnSimpleWall) physicsPart;
				if(wall.verticalInsteadOfHorizontal){ //vertical
					if(wall.maxInsteadOfMin){ //bottom
						bottom = Math.min(bottom, wall.wallPosition);
					}else{ //top
						top = Math.max(top, wall.wallPosition);
					}
				}else{ //horizontal
					if(wall.maxInsteadOfMin){ //right
						right = Math.min(right, wall.wallPosition);
					}else{ //left
						left = Math.max(left, wall.wallPosition);
					}
				}
			}
		}
		if(right <= left || top <= bottom) cachedDynamicBounds = new Rect(hardMaxBounds.y, hardMaxBounds.x, 0, 0);
		cachedDynamicBounds = new Rect(top, left, bottom-top, right-left);
	}
	
	/** TODO use a 2d grid of squares and each square contains any Smartblob whose boundingRect touches that square,
	but for now it compares the Rect to all Smartblobs.
	*/
	public SmartblobTri[] smartblobsPossiblyIntersecting(Rect r){
		List<SmartblobTri> list = new ArrayList();
		synchronized(smartblobs){
			for(SmartblobTri blob : smartblobs){
				if(r.intersects(blob.boundingRectangle())){
					list.add(blob);
				}
			}
		}
		return list.toArray(new SmartblobTri[0]);
	}
	
	/** Example use: To find which smartblob mouse is touching, if any. Collisions are done more efficient ways. */
	public SmartblobTri noncursorSmartblobAtYXOrNull(float y, float x){
		synchronized(smartblobs){
			for(SmartblobTri blob : smartblobs){
				if(!blob.isIgnorePhysics()){
					if(blob.boundingRectangle().containsYX(y, x)){
						if(!(blob instanceof LayeredZigzag)) throw new RuntimeException("Smartblob type unknown: "+blob);
						MovTri closestTri = ((LayeredZigzag)blob).findCollision(y, x);
						if(closestTri != null) return blob; //inside borders of the smartblob, closest to that MovTri
					}
				}
			}
			return null; //outside borders of the smartblob
		}
	}
	
	
	/** Adds the object into the space and returns it */
	public SmartblobTri addRandomObject(Map optionalParams, Random rand){
		throw new RuntimeException("TODO");
	}

}
