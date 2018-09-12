import javafx.application.Application;
import javafx.stage.Stage;

public class MainApplication extends Application{
	
	public static void main(String[] args){
		
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		
		SceneUtil.initStage(stage);
		
		stage.show();
	}

}
