package sample;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by rafal on 25.01.2017.
 */

public class Server extends Thread{
    private static List<Socket> players = new ArrayList<Socket>();
    private static int PORT;
    private static String IP;
    private static Socket socket=null;
    private static ServerSocket serverSocket=null;
    private static int connectionNumber;
    private static int p2_counter=0;
    private static int p1_counter=0;
    private static boolean p1_moved=false;
    private static boolean p2_moved=false;
    private static PrintWriter out1 = null;
    private static PrintWriter out2 = null;

    public Server(int connectionNumber, Socket socket){
        this.connectionNumber = connectionNumber;
        this.socket = socket;
    }

    public static void main(String args[]) throws IOException {
        parseXML();
        try{
            serverSocket = new ServerSocket(PORT);
        }catch(IOException e){
            System.out.println("Server socket error: ");
            e.printStackTrace();
            System.out.println();
        }

        System.out.println("Server launched");

        int connectionCounter = 1;
        String a = Integer.toString(connectionCounter);
        do {
            Socket client = serverSocket.accept();
            players.add(client);
            Server server = new Server(connectionCounter++, client);
            server.setName(a);
            server.start();
        } while (true);
    }

    public void setOut(){
        try {
            Socket socket1 = players.get(0);
            Socket socket2 = players.get(1);
            out1 = new PrintWriter(socket1.getOutputStream(), true);
            out2 = new PrintWriter(socket2.getOutputStream(), true);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Thread started for client number: " + connectionNumber);
        System.out.println(players.size());
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean quit = false;
        String input = null;
        while (!quit) {
            try {
                input = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                // ---------------------------------------------------------
                // HANDLE START BUTTON
                if (input.contains("player") && connectionNumber%2 == 1) out.println("player1");
                if (input.contains("player") && connectionNumber%2 == 0){
                    out.println("player2");
                    setOut();
                }

                // ---------------------------------------------------------
                // HANDLE RANDOM NUMBER
                if (input.contains("random_number")) {
                    String b=String.valueOf(getRandom());
                    out.println(b);
                }

                // ---------------------------------------------------------
                // HANDLE MOVE BUTTON
                if (input.contains("p2_pawnMoved")) {
                    if(input.contains("p2_pawnMoved0")){
                        System.out.println("not moved");
                    }
                    else if (input.contains("p2_pawnMoved")) {
                        p2_counter++;
                    }
                    p2_moved=true;
                    p1_moved=false;
                    out.println("p2_moved");
                }
                if (input.contains("p1_pawnMoved")) {
                    if(input.contains("p1_pawnMoved0")){
                        System.out.println("not moved");
                    }
                    else if (input.contains("p1_pawnMoved")) {
                        p1_counter++;
                    }
                    p2_moved=false;
                    p1_moved=true;
                    out.println("p1_moved");
                }

                // ---------------------------------------------------------
                // UPDATE INFORMATION
                if (input.contains("update1")) {
                    String a=Integer.toString(p2_counter);
                    if (p2_moved == true) {
                        if(p1_counter==8){out.println("p1_won");}
                        else if(p2_counter==8){out.println("p2_won");}
                        else out.println("can_move" + a);
                    }
                    else {out.println("none");}
                }
                if (input.contains("update2")) {
                    String a=Integer.toString(p1_counter);
                    if (p1_moved == true) {
                        if(p1_counter==8){out.println("p1_won");}
                        else if(p2_counter==8){out.println("p2_won");}
                        else out.println("can_move" +a);
                    }
                    else {out.println("none");}
                }
            }catch(NullPointerException e){
                System.out.println("cant handle update");
            }

            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getRandom(){
        int a;
        Random randomGenerator = new Random();
        a = randomGenerator.nextInt(10);
        if (a>=3) a=1;
        else a=0;
        return a;
    }

    public static void parseXML() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new File("serverConf.xml"));
            doc.getDocumentElement().normalize();
            IP = new String(doc.getDocumentElement().getElementsByTagName("id").item(0).getTextContent());
            PORT = new Integer(doc.getDocumentElement().getElementsByTagName("id").item(1).getTextContent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getPort(){
        return PORT;
    }

    public static String getIP(){
        return IP;
    }
}
