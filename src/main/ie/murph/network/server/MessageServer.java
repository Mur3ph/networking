package main.ie.murph.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import main.ie.murph.network.gui.EDebugMessage;
import main.ie.murph.network.gui.INetwork;

public class MessageServer
{
	private static final Logger LOGGER = LogManager.getLogger(MessageServer.class.getName());
	private static Socket CLIENT_SOCKET;

	public static void main(String[] args)
	{
		new MessageServer();
	}

	private MessageServer()
	{
		establishedConnection();
	} // End of myRun method.

	private void establishedConnection()
	{
		try(ServerSocket SERVER_SOCKET_LISTENER = connectToMachinePort();)
		{
			while (true)
			{
				CLIENT_SOCKET = acceptConnection(SERVER_SOCKET_LISTENER);
				Provider connect = startCommunication();
				createThreadForEachClientCommunication(connect);
			}
		}
		catch (IOException e)
		{
			LOGGER.info("--establishedConnection(): " + EDebugMessage.CANNOT_DISCONNECT);
			LOGGER.error("--establishedConnection(): " + EDebugMessage.SERVER_ERROR + e.getMessage());
			LOGGER.error("--establishedConnection(): " + EDebugMessage.LOCALIZED_ERROR + e.getLocalizedMessage());
			LOGGER.error("--establishedConnection(): " + EDebugMessage.STACK_TRACE + " " + e.getStackTrace());
			LOGGER.error("--establishedConnection(): " + EDebugMessage.EXCEPTION_STRING + e.toString());
		}
		finally
		{
			closeConnection();
		}
	}

	private ServerSocket connectToMachinePort() throws IOException
	{
		return new ServerSocket(INetwork.SPECIFIED_PORT_NUMBER);
	}

	private Socket acceptConnection(ServerSocket SERVER_SOCKET_LISTENER) throws IOException
	{
		return SERVER_SOCKET_LISTENER.accept();
	}
	
	private Provider startCommunication() throws IOException
	{
		return new Provider(CLIENT_SOCKET);
	}
	
	private void createThreadForEachClientCommunication(Provider connect)
	{
		Thread thread = new Thread(connect);
		thread.start();
	}

	private static void closeConnection()
	{
		try
		{
			LOGGER.info("++closeConnection(): " + EDebugMessage.CONNECTION_CLOSING);
			CLIENT_SOCKET.close();
		}
		catch (IOException e)
		{
			LOGGER.error("--closeConnection(): " + EDebugMessage.UNABLE_TO_DISCONNECT);
			System.exit(1);
		}
	}

}
