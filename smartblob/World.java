/** Ben F Rayfield offers Smartblob opensource GNU GPL 2+ */
package smartblob;
import humanaicore.common.Time;
import smartblob.Util;
import smartblob.impl.ConstAccel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class World{
	
	//TODO OPTIMIZE pole
	
	//FIXME synchronize between graphics and this object
	
	public final List<Smartblob> blobs = new ArrayList();
	
	//"TODO include Circles that Smartblob is made of?"
	public final List<Circle> stuff = new ArrayList();
	
	protected double lastTimeStartedPhysicsCycle;
	
	public AccelField accelField = new ConstAccel(3f, 0);
	//public AccelField accelField = new ConstAccel(35f, 0);
	//public AccelField accelField = new ConstAccel(350f, 0);
	//public AccelField getAccelField(){ return accelField; }
	
	//protected double fractionOfPreventOverlap = .01;
	//protected double fractionOfPreventOverlap = 0; //TODO?
	
	protected boolean randomizeColorWhenCollide = false;
	
	protected boolean allowFixSkeleton = true;
	
	long totalCycles = 0;
	double totalSecondsSinceLastCall = 0; //not counting when paused
	
	public final List<Circle> allCircles = new ArrayList();
	public void updateListOfAllCircles(){
		allCircles.clear();
		for(Smartblob b : blobs){
			allCircles.addAll(Arrays.asList(b.corner));
			allCircles.addAll(Arrays.asList(b.skeleton));
		}
		allCircles.addAll(stuff);
	}
	
	//TODO remove duplicate code between nextState springs and handlePossibleCollision,
	//but is it duplicated or just similar?
	
	public void nextState(float secondsSinceLastCall){
		float aveSeconds = (float)((totalSecondsSinceLastCall+1)/(totalCycles+100));
		float maxSecondsAllowedSinceLastCall = aveSeconds*3;
		secondsSinceLastCall = Math.max(0, Math.min(secondsSinceLastCall, maxSecondsAllowedSinceLastCall));
		totalCycles++;
		if(totalCycles < 20) secondsSinceLastCall /= 5; //FIXME remove this. dont move as much when start
		totalSecondsSinceLastCall += secondsSinceLastCall;
		double now = Time.time();
		lastTimeStartedPhysicsCycle = now;
		for(Smartblob blob : blobs){ //spring between skeleton and surface
			for(int k=0; k<blob.skeleton.length; k++){
				final Circle b = blob.skeleton[k];
				final float[] targetDistK = blob.targetDistance[k];
				for(int c=0; c<blob.corner.length; c++){
					Circle a = blob.corner[c];
					//if(!a.collisionDetect) continue;
					//if(!a.canBePushed && !b.canBePushed) continue;
					
					//TODO? float springTightness = springTightnessMult*ld.springTightness;
					//TODO, vary this per blob, per spring, or per world?
					//float springTightness = 20;
					//float springTightness = 100;
					//float springTightness = 300;
					float springTightness = 500;
					//float springTightness = 300;
					//float springTightness = 2000;
					//float springTightness = 20;
					//float springTightness = 200;
					//float springTightness = 2;
					//float springTightness = .2f;
					
					//TODO, vary this per blob, per spring, or per world?
					//default in smartblob0.2.0: float springDampen = 1.5f;
					//float springDampen = .1f;
					//float springDampen = .3f;
					//float springDampen = 0f;
					//float springDampen = .5f;
					float springDampen = 1.5f;
					//float springDampen = 3f;
					//float springDampen = 7f;
					
					float dy = b.py-a.py;
					float dx = b.px-a.px;
					float observedDistance = (float)Math.sqrt(dy*dy + dx*dx);
					//float observedDistance = skel.distance(corner);
					if(observedDistance == 0) continue;
					float normDy = dy/observedDistance;
					float normDx = dx/observedDistance;
					float targetDistance = targetDistK[c];
					//float distDiff = targetDistance-observedDistance;
					
					float dSpeedY = b.sy - a.sy;
					float dSpeedX = b.sx - a.sx;
					float springEndsVelocityTowardEachother = dSpeedY*normDy + dSpeedX*normDx; //dotProd
					float springDampenForce = -springDampen*springEndsVelocityTowardEachother;
					
					
					/*
					//FIXME If springDampen is more than 1, it would move the other direction, in theory,
					//but why isnt it doing that? Why does a springDampen of 1.5 or sometimes 5 work?
					//FIXME TODO springDampenForce must only slow the spring, not accelerate it the opposite direction
					float massSumOfSpringEnds = a.pm+b.pm; //mass that spring dampening is slowing
					float springDampenForceAsMomentum = springDampenForce*massSumOfSpringEnds;
					float wantToAddToDistance = targetDistance-observedDistance; //positive or negative
					
					//TODO should springTightness be relative to mass (multiply by massSumOfSpringEnds below)?
					//Or should the same tightness between more mass vibrate slower (dont multiply)?
					
					float springForceAsMomentum = wantToAddToDistance*springTightness*massSumOfSpringEnds;
					float addToMomentum = springForceAsMomentum + springDampenForceAsMomentum;
					float addToDMomentum = secondsSinceLastCall*addToMomentum;
					float addToMomentumY = normDy*addToDMomentum;
					float addToMomentumX = normDx*addToDMomentum;
					b.syAdd += addToMomentumY/b.pm;
					a.syAdd -= addToMomentumY/a.pm;
					b.sxAdd += addToMomentumX/b.pm;
					a.sxAdd -= addToMomentumX/a.pm;
					*/
					
					float wantToAddToDistance = targetDistance-observedDistance; //positive or negative
					float addToSpeed =  wantToAddToDistance*springTightness + springDampenForce;
					float addToDSpeed = secondsSinceLastCall*addToSpeed;
					float addToSpeedY = normDy*addToDSpeed;
					float addToSpeedX = normDx*addToDSpeed;
					//if(a.canBePushed && b.canBePushed){
						b.syAdd += addToSpeedY/2;
						a.syAdd -= addToSpeedY/2;
						b.sxAdd += addToSpeedX/2;
						a.sxAdd -= addToSpeedX/2;
					/*}else{
						if(a.canBePushed){
							a.syAdd -= addToSpeedY;
							a.sxAdd -= addToSpeedX;
						}else{
							b.syAdd += addToSpeedY/2;
							b.sxAdd += addToSpeedX/2;
						}
					}*/
				}
			}
		}
		
		if(allowFixSkeleton){
			//Prevent skeleton from turning insideout. The surface points spread, and skeleton is near middle.
			//To prevent that, each skeleton point is put at its expected location if it gets too close to center.
			//This would normally only happen when physics gets too jumpy such as around large timing differences.
			for(Smartblob blob : blobs){
				blob.refresh(now);
				float minSkelDistance = blob.skeletonRadius*.6f;
				boolean fixSkeleton = false;
				for(int k=0; k<blob.skeleton.length; k++){
					Circle skel = blob.skeleton[k];
					if(skel.distance(blob.cacheCenter) < minSkelDistance){
						fixSkeleton = true;
						break;
					}
				}
				if(fixSkeleton){
					for(int k=0; k<blob.skeleton.length; k++){
						double angle = 2*Math.PI*k/blob.skeleton.length + blob.cachedAngle;
						Circle skel = blob.skeleton[k];
						skel.py = (float)(blob.cacheCenter.py + blob.skeletonRadius*Math.sin(angle));
						skel.px = (float)(blob.cacheCenter.px + blob.skeletonRadius*Math.cos(angle));
						skel.sy = 0;
						skel.sx = 0;
					}
					for(int i=0; i<blob.corner.length; i++){
						Circle c = blob.corner[i];
						c.sy = 0;
						c.sx = 0;
					}
					System.out.println("At time "+Time.string(now)+" fixed skeleton in "+blob+" which should happen rarely");
				}
			}
		}
		
		float yx[] = new float[2];
		for(Circle c : allCircles){
			if(c.pushedByGravity){
				yx[0] = c.py;
				yx[1] = c.px;
				accelField.getAccelAt(yx);
				c.syAdd += yx[0]*secondsSinceLastCall;
				c.sxAdd += yx[1]*secondsSinceLastCall;
			}
		}
		
		
		//float frictionDecay = 2f;
		//float frictionDecay = .5f;
		//float frictionDecay = .3f;
		float frictionDecay = .2f;
		//float frictionDecay = .05f;
		float frictionMult = 1-frictionDecay*secondsSinceLastCall;
		for(Circle c : allCircles){ //decay speed. TODO friction instead, which subtracts?
			c.sy *= frictionMult;
			c.sx *= frictionMult;
		}
		
		
		for(Circle c : allCircles){ //move based on speed of each point
			c.syncSpeedAndMove(secondsSinceLastCall);
		}
		
		
		//TODO use quadtree or columns (with sorting within each) or some datastruct to
		//call handlePossibleCollision on far fewer pairs.
		for(Smartblob b : blobs){
			for(Pole p : b.pole){
				for(Circle c : allCircles){
					handlePossibleCollision(c,p);
				}
			}
		}
		for(Circle a : allCircles){
			for(Circle b : allCircles){
				handlePossibleCollision(a,b);
			}
		}
		
		//TODO collisions between pole and circle
		
		//Nevermind, but do write this in mindmap and copy relevant things wrote in phonedoc 2016-6.
		//TODO? each circle and each pole can only be in 1 collision per cycle? To avoid double-bounce?
		//Or would the conditional mirroring of that part of velocity only happen if not already done?
		//Yes, it would be consistent that way, so no need to do this. Already solved.
		
		for(Circle c : allCircles){ //move based on speed of each point
			c.syncSpeedAndMove(secondsSinceLastCall);
		}
	}
	
	public void handlePossibleCollision(Circle a, Circle b){
		if(ignoreCollisionBetween(a,b)) return;
		float dy = b.py-a.py;
		float dx = b.px-a.px;
		float distSq = dy*dy + dx*dx;
		if(distSq == 0) throw new RuntimeException("2 circles at exact same place");
		float radiusSum = a.pr+b.pr;
		if(distSq <= radiusSum*radiusSum){ //collision
			float dist = (float)Math.sqrt(distSq);
			float normDy = dy/dist;
			float normDx = dx/dist;
			float dsy = b.sy-a.sy;
			float dsx = b.sx-a.sx;
			float speedTowardEachother = dsy*normDy + dsx*normDx; //TODO is this backward?
			speedTowardEachother *= -1; //FIXME?
			if(0 < speedTowardEachother){ //if toward eachother, mirror that part of velocity
				//FIXME compute using momentum instead of just speed. Circle.pm is "position of mass"
				float addToSy = normDy*speedTowardEachother;
				float addToSx = normDx*speedTowardEachother;
				//float addToEachSy = normDy*speedTowardEachother/2;
				//float addToEachSx = normDx*speedTowardEachother/2;
				/*b.syAdd += addToEachSy;
				a.syAdd -= addToEachSy;
				b.sxAdd += addToEachSx;
				a.sxAdd -= addToEachSx;
				*/
				
				if(a.canBePushed && b.canBePushed){
					b.syAdd += addToSy/2;
					a.syAdd -= addToSy/2;
					b.sxAdd += addToSx/2;
					a.sxAdd -= addToSx/2;
				}else{
					if(a.canBePushed){
						a.syAdd -= addToSy;
						a.sxAdd -= addToSx;
					}else{
						b.syAdd += addToSy;
						b.sxAdd += addToSx;
					}
				}
				if(randomizeColorWhenCollide){
					if(a.canBePushed) a.setColor(Util.randColor());
					if(b.canBePushed) b.setColor(Util.randColor());
				}
			}
		}
	}
	
	
	/** Bounces symmetricly if moving toward eachother at nearest point on the line
	if extended out to infinity, but not if the nearest point is past the circle centers.
	*/
	public void handlePossibleCollision(Circle b, Pole p){
		if(1<2) return; //FIXME pole collisions are sticking, so turned them off here
		if(ignoreCollisionBetween(b,p)) return;
		p.refresh(lastTimeStartedPhysicsCycle);
		float dy = b.py-p.py();
		float dx = b.px-p.px();
		float distSq = dy*dy+dx*dx;
		float radiusSum = b.pr+p.pr();
		if(radiusSum*radiusSum < distSq) return; //fast check proves no collision
		
		float getYX[] = new float[2];
		getClosestPointToInfiniteLine(getYX, p.c.py, p.c.px, p.d.py, p.d.px, b.py, b.px);
		
		dy = getYX[0]-p.py();
		dx = getYX[1]-p.px();
		distSq = dy*dy + dx*dx; //between center of pole and getClosestPointToInfiniteLine
		float poleLenSq = p.c.distanceSq(p.d);
		float halfPoleLenSq = poleLenSq/4;
		if(halfPoleLenSq < distSq) return; //exact check, no collision
		
		float vectorY = b.py-getYX[0];
		float vectorX = b.px-getYX[1];
		float vectorLen = (float)Math.sqrt(vectorY*vectorY + vectorX*vectorX);

		/*
		//smartblob collisions must not allow any overlap move them to eachothers borders if past
		if(b.canBePushed){
			b.pyAdd += fractionOfPreventOverlap*vectorY;
			b.pxAdd += fractionOfPreventOverlap*vectorX;
		}*/
		
		float aDy = getYX[0]-p.c.py, aDx = getYX[1]-p.c.px; 
		float distanceA = (float)Math.sqrt(aDy*aDy + aDx*aDx);
		float bDy = getYX[0]-b.py, bDx = getYX[1]-b.px; 
		float distanceB = (float)Math.sqrt(bDy*bDy + bDx*bDx);
		//TODO These fractions may be slightly above 1 and the other negative, but usually normal fractions.
		//By distance, they reverse their behavior when it goes past either end of line,
		//but that only happens when border of smartblob has negative curve, unlike a circle.
		float distanceSum = distanceA+distanceB;
		float fractionLineEndA = distanceA/distanceSum;
		float fractionLineEndB = 1-fractionLineEndA;
		
		//Speed of pointOnLine is a weightedSum of speeds at the line's ends.
		//TODO this is a little inaccurate if the point on the line segment is past either end.
		float speedYOfPointOnLine = p.c.sy*fractionLineEndA + fractionLineEndB*p.d.sy;
		float speedXOfPointOnLine = p.c.sx*fractionLineEndA + fractionLineEndB*p.d.sx;
		
		float ddy = b.sy-speedYOfPointOnLine;
		float ddx = b.sx-speedXOfPointOnLine;
		
		float normVY = vectorY/vectorLen;
		float normVX = vectorX/vectorLen;
		//"If vector is in same direction as third corner, flip it.
		//Since we already know theres a collision, always flip here.
		normVY = -normVY;
		normVX = -normVX;
		//Now (normVY,normVX) is length 1 and point outward from smartblob.
		//Get the part of speed vector aligned with normVector, then flip it.
		//TODO should that speed be difference between the 2 smartblobs at that point,
		//or do them separately? Doing them separately, at least for now.
		
		//float speedDotNorm = point.speedY*normVY + point.speedX*normVX;
		float speedDotNorm = ddy*normVY + ddx*normVX;
		
		//If speedDotNorm is positive, the smartblob is moving away (if the line was at rest)
		if(speedDotNorm < 0){
			//Like the BounceOnSimpleWall code, use absVal.
			//partOfSpeedVec is the part aligned with normVec (TODO opposite?)
			float partOfSpeedVecY = normVY*speedDotNorm;
			float partOfSpeedVecX = normVX*speedDotNorm;
			float addToSpeedY = -2*partOfSpeedVecY;
			float addToSpeedX = -2*partOfSpeedVecX;
			
			
			/*float addToEachSpeedY = addToSpeedY/2;
			float addToEachSpeedX = addToSpeedX/2;
			//point.speedY -= 2*partOfSpeedVecY;
			//point.speedX -= 2*partOfSpeedVecX;
			
			b.sy += addToEachSpeedY;
			p.c.sy -= fractionLineEndA*addToEachSpeedY;
			p.d.sy -= fractionLineEndB*addToEachSpeedY;
			
			b.sx += addToEachSpeedX;
			p.c.sx -= fractionLineEndA*addToEachSpeedX;
			p.d.sx -= fractionLineEndB*addToEachSpeedX;
			*/
			
			//TODO equal and opposite force, between this and weightedSum between 2 ends of line (even if it hangs past ends, just do something like 1.1*endA - .1*endB
			//"TODO use fractionLineEndA and fractionLineEndB"
			//TODO
			
			p.setColor(Util.randColor()); //randomize color during collision
			if(b.canBePushed){ //each gets half the force
				float addToEachSpeedY = addToSpeedY/2;
				float addToEachSpeedX = addToSpeedX/2;
				b.sy += addToEachSpeedY;
				p.c.sy -= fractionLineEndA*addToEachSpeedY;
				p.d.sy -= fractionLineEndB*addToEachSpeedY;
				b.sx += addToEachSpeedX;
				p.c.sx -= fractionLineEndA*addToEachSpeedX;
				p.d.sx -= fractionLineEndB*addToEachSpeedX;
				b.setColor(Util.randColor()); //randomize color during collision
			}else{ //pole bounces on circle that wont absorb force, so pole gets it all
				p.c.sy -= fractionLineEndA*addToSpeedY;
				p.d.sy -= fractionLineEndB*addToSpeedY;
				p.c.sx -= fractionLineEndA*addToSpeedX;
				p.d.sx -= fractionLineEndB*addToSpeedX;
			}
		}
	}
	
	public boolean ignoreCollisionBetween(Circle c, Pole p){
		if(!c.collisionDetect) return true;
		Smartblob pBlob = p.c.blobOrNull;
		if(c.blobOrNull!=null && pBlob!=null && pBlob==c.blobOrNull) return true; //in same blob
		return false;
	}
	
	/** TODO this should include circles of near surface index in the same smartblob */
	public boolean ignoreCollisionBetween(Circle a, Circle b){
		boolean sameBlob = a.blobOrNull != null && b.blobOrNull != null && a.blobOrNull==b.blobOrNull;
		return a==b || !a.collisionDetect || !b.collisionDetect || sameBlob;
	}
	
	public static void getClosestPointToInfiniteLine(float getYX[], float y1, float x1, float y2, float x2, float y, float x){
		//http://www.java2s.com/Code/Java/2D-Graphics-GUI/Returnsclosestpointonsegmenttopoint.htm
		float xDelta = x2 - x1;
		float yDelta = y2 - y1;
		float u = ((x - x1) * xDelta + (y - y1) * yDelta) / (xDelta * xDelta + yDelta * yDelta);
		getYX[0] = y1 + u * yDelta;
		getYX[1] = x1 + u * xDelta;
	}

}