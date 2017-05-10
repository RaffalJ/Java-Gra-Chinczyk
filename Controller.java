package sample;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    @FXML
    Circle p1_pawn1;
    @FXML
    Circle p2_pawn1;
    @FXML
    Label random_result_field;
    @FXML
    Label title_player;
    @FXML
    Label win_lose_title;
    @FXML
    Label player_move_title;
    @FXML
    Button bt_movePawn;
    @FXML
    Button bt_throwRandom;
    @FXML
    Button bt_start_game;

    // -----------------------------------------------------

    private int PORT = 9992;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private int which_player = 1;
    private int p1_pawn_x=152;
    private int p1_pawn_y=310;
    private int p1_pawn_counter=0;
    private int p2_pawn_x=852;
    private int p2_pawn_y=510;
    private int p2_pawn_counter=0;
    private int random_throw_result=1;
    private String reader;
    private String pawnMoved="";
    private boolean yourTurn;
    private boolean randomPressed=false;
    private boolean endGame=false;
    private boolean p1_winner=false;
    private boolean p2_winner=false;

    // ------------------------------------------------

    public String getMoveText(){
        return player_move_title.getText();
    }

    public String getPlayerText(){
        return title_player.getText();
    }

    // ------------------------------------------------

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        endGame=false;
        p1_winner=false;
        p2_winner=false;
        randomPressed=false;
        disableButtons();
    }

    public void movePawn(Event event) {
        Platform.runLater(new Runnable() {
            public void run() {
                if (which_player == 1){
                    setOpponentMoveText();
                    setTurn(false);
                    disableButtons();
                    if (random_throw_result==0){sendMsg(pawnMoved+"0");}
                    if (random_throw_result==1 ){
                        p1_movePawn();
                        sendMsg(pawnMoved);
                    }
                    randomPressed=false;
                }
                else if (which_player== 2) {
                    setOpponentMoveText();
                    setTurn(false);
                    disableButtons();
                    if (random_throw_result==0){sendMsg(pawnMoved+"0");}
                    if (random_throw_result==1){
                        p2_movePawn();
                        sendMsg(pawnMoved);
                    }
                    randomPressed=false;
                }
            }
        });
    }

    public void throw_random_number(Event event) {
        sendMsg("random_number");
        System.out.println("Random result" + reader);
        if(reader.contains("0")) {
            random_result_field.setText("0");
            random_throw_result=0;
        }
        if(reader.contains("1")){
            random_result_field.setText("1");
            random_throw_result=1;
        }
        bt_throwRandom.setDisable(true);
        randomPressed=true;
    }

    public void send_start_game(MouseEvent mouseEvent) {
        connectServer();
        sendMsg("player");
        System.out.println(reader);
        if (reader.contains("player1")) {
            pawnMoved = "p1_pawnMoved";
            which_player = 1;
            setPlayerText("Player 1 Red");
            disableButtons();
            setOpponentMoveText();
            setTurn(false);
            System.out.println(which_player);
        } else if (reader.contains("player2")) {
            pawnMoved = "p2_pawnMoved";
            which_player = 2;
            setPlayerText("Player 2 Blue");
            enableButtons();
            setYourMoveText();
            setTurn(true);
            System.out.println(which_player);
        }

        bt_start_game.setVisible(false);

        new Thread(new Runnable() {
            @Override public void run () {
                while (true){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(endGame==false) update();
                    javafx.application.Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (endGame == false) {
                                if (yourTurn == false) {
                                    disableButtons();
                                    setOpponentMoveText();
                                }
                                if (yourTurn == true) {
                                    bt_throwRandom.setDisable(false);
                                    if (randomPressed == false) {bt_movePawn.setDisable(true);}
                                    else {bt_movePawn.setDisable(false);}
                                    setYourMoveText();
                                }
                                if (randomPressed == true) bt_throwRandom.setDisable(true);
                                if (randomPressed == false) bt_throwRandom.setDisable(false);
                            }
                            if(endGame==true){
                                disableButtons();
                                setEndMoveText();
                                if(which_player==1){
                                    if(p1_winner==true) setWin();
                                    else if(p2_winner==true) setLose();
                                }
                                else if(which_player==2){
                                    if(p1_winner==true) setLose();
                                    else if(p2_winner==true) setWin();
                                }
                            }
                        }
                    });
                }
            }
        }).start();
    }

    public void update(){
        if ( which_player== 1){
            sendMsg("update1");
            if(reader.contains("can_move")){
                setTurn(true);
                update1_move();
            }
            if(p1_pawn_counter == 8){
                endGame=true;
                p1_winner=true;
            }
            if(reader.contains("p2_won")){
                endGame=true;
                p2_winner=true;
            }
        }
        if (which_player == 2){
            sendMsg("update2");
            if(reader.contains("can_move")){
                setTurn(true);
                update2_move();
            }
            if(reader.contains("p1_won")){
                endGame=true;
                p1_winner=true;
            }
            if (p2_pawn_counter==8){
                endGame=true;
                p2_winner=true;
            }
        }
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    void update1_move(){
        if(reader.contains("can_move1")) p2_pawn1.setTranslateX(752);
        if(reader.contains("can_move2")) p2_pawn1.setTranslateX(652);
        if(reader.contains("can_move3")) p2_pawn1.setTranslateX(552);
        if(reader.contains("can_move4")) p2_pawn1.setTranslateX(452);
        if(reader.contains("can_move5")) p2_pawn1.setTranslateX(352);
        if(reader.contains("can_move6")) p2_pawn1.setTranslateX(252);
        if(reader.contains("can_move7")) p2_pawn1.setTranslateX(152);
        if(reader.contains("can_move8")) p2_pawn1.setTranslateY(410);
    }
    void update2_move(){
        if(reader.contains("can_move1")) p1_pawn1.setTranslateX(252);
        if(reader.contains("can_move2")) p1_pawn1.setTranslateX(352);
        if(reader.contains("can_move3")) p1_pawn1.setTranslateX(452);
        if(reader.contains("can_move4")) p1_pawn1.setTranslateX(552);
        if(reader.contains("can_move5")) p1_pawn1.setTranslateX(652);
        if(reader.contains("can_move6")) p1_pawn1.setTranslateX(752);
        if(reader.contains("can_move7")) p1_pawn1.setTranslateX(852);
        if(reader.contains("can_move8")) p1_pawn1.setTranslateY(452);
    }

    void p1_movePawn(){
        if (p1_pawn_counter <7) {
            p1_pawn1.setTranslateX(p1_pawn_x+=(100*random_throw_result));
            p1_pawn_counter+=random_throw_result;
        }
        else if (p1_pawn_counter>=7 && p1_pawn_counter <9) {
            p1_pawn1.setTranslateY(p1_pawn_y+=100*random_throw_result);
            p1_pawn_counter+=random_throw_result;
        }
    }

    void p2_movePawn(){
        if (p2_pawn_counter <7) {
            p2_pawn1.setTranslateX(p2_pawn_x-=100*random_throw_result);
            p2_pawn_counter+=random_throw_result;
        }
        else if (p2_pawn_counter>=7 && p2_pawn_counter <9) {
            p2_pawn1.setTranslateY(p2_pawn_y-=100*random_throw_result);
            p2_pawn_counter+=random_throw_result;
        }
    }

    public void setWin(){
        win_lose_title.setText("Winner !");
    }

    public void setLose(){
        win_lose_title.setText("Loser !");
    }

    public void setYourMoveText(){
        player_move_title.setText("Your move");
    }

    public void setOpponentMoveText(){
        player_move_title.setText("Opponent move");
    }

    public void setEndMoveText() { player_move_title.setText("");}

    public void setPlayerText(String a){ title_player.setText(a);}

    public void setTurn(boolean a){ yourTurn = a;}

    public void disableButtons(){
        bt_movePawn.setDisable(true);
        bt_throwRandom.setDisable(true);
    }

    public void enableButtons(){
        bt_movePawn.setDisable(false);
        bt_throwRandom.setDisable(false);
    }

    public void sendMsg(String a){
        out.println(a);
        serverInput();
    }

    public void serverInput(){
        try {
            reader = in.readLine();
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println(reader);
    }

    public void connectServer() {
        try {
            socket = new Socket(InetAddress.getLocalHost(), PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
