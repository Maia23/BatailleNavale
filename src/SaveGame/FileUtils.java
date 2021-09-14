package SaveGame;

import java.io.File;

/**
 * Class defining utils for file management
 * @author Maia
 *
 */
public abstract class FileUtils {
	
	/**
	 * Deletes file from given path
	 * @param path to file
	 * @return true if file is deleted, false otherwise
	 */
	public static boolean deleteFile(String path) {
		if (new File(path).delete()) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if file exists
	 * @param pathToFile
	 * @return true if file exists, false otherwise
	 */
	public static boolean fileExists(String pathToFile) {
		return new File(pathToFile).exists();
	}

}
