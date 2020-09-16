package ui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import javax.swing.JOptionPane;

public class Input {
	/**
	 * Gets a date input from the user.
	 * Does not allow a null input
	 * 
	 * @param str Prompt to show the user
	 * @param err Unused
	 * @return a parsed LocalDate supplied by the user
	 */
	public static LocalDate getDate(String str, String err) {
		String in;
		LocalDate date = null;
		boolean keepGoing;
		
		do {
			in = Input.nonNullString(str + ". The format is YYYY-MM-DD", err);
			keepGoing = false;
			if(in == null) {
				return null;
			}
			
			try {
				date = LocalDate.parse(in);
			} catch(DateTimeParseException e) {
				Output.userError("That is not the correct format!");
				keepGoing = true;
			}
		}while(keepGoing);
		
		return date;
	}
	
	/**
	 * Gets a LocalTime input from the user, while not allowing
	 * null inputs.
	 * 
	 * @param str Message to show the user
	 * @param err Error to show if the user enters an invalid string
	 * @return
	 */
	public static LocalTime getTime(String str, String err) {
		String in;
		LocalTime time = null;
		boolean keepGoing;
		
		do {
			in = Input.nonNullString(str + ". The format is HH:MM", err);
			keepGoing = false;
			if(in == null) {
				return null;
			}
			
			try {
				time = LocalTime.parse(in);
			} catch(DateTimeParseException e) {
				Output.userError("That is not the correct format!");
				keepGoing = true;
			}
		}while(keepGoing);
		
		return time;
	}
	
	/**
	 * Gets a positive number (0 or greater) from the user
	 * 
	 * @param msg The string to prompt the user with
	 * @param err Error message to show if an input is invalid
	 * @return the integer supplied by the user
	 * @return -1 if the user clicks the "cancel" button
	 */
	public static int positiveNumberInput(String msg, String err) {
		String input;
		int num = -1;
		boolean keepGoing = true;
		
		do {
			try {
				input = JOptionPane.showInputDialog(null, msg, JOptionPane.QUESTION_MESSAGE);
				if(input == null) {
					return -1;
				}
				
				num = Integer.parseInt(input);
				keepGoing = false;
				
				if(num < 0) {
					Output.userError(err);
					keepGoing = true;
				}
			}
			catch(NumberFormatException e) {
				Output.userError(err);
				keepGoing = true;
			}
		}while(keepGoing);
		
		return num;
	}
	
	/**
	 * Gets any non-null string from the user - this will not be database safe
	 * 
	 * @param message Message to prompt the user with
	 * @param error Error to show the user if they enter an invalid string
	 * 
	 * @return A non-null string
	 */
	public static String nonNullString(String message, String error) {
		boolean bad;
		String input;
		
		do {
			bad = false;
			input = JOptionPane.showInputDialog(null, message);
			if(input == null) {
				return null;
			}
			if(input.equals("")) {
				JOptionPane.showMessageDialog(null, error, "Warning", JOptionPane.WARNING_MESSAGE);
				bad = true;
			}
		}while(bad);
		
		return input;
	}
	
	/**
	 * 
	 * @param message
	 * @return True for yes, false for no
	 */
	public static Boolean getYesNo(String message) {
		return JOptionPane.showConfirmDialog(null, message, "Input", JOptionPane.YES_NO_OPTION) != 1;
	}
	
	/**
	 * Checks whether a supplied string is a valid email address
	 * A valid email address must contain an @ symbol, with text on either side of it.
	 * 
	 * @param text
	 * @return
	 */
	public static boolean isValidEmail(String text) {
		return text.matches("^(.+)@(.+)$");
	}
	
	/**
	 * Checks whether a supplied string is a valid phone number
	 * Valid if the following conditions are met:
	 * <ol>
	 * <li>Length is exactly 11 characters
	 * <li>Contains only numbers
	 * <li>Is not null
	 * </ol>
	 * 
	 * @param text The string to be validated
	 * @return True if the string is a valid phone number. 
	 */
	public static boolean isValidPhone(String text) {
		return (text != null && text.length() == 11 && text.matches("[0-9]+"));
	}
	
	/**
	 * Gets a valid client email from the user.
	 * @return null if no email is supplied
	 * @return a valid email if one is supplied
	 */
	public static String getClientEmail() {
		boolean valid;
		String email;
		do {
			valid = true;
			email = JOptionPane.showInputDialog(null, "Enter clients email, or leave it blank if not supplied");
			
			if(email == null || email.equals("")) {
				email = null;
			}
			
			if(email != null && !isValidEmail(email)) {
				Output.userError("That email is not valid");
				valid = false;
			}
		}while(!valid);
		
		return email == null ? "Not Supplied" : email;
	}
	
	/**
	 * Gets a valid client phone number from the user.
	 * @return null if no phone number is supplied
	 * @return a valid telephone if one is supplied
	 */
	public static String getClientTelephone() {
		boolean valid;
		String phone;
		do {
			valid = true;
			phone = JOptionPane.showInputDialog(null, "Enter clients telephone number, or leave it blank if not supplied");
			
			if(phone == null || phone.equals("")) {
				phone = null;
			}
			
			if(! isValidPhone(phone)) {
				Output.userError("That phone number is not valid");
				valid = false;
			}
		}while(!valid);
		
		return phone == null ? "Not Supplied" : phone;
	}
}
