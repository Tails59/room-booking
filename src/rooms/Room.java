package rooms;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

import fileio.FileIO;
import ui.Input;
import ui.Output;

public class Room implements Serializable, Comparable<Room> {
	private static HashMap<Integer, Room> rooms;
	private static final long             serialVersionUID = 1L;
	private static final String           PATH             = "rooms.bin";

	private int     roomNumber;
	private int     numComputers;
	private int     breakoutSeats;
	private boolean hasSmartboard;
	private boolean hasPrinter;

	static {
		rooms = new HashMap<Integer, Room>();
		Output.console("Attempting to load Rooms file...");
		try {
			unpack(FileIO.load(PATH));
		}
		// TODO handle these exceptions in a more user-friendly manner
		catch (ClassNotFoundException e) {
			Output.consoleError("Something went wrong loading rooms");
			e.printStackTrace();
		} catch (EOFException e) {
			Output.consoleError("Something went wrong loading rooms");
			e.printStackTrace();
		} catch (Exception e) {
			Output.consoleError("Something went wrong loading rooms");
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new persistent room
	 * 
	 * @param roomNumber
	 *            The room number
	 * @param numComputers
	 *            Number of computers in the room
	 * @param breakoutSeats
	 *            Number of breakout seats in the room
	 * @param hasSmartboard
	 *            True if the room has a smartboard
	 * @param hasPrinter
	 *            True if the room has a printer
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private Room(int roomNumber, int numComputers, int breakoutSeats, boolean hasSmartboard, boolean hasPrinter)
			throws ExistingRoomException {
		if (hasSmartboard && numComputers == 0)
			Output.consoleWarn(
					"Room %s created with no computers but has a smartboard - doesnt seem right",
					roomNumber);
		if (hasPrinter && numComputers == 0)
			Output.consoleWarn(
					"Room %s created with no computers but has a printer - doesnt seem right",
					roomNumber);

		this.roomNumber = roomNumber;
		this.numComputers = numComputers;
		this.breakoutSeats = breakoutSeats;
		this.hasSmartboard = hasSmartboard;
		this.hasPrinter = hasPrinter;

		rooms.put(roomNumber, this);

		try {
			save();
		} catch (IOException e) {
			Output.consoleError("Failed trying to save Rooms");
			e.printStackTrace();
		} catch (Exception e) {
			Output.consoleError("Failed trying to save Rooms");
			e.printStackTrace();
		}
	}

	public int getRoomNumber() { return this.roomNumber; }

	/**
	 * Prompts the user for information and attempts to add a room to the
	 * system
	 * 
	 * @return The new room, or if one exists it will return the old room.
	 */
	public static Room addRoom() {
		String str;
		int roomNumber, numComputers, breakoutSeats;
		Boolean hasSmartboard, hasPrinter;

		do {
			roomNumber = Input.positiveNumberInput("Enter the room number",
					"Room number must be a number!");
			
			if (roomNumber < 0) {
				return null;
			}
			
			do {
				numComputers = Input.positiveNumberInput(
						"Enter the number of computers (Must be 0 or greater)",
						"Number of computers must be a number!");
				
				if (numComputers < 0) {
					return null;
				}
			} while (roomNumber < 0);

			do {
				breakoutSeats = Input.positiveNumberInput(
						"Enter the number of breakout seats",
						"Number of breakout seats must be a number!");
				if (breakoutSeats < 0) {
					return null;
				}
			} while (roomNumber < 0);

			hasSmartboard = Input
					.getYesNo("Does this room have a smart board?");
			if(hasSmartboard == null) {
				return null;
			}
			hasPrinter = Input.getYesNo("Does this room have a printer?");
			if(hasPrinter == null) {
				return null;
			}
			str = "Room No. : " + roomNumber + "\n" + "Num. Computers: "
					+ numComputers + "\n" + "Num. Breakout Seats: "
					+ breakoutSeats + "\n" + "Has Printer: " + hasPrinter + "\n"
					+ "Has Smartboard: " + hasSmartboard;

		} while (!Input.getYesNo("Are these details correct?\n" + str));

		try {
			return new Room(roomNumber, numComputers, breakoutSeats,
					hasSmartboard, hasPrinter);
		} catch (ExistingRoomException e) {
			Output.userError("A room with that number already exists!");
			return findRoom(roomNumber);
		}
	}

	/**
	 * Finds a room with the given parameter roomNumber
	 * 
	 * @param roomNumber
	 *            a room number to search for
	 * @return A room if one has the given room number, otherwise null
	 */
	public static Room findRoom(int roomNumber) {
		for (Room r : rooms.values()) {
			if (r.roomNumber == roomNumber) { return r; }
		}

		return null;
	}

	/**
	 * Checks whether a room has any computers (more than 0)
	 * 
	 * @return true if the room has computers
	 */
	public boolean hasComputers() {
		return this.numComputers > 0;
	}

	/**
	 * Gets the number of computers a room has
	 * 
	 * @return the number of computers
	 */
	public int getComputers() { return this.numComputers; }

	/**
	 * Gets whether a room has a smartboard
	 * 
	 * @return True if the room contains a smartboard
	 */
	public boolean hasSmartboard() {
		return this.hasSmartboard;
	}

	/**
	 * @return True if the room has a printer
	 */
	public boolean hasPrinter() {
		return this.hasPrinter;
	}

	/**
	 * @return True if the room has breakout seats
	 */
	public boolean hasBreakoutSeats() {
		return this.breakoutSeats > 0;
	}

	/**
	 * @return The number of breakout seats
	 */
	public int getBreakoutSeats() { return this.breakoutSeats; }

	/**
	 * Returns a TreeSet containing all the rooms.
	 * The Rooms in the set will be ordered in ascending order based on
	 * the number of computers in each room.
	 * 
	 * @return A TreeSet containing all the rooms
	 */
	public static ArrayList<Room> getRooms() { 
		return new ArrayList<Room>(rooms.values()); 
	}

	/**
	 * Saves a map to a file with the following information:
	 * <ol>
	 * <li>The amount of records in the map</li>
	 * <li>A map with all saved rooms</li>
	 * <li>The date the file was saved</li>
	 * <li>The time the file was saved</li>
	 * </ol>
	 * 
	 * @throws IOException
	 */
	private static void save() throws IOException {
		HashMap<String, Object> dat = new HashMap<String, Object>();
		dat.put("count", rooms.size());
		dat.put("rooms", rooms);
		dat.put("date", LocalDate.now());
		dat.put("time", LocalTime.now());

		FileIO.save(dat, PATH);
	}

	/**
	 * Unpacks the data from a hashmap loaded from file into a useable
	 * format
	 * 
	 * @param hashMap
	 *            A hashmap with data about the rooms.
	 */
	@SuppressWarnings("unchecked")
	private static void unpack(HashMap<Object, Object> hashMap) {
		if (hashMap == null) { return; }

		rooms = (HashMap<Integer, Room>) hashMap.get("rooms");

		Output.console("Loaded %s rooms (File updated %s, %s)",
				hashMap.get("count"), hashMap.get("date"), hashMap.get("time"));
	}

	@Override
	public int compareTo(Room o) {
		return this.getComputers() - ((Room) o).getComputers();
	}
	
	@Override
	public String toString() {
		return Integer.toString(this.roomNumber);
	}
}
