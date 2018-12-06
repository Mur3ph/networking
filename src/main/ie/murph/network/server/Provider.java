package main.ie.murph.network.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import main.ie.murph.network.domain.MessageDefault;
import main.ie.murph.network.gui.EDebugMessage;
import main.ie.murph.network.gui.IGUIRequest;

public class Provider implements Runnable
{
	private Socket CLIENT_SOCKET;
	private MessageDefault OBJECT_PASSED_IN, OBJECT_PASSED_OUT;
	private ObjectInputStream STREAM_IN_FROM_CLIENT;
	private ObjectOutputStream STREAM_OUT_TO_CLIENT;

	public Provider(Socket socket) throws IOException
	{
		CLIENT_SOCKET = socket;
		STREAM_OUT_TO_CLIENT = createObjectOutputStream();
		STREAM_IN_FROM_CLIENT = createObjectInputStream();
	}

	public void run()
	{
		System.out.println(IGUIRequest.INTERACTION_WITH_SERVER);
		try
		{
			startRespondingToClient();
		}
		catch (IOException | ClassNotFoundException e)
		{
			System.out.println(EDebugMessage.CONNECTION_LOST);
			System.err.println(EDebugMessage.SERVER_ERROR + e.getMessage());
			System.err.println(EDebugMessage.LOCALIZED_ERROR + e.getLocalizedMessage());
			System.err.println(EDebugMessage.STACK_TRACE + " " + e.getStackTrace());
			System.err.println(EDebugMessage.EXCEPTION_STRING + e.toString());
		}
		finally
		{
			System.out.println(EDebugMessage.CONNECTION_CLOSED);
			closeConnection();
		}
	} // End of Thread inherited method run()..........

	private void startRespondingToClient() throws IOException, ClassNotFoundException
	{
		OBJECT_PASSED_IN = readObjRequestFromClient();

		while (inputNotEqualToExit(OBJECT_PASSED_IN.getMessageBody()))
		{
			if (inputEqualsHello(OBJECT_PASSED_IN.getMessageBody()))
			{
				OBJECT_PASSED_OUT = new MessageDefault(IGUIRequest.STAR_TREK_QUOTE, OBJECT_PASSED_IN.getMessageBody() + IGUIRequest.CORRECT);
				STREAM_OUT_TO_CLIENT.writeObject(OBJECT_PASSED_OUT);
			}
			else
			{
				OBJECT_PASSED_OUT = new MessageDefault(IGUIRequest.SCARFACE_QUOTE, OBJECT_PASSED_IN.getMessageBody() + IGUIRequest.INCORRECT);
				STREAM_OUT_TO_CLIENT.writeObject(OBJECT_PASSED_OUT);
			}
			this.startRespondingToClient();
		}
		System.out.println(EDebugMessage.REQUEST_TO_END_SESSION);
		closeConnection();
	}

	private ObjectOutputStream createObjectOutputStream() throws IOException
	{
		return new ObjectOutputStream(CLIENT_SOCKET.getOutputStream());
	}

	private ObjectInputStream createObjectInputStream() throws IOException
	{
		return new ObjectInputStream(CLIENT_SOCKET.getInputStream());
	}

	private MessageDefault readObjRequestFromClient() throws IOException, ClassNotFoundException
	{
		return (MessageDefault) STREAM_IN_FROM_CLIENT.readObject();
	}

	private boolean inputNotEqualToExit(String input)
	{
		return !input.equalsIgnoreCase(IGUIRequest.EXIT);
	}

	public boolean inputEqualsHello(String input)
	{
		return input.equals(IGUIRequest.HELLO);
	}

	private void closeConnection()
	{
		System.out.println(EDebugMessage.CONNECTION_CLOSING);
		try
		{
			closeBufferedReaderRequestStream();
			closePrinterWriterResponseStream();
			closeSocketStreamConnection();
			System.out.println(EDebugMessage.CONNECTION_CLOSING);
		}
		catch (IOException e)
		{
			System.out.println(EDebugMessage.UNABLE_TO_DISCONNECT);
			System.exit(1);
		}
	} // End of close connection method...

	private void closeBufferedReaderRequestStream() throws IOException
	{
		if (STREAM_IN_FROM_CLIENT != null)
			STREAM_IN_FROM_CLIENT.close();
	}

	private void closePrinterWriterResponseStream() throws IOException
	{
		if (STREAM_OUT_TO_CLIENT != null)
			STREAM_OUT_TO_CLIENT.close();
	}

	private void closeSocketStreamConnection() throws IOException
	{
		if (CLIENT_SOCKET != null)
			CLIENT_SOCKET.close();
	}
}
