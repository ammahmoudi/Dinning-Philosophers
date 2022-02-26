package com.amg.dinningroom.server;

import com.amg.dinningroom.JSon.JSonController;
import com.amg.dinningroom.Timer;
import com.amg.dinningroom.models.Status;
import com.amg.dinningroom.request.Request;
import com.amg.dinningroom.response.Response;
import com.amg.dinningroom.response.ResponseType;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.PrintWriter;
import java.util.Random;

public
class RequestHandler {

    PhilosopherHandler philosopherHandler;
    DinningRoom dinningRoom;
    HBox hBox;
    com.amg.dinningroom.models.ChopStick l;
    int rightP;
    com.amg.dinningroom.models.ChopStick r;
    int leftP;

    public RequestHandler(PhilosopherHandler philosopherHandler, DinningRoom dinningRoom) {

        this.philosopherHandler = philosopherHandler;
        this.dinningRoom = dinningRoom;

    }

    public void executeRequest(Request request) {
        Response response1;
        if (dinningRoom.started) {

            l = dinningRoom.chopSticks[philosopherHandler.getPhilosopher().getLeftC()];

            r = dinningRoom.chopSticks[philosopherHandler.getPhilosopher().getRightC()];

            rightP = philosopherHandler.getPhilosopher().getpId() - 1;
            if (rightP < 0) rightP += dinningRoom.philosopherHandlers.size();

            leftP = philosopherHandler.getPhilosopher().getpId() + 1;
            if (leftP == dinningRoom.philosopherHandlers.size()) leftP = 0;
        }
        switch (request.getType()) {

            case ALIVE_CONNECTION:
                if (dinningRoom.started) {
                    sendResponse(philosopherHandler.getPrintWriter(), new Response(ResponseType.STARTED, "yes you Are alive :)"));
                    dinningRoom.mainController.getConsole().appendText("Sending start message to Client " + philosopherHandler.getPhilosopher().getpId() + "\n");
                } else
                    sendResponse(philosopherHandler.getPrintWriter(), new Response(ResponseType.ALIVE_CONNECTION, "yes you Are alive :)"));
                break;
            case REGISTER:

                sendResponse(philosopherHandler.getPrintWriter(), new Response(ResponseType.REGISTERED, String.valueOf(philosopherHandler.getPhilosopher().getpId())));
                philosopherHandler.getPhilosopher().setStatus(Status.WAITING_FOR_START);

                System.out.println("regiseterd:" + philosopherHandler.getPhilosopher().getpId());
                dinningRoom.mainController.getConsole().appendText("Client " + philosopherHandler.getPhilosopher().getpId() + " registered.\n");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        hBox = new HBox();
                        dinningRoom.mainController.getpTable().getChildren().add(hBox);

                    }
                });

                dinningRoom.updateHBoxes();
                dinningRoom.startTable();
                break;
            case START:
                dinningRoom.chopSticks = new com.amg.dinningroom.models.ChopStick[dinningRoom.philosopherHandlers.size()];
                for (int i = 0; i < dinningRoom.philosopherHandlers.size(); i++) {
                    dinningRoom.chopSticks[i] = new com.amg.dinningroom.models.ChopStick(i);
                }
                for (int i = 0; i < dinningRoom.philosopherHandlers.size(); i++) {
                    dinningRoom.philosopherHandlers.get(i).getPhilosopher().setLeftC(i);
                    int rightIndex = (i - 1) % dinningRoom.philosopherHandlers.size();
                    if (rightIndex < 0) rightIndex += dinningRoom.philosopherHandlers.size();
                    dinningRoom.philosopherHandlers.get(i).getPhilosopher().setRightC(rightIndex);
                    dinningRoom.philosopherHandlers.get(i).getPhilosopher().setStatus(Status.THINKING);
                    dinningRoom.philosopherHandlers.get(i).timer.start();

                }
                Random random = new Random(System.currentTimeMillis());
                dinningRoom.philosopherHandlers.get(random.nextInt(dinningRoom.philosopherHandlers.size())).getPhilosopher().setLeftHand(true);
                Response response = new Response(ResponseType.STARTED, JSonController.objectToStringMapper(philosopherHandler.getPhilosopher()));
                dinningRoom.started = true;
                sendResponse(philosopherHandler.getPrintWriter(), response);
                dinningRoom.mainController.getConsole().appendText("Client " + philosopherHandler.getPhilosopher().getpId() + " has started the game.\n");
                dinningRoom.updateHBoxes();
                dinningRoom.startTable();
                dinningRoom.startchart();
                break;
            case WANT_LEFT_CHOPSTICK:

                if (!l.inUse) {
                    if (philosopherHandler.getPhilosopher().isLeftHand()) {

                        if (l.isLeftWants() && dinningRoom.philosopherHandlers.get(leftP).timer.getElapsedTime() > philosopherHandler.timer.getElapsedTime()) {
                            l.setRightWants(true);
                            response1 = new Response(ResponseType.LEFT_PHILOSOPHER_JUSTICE, "left need it more than you.");
                            dinningRoom.mainController.getConsole().appendText("Client "+philosopherHandler.getPhilosopher().getpId()+"requested the left chop stick("+philosopherHandler.getPhilosopher().getLeftC()+") but the left neighbour need it more.\n");
                        } else {
                            l.setRightWants(false);
                            l.setHandler(philosopherHandler.getPhilosopher().getpId());
                            l.setInUse(true);
                            response1 = new Response(ResponseType.GIVE_LEFT_CHOPSTICK, "you have it");
                            dinningRoom.mainController.getConsole().appendText("Client "+philosopherHandler.getPhilosopher().getpId()+" has chop stick("+philosopherHandler.getPhilosopher().getLeftC()+") in his left hand.\n");

                            Timer time_out = new Timer(1000, 20000) {
                                @Override
                                protected void onTick() {

                                }

                                @Override
                                protected void onFinish() {
                                    l.setHandler(-1);
                                    l.setInUse(false);
                                    dinningRoom.updateHBoxes();
                                    dinningRoom.startTable();
                                    dinningRoom.mainController.getConsole().appendText("Timeout: Client "+philosopherHandler.getPhilosopher().getpId()+" lost chop stick("+philosopherHandler.getPhilosopher().getLeftC()+") from his left hand.\n");
                                }
                            };
                            time_out.start();
                        }

                    } else {
                        if (r.handler == philosopherHandler.getPhilosopher().getpId()) {
                            if (l.isLeftWants() && dinningRoom.philosopherHandlers.get(leftP).timer.getElapsedTime() > philosopherHandler.timer.getElapsedTime()) {
                                l.setRightWants(true);
                                response1 = new Response(ResponseType.LEFT_PHILOSOPHER_JUSTICE, "left need it more than you.");
                                dinningRoom.mainController.getConsole().appendText("Client "+philosopherHandler.getPhilosopher().getpId()+"requested the left chop stick("+philosopherHandler.getPhilosopher().getLeftC()+") but the left neighbour need it more.\n");

                            } else {
                                l.setRightWants(false);
                                l.setHandler(philosopherHandler.getPhilosopher().getpId());
                                l.setInUse(true);
                                response1 = new Response(ResponseType.GIVE_LEFT_CHOPSTICK, "you have it");
                                dinningRoom.mainController.getConsole().appendText("Client "+philosopherHandler.getPhilosopher().getpId()+" has chop stick("+philosopherHandler.getPhilosopher().getLeftC()+") in his left hand.\n");
                                Timer time_out = new Timer(1000, 20000) {
                                    @Override
                                    protected void onTick() {

                                    }

                                    @Override
                                    protected void onFinish() {
                                        l.setHandler(-1);
                                        l.setInUse(false);
                                        dinningRoom.updateHBoxes();
                                        dinningRoom.startTable();
                                        dinningRoom.mainController.getConsole().appendText("Timeout: Client "+philosopherHandler.getPhilosopher().getpId()+" lost chop stick("+philosopherHandler.getPhilosopher().getLeftC()+") from his left hand.\n");

                                    }
                                };
                                time_out.start();
                            }

                        } else {
                            response1 = new Response(ResponseType.FIRST_RIGHT_CHOPSTICK, "right chopstick first");
                            dinningRoom.mainController.getConsole().appendText("Client "+philosopherHandler.getPhilosopher().getpId()+" requested left chop stick("+philosopherHandler.getPhilosopher().getLeftC()+") but it need to take right chopstick first.\n");

                        }

                    }
                } else {
                    l.setRightWants(true);
                    response1 = new Response(ResponseType.LEFT_CHOPSTICK_IN_USE, "in use");
                    dinningRoom.mainController.getConsole().appendText("Client "+philosopherHandler.getPhilosopher().getpId()+" requested left chop stick("+philosopherHandler.getPhilosopher().getLeftC()+") but it is in use.(going to waiting for it)\n");


                }

                sendResponse(philosopherHandler.getPrintWriter(), response1);

                dinningRoom.updateHBoxes();
                dinningRoom.startTable();
                break;
            case WANT_RIGHT_CHOPSTICK:
                if (!r.inUse) {
                    if (!philosopherHandler.getPhilosopher().isLeftHand()) {

                        if (r.isRightWants() && dinningRoom.philosopherHandlers.get(rightP).timer.getElapsedTime() > philosopherHandler.timer.getElapsedTime()) {
                            r.setLeftWants(true);
                            response1 = new Response(ResponseType.RIGHT_PHILOSOPHER_JUSTICE, "right need it more than you.");
                            dinningRoom.mainController.getConsole().appendText("Client "+philosopherHandler.getPhilosopher().getpId()+"requested the right chop stick("+philosopherHandler.getPhilosopher().getRightC()+") but the right neighbour need it more.\n");

                        } else {
                            r.setLeftWants(false);
                            r.setHandler(philosopherHandler.getPhilosopher().getpId());
                            r.setInUse(true);
                            response1 = new Response(ResponseType.GIVE_RIGHT_CHOPSTICK, "you have it");
                            dinningRoom.mainController.getConsole().appendText("Client "+philosopherHandler.getPhilosopher().getpId()+" has chop stick("+philosopherHandler.getPhilosopher().getRightC()+") in his right hand.\n");

                            Timer time_out = new Timer(1000, 20000) {
                                @Override
                                protected void onTick() {

                                }

                                @Override
                                protected void onFinish() {
                                    r.setHandler(-1);
                                    r.setInUse(false);
                                    dinningRoom.updateHBoxes();
                                    dinningRoom.startTable();
                                    dinningRoom.mainController.getConsole().appendText("Timeout: Client "+philosopherHandler.getPhilosopher().getpId()+" lost chop stick("+philosopherHandler.getPhilosopher().getRightC()+") from his rgiht hand.\n");

                                }
                            };
                            time_out.start();
                        }

                    } else {

                        if (l.handler == philosopherHandler.getPhilosopher().getpId()) {
                            if (r.isRightWants() && dinningRoom.philosopherHandlers.get(rightP).timer.getElapsedTime() > philosopherHandler.timer.getElapsedTime()) {
                                r.setLeftWants(true);
                                response1 = new Response(ResponseType.RIGHT_PHILOSOPHER_JUSTICE, "right need it more than you.");
                            } else {
                                r.setLeftWants(false);
                                r.setHandler(philosopherHandler.getPhilosopher().getpId());
                                r.setInUse(true);
                                response1 = new Response(ResponseType.GIVE_RIGHT_CHOPSTICK, "you have it");
                                dinningRoom.mainController.getConsole().appendText("Client "+philosopherHandler.getPhilosopher().getpId()+" has chop stick("+philosopherHandler.getPhilosopher().getRightC()+") in his right hand.\n");

                                Timer time_out = new Timer(1000, 20000) {
                                    @Override
                                    protected void onTick() {

                                    }

                                    @Override
                                    protected void onFinish() {
                                        r.setHandler(-1);
                                        r.setInUse(false);
                                        dinningRoom.updateHBoxes();
                                        dinningRoom.startTable();
                                        dinningRoom.mainController.getConsole().appendText("Timeout: Client "+philosopherHandler.getPhilosopher().getpId()+" lost chop stick("+philosopherHandler.getPhilosopher().getRightC()+") from his rgiht hand.\n");

                                    }
                                };
                                time_out.start();
                            }

                        } else {
                            response1 = new Response(ResponseType.FIRST_LEFT_CHOPSTICK, "left chopstick first");
                            dinningRoom.mainController.getConsole().appendText("Client "+philosopherHandler.getPhilosopher().getpId()+" requested right chop stick("+philosopherHandler.getPhilosopher().getRightC()+") but it need to take left chopstick first.\n");


                        }

                    }
                } else {
                    r.setLeftWants(true);
                    response1 = new Response(ResponseType.RIGHT_CHOPSTICK_IN_USE, "in use");
                    dinningRoom.mainController.getConsole().appendText("Client "+philosopherHandler.getPhilosopher().getpId()+" requested right chop stick("+philosopherHandler.getPhilosopher().getRightC()+") but it is in use.(going to waiting for it)\n");

                }

                sendResponse(philosopherHandler.getPrintWriter(), response1);

                dinningRoom.updateHBoxes();
                dinningRoom.startTable();
                break;
            case SAY_HUNGRY:
                int duration = Integer.parseInt(request.getData());
                dinningRoom.updateHBoxes();
                if (philosopherHandler.startEating()) {
                    response1 = new Response(ResponseType.EAT, "yeah eat");
                    dinningRoom.mainController.getConsole().appendText("Client "+philosopherHandler.getPhilosopher().getpId()+"requested started eating for "+duration+" ms.\n");

                    sendResponse(philosopherHandler.getPrintWriter(), response1);
                    philosopherHandler.timer.pause();
                    Timer eatingTimer = new Timer(1000, duration) {
                        @Override
                        protected void onTick() {
                        }

                        @Override
                        protected void onFinish() {
                            philosopherHandler.timer.resume();
                            philosopherHandler.doneEating();
                            dinningRoom.updateHBoxes();
                            dinningRoom.startTable();
                            dinningRoom.mainController.getConsole().appendText("Client "+philosopherHandler.getPhilosopher().getpId()+" finished eating.\n");

                        }
                    };
                    eatingTimer.start();
                } else {
                    response1 = new Response(ResponseType.WAIT, "you cant eat");
                    dinningRoom.mainController.getConsole().appendText("Client "+philosopherHandler.getPhilosopher().getpId()+"has been refused for eating .\n");

                    sendResponse(philosopherHandler.getPrintWriter(), response1);
                }
                dinningRoom.updateHBoxes();
                dinningRoom.startTable();
                break;
        }

    }

    public void updateHBOX() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                hBox.getChildren().clear();
                hBox.setSpacing(8);
                hBox.setAlignment(Pos.CENTER_LEFT);
                if (philosopherHandler.getPhilosopher().isLeftHand()) {
                    hBox.getChildren().add(new Label("(L)"));
                }
                hBox.getChildren().add(new Label("Id:\n" + philosopherHandler.getPhilosopher().getpId()));
                hBox.getChildren().add(new Label("Status:\n" + philosopherHandler.getPhilosopher().getStatus()));
                if (philosopherHandler.getPhilosopher().getLeftC() != null) {
                    hBox.getChildren().add(new Label("Left:\n "+philosopherHandler.getPhilosopher().getLeftC() +"("+ dinningRoom.chopSticks[philosopherHandler.getPhilosopher().getLeftC()].isInUse()+")"));
                    hBox.getChildren().add(new Label("Right:\n "+philosopherHandler.getPhilosopher().getRightC() +"("+ dinningRoom.chopSticks[philosopherHandler.getPhilosopher().getRightC()].isInUse()+")"));

                    hBox.getChildren().add(new Label("Waited time:\n" + philosopherHandler.timer.getElapsedTime()));
                    hBox.getChildren().add(new Label("Right_n_want:\n" + dinningRoom.chopSticks[philosopherHandler.getPhilosopher().getRightC()].isLeftWants()));
                    hBox.getChildren().add(new Label("Left_n_want:\n" + dinningRoom.chopSticks[philosopherHandler.getPhilosopher().getLeftC()].isRightWants()));

                }


            }
        });

    }

    public void sendResponse(PrintWriter printWriter, Response response) {
        String data;
        data = JSonController.objectToStringMapper(response);
        printWriter.println(data);

    }

}
