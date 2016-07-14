/** Ben F Rayfield offers Smartblob opensource GNU GPL 2+ */
package smartblob;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import humanaicore.common.Rand;
import occamserver.Occamserver;

public class Util{
	private Util(){}
	
	public static float centerDistance(GameOb a, GameOb b){
		float dy = b.py()-a.py();
		float dx = b.px()-a.px();
		return (float)Math.sqrt(dy*dy+dx*dx);
	}
	
	public static Color randColor(){
		return new Color(0xff000000 | Rand.strongRand.nextInt(0x01000000));
	}
	
	public static byte[] readFileRel(String relPath){
		//TODO merge duplicate code
		String r = relPath.startsWith("\\")||relPath.startsWith("/") ? relPath.substring(1) : relPath;
		File f = new File(r);
		if(f.exists()){ //avoid cache problems with using file url
			System.out.println("Reading file "+f);
			InputStream in = null;
			try{
				in = new FileInputStream(f);
				if(Integer.MAX_VALUE < f.length()) throw new RuntimeException("File too big: "+f);
				byte b[] = new byte[(int)f.length()];
				in.read(b);
				return b;
			}catch(IOException e){
				throw new RuntimeException(e);
			}finally{
				if(in != null) try{ in.close(); }catch(IOException e){}
			}
		}else{
			System.out.println("Reading Class.getResourceAsStream "+relPath);
			InputStream in = Util.class.getResourceAsStream(relPath);
			try{
				return Occamserver.readFully(in, 1<<28, 5, true);
			}catch(IOException e){
				throw new RuntimeException(e);
			}finally{
				if(in != null) try{ in.close(); }catch(IOException e){}
			}
		}
	}
}
