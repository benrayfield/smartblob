/** Ben F Rayfield offers Smartblob opensource GNU GPL 2+ */
package smartblob.ui;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import smartblob.Circle;
import smartblob.Pole;
import smartblob.Smartblob;
import smartblob.World;

import javax.swing.JPanel;

import humanaicore.common.Time;
import humanaicore.realtimeschedulerTodoThreadpool.Task;
import humanaicore.realtimeschedulerTodoThreadpool.TimedEvent;

public class GamePanel extends JPanel implements MouseListener, MouseMotionListener, Task{
	
	public final World world;
	
	protected Circle dragging;
	
	protected final double gameControllerY[], gameControllerX[];
	
	protected Aftra aftra = new Aftra(2);
	
	protected float magnify = .8f;
	
	/** backed arrays of 32 x and y points all in range -1 to 1,
	from bendable-loop game controller seen by webcam, for example.
	*/
	public GamePanel(World w, double gameControllerY[], double gameControllerX[]){
		world = w;
		this.gameControllerY = gameControllerY;
		this.gameControllerX = gameControllerX;
		setBackground(Color.black);
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	public Smartblob playerBlob(){
		return world.blobs.get(0); //FIXME if theres multiple blobs and multiple players
	}
	
	protected void readGameControllerAndWriteTargetDistances(double decay){
		Smartblob b = playerBlob();
		if(b.size != gameControllerY.length) throw new RuntimeException("diff sizes");
		b.refresh(Time.time());
		for(int i=0; i<gameControllerY.length; i++){
			//FIXME should this negation be here or in the js or remove a double-negation?
			//Probably should remove a double-negation I put in somewhere to flip y
			//since I didnt like it defining positive y as down
			b.decayTowardTargetPositionRelToAngle(i, gameControllerY[i], gameControllerX[i], decay);
		}
	}
	
	public static void updateAftra2dViewFrom(Aftra a, Smartblob blob, Rectangle screen, float magnify){
		blob.refresh(Time.time());
		a.centerIn[0] = blob.cacheCenter.py;
		a.centerIn[1] = blob.cacheCenter.px;
		a.centerOut[0] = screen.y+screen.height*.5f;
		a.centerOut[1] = screen.x+screen.width*.5f;
		float yAxis[] = a.axis[0];
		float xAxis[] = a.axis[1];
		double ang = blob.cachedAngle+Math.PI/2;
		yAxis[0] = (float)Math.sin(ang)*magnify;
		yAxis[1] = (float)Math.cos(ang)*magnify;
		//perpendicular
		//TODO mirror this?
		xAxis[0] = yAxis[1];
		xAxis[1] = -yAxis[0];
		
	}
	
	public void paint(Graphics g){
		//TODO only paint within scrolled visible section. What was that function in Graphics?
		updateAftra2dViewFrom(aftra, playerBlob(), getVisibleRect(), magnify);
		double now = Time.time();
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		for(Circle c : world.stuff){
			paint(g, aftra, c);
		}
		for(Smartblob b : world.blobs){
			b.refresh(now);
			paint(g, aftra, b);
			
			/*g.setColor(Color.green);
			for(int k=0; k<b.skeleton.length; k++){
				Circle skel = b.skeleton[k];
				paint(g, skel);
			}*/
			
			/*g.setColor(Color.blue);
			int fromY = (int)(b.cacheCenter.py);
			int fromX = (int)(b.cacheCenter.px);
			float r = 200;
			//double ang = b.cachedAngle+Math.PI/2;
			double ang = b.cachedAngle;
			int toY = (int)(b.cacheCenter.py + 200*Math.sin(ang));
			int toX = (int)(b.cacheCenter.px + 200*Math.cos(ang));
			g.drawLine(fromX, fromY, toX, toY);
			*/
		}
	}
	
	protected void paint(Graphics g, Aftra a, Circle c){
		g.setColor(c.color());
		int centerY = (int)(a.forward1DimYX(0,c.py,c.px)+.5f);
		int centerX = (int)(a.forward1DimYX(1,c.py,c.px)+.5f);
		int r = (int)(c.pr*a.maxAxisLen()+.5f);
		g.fillOval(centerX-r, centerY-r, 2*r, 2*r);
	}
	
	/** paints only the rotated rectangle, as 2 triangles, not the circles its between */
	protected void paint(Graphics g, Aftra a, Pole p){
		g.setColor(p.color());
		//find 2 tangent lines
		float pcPy = a.forward1DimYX(0,p.c.py,p.c.px);
		float pcPx = a.forward1DimYX(1,p.c.py,p.c.px);
		float pdPy = a.forward1DimYX(0,p.d.py,p.d.px);
		float pdPx = a.forward1DimYX(1,p.d.py,p.d.px);
		float dy = pdPy-pcPy;
		float dx = pdPx-pcPx;
		float r = Math.min(p.d.pr, p.c.pr);
		r *= a.maxAxisLen(); //TODO optimize by caching if paint many objects
		float normVecY = dx, normVecX = -dy;
		float temp = (float)Math.sqrt(normVecY*normVecY+normVecX*normVecX);
		if(temp == 0) return; //draw length 0
		normVecY /= temp;
		normVecX /= temp;
		float addX = normVecX*r;
		float addY = normVecY*r;
		//d0 d d1
		//c0 c c1
		int c0x = (int)(pcPx-addX);
		int d0x = (int)(pdPx-addX);
		int c1x = (int)(pcPx+addX);
		int d1x = (int)(pdPx+addX);
		int c0y = (int)(pcPy-addY);
		int d0y = (int)(pdPy-addY);
		int c1y = (int)(pcPy+addY);
		int d1y = (int)(pdPy+addY);
		g.fillPolygon(new int[]{c0x,d0x,c1x}, new int[]{c0y,d0y,c1y}, 3);
		g.fillPolygon(new int[]{d0x,c1x,d1x}, new int[]{d0y,c1y,d1y}, 3);
	}
	
	protected void paint(Graphics g, Aftra a, Smartblob b){
		//Color firstColor = g.getColor();
		final int siz = b.size;
		/*int y[] = new int[siz], x[] = new int[siz];
		for(int i=0; i<siz; i++){
			Circle corner = b.corner[i];
			x[i] = (int)(corner.px+.5f);
			y[i] = (int)(corner.py+.5f);
		}*/
		//g.setColor(Color.blue);
		//FIXME TODO g.fillPolygon(x, y, siz);
		//g.setColor(firstColor);
		for(int i=0; i<siz; i++){
			Circle corner = b.corner[i];
			paint(g, a, corner);
			paint(g, a, b.pole[i]); //between corner[i] and corner[(i+1)%size]
		}
	}

	public void mouseClicked(MouseEvent e){
		world.accelField = world.accelField.mult(-1);
	}

	public void mousePressed(MouseEvent e){
		int mouseX = e.getX(), mouseY = e.getY();
		float minDistSq = Float.MAX_VALUE;
		for(Smartblob b : world.blobs){
			for(Circle c : b.corner){
				float dx = mouseX-c.px, dy = mouseY-c.py;
				float distSq = dx*dx + dy*dy;
				if(distSq < minDistSq){
					dragging = c;
					minDistSq = distSq;
				}
			}
		}
	}

	public void mouseReleased(MouseEvent e){
		dragging = null;
	}

	public void mouseEntered(MouseEvent e){}

	public void mouseExited(MouseEvent e){}

	public void mouseDragged(MouseEvent e){
		mouseMoved(e);
	}
	
	protected double timeLastSchedEvent = Time.time();
	protected double timeLastMouseMove = timeLastSchedEvent;

	public void mouseMoved(MouseEvent e){
		timeLastMouseMove = Time.time();
		if(dragging != null){
			dragging.px = e.getX();
			dragging.py = e.getY();
			repaint();
		}
	}

	public void event(Object context){
		if(context instanceof TimedEvent){
			TimedEvent t = (TimedEvent)context;
			//May correctly slightly differ from Time.time() to simulate multiple physics cycles
			//in a loop between waiting a small fraction of a second.
			//Each of those inner cycles takes an equal fraction of the time of an outer cycle.
			//So dont use Time.time() here.
			double now = t.time;
			double secondsSinceMouseMove = now-timeLastMouseMove;
			if(secondsSinceMouseMove < 5*60){ //pause game after 10 seconds no mouse move
				double secondsSinceLastCall = now-timeLastSchedEvent;
				//world.nextState((float)secondsSinceLastCall);
				int repeat=10;
				//int repeat=30;
				//int repeat=5;
				for(int r=0; r<repeat; r++){
					world.nextState((float)secondsSinceLastCall/repeat);
				}
				timeLastSchedEvent = now;
				//FIXME this causes lag so use bigger number, but that makes it shake more
				//double decay = secondsSinceLastCall*5;
				double decay = secondsSinceLastCall*15;
				readGameControllerAndWriteTargetDistances(decay);
				//readGameControllerAndWriteTargetDistances(1); //no extra lag
				repaint();
			}
		}
	}

	public double preferredInterval(){ return .02; }

}