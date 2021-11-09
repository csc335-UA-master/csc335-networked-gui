import java.io.Serializable;

public class CircleMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/*
	 * Turns out JavaFX Color objects are not serializable
	 * so we just make a quick int version of color (an enumeration
	 * would be better, but it's not something we cover).
	 */
	public static final int RED=1;
	public static final int BLUE=2;
	
	private double x;
	private double y;
	private int color;
	
	public CircleMessage(double x, double y, int c) {
		this.x = x;
		this.y = y;

		this.color = c;
	}
	
	public double getX() { return x; }
	public double getY() { return y; }
	public int getColor() { return color; }
}