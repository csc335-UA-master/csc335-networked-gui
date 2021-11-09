import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.application.Platform;

public class CircleController {
	private CircleModel model;
	private Socket connection;
	private boolean isServer = false;
	private boolean isConnected = false;
	
	ObjectOutputStream oos;
	ObjectInputStream ois;
	
	public CircleController(CircleModel m) {
		model = m;
	}
	
	public void startServer() {
		try {
			ServerSocket server = new ServerSocket(4000);
			//This blocks, and that's bad. But it's only a short block
			//so we won't solve it. Once the client connects, it will
			//unblock.
			connection = server.accept();
			
			//When we reach here, we have a client, so grab the
			//streams to do our message sending/receiving
			oos = new ObjectOutputStream(connection.getOutputStream());
			ois = new ObjectInputStream(connection.getInputStream());
			
			isServer = true;
			isConnected = true;
			model.setMyTurn(true);
		}
		catch(IOException e) {
			System.err.println("Something went wrong with the network! " + e.getMessage());
		}
	}
	
	public void startClient() {
		try {
			connection = new Socket("localhost", 4000);
			
			isServer = false;
			isConnected = true;
			model.setMyTurn(false);
			oos = new ObjectOutputStream(connection.getOutputStream());
			ois = new ObjectInputStream(connection.getInputStream());

			//A thread represents code that can be done concurrently
			//to other code. We have the "main" thread where our program
			//is happily running its event loop, and now we
			//create a second thread whose entire job it is to send
			//our message to the other program and to block (wait) for 
			//their response.
			Thread t = new Thread(() -> {
				try {
					CircleMessage otherMsg = (CircleMessage)ois.readObject();
					
					//The runLater method places an event on the main
					//thread's event queue. All things that change UI
					//elements must be done on the main thread. 
					Platform.runLater(() -> {
						model.setMyTurn(true);
						model.addCircle(otherMsg.getX(), otherMsg.getY(), otherMsg.getColor());		
					});
					//We let the thread die after receiving one message.
					//We'll create a new one on the next click.
				}
				catch(IOException | ClassNotFoundException e) {
					System.err.println("Something went wrong with serialization: " + e.getMessage());
				}
			});
			//This is when the thread begins running - so now. 
			t.start();
			//Even though this method is done, the code of the thread
			//keeps doing its job. We've just allowed the event handler
			//to return and the event loop to keep running.
		}
		catch(IOException e) {
			System.err.println("Something went wrong with the network! " + e.getMessage());
		}
	}
	
	public void makeCircle(double x, double y) {
		//Add the circle to our model
		model.addCircle(x, y, (isServer) ? CircleMessage.BLUE : CircleMessage.RED);
		
		//Send the new circle to the other person
		CircleMessage msg = new CircleMessage(x, y, (isServer) ? CircleMessage.BLUE : CircleMessage.RED);
		sendMessage(msg);
	}
	
	private void sendMessage(CircleMessage msg) {
		if(!isConnected) { return; }
		
		//See the code above that does the same. Here we will send
		//a message and receive one in the new thread, avoiding
		//blocking the event loop.
		Thread t = new Thread(() -> {
			try {
				oos.writeObject(msg);
				oos.flush();
				
				CircleMessage otherMsg = (CircleMessage)ois.readObject();
				
				Platform.runLater(() -> {
					model.setMyTurn(true);
					model.addCircle(otherMsg.getX(), otherMsg.getY(), otherMsg.getColor());
				});
			}
			catch(IOException | ClassNotFoundException e) {
				System.err.println("Something went wrong with serialization: " + e.getMessage());
			}
			
		});
		t.start();
		
	}
}
