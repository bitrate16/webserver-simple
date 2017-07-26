package bitrate16.webserver.http;

public class Constants {
	public static final byte[]	END_OF_LINE			= { 13, 10 };
	public static final byte[]	END_OF_BLOCK		= { 13, 10, 13, 10 };
	public static final String	END_OF_LINE_STRING	= new String(END_OF_LINE);
	public static final String	END_OF_BLOCK_STRING	= new String(END_OF_BLOCK);
	public static final int		TIMEOUT				= 10000;
	public static final int		BUFFER_SIZE			= 1024;
	// InputStream TIMEOUT
	public static final int		INPUTSTREAM_TIMEOUT	= 1000;
	// Server name
	public static final String	SERVER				= "Cupcake";
}
