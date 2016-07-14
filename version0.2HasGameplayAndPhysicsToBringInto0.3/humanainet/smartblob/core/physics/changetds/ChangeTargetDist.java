package humanainet.smartblob.core.physics.changetds;

import humanainet.smartblob.core.trianglemesh.SmartblobTri;

/** Similar to ChangeSpeed except changes MovLine.targetDistance instead of adding to points speeds.
This is a potential replacement for MusclesFeelAndReact.
*/
public interface ChangeTargetDist{
	
	public void changeTargetDists(SmartblobTri blob, float secondsSinceLastCall);

}
