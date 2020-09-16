package reports;

import java.util.ArrayList;
import java.util.function.Predicate;

import bookings.Booking;
import clients.Client;

class BookingsFinder {
	private ArrayList<Booking> bookings;
	
	public BookingsFinder() {
		bookings = Booking.getBookings();
	}
	
	public void filterByClient(Client cl) {
		Predicate<Booking> pred = b -> b.getClient() == cl;
		bookings.removeIf(pred);
	}
	
	public ArrayList<Booking> get() {
		return this.bookings;
	}
}
