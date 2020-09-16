package bookings;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.JOptionPane;

import clients.Client;
import fileio.FileIO;
import rooms.Room;
import ui.Input;
import ui.Output;

public class Booking implements Serializable {
	private static final long               serialVersionUID = 1L;
	private static final String             PATH             = "bookings.bin";
	private static HashMap<String, Booking> bookings;
	private static int                      bookingCount     = 0;

	private Client    client;
	private String    bookingID;
	private Room      room;
	private LocalDate date;
	private LocalTime startTime;
	private int       length;

	static {
		bookings = new HashMap<String, Booking>();

		try {
			unpack(FileIO.load(PATH));
		} catch (Exception e) {

		}
	}
	
	public static void cancelBooking(String ID) {
		bookings.remove(ID);
		try {
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Booking(int numComputers, int breakoutSeats, boolean hasPrinter, boolean hasSmartboard, LocalDate date, LocalTime time, int hours, Client client)
			throws NoRoomFoundException {

		RoomFinder rf = new RoomFinder();
		this.room = rf.getBestFit(numComputers, breakoutSeats, hasPrinter,
				hasSmartboard, date, time, hours);

		if (room == null) {
			throw new NoRoomFoundException(
					"No room found for the given parameters");
		}

		this.date = date;
		this.startTime = time;
		this.length = hours;
		this.client = client;
		this.bookingID = generateBookingID();

		bookings.put(this.bookingID, this);

		try {
			save();
		} catch (IOException e) {
			Output.consoleError("Failed trying to save Bookings");
		}
	}
	
	//placeholder for future UI implementation
	/*public static Booking newBooking(int comps, int seats, boolean printer, boolean smartboard, Client cl) {
		Booking b = new Booking(comps, seats, printer, smartboard, date, time, len, cl);
		return null;
	}*/

	public static Booking newBooking() {
		Client client = null;
		String str;
		int numComputers, numBreakoutSeats, hours;
		Boolean hasPrinter, hasSmartboard;
		LocalDate date = null;
		LocalTime time = null;

		do {
			numComputers = Input.positiveNumberInput(
					"Enter the number of computers required",
					"Number of computers must be 0 or more");
			if(numComputers == -1) {
				return null;
			}
			numBreakoutSeats = Input.positiveNumberInput(
					"Enter the number of breakout seats required",
					"Number of breakout seats must be 0 or more");
			if(numBreakoutSeats == -1) {
				return null;
			}
			
			hasPrinter = Input.getYesNo("Does the client require a printer?");
			if(hasPrinter == null) {
				return null;
			}
			
			hasSmartboard = Input
					.getYesNo("Does the client require a smartboard?");
			if(hasSmartboard == null) {
				return null;
			}
			
			date = Input.getDate("Enter the date of the booking",
					"That is not the correct format");
			if(date == null) {
				return null;
			}
			
			time = Input.getTime("Enter the start time of the booking",
					"That is not the correct format");
			if(time == null) {
				return null;
			}
			
			hours = Input.positiveNumberInput(
					"How many hours is this booking required for? (Min. 1 hour)",
					"Must be => 0");
			if(hours == -1) {
				return null;
			}

			Collection<Client> temp = Client.getClients();
			Object options[] = new Object[temp.size() + 1];
			
			int i = 0;
			for(Client c : temp) {
				options[i] = c;
				i++;
			}
			
			options[temp.size()] = "Add New Client"; 
			
			Object selection = JOptionPane.showInputDialog(null,
					"Enter Client", "Enter Client",
					JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

			if(selection instanceof Client) {
				client = (Client) selection;
			} else {
				client = Client.addClient();
			}
			
			str = "Number of computers: " + numComputers + "\n"
					+ "Number of Breakout Seats: " + numBreakoutSeats + "\n"
					+ "Printer Required: " + hasPrinter + "\n"
					+ "Smartboard Required: " + hasSmartboard + "\n"
					+ "Client: " + client;

		} while (!Input.getYesNo("Are these details correct?\n" + str));

		try {
			Booking b = new Booking(numComputers, numBreakoutSeats, hasPrinter,
					hasSmartboard, date, time, hours, client);
			Output.console("Booking " + b.bookingID + " created");
			return b;
		} catch (NoRoomFoundException e) {
			Output.userError(
					"No availabe rooms found for the given parameters");
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private static void unpack(HashMap<Object, Object> load) {
		if(load == null) { return; }
		
		bookings = (HashMap<String, Booking>) load.get("bookings");
		bookingCount = (int) load.get("count");
		
		Output.console("Loaded %s clients (File updated %s, %s", bookingCount,
				load.get("date"), load.get("time") + ")");
	}

	private static void save()
			throws IOException {
		HashMap<String, Object> dat = new HashMap<String, Object>();
		dat.put("count", bookings.size());
		dat.put("bookings", bookings);
		dat.put("date", LocalDate.now());
		dat.put("time", LocalTime.now());

		FileIO.save(dat, PATH);
	}

	/**
	 * Returns a unique string to identify the booking The string is in the
	 * format: RN(room number)-(booking count), i.e. RN14-38, would
	 * correlate to the 38th booking of room 14.
	 */
	private String generateBookingID() {
		return "RN" + this.room.getRoomNumber() + "-" + bookingCount;
	}

	public Room getRoom() { return this.room; }

	public LocalDate getDate() { return this.date; }

	public LocalTime getTime() { return this.startTime; }

	public int getLength() { return this.length; }

	/**
	 * Gets a collection with all bookings
	 * 
	 * @return A collection containing all the bookings in the system
	 */
	public static ArrayList<Booking> getBookings() {
		return new ArrayList<Booking>(bookings.values());
	}

	public Client getClient() { return this.client; }
	
	public String toString() {
		String str = "Booking ID: " + this.bookingID + "\n"
					 + "Client: " + this.client + "\n"
					 + "Room: " + this.room + "\n"
					 + "Date: " + this.date + "\n"
					 + "Start Time: " + this.startTime + "\n"
					 + "Length: " + this.length + (this.length == 1 ? " hour" : " hours") +"\n";
		
		return str;
	}
}
