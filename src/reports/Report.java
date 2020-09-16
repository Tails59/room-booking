package reports;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import bookings.Booking;
import clients.Client;

public class Report {
	//private static final String DEFAULT_PATH = ;
	private ArrayList<Booking> bookings;
	File file;
	FileWriter fw;
	
	public Report() throws IOException{
		makeFile();
		getBookings();
		print();
	}
	
	private void makeFile() throws IOException {
		String path = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ", " 
					+ LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)+ ".txt"; //Create a report named with today's date and time
		path = path.replace(":", ".");
		
		System.out.println(path);
		file = new File(path);
		
		if (! file.createNewFile()) {
			throw new IOException();
		}
		
		fw = new FileWriter(path);
	}
	
	private void getBookings() {
		Client selection = (Client) JOptionPane.showInputDialog(null,
				"Enter Client", "Enter client to find bookings for",
				JOptionPane.INFORMATION_MESSAGE, null, Client.getClients().toArray(), Client.getClients().get(0));
		
		BookingsFinder bf = new BookingsFinder();
		bf.filterByClient(selection);
		bookings = bf.get();
	}
	
	private void print() {
		try {
			StringBuilder sb = new StringBuilder();
			for(Booking b : bookings) {
				sb.append(b.toString());
				sb.append("\n");
			}
			System.out.println(sb.toString());
			
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(sb.toString().getBytes());
			JOptionPane.showMessageDialog(null, "Your report has been printed to file");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
