package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

import sample.Controller;

public class Client extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Chinczyk by Rafal Jaworowski");
        primaryStage.setScene(new Scene(root,1100,700));
        primaryStage.show();
    }

    public static void startApp(String[] args){
        launch(args);
    }

    public static void main(String[] args) {startApp(args);}
}
