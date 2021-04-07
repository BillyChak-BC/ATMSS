package ATMSS.BuzzerHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import javafx.scene.control.TextArea;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.logging.Logger;

public class BuzzerEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private BuzzerEmulator buzzerEmulator;
    private MBox buzzerMBox;
    public TextArea BuzzerTextArea;

    String buzzerSound = "realBuzzerSound.mp3";

    public void initialize(String id, AppKickstarter appKickstarter, Logger log, BuzzerEmulator buzzerEmulator){
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.buzzerEmulator = buzzerEmulator;
        this.buzzerMBox = appKickstarter.getThread("BuzzerHandler").getMBox();
        //buzzerEmulator.alert();         //make changes here
    }

    public void appendTextArea(String status) {
        BuzzerTextArea.appendText(status+"\n");
    } // appendTextArea

    //buzzer sound effect (use javafx media and mediaplayer)
    protected void sound() {
        Media buzzer = new Media(new File(buzzerSound).toURI().toString());
        MediaPlayer buzzerPlay = new MediaPlayer(buzzer);
        buzzerPlay.play();
    }
}
