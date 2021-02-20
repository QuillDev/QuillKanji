package GUI;

import Scanner.KanjiScanner;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.awt.*;

public class App extends Application {

    private KanjiScanner scanner;
    private final Label kanjiLabel = new Label("Kanji");
    private final Label meaningLabel = new Label("Meaning");

    public App() {
        try {
            this.scanner = new KanjiScanner();
        } catch (AWTException awtException) {
            awtException.printStackTrace();
            System.out.println("Failed to start app.");
            System.exit(0);
        }
    }

    @Override
    public void start(Stage stage) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.add(kanjiLabel, 0, 0);
        grid.add(meaningLabel, 0, 1);
        Scene scene = new Scene(grid, 640, 480);

        var font = new Font("Segoe UI", 24);
        kanjiLabel.setFont(font);
        meaningLabel.setFont(font);
        stage.setScene(scene);
        stage.show();

        //start the scanner thread
        scannerThread.start();
    }

    final Thread scannerThread = new Thread( () -> {
        var lastResult = "";
        for(;;){
            try {

                //scan for any kanji we might want
                var result = scanner.scan();
                if(result == null){
                    continue;
                }

                if(result.bestMatchingCharacters.equals(lastResult)){
                    continue;
                }
                //Change the kanji labels text
                Platform.runLater( () -> {
                    this.kanjiLabel.setText(result.words.get(0).kanji);
                    this.meaningLabel.setText(result.words.get(0).description);
                });

                lastResult = result.bestMatchingCharacters;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });
}
