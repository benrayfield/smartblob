/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.smartblob.pc;
import humanaicore.common.Rand;
import humanaicore.realtimeschedulerTodoThreadpool.RealtimeScheduler;
import humanainet.smartblob.core.brain.Brain;
import humanainet.smartblob.core.physics.SmartblobSim;
import humanainet.smartblob.core.trianglemesh.LayeredZigzag;
import humanainet.smartblob.core.trianglemesh.SmartblobTri;
import humanainet.smartblob.core.trianglemesh.radial.Radiblob;
import humanainet.smartblob.core.util.RadiblobUtil;
import humanainet.smartblob.core.util.Util;
import humanainet.smartblob.pc.ui.SmartblobsPanelAdvancedCurve;
import humanainet.ui.core.shapes.Rect;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class StartRadial{
	
	public static void main(String args[]) throws Exception{
		//humanaicore.jselfmodify.PluginLoader.loadFirstPlugins();
		JFrame w = new JFrame("Smartblob - opensource GNU GPL 2+ - unzip this jar file to get source code");
		w.setLocation(50,50);
		//Rectangle rect = new Rectangle(700,700);
		//Rectangle rect = new Rectangle(450,450);
		//Rectangle rect = new Rectangle(1000,800);
		Rect rect = new Rect(0,0,800,1000);
		w.setSize((int)(rect.width+.5f), (int)(rect.height+.5f));
		int dims = 2;
		//RadialSim sim = new RadialSim(dims, rect, Rand.strongRand);
		SmartblobSim sim = RadiblobUtil.newSimWithDefaultOptions();
		int firstCursorTeam = 0;
		SmartblobTri firstCursor = RadiblobUtil.newCursor(firstCursorTeam);
		sim.addCursor(firstCursor);
		//float minRadius = 30;
		//float minRadius = 15;
		//float minRadius = 25;
		float minRadius = 40;
		//float minRadius = 35;
		//float minRadius = 95;
		float maxEstimatedRadius = minRadius*2;
		float minCenterX = rect.x+maxEstimatedRadius;
		float maxCenterX = rect.x+rect.width-maxEstimatedRadius;
		float minCenterY = rect.y+maxEstimatedRadius;
		float maxCenterY = rect.y+rect.height-maxEstimatedRadius;
		Random rand = Rand.strongRand;
		//for(char c='a'; c<='z'; c++){
		boolean alternateTeam = false;
		/*
		for(char c='a'; c<'a'+12; c++){
		//for(char c='a'; c<'a'+1; c++){
		//for(char c='a'; c<='b'; c++){
			String ballName = ""+c;
			int team = alternateTeam ? 1 : 0; //2 opposite teams, is 1 way to play. opposite colors attract. same repels.
			alternateTeam = !alternateTeam;
			Popbol b = new Popbol(2,minRadius, team);
			b.position[0] = minCenterX+rand.nextFloat()*(maxCenterX-minCenterX);
			b.position[1] = minCenterY+rand.nextFloat()*(maxCenterY-minCenterY);
			b.speed[0] = .1;
			b.speed[1] = .1;
			b.frictionMult = .1;
			b.heat = .2;
			float red = rand.nextFloat();
			float green = rand.nextFloat();
			float blue = rand.nextFloat();
			//b.mainColor = new Color(red,green,blue);
			b.textColor = new Color(1-red,1-green,1-blue);
			synchronized(sim.balls){
				sim.balls.put(ballName, b);
				if(1 < sim.balls.size()){
					sim.moveBallToRandomMoreEmptyPlace(b, 0, CoreUtil.strongRand);
				}
			}
		}
		*/
		
		String nameOfPlayerBall = "p";
		
		addBall(200, 200, rand, rect, sim, 0, nameOfPlayerBall, 0, 0, 1);
		addBall(200, 500, rand, rect, sim, 1, "b", .6f, .4f, 0);
		addBall(500, 200, rand, rect, sim, 1, "c", .6f, .4f, 0);
		addBall(500, 500, rand, rect, sim, 1, "d", .6f, .4f, 0);
		/*addBall(rand, rect, sim, 1, "c", .7f, .3f, 0);
		addBall(rand, rect, sim, 1, "d", .8f, .2f, 0);
		addBall(rand, rect, sim, 1, "e", .9f, .1f, 0);
		addBall(rand, rect, sim, 1, "f", .9f, .1f, 0);
		addBall(rand, rect, sim, 1, "g", .9f, .1f, 0);
		*/
		
		//addBall(rand, rect, sim, 1, "h", .9f, .1f, 0);
		//addBall(rand, rect, sim, 1, "i", .9f, .1f, 0);
		
		//BlobPanel panel = new BlobPanel(sim);
		SmartblobsPanelAdvancedCurve panel = new SmartblobsPanelAdvancedCurve(sim);
		w.add(panel);
		w.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		w.setVisible(true);
		RealtimeScheduler.start(panel);
	}
	
	static void addBall(float y, float x, Random rand, Rect rect, SmartblobSim sim, int team, String ballName, float red, float green, float blue){
		//float minRadius = 40;
		float minRadius = 70;
		//float minRadius = 30;
		float maxRadius = minRadius*1.7f;
		float maxEstimatedRadius = minRadius*2;
		float minCenterX = rect.x+maxEstimatedRadius;
		float maxCenterX = rect.x+rect.width-maxEstimatedRadius;
		float minCenterY = rect.y+maxEstimatedRadius;
		float maxCenterY = rect.y+rect.height-maxEstimatedRadius;
		///int angles = 128;
		int angles = 64;
		//int angles = 8;
		//int angles = 16;
		//Blob b = new Blob(2, minRadius, team, angles);
		//int team = 0;
		boolean isIgnoreCollisions = false;
		Brain brain = null;
		int frequency = 5;
		Radiblob b = RadiblobUtil.wavegear(team, isIgnoreCollisions, brain, y, x, minRadius, maxRadius, angles, frequency);
		//b.position[0] = minCenterX+rand.nextFloat()*(maxCenterX-minCenterX);
		//b.position[1] = minCenterY+rand.nextFloat()*(maxCenterY-minCenterY);
		//b.speed[0] = .1;
		//b.speed[1] = .1;
		//b.frictionMult = .1;
		//b.frictionMult = .02;
		//b.heat = .95;
		float totalColor = red+green+blue;
		//b.color[0] = red/totalColor;
		//b.color[1] = green/totalColor;
		//b.color[2] = blue/totalColor;
		//b.textColor = Color.black;
		//sim.smartblobs.put(ballName, b);
		sim.smartblobs.add(b);
		/*TODO Why isnt moveBallToRandomMoreEmptyPlace code working? if(1 < sim.smartblobs.size()){
			//sim.moveBallToRandomMoreEmptyPlace(b, 0, Rand.strongRand);
			SmartblobTri blobs[] = sim.smartblobs.toArray(new SmartblobTri[0]); //only inefficient if do this many times
			Util.moveBallToRandomMoreEmptyPlace(rect, b, blobs, 0, Rand.strongRand);
		}*/
	}

}
