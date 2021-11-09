import java.util.Observable;

@SuppressWarnings("deprecation")
public class CircleModel extends Observable {
	//Maybe we'd keep a list of the circles here, but we don't
	//need them for the example.
	
	//This is whether its the current user's turn to draw a circle
	//or not. The UI uses this to know whether to respond to clicks
	//or to ignore them
	private boolean myTurn = false;
	
	public void setMyTurn(boolean mine) {
		myTurn = mine;
	}
	
	public boolean isMyTurn() { return myTurn; }
	
	//We don't really do anything here except notify the UI
	//that we want it to redraw itself with the circle we just added
	public void addCircle(double x, double y, int color) {
		setChanged();
		notifyObservers(new CircleMessage(x, y, color));
	}
	
}
