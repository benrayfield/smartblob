package humanainet.smartblob.pc;
import humanainet.smartblob.pc.ui.SmartblobsPanelRadial;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;
import humanaicore.common.ScreenUtil;

/** starts Smartblob in a window */
public class StartRadial_Old{
	
	public static void main(String args[]){
		JFrame window = new JFrame("Smartblob 0.2.0_inProgress in-progress physics without smarts so far");
		window.setJMenuBar(newMenubar());
		window.add(new SmartblobsPanelRadial());
		window.setSize(new Dimension(650,450));
		ScreenUtil.moveToScreenCenter(window);
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.setVisible(true);
	}
	
	protected static JMenuBar newMenubar(){
		JMenuBar m = new JMenuBar();
		
		JMenu file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);
		m.add(file);
		
		JMenuItem newHumanainet = new JMenuItem(new AbstractAction("New"){
			public void actionPerformed(ActionEvent e){
				System.out.println("action new");
			}
		});
		newHumanainet.setMnemonic(KeyEvent.VK_N);
		file.add(newHumanainet);
		
		JMenuItem open = new JMenuItem(new AbstractAction("Open"){
			public void actionPerformed(ActionEvent e){
				System.out.println("action open");
			}
		});
		open.setMnemonic(KeyEvent.VK_O);
		file.add(open);
		
		JMenuItem save = new JMenuItem(new AbstractAction("Save"){
			public void actionPerformed(ActionEvent e){
				System.out.println("action save");
			}
		});
		save.setMnemonic(KeyEvent.VK_S);
		file.add(save);
		
		JMenuItem saveAs = new JMenuItem(new AbstractAction("Save As"){
			public void actionPerformed(ActionEvent e){
				System.out.println("action saveas");
			}
		});
		saveAs.setMnemonic(KeyEvent.VK_A);
		file.add(saveAs);
		
		JMenu opensource = new JMenu("OpenSource");
		opensource.setMnemonic(KeyEvent.VK_O);
		m.add(opensource);
		
		opensource.add(new JLabel("<html>This program is GNU GPL 2+ kind of opensource, created by Ben F Rayfield.<br>You can unzip this jar file (in any unzipping program) to get the source code.<br>Take it apart, play with it, see how it works, and build your own GPL'ed opensource programs.</html>"));
		
		return m;
	}

}
