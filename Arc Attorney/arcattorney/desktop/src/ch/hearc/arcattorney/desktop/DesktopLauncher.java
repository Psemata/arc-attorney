package ch.hearc.arcattorney.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ch.hearc.arcattorney.ArcAttorney;
import ch.hearc.arcattorney.utils.Constants;

public class DesktopLauncher {
	public static void main(String[] arg) {
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Arc'Attorney";
		config.width = 1280;
		config.height = 720;
		config.addIcon(Constants.LOGO, FileType.Internal);
		new LwjglApplication(new ArcAttorney(), config);
	}
}
