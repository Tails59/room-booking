package bookings;

@SuppressWarnings("serial")
class NoRoomFoundException extends Exception{
	NoRoomFoundException(String str){
		super(str);
	}
	
	NoRoomFoundException(){
		super();
	}
}
