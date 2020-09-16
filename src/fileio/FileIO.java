package fileio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import ui.Output;

/**
 * Loads and saves HashMap<Object, Object> objects from files in the
 * Room Booking System
 * @author 560505
 *
 */
public final class FileIO {
	@SuppressWarnings("unchecked")
	public static HashMap<Object, Object> load(String PATH) throws ClassNotFoundException, IOException{
		File file = new File(PATH);
		if(!file.exists()) {
			Output.console(PATH + " doesnt exist, creating");
			file.createNewFile();
		}
		
		if(file.length() == 0) {
			Output.console(PATH + " is empty, aborting load.");
			return null;
		}
		
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(PATH))) {
			HashMap<Object, Object> map = (HashMap<Object, Object>) in.readObject();
			Output.console(PATH + " file loaded");
			
			return map;
		}
	}
	
	public static boolean save(HashMap<String, Object> data, String PATH) throws IOException {
		try (ObjectOutputStream in = new ObjectOutputStream(new FileOutputStream(PATH))) {
			in.writeObject(data);
			return true;
		}
	}
}
