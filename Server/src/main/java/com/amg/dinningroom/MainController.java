package com.amg.dinningroom;

import com.amg.dinningroom.models.ChopStick;
import com.amg.dinningroom.server.DinningRoom;
import com.amg.dinningroom.server.PhilosopherHandler;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.LinkedList;

public class MainController {
    private double orgSceneX, orgSceneY;
    private double orgTranslateX, orgTranslateY;
    @FXML
    private TextArea console;
    @FXML
    private PieChart pieChart;
    @FXML
    private Pane pane;

    @FXML
    private VBox pTable;
    Bounds paneBounds;

    PhilosopherNode[] philosopherNodes;
    ChopStickNode[] chopStickNodes;


//    @FXML
//    void onHelloButtonClick(ActionEvent event) {
//        PhilosopherHandler p1 = new PhilosopherHandler();
//        System.out.println(pane.getLayoutBounds().getCenterX());
//        LinkedList<PhilosopherHandler> x = new LinkedList<>();
//        x.add(new PhilosopherHandler(1, null, null));
//        x.add(new PhilosopherHandler(2, null, null));
//        x.add(new PhilosopherHandler(3, null, null));
//        x.add(new PhilosopherHandler(4, null, null));
//        x.add(new PhilosopherHandler(5, null, null));
//        ChopStick[] chopSticks = new ChopStick[x.size()];
//        for (int i = 0; i < x.size(); i++) {
//            chopSticks[i] = new ChopStick(i);
//        }
//        chopStickNodes = new ChopStickNode[x.size()];
//        philosopherNodes = new PhilosopherNode[x.size()];
//        makePhilosophers(x);
//        makeChopsticks(chopSticks);
//
//
//    }

    @FXML
    void initialize() {

        paneBounds = pane.localToScene(pane.getLayoutBounds());

        ObservableList<PieChart.Data> dataObservableList = FXCollections.observableArrayList();
        pieChart.setData(dataObservableList);
        DinningRoom dinningRoom = new DinningRoom(this);
        new Thread(dinningRoom).start();

//
        Circle c = new Circle(paneBounds.getCenterX(), paneBounds.getCenterY(), 50);
        Node n;
     //   pane.getChildren().add(c);


    }

    public void makeChart(DinningRoom dinningRoom) {
        ObservableList<PieChart.Data> p = pieChart.getData();
        for (PhilosopherHandler philosopherHandler : dinningRoom.getPhilosopherHandlers()) {
            p.add(philosopherHandler.getPhilosopher().getpId(), new PieChart.Data(String.valueOf(philosopherHandler.getPhilosopher().getpId()), philosopherHandler.getEatingTime().getElapsedTime() / 1000.0));
        }
    }

    public void makeTable(DinningRoom dinningRoom) {
        ObservableList<PieChart.Data> p = pieChart.getData();
if(dinningRoom.started) {
    Timer timer = new Timer(1000, Timer.DURATION_INFINITY) {
        @Override
        protected void onTick() {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {

                    for (int i = 0; i < dinningRoom.getPhilosopherHandlers().size(); i++) {
                        PhilosopherHandler philosopherHandler = dinningRoom.getPhilosopherHandlers().get(i);
                        PieChart.Data data = p.get(i);
                        data.setPieValue(philosopherHandler.getEatingTime().getElapsedTime() / 1000.0);

                    }

                }
            });

        }

        @Override
        protected void onFinish() {

        }
    };
    timer.start();
}
        pane.getChildren().clear();
        LinkedList<PhilosopherHandler> philosopherHandlers = dinningRoom.getPhilosopherHandlers();
        ChopStick[] chopSticks = dinningRoom.getChopSticks();
        chopStickNodes = new ChopStickNode[philosopherHandlers.size()];
        philosopherNodes = new PhilosopherNode[philosopherHandlers.size()];
        makePhilosophers(philosopherHandlers);
        if(chopSticks!=null){
        makeChopsticks(chopSticks);}

    }

    public void makePhilosophers(LinkedList<PhilosopherHandler> philosopherHandlers) {

        int circleRadius = 35;
        double radius = (2 * circleRadius * philosopherHandlers.size() / 3.14) + 50;

        double paneX = pane.getLayoutBounds().getCenterX();
        double paneY = pane.getLayoutBounds().getCenterY();
        Point2D pivot = new Point2D(paneX, paneY);

        for (int i = 0; i < philosopherHandlers.size(); i++) {
            Point2D circleCenter = new Point2D(paneX + radius, paneY);
            double angle = 0;

            angle = 360.0 / (philosopherHandlers.size()) * i;
            System.out.println(angle);
            circleCenter = UtilitiesPoint2D.rotate(circleCenter, pivot, angle);
            PhilosopherNode philosopherNode = createNode(philosopherHandlers.get(i), circleCenter.getX(), circleCenter.getY(), Color.DARKGRAY, circleRadius);
            philosopherNode.updateStatus();
            if (philosopherNodes[i] != null) {
                philosopherNodes[i].setVisible(false);
                pane.getChildren().remove(philosopherNodes[i]);
            }
            philosopherNodes[i] = philosopherNode;

            pane.getChildren().add(philosopherNode);


        }
    }

    public void makeChopsticks(ChopStick[] chopSticks) {

        int circleRadius = 15;
        double radius = (2 * circleRadius * chopStickNodes.length / 3.14+30);

        double paneX = pane.getLayoutBounds().getCenterX()+circleRadius;
        double paneY = pane.getLayoutBounds().getCenterY()+circleRadius;
        Point2D pivot = new Point2D(paneX, paneY);

        for (int i = 0; i < chopSticks.length; i++) {
            Point2D circleCenter = new Point2D(paneX + radius, paneY);
            double angle = 0;
            if (chopSticks[i].isInUse()) {
                int rightP = i;
                int leftP = i + 1;
                if (leftP == chopSticks.length) leftP = 0;
                if (chopSticks[i].getHandler() == rightP) {
                    angle = 360.0 / chopStickNodes.length * rightP + 5;
                } else {
                    angle = 360.0 / chopStickNodes.length * leftP - 5;
                }

            } else {
                angle = 360.0 / chopStickNodes.length * i + (360.0 / chopSticks.length / 2.0);
            }
            System.out.println(angle);
            circleCenter = UtilitiesPoint2D.rotate(circleCenter, pivot, angle);


            ChopStickNode chopStickNode = createCh(chopSticks[i], circleCenter.getX(), circleCenter.getY(), Color.GRAY, circleRadius);
            chopStickNode.updateStatus();
            if (chopStickNodes[i] != null) {
                chopStickNodes[i].setVisible(false);
                pane.getChildren().remove(chopStickNodes[i]);
            }
            chopStickNodes[i] = chopStickNode;
            pane.getChildren().add(chopStickNode);


        }
    }

    public TextArea getConsole() {
        return console;
    }

    public VBox getpTable() {
        return pTable;
    }


    private void connectNodes(PhilosopherNode node1, PhilosopherNode node2, String edgeText) {

        Line edgeLine = new Line(node1.getCenterX(), node1.getCenterY(), node2.getCenterX(), node2.getCenterY());
        Label edgeLabel = new Label(edgeText);

        node1.addNeighbor(node2);
        node2.addNeighbor(node1);

        node1.addEdge(edgeLine, edgeLabel);
        node2.addEdge(edgeLine, edgeLabel);

        pane.getChildren().addAll(edgeLine, edgeLabel);

    }

    private PhilosopherNode createNode(PhilosopherHandler philosopherHandler, double xPos, double yPos, Color color, double radius) {
        PhilosopherNode node = new PhilosopherNode(philosopherHandler, xPos, yPos, color, radius);
        node.setOnMousePressed(circleOnMousePressedEventHandler);
        node.setOnMouseDragged(circleOnMouseDraggedEventHandler);

        return node;
    }

    private ChopStickNode createCh(ChopStick chopStick, double xPos, double yPos, Color color, double radius) {
        ChopStickNode node = new ChopStickNode(chopStick, xPos, yPos, color, radius);
        // node.setOnMousePressed(circleOnMousePressedEventHandler);
        //  node.setOnMouseDragged(circleOnMouseDraggedEventHandler);

        return node;
    }

    EventHandler<MouseEvent> circleOnMousePressedEventHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            orgSceneX = t.getSceneX();
            orgSceneY = t.getSceneY();

            PhilosopherNode node = (PhilosopherNode) t.getSource();

            orgTranslateX = node.getTranslateX();
            orgTranslateY = node.getTranslateY();
        }
    };

    EventHandler<MouseEvent> circleOnMouseDraggedEventHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            double offsetX = t.getSceneX() - orgSceneX;
            double offsetY = t.getSceneY() - orgSceneY;
            double newTranslateX = orgTranslateX + offsetX;
            double newTranslateY = orgTranslateY + offsetY;

            PhilosopherNode node = (PhilosopherNode) t.getSource();

            node.setTranslateX(newTranslateX);
            node.setTranslateY(newTranslateY);

            updateLocations(node);
        }
    };

    private void updateLocations(PhilosopherNode node) {

        ArrayList<PhilosopherNode> connectedNodes = node.getConnectedNodes();

        ArrayList<Line> edgesList = node.getEdges();

        for (int i = 0; i < connectedNodes.size(); i++) {

            PhilosopherNode neighbor = connectedNodes.get(i);
            Line l = edgesList.get(i);

            l.setStartX(node.getCenterX());

            l.setStartY(node.getCenterY());

            l.setEndX(neighbor.getCenterX());

            l.setEndY(neighbor.getCenterY());
        }
    }

}
