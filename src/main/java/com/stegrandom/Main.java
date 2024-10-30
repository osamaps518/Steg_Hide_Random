package com.stegrandom;

import com.stegrandom.Model.SteganographyImage;
import com.stegrandom.steganography.Steganography;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class Main extends Application {

    private File selectedFile;
    private TextArea messageInput;
    private Label decodeOutput;
    private ImageView originalImageView;
    private ImageView encodedImageView;
    private boolean originalImageVisible = true; // Track original image visibility
    private boolean encodedImageVisible = true;  // Track encoded image visibility

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

        // Labels for the images
        Label originalImageLabel = new Label("Original Image:");
        Label encodedImageLabel = new Label("Encoded Image:");

        // Image views for displaying selected and encoded images
        originalImageView = new ImageView();
        encodedImageView = new ImageView();
        originalImageView.setFitWidth(200);
        originalImageView.setPreserveRatio(true);
        encodedImageView.setFitWidth(200);
        encodedImageView.setPreserveRatio(true);

        // Toggle visibility buttons for each image
        Button toggleOriginalImageButton = new Button("Toggle Original Image");
        toggleOriginalImageButton.setOnAction(e -> toggleOriginalImageVisibility());

        Button toggleEncodedImageButton = new Button("Toggle Encoded Image");
        toggleEncodedImageButton.setOnAction(e -> toggleEncodedImageVisibility());

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
        VBox originalImageBox = new VBox(5, originalImageLabel, originalImageView, toggleOriginalImageButton);
        VBox encodedImageBox = new VBox(5, encodedImageLabel, encodedImageView, toggleEncodedImageButton);
        HBox imagesBox = new HBox(10, originalImageBox, encodedImageBox);
        VBox layout = new VBox(10, fileButton, fileLabel, imagesBox, encodeLabel, messageInput, encodeButton, decodeButton, decodeOutput);
        layout.setPadding(new Insets(15));
        Scene scene = new Scene(layout, 450, 700);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void selectFile(Stage primaryStage, Label fileLabel) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            fileLabel.setText("Selected File: " + selectedFile.getName());

            // Reset images and visibility
            originalImageView.setImage(null);
            encodedImageView.setImage(null);
            originalImageVisible = true;
            encodedImageVisible = true;
            originalImageView.setVisible(originalImageVisible);
            encodedImageView.setVisible(encodedImageVisible);

            Image originalImage = new Image(selectedFile.toURI().toString());
            originalImageView.setImage(originalImage);
        } else {
            fileLabel.setText("No file selected");
        }
    }

    private void encodeMessage() {
        if (selectedFile == null || messageInput.getText().isEmpty()) {
            showAlert("Please select an image and enter a message to encode.");
            return;
        }

        // Prompt for the custom filename
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Save Encoded Image");
        dialog.setHeaderText("Enter a name for the encoded image file");
        dialog.setContentText("Filename:");

        // Show the dialog and capture the input
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String customFilename = result.get().trim();

            try {
                BufferedImage originalImage = ImageIO.read(selectedFile);
                SteganographyImage stegImage = new SteganographyImage(originalImage);
                BufferedImage modifiedImage = Steganography.hideMessage(stegImage, messageInput.getText());

                // Save the encoded image with the user-provided filename
                File outputFile = new File(customFilename + ".png");
                ImageIO.write(modifiedImage, "PNG", outputFile);

                // Load and display the encoded image
                Image encodedImage = new Image(outputFile.toURI().toString());
                encodedImageView.setImage(encodedImage);
                encodedImageVisible = true;
                encodedImageView.setVisible(encodedImageVisible);

                showAlert("Message encoded! Saved as " + outputFile.getName());

            } catch (IOException e) {
                showAlert("Error reading or writing the image.");
            } catch (Exception e) {
                showAlert("An error occurred: " + e.getMessage());
            }
        } else {
            showAlert("Encoding canceled. Please enter a valid filename.");
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

            String extracted = Steganography.extractMessage(stegImage, stegImage.getTotalPixels());
            String decodedMessage = extracted.substring(0, extracted.indexOf("EOF"));
            decodeOutput.setText("Decoded Message: " + decodedMessage);
        }

        catch (IOException e) {
            showAlert("Error reading the image.");
        } catch (Exception e) {
            showAlert("An error occurred: " + e.getMessage());
        }
    }

    private void toggleOriginalImageVisibility() {
        originalImageVisible = !originalImageVisible;
        originalImageView.setVisible(originalImageVisible);
    }

    private void toggleEncodedImageVisibility() {
        encodedImageVisible = !encodedImageVisible;
        encodedImageView.setVisible(encodedImageVisible);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
