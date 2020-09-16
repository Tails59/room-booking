package clients;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fileio.FileIO;
import ui.Input;
import ui.Output;

@SuppressWarnings("unused")
public class Client implements Serializable, Comparable<Client> {
	private static HashMap<Integer, Client> clients;
	private static int                      idCount;
	private static final long               serialVersionUID = 1L;
	private static final String             PATH             = "clients.bin";

	private int    clientID;
	private String fname;
	private String lname;
	private String telephone;
	private String email;

	static {
		clients = new HashMap<Integer, Client>();
		Output.console("Atttempting to load Clients file...");
		
		try {
			unpack(FileIO.load(PATH));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param fname
	 *            Clients first name
	 * @param lname
	 *            Clients last name
	 * @param telephone
	 *            Clients telephone number - can be null, but then email is
	 *            required
	 * @param email
	 *            Clients email - can be null, but then telephone is
	 *            required.
	 * @throws ExistingClientException
	 * @throws IOException
	 */
	private Client(String fname, String lname, String telephone, String email)
			throws ExistingClientException {
		this.fname = fname;
		this.lname = lname;
		this.telephone = telephone;
		this.email = email;
		this.clientID = idCount++;

		if (clientExists(this)) { throw new ExistingClientException(); }

		clients.put(idCount, this);
		try {
			save();
		} catch (IOException e) {
			Output.consoleError("Failed trying to save Clients");
			e.printStackTrace();
		}
	}
	
	private Client(String fname, String lname)
			throws ExistingClientException {
		this(fname, lname, Input.getClientTelephone(), Input.getClientEmail());
	}

	/**
	 * Attempts to add a client to the system
	 * 
	 * @return the new client if it was created, otherwise null.
	 */
	public static Client addClient() {
		String telephone, email, lname, fname, str;

		do {
			fname = Input.nonNullString("Enter clients first name",
					"First name cannot be blank!");
			if(fname == null) {
				return null;
			}
			lname = Input.nonNullString("Enter clients last name",
					"Last name cannot be blank!");
			if(lname == null) {
				return null;
			}
			
			boolean valid;
			do {
				valid = true;
				telephone = Input.getClientTelephone();
				email = Input.getClientEmail();

				if (telephone == null && email == null) {
					Output.consoleWarn(
							"You must supply a telephone number and/or email");
					valid = false;
				}

			} while (!valid);

			// Creates a string with the supplied details, and
			// avoids trying to concatenate "null" for
			// phone/email if either wasnt supplied.
			str = "First name: " + fname + "\n" + "Last Name: " + lname + "\n"
					+ "Telephone " + telephone + "\n" + "Email Address: "
					+ email;

		} while (!Input
				.getYesNo("Please confirm the following details:\n" + str));

		try {
			Client c = new Client(fname, lname, telephone, email);
			Output.console("Added new client with details\n" + str);
			return c;
		} catch (ExistingClientException e) {
			Output.userError("The client details you entered already exist");
			return email == null ? findByPhone(telephone) : findByEmail(email);
		}
	}

	/**
	 * Gets a clone of the list of all clients
	 * 
	 * @return a list of all clients
	 */
	public static ArrayList<Client> getClients() {
		return new ArrayList<Client>(clients.values());
	}

	/**
	 * Gets the total number of clients
	 * 
	 * @return the total number of clients as an int
	 */
	public static int getCount() { return idCount; }

	/**
	 * Changes a clients phone number
	 * 
	 * @param phone
	 *            The new phone number
	 */
	private void setPhone(String phone) { this.telephone = phone; }

	/**
	 * Changes a clients email address
	 * 
	 * @param email
	 *            The new email address
	 */
	private void setEmail(String email) { this.email = email; }

	/**
	 * Finds a client with the given telephone number
	 */
	public static Client findByPhone(String telephone) {
		for (Client c : clients.values()) {
			if (c.telephone != null && c.telephone.equals(telephone)) {
				return c;
			}
		}

		return null;
	}

	/**
	 * Finds a client with the given email address
	 */
	public static Client findByEmail(String email) {
		for (Client c : clients.values()) {
			if (c.email != null && c.email.equals(email)) { return c; }
		}

		return null;
	}

	/**
	 * Checks whether a given client already exists in the system. Does
	 * this by comparing the given client to all clients in the clients
	 * list.
	 * 
	 * @param c
	 *            Client to search for
	 * @return true if the client already exists
	 */
	public static boolean clientExists(Client c) {
		return findByPhone(c.telephone) != null || findByEmail(c.email) != null;
	}

	/**
	 * Unpacks a hash map loaded from a file
	 */
	@SuppressWarnings("unchecked")
	private static void unpack(HashMap<Object, Object> hashMap) {
		if (hashMap == null) { return; }
		clients = (HashMap<Integer, Client>) hashMap.get("clients");
		idCount = (int) hashMap.get("count");

		Output.console("Loaded %s clients (File updated %s, %s", idCount,
				hashMap.get("date"), hashMap.get("time") + ")");
	}

	/**
	 * Saves the clients to a file with the following information
	 * <ol>
	 * <li>The amount of clients</li>
	 * <li>A map with all of the clients</li>
	 * <li>The date the file was saved</li>
	 * <li>The time the file was saved</li>
	 * </ol>
	 * 
	 * @throws IOException
	 */
	private static void save()
			throws IOException {
		HashMap<String, Object> dat = new HashMap<String, Object>();
		dat.put("count", idCount);
		dat.put("clients", clients);
		dat.put("date", LocalDate.now());
		dat.put("time", LocalTime.now());

		FileIO.save(dat, PATH);
	}

	@Override
	public int compareTo(Client o) {
		return this.lname.compareTo(o.lname);
	}
	
	@Override
	public String toString() {
		return this.fname + " " + this.lname;
	}
	
	public String getFName() {
		return this.fname;
	}
	
	public String getLName() {
		return this.lname;
	}
	
	public String getName() {
		return this.fname + " " + this.lname;
	}
	
	public String getPhoneNum() {
		if (this.telephone != null) {
			return this.telephone;
		}
		
		return "NO TELEPHONE SUPPLIED";
	}
	
	public String getEmail() {
		if (this.email != null) {
			return this.email;
		}
		
		return "NO EMAIL SUPPLIED";
	}
}
