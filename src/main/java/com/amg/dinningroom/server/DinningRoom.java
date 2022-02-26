package com.amg.dinningroom.server;

import com.amg.dinningroom.MainController;
import com.amg.dinningroom.models.ChopStick;
import javafx.application.Platform;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public
class DinningRoom implements Runnable {
    public static int SERVER_PORT = 8087;
    public volatile boolean started = false;
    private ServerSocket serverSocket;
    public MainController mainController;

    LinkedList<PhilosopherHandler> philosopherHandlers = new LinkedList<>();
    com.amg.dinningroom.models.ChopStick[] chopSticks;


    public DinningRoom(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void run() {
        try {
            System.out.println("Dinning Room ");

            serverSocket = new ServerSocket(SERVER_PORT);

            // SERVER_PORT = serverSocket.getLocalPort();

            System.out.println("Server running on: " + serverSocket.getLocalSocketAddress());
            System.out.println("Port: " + SERVER_PORT);
            mainController.getConsole().appendText( "Server running on: " + serverSocket.getLocalSocketAddress()+"\n");
            mainController.getConsole().appendText( "Port: " + SERVER_PORT+"\n");

            while (!serverSocket.isClosed()) {

                Socket socket = serverSocket.accept();


                //create a new connection
                PhilosopherHandler philosopherHandler = new PhilosopherHandler(philosopherHandlers.size(), this, socket);
                philosopherHandler.start();
                System.out.println("client connected as socket " + socket + " in ClientHandler " + philosopherHandler.getName());
                mainController.getConsole().appendText("" + "client connected as socket " + socket + " in ClientHandler " + philosopherHandler.getName()+"\n");
                philosopherHandlers.add(philosopherHandler);
            }

        } catch (Exception e) {

            System.out.println("Something went wrong while creating the serversocket");

        } finally {
            try {
                if (serverSocket != null & !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {

                System.out.println("Something went wrong while closing the serversocket");


            }
        }

    }

    public void updateHBoxes() {
        for (PhilosopherHandler x : philosopherHandlers) {
            x.requestHandler.updateHBOX();
        }

    }
    public void startchart() {
        Platform.runLater(() -> mainController.makeChart(this));

    }
    public void startTable() {
        Platform.runLater(() -> mainController.makeTable(this));

    }

    public LinkedList<PhilosopherHandler> getPhilosopherHandlers() {
        return philosopherHandlers;
    }

    public void setPhilosopherHandlers(LinkedList<PhilosopherHandler> philosopherHandlers) {
        this.philosopherHandlers = philosopherHandlers;
    }

    public ChopStick[] getChopSticks() {
        return chopSticks;
    }

    public void setChopSticks(ChopStick[] chopSticks) {
        this.chopSticks = chopSticks;
    }
}
