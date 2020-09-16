package ui;

import java.io.IOException;

import javax.swing.JOptionPane;

import bookings.Booking;
import clients.Client;
import reports.Report;
import rooms.Room;

public class Main {
	// Static block is used to load the Booking, Client and Room classes
	// before the user can do anything else
	// Forces rooms, bookings and clients files to load into the system at
	// the right time.
	static {
		try {
			Class.forName("clients.Client");
			Class.forName("rooms.Room");
			Class.forName("bookings.Booking");
		} catch (ExceptionInInitializerError e) {
			// Print an error to the console and then exit the JVM if a
			// class fails to load.
			// We exit the JVM as continuing to run might run the risk of
			// corrupting data for
			// bookings, clients or rooms.
			Output.consoleError("An exception is being thrown in a static initializer");
			e.printStackTrace();
			System.exit(-1);
		} catch (ClassNotFoundException e) { //Unlikely to happen, but best to catch it anyway just incase
			Output.consoleError("One of the classes is missing - exiting");
			e.printStackTrace();
			System.exit(-1);
		} catch(Exception e ) {
			Output.consoleError("Couldn't load one of the classes!");
			System.exit(-1);
		}
	}

	public static void main(String[] args) {
		showMenu();
	}

	private static void showMenu() {
		String[] options = { "Make a Booking", "Cancel Booking", "Add Room",
				"Add Client", "Generate Report", "Exit" };

		do {
			String input = (String) JOptionPane.showInputDialog(null,
					"Select an Option", "Main Menu",
					JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			
			// Update input to "Exit" if null, or else keep it as whatever
			// input was supplied
			// Basically, closes the program safely when the "x" is pushed
			// or whenever any other strange value is given
			// rather than crashing
			input = input == null ? "Exit" : input;

			switch (input) {
			case "Make a Booking":
				makeBooking();
				break;
			case "Add Room":
				addRoom();
				break;
			case "Add Client":
				addClient();
				break;
			// TODO better exiting of the program
			case "Cancel Booking":
				Booking.cancelBooking(JOptionPane.showInputDialog("Enter booking ID"));
				break;
			case "Generate Report":
				try {
					new Report();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Report could not be generated");
					e.printStackTrace();
				}
			case "Exit":
			default:
				Output.console("Exiting...");
				System.exit(0);
			}
		} while (true);
	}

	private static Booking makeBooking() {
		return Booking.newBooking();
	}

	private static Client addClient() {
		return Client.addClient();
	}

	private static Room addRoom() {
		return Room.addRoom();
	}

	private static Booking cancelBooking() {
		return null;
	}
}
