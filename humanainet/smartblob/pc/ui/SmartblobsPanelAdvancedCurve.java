/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.pc.ui;
import humanainet.smartblob.core.physics.ChangeSpeed;
import humanainet.smartblob.core.physics.GlobalChangeSpeed;
import humanainet.smartblob.core.physics.Muscle;
import humanainet.smartblob.core.physics.SmartblobSim;
import humanainet.smartblob.core.physics.changespeeds.Push;
import humanainet.smartblob.core.physics.globalchangespeeds.BounceOnSimpleWall;
import humanainet.smartblob.core.physics.globalchangespeeds.GravityAsGlobalChangeSpeed;
import humanainet.smartblob.core.physics.muscle.LineMuscle;
import humanainet.smartblob.core.trianglemesh.CornerName;
import humanainet.smartblob.core.trianglemesh.LayeredZigzag;
import humanainet.smartblob.core.trianglemesh.LineName;
import humanainet.smartblob.core.trianglemesh.MovCorner;
import humanainet.smartblob.core.trianglemesh.MovLine;
import humanainet.smartblob.core.trianglemesh.MovTri;
import humanainet.smartblob.core.trianglemesh.SmartblobTri;
import humanainet.smartblob.core.trianglemesh.centri.Centriblob;
import humanainet.smartblob.core.trianglemesh.radial.Radiblob;
import humanainet.smartblob.core.util.CurvblobUtil;
import humanainet.ui.core.ColorUtil;
import humanainet.ui.core.shapes.Rect;

//import humanainet.statsys.Statsys;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import humanaicore.common.CoreUtil;
import humanaicore.common.DebugString;
import humanaicore.common.Nanotimer;
import humanaicore.realtimeschedulerTodoThreadpool.RealtimeScheduler;
import humanaicore.realtimeschedulerTodoThreadpool.Task;
import humanaicore.realtimeschedulerTodoThreadpool.TimedEvent;

/** Many smartblobs can bounce and reshape and grab eachother as tools (in theory) on screen */
public class SmartblobsPanelAdvancedCurve extends JPanel implements MouseMotionListener, MouseListener, Task{
	
	/** Changes to sim.smartblobs must be synchronized. It is when painted. */
	public final SmartblobSim sim;
	
	public boolean paintPartsRandomly = true;
	
	protected final Nanotimer timer = new Nanotimer();
	
	protected double lastTimeMouseMoved = CoreUtil.time();
	
	public double simulateThisManySecondsAfterMouseMove = 120;
	//public double simulateThisManySecondsAfterMouseMove = 20;
	//public double simulateThisManySecondsAfterMouseMove = 2e50;
	
	//public int simCyclesPerDraw = 50;
	//This is the big victory of changing from curvesmartblob to radialsmartblob.
	//Physics is so stable it needs to be computed far less times per second.
	//public int simCyclesPerDraw = 1; //It does work at 1 with Centriblob, but I cant get it to bounce as high as it falls from
	//public int simCyclesPerDraw = 5;
	//public int simCyclesPerDraw = 10;
	public int simCyclesPerDraw = 20;
	//public int simCyclesPerDraw = 200;
	//public int simCyclesPerDraw = 30;
	//public int simCyclesPerDraw = 20;
	//public int simCyclesPerDraw = 50;
	
	public boolean drawBoundingRectangles = false;
	
	public boolean drawBoundingShapes = false;
	
	/** 2015-12 When cursor is a radialsmartblob, this causes the closest triangle to mouse
	to get really big when click. Why?
	Maybe its a thread error. It doesnt happen when this is false. Since I dont need it when using
	smartblob as cursor, I'll just turn it off while I'm developing radialsmartblobPhysics.
	*/
	public boolean drawOuterTriMouseIsClosestTo = false;
	
	public int mouseY, mouseX;
	
	public final boolean mouseButtonDown[] = new boolean[3];
	
	public float maxSecondsToSimAtOnce = .01f;
	
	public long frames;
	
	protected boolean ifDrawLinesThenAsBrightnessOfLineMuscle = false; //normal lines only as in drawNormalLayeredZigzagLines 
	protected boolean drawNormalLayeredZigzagLines = false;
	protected boolean drawExtraCentriblobLinesIfExist = false; //to see centri on screen
	
	protected boolean drawTestOfRadialZigzag = true;
	
	protected float mouseTestAccelX=1, mouseTestAccelY=1;
	
	protected final NavigableMap<String,DebugString> debugStrings = new TreeMap();
	
	protected boolean mouseClickAcceleratesBlob = true;
	
	//protected float testPointA[] = new float[2], testPointB[] = new float[2];
	
	/** Starts self as task. Includes an example smartblob. They can be changed later *
	public SmartblobsPanelAdvancedCurve(){
		this(CurvesmartblobUtil.newSimWithDefaultOptions(0));
		LayeredZigzag y = CurvesmartblobUtil.simpleSmartblobExample();
		synchronized(sim.smartblobs){
			sim.smartblobs.add(y);
		}
		LayeredZigzag z = CurvesmartblobUtil.simpleSmartblobExample();
		for(MovCorner cd : z.corners()){
			cd.x += 150;
			cd.speedX = 15;
		}
		synchronized(sim.smartblobs){
			sim.smartblobs.add(z);
		}
		
		LayeredZigzag x = CurvesmartblobUtil.simpleSmartblobExample();
		for(MovCorner cd : x.corners()){
			cd.x += 300;
			cd.speedX = 30;
		}
		synchronized(sim.smartblobs){
			sim.smartblobs.add(x);
		}
		
		//addDebugStrings();
	}*/
	
	protected SmartblobTri anyNoncursorSmartblob(){
		synchronized(sim.smartblobs){
			for(SmartblobTri b : sim.smartblobs){
				if(!b.isIgnorePhysics()) return b;
			}
			return null;
		}
	}
	
	protected void addDebugStrings(){
		synchronized(debugStrings){
			/*debugStrings.put("firstblob y x", new DebugString(){
				public String toString(){
					Radiblob r = (Radiblob) anyNoncursorSmartblob();
					return r.blobY+" "+r.blobX;
				}
			});
			debugStrings.put("firstblob speeds y x", new DebugString(){
				public String toString(){
					Radiblob r = (Radiblob) anyNoncursorSmartblob();
					return r.blobYSpeed+" "+r.blobXSpeed;
				}
			});*/
			debugStrings.put("frames", new DebugString(){
				long frames;
				public String toString(){
					return ""+(++frames);
				}
			});
			debugStrings.put("mouseTestAccelY and X", new DebugString(){
				public String toString(){
					return mouseTestAccelY+" "+mouseTestAccelX;
				}
			});
			/*debugStrings.put("maxX", new DebugString(){
				public String toString(){
					float max = -Float.MAX_VALUE;
					synchronized(sim.smartblobs){
						for(SmartblobTri b : sim.smartblobs){
							max = Math.max(max, ((Radiblob)b).maxX());
						}
					}
					return ""+max;
				}
			});*/
		}
	}
	
	/** Starts self as Task. */
	public SmartblobsPanelAdvancedCurve(SmartblobSim sim){
		this.sim = sim;
		setBackground(Color.black);
		addMouseMotionListener(this);
		addMouseListener(this);
		RealtimeScheduler.start(this);
		addDebugStrings();
	}
	
	//protected LayeredZigzag testBlob = new LayeredZigzag(null, 5, 16, 100, 100, 90);
	//protected LayeredZigzag testBlob = new LayeredZigzag(null, 9, 64, 100, 100, 90);
	//protected LayeredZigzag testBlob = new LayeredZigzag(null, 7, 32, 100, 100, 90);
	
	int waitTestCount;
	
	public void paint(Graphics g){
		if((waitTestCount++ % 8) == 0) testCount++;
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		
		/*if(testPointA[0] == 0){
			testPointA[0] = getHeight()*CoreUtil.strongRand.nextFloat();
			testPointA[1] = getWidth()*CoreUtil.strongRand.nextFloat();
			testPointB[0] = getHeight()*CoreUtil.strongRand.nextFloat();
			testPointB[1] = getWidth()*CoreUtil.strongRand.nextFloat();
		}*/

		//int w = getWidth(), h = getHeight();
		//testBlob = new LayeredZigzag(null, 5, 16, h/2, w/2, Math.min(w,h)/2);
		//testBlob = new LayeredZigzag(null, 9, 16, h/2, w/2, Math.min(w,h)/2);
		//testBlob = new LayeredZigzag(null, 7, 32, h/2, w/2, Math.min(w,h)/2);
		
		SmartblobTri blobsArray[];
		synchronized(sim.smartblobs){
			blobsArray = sim.smartblobs.toArray(new SmartblobTri[0]);
		}
		LayeredZigzag firstNoncursorBlob = null;
		for(SmartblobTri blob : blobsArray){
			draw(g, blob);
			if(!blob.isIgnorePhysics()) firstNoncursorBlob = (LayeredZigzag)blob;
		}
		
		/*
		//test nearest point on line math
		float getYX[] = new float[2];
		SmartblobUtil.getClosestPointToInfiniteLine(
			getYX, testPointA[0], testPointA[1], testPointB[0], testPointB[1], mouseY, mouseX);
		g.setColor(Color.pink);
		g.drawLine((int)testPointA[1], (int)testPointA[0], (int)testPointB[1], (int)testPointB[0]);
		g.fillRect((int)getYX[1]-5, (int)getYX[0]-5, 10, 10);
		*/
		
		g.setColor(Color.white);
		float lengthMult = 290;
		g.drawLine(
			mouseX, mouseY,
			(int)(mouseX+mouseTestAccelX*lengthMult+.5), (int)(mouseY+mouseTestAccelY*lengthMult+.5));
		
		if(blobsArray.length > 0){
			//Radialsmartblob blob = (Radialsmartblob) blobsArray[1];
			if(firstNoncursorBlob instanceof Radiblob){
				Radiblob blob = (Radiblob)firstNoncursorBlob;
				g.setColor(Color.orange);
				//lengthMult *= 70;
				g.drawLine(
					mouseX, mouseY,
					(int)(mouseX+blob.displayVectorX*lengthMult+.5), (int)(mouseY+blob.displayVectorY*lengthMult+.5));
				//System.out.println("blob.displayVectorY = "+blob.displayVectorY+" X "+blob.displayVectorX);
			}
		}
		
		g.setColor(Color.white);
		//g.drawString("frames: "+frames, 20, 20);
		int i=0;
		synchronized(debugStrings){
			for(Map.Entry<String,DebugString> entry : debugStrings.entrySet()){
				String text = entry.getKey()+": "+entry.getValue();
				g.drawString(text, 20, 20+30*i);
				i++;
			}
		}
		frames++;
	}
	
	public static void drawLineWithCurrentSettings(Graphics g, MovCorner a, MovCorner b){
		g.drawLine((int)a.x, (int)a.y, (int)b.x, (int)b.y);
	}
	
	public void draw(Graphics g, SmartblobTri smartblob){
		boolean drawShape = drawBoundingShapes || !(smartblob instanceof LayeredZigzag);
		Shape s = null;
		Polygon p = null;
		if(drawShape){
			throw new RuntimeException("TODO");
			/*s = smartblob.shape();
			if(s instanceof Polygon){ //TODO what to draw here
				p = (Polygon) s;
			}else{
				throw new RuntimeException("TODO use pathiterator of Shape for "+s);
			}
			*/
		}
		if(smartblob instanceof LayeredZigzag){
			draw(g, (LayeredZigzag)smartblob);
		}else{
			g.drawPolygon(p);
		}
		if(drawBoundingShapes){
			g.setColor(new Color(.8f,0,.8f));
			g.drawPolygon(p);
		}
		if(drawBoundingRectangles){
			g.setColor(Color.red);
			Rect r = smartblob.boundingRectangle();
			//If rectangle hangs off positive y (bottom) of the panel,
			//panel enlarges and it continues appearing to fall.
			int h = getHeight(), w = getWidth();
			int startY = Math.max(0, (int)r.y);
			int startX = Math.max(0, (int)r.x);
			int endY = Math.min((int)(r.y+r.height-1), h-1); //inclusive
			int endX = Math.min((int)(r.x+r.width-1), w-1);
			g.drawRect(startX, startY, endX-startX+1, endY-startY+1);
			//System.out.println("w="+w+" h="+h+" startY="+startY+" endY="+endY+" r="+r);
			//System.out.println("blob="+smartblob);
		}
		if(drawTestOfRadialZigzag) drawTestOfRadialZigzag(g, smartblob);
	}
	
	public void draw(Graphics g, LayeredZigzag smartblob){
		int triX[] = new int[3], triY[] = new int[3]; //filled in from corners float positions
		//Color defaultColor = new Color(.9f, .9f, .9f);
		for(int layer=1; layer<smartblob.layers; layer++){
			for(int p=0; p<smartblob.layerSize; p++){
				/*Shape triangle = testBlob.triangleShape(layer, p, true);
				if(triangle instanceof Polygon){
					g.fillPolygon((Polygon)triangle);
				}
				*/
				MovTri t = smartblob.trianglesInward[layer][p];
				for(int c=0; c<3; c++){
					MovCorner cd = t.adjacentCorners.get(c);
					triY[c] = (int) cd.y;
					triX[c] = (int) cd.x;
				}
				//g.setColor(t.colorOrNull==null ? defaultColor : t.colorOrNull);
				//java's color bits match ARGB where A is highest 8 bits and B is lowest.
				g.setColor(new Color(t.colorARGB));
				g.fillPolygon(triX, triY, 3);
			}
		}
		//defaultColor = new Color(0,0,1f);
		for(int layer=0; layer<smartblob.layers-1; layer++){
			for(int p=0; p<smartblob.layerSize; p++){
				/*Shape triangle = testBlob.triangleShape(layer, p, false);
				if(triangle instanceof Polygon){
					g.fillPolygon((Polygon)triangle);
				}*/
				MovTri t = smartblob.trianglesOutward[layer][p];
				for(int c=0; c<3; c++){
					MovCorner cd = t.adjacentCorners.get(c);
					triY[c] = (int) cd.y;
					triX[c] = (int) cd.x;
				}
				//g.setColor(t.colorOrNull==null ? defaultColor : t.colorOrNull);
				g.setColor(new Color(t.colorARGB));
				g.fillPolygon(triX, triY, 3);
			}
		}
		
		if(drawOuterTriMouseIsClosestTo){
			MovTri t = smartblob.findCollision(mouseY, mouseX);
			if(t != null){
				for(int c=0; c<3; c++){
					MovCorner cd = t.adjacentCorners.get(c);
					triY[c] = (int) cd.y;
					triX[c] = (int) cd.x;
				}
				//g.setColor(Color.red);
				//g.fillPolygon(triX, triY, 3);
				
				float getYX[] = new float[2];
				CurvblobUtil.getClosestPointToInfiniteLine(getYX, t, mouseY, mouseX);
				g.setColor(Color.orange);
				g.fillRect((int)getYX[1]-3, (int)getYX[0]-3, 7, 7);
				/*TODO if(mouseButtonDown[0] || mouseButtonDown[2]){
					CornerData cd = t.adjacentCorners[2];
					t.smartblob.onStartUpdateSpeeds();
					float secondsSinceLastDraw = .02;
					cd.speedY -= 10*secondsSinceLastDraw; //TODO do this in nextState
					t.smartblob.onEndUpdateSpeeds();
				}*/
			}
		}
		

		if(g instanceof Graphics2D){
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(1.5f));
			//g2.setStroke(new BasicStroke(2.5f));
		}
		boolean drawAnyLines = drawNormalLayeredZigzagLines || drawExtraCentriblobLinesIfExist;
		if(drawAnyLines){
			if(ifDrawLinesThenAsBrightnessOfLineMuscle){
				//TODO what if some lines dont have muscles? They wont be drawn as anything.
				for(Muscle m : smartblob.mutableMuscles()){
					if(m instanceof LineMuscle){
						draw(g, (LineMuscle)m);
					}
				}
			}else{ //all same color and ignore muscles
				LineName lines[] = smartblob.allLines();
				//if(!smartblob.isIgnorePhysics()) System.out.println("lines.length="+lines.length);
				for(LineName line : lines){
					//If line has any centri corner, its layer is -1
					boolean bothCentri = line.cornerLow.layer == -1 && line.cornerHigh.layer == -1;
					boolean eitherCentri = line.cornerLow.layer == -1 || line.cornerHigh.layer == -1;
					MovCorner a = line.cornerLow.layer == -1
						? ((Centriblob)smartblob).centriCorners.get(line.cornerLow.point)
						: smartblob.corners[line.cornerLow.layer][line.cornerLow.point];
					MovCorner b = line.cornerHigh.layer == -1
						? ((Centriblob)smartblob).centriCorners.get(line.cornerHigh.point)
						: smartblob.corners[line.cornerHigh.layer][line.cornerHigh.point];
					if(bothCentri){
						if(drawExtraCentriblobLinesIfExist){
							g.setColor(Color.red);
							drawLineWithCurrentSettings(g, a, b);
						}
					}else if(eitherCentri){
						if(drawExtraCentriblobLinesIfExist){
							g.setColor(Color.blue);
							drawLineWithCurrentSettings(g, a, b);
						}
					}else{ //normal layeredZigzag lines
						if(drawNormalLayeredZigzagLines){
							g.setColor(Color.green);
							drawLineWithCurrentSettings(g, a, b);
						}
					}
				}
			}
		}
		
		//already done in drawLayeredZigzagLines
		//if(drawExtraCentriblobLinesIfExist && smartblob instanceof Centriblob){
		//}
		
		if(g instanceof Graphics2D){
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(3f));
		}
		if(drawBoundingShapes){
			g.setColor(Color.magenta);
			/*Shape s = smartblob.shape();
			if(s instanceof Polygon){
				g.drawPolygon((Polygon)s);
			}else{
				System.out.println("Unknown shape type: "+s.getClass().getName());
			}*/
			throw new RuntimeException("TODO smartblob tells its triangles. use those on the outer ring");
		}
		/*TODO when hook in CornerData pointers in LineData for(LineData lineData : testBlob.allLineDatas()){
			drawLineInCurrentColor(g, lineData.adjacentCorners[0], lineData.adjacentCorners[1]);
		}*/
	}
	
	static int testCount;

	protected static void drawTestOfRadialZigzag(Graphics g, SmartblobTri blob){
		int doubleAngles[] = new int[]{0, 3};
		//int doubleAngles[] = new int[]{5};
		for(int highLayer=1; highLayer<blob.layers(); highLayer++){
			for(int d=0; d<doubleAngles.length; d++){
				int doubleAngle = (doubleAngles[d]+testCount)%(blob.layerSize()*2);
				MovLine m = blob.zigzag(highLayer, doubleAngle);
				byte sign = blob.sign(highLayer, doubleAngle);
				g.setColor(sign==1 ? Color.red : Color.orange);
				drawLineWithCurrentSettings(g, m.adjacentCorners.get(0), m.adjacentCorners.get(1));
			}
		}
	}
	
	protected void draw(Graphics g, LineMuscle m){
		float bright = m.read();
		//g.setColor(new Color(bright, bright, bright));
		//g.setColor(new Color(0, bright, 0));
		float halfBright = bright/2;
		g.setColor(new Color(halfBright, bright, halfBright));
		MovCorner a = m.line.adjacentCorners.get(0);
		MovCorner b = m.line.adjacentCorners.get(1);
		g.drawLine((int)a.x, (int)a.y, (int)b.x, (int)b.y);
	}

	public void mouseMoved(MouseEvent e){
		lastTimeMouseMoved = CoreUtil.time();
		float dy = mouseY-e.getY();
		float dx = mouseX-e.getX();
		float dist = (float)Math.sqrt(dy*dy + dx*dx);
		float decay = .15f;
		if(dist != 0){
			mouseTestAccelY = mouseTestAccelY*(1-decay) + decay*(dy/dist);
			mouseTestAccelX = mouseTestAccelX*(1-decay) + decay*(dx/dist);
		}
		mouseY = e.getY();
		mouseX = e.getX();
		SmartblobTri cursors[] = sim.cursors();
		if(cursors.length != 0){
			cursors[0].setCenterOfGravityYX(mouseY, mouseX);
		}
	}
	
	protected void setCursorScalar(float f){
		SmartblobTri cursors[] = sim.cursors();
		if(cursors.length != 0){
			LayeredZigzag firstCursor = (LayeredZigzag) cursors[0]; //FIXME nor reliable in a Set if theres more than 1
			firstCursor.setCursorScalar(f);
		}
	}
	
	public void mouseDragged(MouseEvent e){
		mouseMoved(e);
	}
	
	protected double lastTime = CoreUtil.time();

	public void event(Object context){
		if(!(context instanceof TimedEvent)) return;
		//System.out.println("event time: "+((TimedEvent)context).time);
		//double now = CoreUtil.time();
		double now = ((TimedEvent)context).time; //not exactly CoreUtil.time() since many cycles in a loop could be incremented a little, while actually nearly the same physical time.
		double secondsSinceLastCall = now-lastTime;
		lastTime = now;
		double sinceMouseMove = now-lastTimeMouseMoved;
		if(sinceMouseMove <= simulateThisManySecondsAfterMouseMove){
			//double secondsSinceLast = timer.secondsSinceLastCall();
			int h = getHeight(), w = getWidth();
			//System.out.println("Width and height of smartblobspanel are w="+w+" h="+h);
			if(h == 0 || w == 0) return;
			for(GlobalChangeSpeed p : sim.physicsParts){
				if(p instanceof BounceOnSimpleWall){
					BounceOnSimpleWall b = (BounceOnSimpleWall) p;
					//the left and top sides of screen stay at 0
					if(b.maxInsteadOfMin){
						float newVal = b.verticalInsteadOfHorizontal ? h : w;
						if(b.verticalInsteadOfHorizontal){
							System.err.println("Setting bottom wall to "+newVal);
						}else{
							System.err.println("Setting right wall to "+newVal);
						}
						b.wallPosition = newVal;
					}
				}
			}
			long cyc = 0;
			
			
			
			
			
			
			
			
			
			
			float sec = Math.min(maxSecondsToSimAtOnce*simCyclesPerDraw,(float)secondsSinceLastCall);
			//TODO!!! FIXME float sec = Math.min(maxSecondsToSimAtOnce,(float)secondsSinceLast);
			//float sec = 1f/40; //trying constant update time to see if it improves stability of smartblob bouncing vs sticking together
			//float sec = 1f/25; //trying constant update time to see if it improves stability of smartblob bouncing vs sticking together
			//float sec = Math.min(maxSecondsToSimAtOnce,(float)secondsSinceLast);
			//float sec = ((TimedEvent)context).;
			
			SmartblobTri blobsArray[];
			synchronized(sim.smartblobs){
				blobsArray = sim.smartblobs.toArray(new SmartblobTri[0]);
			}
			for(SmartblobTri blob : blobsArray){
				if(!blob.isIgnorePhysics() && blob instanceof Centriblob){
					float mouseYFraction = Math.max(0, Math.min((float)mouseY/getHeight(), 1));
					float mouseXFraction = Math.max(0, Math.min((float)mouseX/getWidth(), 1));
					Centriblob b = (Centriblob) blob;
					for(int i=0; i<b.layerSize(); i++){
						float fraction = (float)i/b.layerSize();
						//float radius = (float)(mouseXFraction*Math.sin(fraction*2*Math.PI*7) + mouseYFraction*Math.sin(fraction*2*Math.PI*3));
						
						/*float wave = (float)(1+.7*Math.sin(fraction*2*Math.PI*5));
						//float wave = 1;
						float radius = 60+60*mouseYFraction*wave;
						//90*(1+.3f*(float)Math.sin(fraction*2*Math.PI*7)
						b.setTargetRadius(i, radius);
						*/
						
						//float waveFractionY = mouseYFraction*(.5f+.5f*(float)Math.sin(fraction*2*Math.PI*2));
						//float waveFractionX = mouseXFraction*(.5f+.5f*(float)Math.sin(Math.PI/2+fraction*2*Math.PI*2));
						//float waveFraction = (waveFractionY+waveFractionX)/2;
						float xAngleChoice = mouseXFraction*20;
						//float waveFraction = mouseYFraction*(.5f+.5f*(float)Math.sin(xAngleChoice+fraction*2*Math.PI*2));
						float waveFraction = (.5f+.5f*(float)Math.sin(xAngleChoice+fraction*2*Math.PI*2));
						//float waveFraction = waveFractionY;
						//float decay = Math.min((float)secondsSinceLastCall*5f,1);
						float decay = 1; //instant change
						b.mutableMuscles().get(i).pushToward(waveFraction, decay);
					}
					break;
				}
			}
			
			
			if(drawOuterTriMouseIsClosestTo){
				if(mouseButtonDown[0]){
					
					for(SmartblobTri blob : blobsArray){
						if(blob instanceof LayeredZigzag){
							LayeredZigzag z = (LayeredZigzag) blob;
							MovTri t = z.findCollision(mouseY, mouseX);
							if(t != null){
								//t.colorOrNull = Color.red;
								
								Iterator<ChangeSpeed> iter = z.mutablePhysics().iterator();
								while(iter.hasNext()){
									ChangeSpeed cs = iter.next();
									if(cs instanceof Push) iter.remove();
								}
								MovCorner c = t.adjacentCorners.get(2);
								ChangeSpeed p = new Push(c, -5000, 0);
								z.mutablePhysics().add(p);
							}
						}
					}
				}
			}
			
			
			
			//float accelMult = 150;
			float accelMult = 1400;
			//float accelMult = 14;
			for(int cycle=0; cycle<simCyclesPerDraw; cycle++){
				float seconds = sec/simCyclesPerDraw;
				if(mouseButtonDown[0]){
					SmartblobTri s = sim.noncursorSmartblobAtYXOrNull(mouseY, mouseX);
					if(s != null){
						//test force at point in direction. hardbody physics research toward
						//radialsmartblobPhysics equalAndOpposite force at angles of surface hitting eachother
						float fromY = mouseY;
						float fromX = mouseX;
						float addToSpeedY = mouseTestAccelY*sec*accelMult;
						float addToSpeedX = mouseTestAccelX*sec*accelMult;
						//FIXME "LayeredZigzag's blobX and blobY vars are still 0, so noncursorSmartblobAtYXOrNull is giving wrong answer"
						//System.err.println("ui fromYXPointAccelerateYX "+addToSpeedY+" "+addToSpeedX);
						if(mouseClickAcceleratesBlob) s.fromYXPointAccelerateYX(fromY, fromX, addToSpeedY, addToSpeedX);
					}
				}
				sim.nextState(seconds);
				cyc++;
			}
			//System.out.println("cyc this time "+cyc);
			repaint();
		}
	}
	
	public double preferredInterval(){
		return .01;
	}

	public void mouseClicked(MouseEvent e){}

	public void mousePressed(MouseEvent e){
		
		SmartblobTri blobsArray[];
		synchronized(sim.smartblobs){
			blobsArray = sim.smartblobs.toArray(new SmartblobTri[0]);
		}
		
		switch(e.getButton()){
		case MouseEvent.BUTTON1:
			mouseButtonDown[0] = true;
			setCursorScalar(1f);

			for(SmartblobTri blob : blobsArray){
				for(MovCorner c : blob.allMovCorners()){
					c.speedY = 0;
					c.speedX = 0;
				}
			}
			
		break; case MouseEvent.BUTTON2:
			mouseButtonDown[1] = true;
		break; case MouseEvent.BUTTON3:
			mouseButtonDown[2] = true;
	
			//float mouseXFraction = (float)mouseX/getWidth();
			//float newGravity = (2*mouseXFraction-1)*3000;
			for(GlobalChangeSpeed g : sim.physicsParts){
				if(g instanceof GravityAsGlobalChangeSpeed){
					//((Gravity)g).acceleration = newGravity;
					((GravityAsGlobalChangeSpeed)g).acceleration *= -1;
				}
			}
			//FIXME TODO same for Gravity the ChangeSpeed in each SmartblobTri
			
		break;
		}
	}

	public void mouseReleased(MouseEvent e){
		switch(e.getButton()){
		case MouseEvent.BUTTON1:
			mouseButtonDown[0] = false;
			setCursorScalar(0f);
		break; case MouseEvent.BUTTON2:
			mouseButtonDown[1] = false;
		break; case MouseEvent.BUTTON3:
			mouseButtonDown[2] = false;
		break;
		}
	}

	public void mouseEntered(MouseEvent e){}

	public void mouseExited(MouseEvent e){}

}