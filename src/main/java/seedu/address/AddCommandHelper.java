package seedu.address;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


public class AddCommandHelper {

    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField userInput;
    private Button sendButton;
    private Scene scene;

    private status status;


    public String getResponse(String text) {
        return text;
    }





    private enum status {GET_NAME, GET_NUMBER, GET_EMAIL, GET_ADDRESS, GET_TAG}



}

