package com.amg.dinningphilosophers;

import com.amg.dinningphilosophers.Client.ServerHandler;
import com.amg.dinningphilosophers.models.Philosopher;
import com.amg.dinningphilosophers.request.Request;
import com.amg.dinningphilosophers.request.RequestType;
import com.amg.dinningphilosophers.response.Response;
import com.amg.dinningphilosophers.response.ResponseType;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainController {


    @FXML
    private Label pId;



    @FXML
    private Button startButton;

    @FXML
    private TextField time_input;
    @FXML
    private Button leftButton;
    @FXML
    private Button rightButton;
    @FXML
    private TextArea console;
    boolean waiting = true;

    @FXML
    void onStartButtonClick(ActionEvent event) {

        if (startButton.getText().equals("Hungry")) {
            if(time_input.getText().equals("")){time_input.setText("60");}
            Response response = ServerHandler.transmitter(new Request(RequestType.SAY_HUNGRY, time_input.getText()));
            if (response.getType() == ResponseType.EAT) {
                System.out.println("Eating");
            console.appendText("You are eating!\n");
                ScheduledExecutorService ex= Executors.newSingleThreadScheduledExecutor();
                startButton.setText("Eating");
                startButton.setDisable(true);

                TimerTask timerTask=new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {

                                startButton.setDisable(false);
                                startButton.setText("Hungry");
                            }
                        });


                    }
                };
                ex.schedule(timerTask,Integer.parseInt(time_input.getText()), TimeUnit.MILLISECONDS);
                
            }


        } else {
            leftButton.setDisable(false);
            rightButton.setDisable(false);
            Response response = ServerHandler.transmitter(new Request(RequestType.START, String.valueOf(Philosopher.id)));
            if (response.getType() == ResponseType.STARTED) {
                Platform.runLater(() -> {
                    console.appendText("started\n");
                });

                waiting = false;
            }
            startButton.setText("Hungry");
        }

    }


    @FXML
    void onLeftButtonClick(ActionEvent event) {
        Response response = ServerHandler.transmitter(new Request(RequestType.WANT_LEFT_CHOPSTICK, String.valueOf(Philosopher.id)));
        if (response.getType() == ResponseType.LEFT_CHOPSTICK_IN_USE) {
            Platform.runLater(()->{
                console.appendText("left chop stick is busy .you are waiting for it.\n");
            });

        } else if (response.getType() == ResponseType.FIRST_RIGHT_CHOPSTICK) {
            Platform.runLater(()->{
                console.appendText("please first take the right chopstick.\n");
            });

        } else if (response.getType() == ResponseType.LEFT_PHILOSOPHER_JUSTICE) {
            Platform.runLater(()->{
                console.appendText("left neighbour wants it more.\n");
            });

        }
        else if(response.getType()==ResponseType.GIVE_LEFT_CHOPSTICK){
            Platform.runLater(()->{
                console.appendText("you have left chopstick.\n");
            });

        }

    }

    @FXML
    void onRightButtonClick(ActionEvent event) {
        Response response = ServerHandler.transmitter(new Request(RequestType.WANT_RIGHT_CHOPSTICK, String.valueOf(Philosopher.id)));
        if (response.getType() == ResponseType.RIGHT_CHOPSTICK_IN_USE) {
            Platform.runLater(()->{
                console.appendText("right chop stick is busy .you are waiting for it.\n");
            });

        } else if (response.getType() == ResponseType.FIRST_LEFT_CHOPSTICK) {
            Platform.runLater(()->{
                console.appendText("please first take the left chopstick.\n");
            });

        } else if (response.getType() == ResponseType.RIGHT_PHILOSOPHER_JUSTICE) {
            Platform.runLater(()->{
                console.appendText("your right need it more.\n");
            });

        }
        else if(response.getType()==ResponseType.GIVE_RIGHT_CHOPSTICK){
            Platform.runLater(()->{
                console.appendText("you have right chopstick.\n");
            });

        }

    }

    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        ServerHandler.connectToServer();
        Response response = ServerHandler.transmitter(new Request(RequestType.REGISTER, "hi"));
        if (response.getType() == ResponseType.REGISTERED) {
            Philosopher.id = Integer.parseInt(response.getData());
            pId.setText(response.getData());
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (waiting) {
                    Response response1 = ServerHandler.transmitter(new Request(RequestType.ALIVE_CONNECTION, "hi"));

                    if (response1.getType() == ResponseType.STARTED) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                startButton.setText("Hungry");
                                leftButton.setDisable(false);
                                rightButton.setDisable(false);
                                console.appendText("started.\n");
                            }
                        });
                        System.out.println("started");
                        waiting = false;

                    }
                }
            }
        }).start();

    }


}


