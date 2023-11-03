package com.amg.dinningroom;

import com.amg.dinningroom.server.PhilosopherHandler;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;

public class PhilosopherNode extends StackPane {


    private FontIcon statusIcon=new FontIcon("fltfmz-thinking-24");
    private Circle circle;
    private Label text;
    private Label idLabel;
    private PhilosopherHandler philosopherHandler;
    private ArrayList<PhilosopherNode> connectedNodesList = new ArrayList<>();
    private ArrayList<Line> edgesList = new ArrayList<>();
    private ArrayList<Label> edgesLabelList = new ArrayList<>();

    private double radius = 20.0;
public  void updateStatus(){
    switch (philosopherHandler.getPhilosopher().getStatus()){
        case THINKING -> {statusIcon.setIconLiteral("fltfmz-thinking-24") ;
        text.setText("Thinking");
        }
        case EATING ->{ statusIcon.setIconLiteral("fltfal-food-24");circle.setFill(Color.GREEN);
            text.setText("Eating");
        }
    }
    }
    public PhilosopherNode(PhilosopherHandler philosopherHandler, double xPos, double yPos, Color color, double radius) {
        VBox box=new VBox();
        this.philosopherHandler=philosopherHandler;
        this.radius=radius;
        circle = new Circle(radius, color);
        idLabel = new Label(String.valueOf(philosopherHandler.getPhilosopher().getpId()));
        text=new Label();
        idLabel.setFont(Font.font(15));
        idLabel.setTextFill(Color.WHITE);
        text.setFont(Font.font(10));
        text.setGraphic(statusIcon);

        text.setTextFill(Color.BLACK);
        statusIcon.setIconLiteral("fltfmz-shield-24");
        setLayoutX(xPos);
        setLayoutY(yPos);
box.getChildren().addAll(idLabel,text);
box.setAlignment(Pos.CENTER);
        getChildren().addAll(circle,box);
        layout();
    }

    public void addNeighbor(PhilosopherNode node) {
        connectedNodesList.add(node);
    }

    public void addEdge(Line edgeLine, Label edgeLabel) {
        edgesList.add(edgeLine);
        edgesLabelList.add(edgeLabel);

        // If user move the node we should translate the edge labels as well
        // one way of doing that is by make a custom binding to the layoutXProperty as well
        // as to layoutYProperty. We will listen for changes to the currentNode translate properties
        // and for changes of our neighbor.


        edgeLabel.layoutXProperty().bind(new DoubleBinding() {
            {
                bind(translateXProperty());
                bind(connectedNodesList.get(connectedNodesList.size() - 1).translateXProperty());
            }

            @Override
            protected double computeValue() {

                // We find the center of the line to translate the text
                double width = edgeLine.getEndX() - edgeLine.getStartX();

                return edgeLine.getStartX() + width / 2.0;
            }
        });

        edgeLabel.layoutYProperty().bind(new DoubleBinding() {
            {
                bind(translateYProperty());
                bind(connectedNodesList.get(connectedNodesList.size() - 1).translateYProperty());
            }

            @Override
            protected double computeValue() {

                double width = edgeLine.getEndY() - edgeLine.getStartY();
                return edgeLine.getStartY() + width / 2.0;
            }
        });

    }

    public ArrayList<PhilosopherNode> getConnectedNodes() {
        return connectedNodesList;
    }

    public ArrayList<Line> getEdges() {
        return edgesList;
    }

    public double getX() {
        return getLayoutX() + getTranslateX();
    }

    public double getY() {
        return getLayoutY() + getTranslateY();
    }

    public double getCenterX() {
        return getX() + radius;
    }

    public double getCenterY() {
        return getY() + radius;
    }

}
