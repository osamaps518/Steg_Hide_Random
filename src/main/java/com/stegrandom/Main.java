package com.stegrandom;

import com.stegrandom.Model.SteganographyImage;
import com.stegrandom.steganography.Steganography;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main extends Application {

    private File selectedFile;
    private TextArea messageInput;
    private Label decodeOutput;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Steganography App");

        // File selection for encoding/decoding
        Button fileButton = new Button("Select Image File");
        Label fileLabel = new Label("No file selected");
        fileButton.setOnAction(e -> selectFile(primaryStage, fileLabel));

        // Encoding section
        Label encodeLabel = new Label("Enter Secret Message:");
        messageInput = new TextArea();
        messageInput.setPromptText("Enter message to hide...");
        Button encodeButton = new Button("Encode Message");
        encodeButton.setOnAction(e -> encodeMessage());

        // Decoding section
        Button decodeButton = new Button("Decode Message");
        decodeOutput = new Label("Decoded Message will appear here...");
        decodeButton.setOnAction(e -> decodeMessage());

        // Layout
        VBox layout = new VBox(10, fileButton, fileLabel, encodeLabel, messageInput, encodeButton, decodeButton, decodeOutput);
        layout.setPadding(new Insets(15));
        Scene scene = new Scene(layout, 400, 500);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void selectFile(Stage primaryStage, Label fileLabel) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            fileLabel.setText("Selected File: " + selectedFile.getName());
        } else {
            fileLabel.setText("No file selected");
        }
    }

    private void encodeMessage() {
        if (selectedFile == null || messageInput.getText().isEmpty()) {
            showAlert("Please select an image and enter a message to encode.");
            return;
        }

        try {
            BufferedImage originalImage = ImageIO.read(selectedFile);
            SteganographyImage stegImage = new SteganographyImage(originalImage);
            BufferedImage modifiedImage = Steganography.hideMessage(stegImage, messageInput.getText());

            File outputFile = new File("steg_output.png");
            ImageIO.write(modifiedImage, "PNG", outputFile);

            showAlert("Message encoded! Saved as steg_output.png");

        } catch (IOException e) {
            showAlert("Error reading or writing the image.");
        } catch (Exception e) {
            showAlert("An error occurred: " + e.getMessage());
        }
    }

    private void decodeMessage() {
        if (selectedFile == null) {
            showAlert("Please select an image to decode.");
            return;
        }

        try {
            BufferedImage encodedImage = ImageIO.read(selectedFile);
            SteganographyImage stegImage = new SteganographyImage(encodedImage);

            // Estimate message length for extraction (adjust as needed)
            int estimatedMessageLength = 100 * 8;  // Approximate bits needed for a 100-character message
            String decodedMessage = Steganography.extractMessage(stegImage, estimatedMessageLength);

            decodeOutput.setText("Decoded Message: " + decodedMessage);

        } catch (IOException e) {
            showAlert("Error reading the image.");
        } catch (Exception e) {
            showAlert("An error occurred: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
