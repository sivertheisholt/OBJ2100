package com.eksamen.uis;

import com.eksamen.uis.layouts.TestLayout;
import javafx.scene.layout.GridPane;

public class ServerUi {
    private GridPane hovedPane;
    private TestLayout test;

    public ServerUi() {
        test = new TestLayout();
        hovedPane = new GridPane();
        hovedPane.getChildren().add(test.getPane());
    }

    public GridPane getHovedPane() {
        return hovedPane;
    }

    public TestLayout getTest() {
        return test;
    }
}
