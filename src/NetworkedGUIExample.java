import java.util.Observable;
import java.util.Observer;

import javafx.application.Application;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

@SuppressWarnings("deprecation")
public class NetworkedGUIExample extends Application implements Observer {
	private Canvas canvas;
	private boolean canClick = true;
	
	private CircleController controller; 
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage mainStage)  {
		CircleModel model = new CircleModel();
		model.addObserver(this);
		controller = new CircleController(model);
		
		BorderPane border = new BorderPane();
		FlowPane toolbar = new FlowPane();
		Button startServer = new Button("Start as Server");
		Button startClient = new Button("Start as Client");
		
		startServer.setOnAction(event -> {
			startClient.setDisable(true);
			startServer.setDisable(true);
			mainStage.setTitle("SERVER");
			
			controller.startServer();
		});
		
		startClient.setOnAction(event -> {
			startClient.setDisable(true);
			startServer.setDisable(true);
			mainStage.setTitle("CLIENT");
			canClick = false;
			controller.startClient();
		});
		
		toolbar.getChildren().addAll(startServer, startClient);
		border.setTop(toolbar);
		canvas = new Canvas(300,300);
		canvas.setOnMouseClicked(event -> {
			//We still get clicks, but that doesn't mean we have to
			//do anything with them
			if(!canClick) { return; }
			
			controller.makeCircle(event.getX(), event.getY());
			canClick = false;
		});
		
		border.setCenter(canvas);
		Scene scene = new Scene(border, 300, 300);
		mainStage.setScene(scene);
		mainStage.setResizable(false);
		mainStage.setTitle("Click to add a circle");
		mainStage.show();
	}
	
	@Override
	//When we get a notification of an update, we are either
	//drawing the circle we clicked, or the circle we get from
	//the Internet. Draw it based on color, and then use the
	//turn state of the model to determine if we're allowed to
	//click again.
	public void update(Observable o, Object arg) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		CircleMessage circle = (CircleMessage)arg;
		
		int color = circle.getColor();
		if(color == CircleMessage.RED) {
			gc.setFill(Color.RED);
		}
		else {
			gc.setFill(Color.BLUE);
		}

		gc.fillOval(circle.getX()-10, circle.getY()-10, 20, 20);
		
		CircleModel model = (CircleModel)o;
		if(model.isMyTurn()) {
			canClick = true;
		}
	}

}
