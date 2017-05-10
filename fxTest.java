package tests;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import sample.Client;
import sample.Controller;
import sample.Server;

import java.net.Socket;

import static org.junit.Assert.assertTrue;

/**
 * Created by ruffy on 29.01.2017.
 */
public class fxTest {
    Socket socket=null;
    Server server=null;
    Controller controller=null;

    @org.junit.Before
    public void setUp() throws Exception {
        socket=null;
        server = new Server(1,socket);
        controller = new Controller();
    }

    @org.junit.Test
    public void Test_runClient() throws InterruptedException {
        String a="started";
        final String[] b = new String[1];
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                new JFXPanel();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new Client().start(new Stage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                b[0] ="started";
            }
        });
        thread.start();
        Thread.sleep(1000);
        assertTrue(a== b[0]);
    }

    @org.junit.Test
    public void Test_runClientRepeat() throws InterruptedException {
        for (int i = 0; i < 2; i++) {
            Test_runClient();
        }
    }

}

