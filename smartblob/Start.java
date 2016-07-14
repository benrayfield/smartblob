/** Ben F Rayfield offers Smartblob opensource GNU GPL 2+ */
package smartblob;
import java.awt.Color;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import humanaicore.common.Time;
import humanaicore.realtimeschedulerTodoThreadpool.RealtimeScheduler;
import occamserver.MapFunc;
import occamserver.Occamserver;
import occamserver.WrapMapFuncInHttpBytesFunc;
import smartblob.ui.GamePanel;

public class Start{
	
	//FIXME TODO after stop editing it much: static final byte[] htmlBytes = readHtmlFile();
	static byte[] readHtmlFile(){
		return Util.readFileRel("/data/smartblob/WebcamSeesBendableLoopAsGameControllerAjaxToServer.html");
	}
	
	public static String urlSuffixFromFirstHttpLine(String line){
		return line.split("\\s+")[1];
	}
	
	//all doubles range -1 to 1
	public static double gameControllerY[] = new double[32];
	public static double gameControllerX[] = new double[32];
	static{
		for(int i=0; i<32; i++){
			double angle = 2*Math.PI*i/32;
			double r = .9;
			gameControllerY[i] = r*Math.sin(angle);
			gameControllerX[i] = r*Math.cos(angle);
		}
	}
	
	public static double[] eachFourHexDigitsToScalar(String hex){
		double d[] = new double[hex.length()/4];
		for(int i=0; i<d.length; i++){
			int uint16 = Integer.parseInt(hex.substring(i*4,(i+1)*4), 16);
			d[i] = ((double)uint16-(1<<15))/(1<<15);
		}
		return d;
	}
	
	/** use server.setFunc(MapFunc) to change its behaviors */
	public static final Occamserver server = new Occamserver(new MapFunc(){
		public Map call(Map in){
			String u = urlSuffixFromFirstHttpLine(WrapMapFuncInHttpBytesFunc.asString(in.get("firstLine")));
			Map out = new HashMap();
			out.put("firstLine", "HTTP/1.1 200 OK");
			if(u.startsWith("/player0Smartblob/")){
				u = u.substring("/player0Smartblob/".length());
				double d[] = eachFourHexDigitsToScalar(u);
				for(int i=0; i<32; i++){
					gameControllerY[i] = d[i*2];
					gameControllerX[i] = d[i*2+1];
				}
				out.put("Content-Type", "text/plain; charset=UTF-8");
				out.put("content", "Got your game controller data: "+u); //byte[] or String
			}else{
				out.put("firstLine", "HTTP/1.1 200 OK");
				out.put("Content-Type", "text/html; charset=UTF-8");
				//out.put("content", "Smartblob sever. Time: "+Time.time()); //byte[] or String
				//FIXME TODO out.put("content", htmlBytes);
				out.put("content", readHtmlFile()); //so can edit the file without restarting
			}
			return out;
		}
	});
	
	public static void main(String args[]){
		System.out.println("Smartblob with Webcam and Occamserver at http://localhost starting at time "+Time.string());
		World w = new World();
		float centerY=350, centerX=350;
		//Smartblob b = new Smartblob(new Circle(true, centerY, centerX, 80));
		Smartblob b = new Smartblob(new Circle(null, -1, false, false, false, centerY, centerX, 60, 0, 0));
		w.blobs.add(b);
		int borderCircles = 150;
		//int borderCircles = 50;
		for(int i=0; i<borderCircles; i++){
			double angle = 2*Math.PI*i/borderCircles;
			double r = 300;
			float y = (float)(centerY+r*Math.sin(angle));
			float x = (float)(centerX+r*Math.cos(angle));
			Circle c = new Circle(null, -1, true, false, false, y, x, 10, 0, 0);
			w.stuff.add(c);
			float bright = .7f-.3f*(float)Math.sin(angle);
			c.setColor(new Color(bright, bright, bright));
		}
		int midairCirclesFirstRing = 5;
		for(int i=0; i<midairCirclesFirstRing; i++){
			double angle = 2*Math.PI*i/midairCirclesFirstRing;
			double r = 170;
			float y = (float)(centerY+r*Math.sin(angle));
			float x = (float)(centerX+r*Math.cos(angle));
			Circle c = new Circle(null, -1, true, false, false, y, x, 30, 0, 0);
			w.stuff.add(c);
			float bright = .7f-.3f*(float)Math.sin(angle);
			c.setColor(new Color(bright, bright, bright));
		}
		w.updateListOfAllCircles();
		GamePanel p = new GamePanel(w, gameControllerY, gameControllerX);
		JFrame window = new JFrame("Smartblob Webcam (opensource GNU GPL 2+) Open Firefox to http://localhost for \"bendable loop game controller\"");
		window.setSize((int)(centerX*2), (int)(centerY*2));
		//window.setLocation(300, 50);
		window.setLocation(0, 50);
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.add(p);
		window.setVisible(true);
		RealtimeScheduler.start(p);
		new Thread(server).start();
		
		try{
			Desktop.getDesktop().browse(new URI("http://localhost"));
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

}
