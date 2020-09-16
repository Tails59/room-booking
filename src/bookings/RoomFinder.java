package bookings;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Predicate;

import clients.Client;
import rooms.Room;

/**
 * This class is used to find and filter bookings and rooms. It is used by
 * the Booking class to find the most suitable room for a client, and also
 * can be used to help generate reports on bookings given different critera
 * 
 * @author 560505
 *
 */
public class RoomFinder {
	private ArrayList<Room>    rooms;
	private ArrayList<Booking> bookings;

	public RoomFinder() {
		rooms = Room.getRooms();
		bookings = Booking.getBookings();
	}

	/**
	 * Gets a copy of the filtered list held by this RoomFinder object.
	 * 
	 * @return
	 */
	public ArrayList<Room> getRooms() { return this.rooms; }

	/**
	 * Returns the room in the TreeSet rooms which has the lowest number of
	 * computers. Based on the natural ordering of the Room object, this
	 * should be the first element in the Set.
	 * 
	 * If no elements exist, it will return null.
	 * 
	 * @return The room in the Set with the lowest number of computers, or
	 *         null
	 */
	private Room getLowestComputers() {
		if (rooms.size() > 0) { 
			Collections.sort(rooms);
			return rooms.get(0); 
		}

		return null;
	}

	/**
	 * Returns a room that best fits the given parameters
	 * 
	 * @param numComputers
	 * @param breakoutSeats
	 * @param hasPrinter
	 * @param hasSmartboard
	 * @param date
	 * @param time
	 * @param hours
	 * @return
	 */
	public Room getBestFit(int numComputers, int breakoutSeats, boolean hasPrinter, boolean hasSmartboard, LocalDate date, 
			LocalTime time, int hours) {
		filterByDateTime(date, time, hours);
		filterByBreakouts(breakoutSeats);
		filterByComputers(numComputers);
		filterByPrinter(hasPrinter);
		filterBySmartboard(hasSmartboard);

		return getLowestComputers();
	}

	/**
	 * Removes all rooms from the collection which have less than the
	 * {@code numComputer} parameter.
	 * 
	 * @param numComputers
	 *            Number of computers to act as a minimum threshold
	 */
	public void filterByComputers(int numComputers) {
		Predicate<Room> pred = r -> r.getComputers() < numComputers;
		 
		rooms.removeIf(pred);
	}

	/**
	 * Removes all rooms from the collection which have less than the
	 * {@code numBreakouts} parameter.
	 * 
	 * @param numBreakouts
	 *            Number of breakout seats to act as a minimum threshold
	 */
	public void filterByBreakouts(int numBreakouts) {
		Predicate<Room> pred = r -> r.getBreakoutSeats() < numBreakouts;
		rooms.removeIf(pred);
	}

	/**
	 * Removes all rooms from the list which do not have a
	 * {@code hasSmartboard} field value the same as {@code has}
	 * 
	 * @param has
	 *            Rooms with this smartboard value will be kept in the list
	 */
	public void filterBySmartboard(boolean has) {
		Predicate<Room> pred = r -> !(r.hasSmartboard() == has);
		rooms.removeIf(pred);
	}

	/**
	 * Removes all rooms from the list which do not have a
	 * {@code hasPrinter} field value the same as {@code has}
	 * 
	 * @param has
	 *            Rooms with this printer value will be kept in the list
	 */
	public void filterByPrinter(boolean has) {
		Predicate<Room> pred = r -> !(r.hasPrinter() == has);
		rooms.removeIf(pred);
	}

	/**
	 * @param date
	 *            The date that the new booking is on
	 * @param time
	 *            The time for the new booking
	 * @param length
	 *            Length of time in hours the new booking lasts
	 */
	public void filterByDateTime(LocalDate date, LocalTime time, int length) {
		filterByDate(date);
		filterByTime(time, length);
	}

	/**
	 * TODO write something explanatory here
	 * 
	 * @param date
	 */
	public void filterByDate(LocalDate date) {
		for (Booking b : bookings) {
			if (b.getDate().compareTo(date) != 0) {
				rooms.remove(b.getRoom());
			}
		}
	}
	
	public void filterByClient(Client cl) {
		for (Booking b : bookings) {
			if (b.getClient() != cl) {
				rooms.remove(b.getRoom());
			}
		}
	}

	/*
	 * Removes a room from the list if it has a booking colliding with the
	 * given time and length. Will not remove any that collide exactly with
	 * the time. I.e. if the booking ends at 1600, and the parameter "time"
	 * is 1600, it will pass.
	 * 
	 * Gives a value of true if the times overlap. Help with this piece of
	 * logic came from answers on the following thread:
	 * https://stackoverflow.com/questions/325933/determine-whether-two-
	 * date-ranges-overlap
	 * 
	 * For a range to overlap, the beginning of the FIRST range must be
	 * before the end of the SECOND range and the end of the FIRST range
	 * must be before the start of the SECOND range i.e. range overlaps
	 * when !(StartA > EndB) && !(EndA < StartB)
	 */
	public void filterByTime(LocalTime time, int length) {
		for (Booking b : bookings) {
			boolean exp = !(b.getTime().getHour() > time.getHour() + length)
					&& !(b.getTime().getHour() + b.getLength() < time.getHour());

			if (exp) {
				rooms.remove(b.getRoom());
				continue;
			}
		}
	}
}