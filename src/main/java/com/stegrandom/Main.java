package com.stegrandom;

import com.stegrandom.Model.SteganographyImage;
import com.stegrandom.steganography.Steganography;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Main extends Application {
    private File selectedFile;
    private TextArea messageInput;
    private Label decodeOutput;
    private ImageView originalImageView;
    private ImageView encodedImageView;
    private boolean originalImageVisible = true;
    private boolean encodedImageVisible = true;
    private ProgressBar progressBar;
    private Label statusLabel;
    private VBox mainLayout;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Steganography Tool");
        mainLayout = createMainLayout(primaryStage);
        setupDragAndDrop(mainLayout);

        ScrollPane scrollPane = new ScrollPane(mainLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("edge-to-edge");

        Scene scene = new Scene(scrollPane, 800, 900);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createMainLayout(Stage primaryStage) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));

        statusLabel = new Label("Ready");
        progressBar = new ProgressBar(0);
        progressBar.setVisible(false);
        progressBar.setPrefWidth(Double.MAX_VALUE);

        VBox statusSection = new VBox(5, statusLabel, progressBar);

        layout.getChildren().addAll(
                createSectionWithTitle("File Selection", createFileSection(primaryStage)),
                createSectionWithTitle("Image Preview", createImageSection()),
                createSectionWithTitle("Message Encoding", createEncodingSection()),
                createSectionWithTitle("Message Decoding", createDecodingSection()),
                statusSection
        );

        return layout;
    }

    private VBox createFileSection(Stage primaryStage) {
        Button fileButton = new Button("Select Image File");
        fileButton.getStyleClass().add("primary-button");
        Label fileLabel = new Label("No file selected");
        fileButton.setOnAction(e -> selectFile(primaryStage, fileLabel));

        VBox section = new VBox(10);
        section.setAlignment(Pos.CENTER_LEFT);
        section.getChildren().addAll(fileButton, fileLabel);
        return section;
    }

    private HBox createImageSection() {
        originalImageView = new ImageView();
        encodedImageView = new ImageView();
        configureImageView(originalImageView);
        configureImageView(encodedImageView);

        VBox originalContainer = createImageContainer("Original Image", originalImageView, "Toggle Original");
        VBox encodedContainer = createImageContainer("Encoded Image", encodedImageView, "Toggle Encoded");

        HBox imageSection = new HBox(20);
        imageSection.setAlignment(Pos.CENTER);
        imageSection.getChildren().addAll(originalContainer, encodedContainer);
        return imageSection;
    }

    private void configureImageView(ImageView imageView) {
        imageView.setFitWidth(350);
        imageView.setFitHeight(350);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ddd; -fx-border-width: 1px;");
    }

    private VBox createImageContainer(String title, ImageView imageView, String buttonText) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("section-subtitle");

        StackPane imageContainer = new StackPane(imageView);
        imageContainer.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #ddd; -fx-border-radius: 5;");

        Button toggleButton = new Button(buttonText);
        toggleButton.getStyleClass().add("secondary-button");
        toggleButton.setOnAction(e -> imageView.setVisible(!imageView.isVisible()));

        VBox container = new VBox(10);
        container.setAlignment(Pos.TOP_CENTER);
        container.getChildren().addAll(titleLabel, imageContainer, toggleButton);
        return container;
    }

    private VBox createEncodingSection() {
        Label encodeLabel = new Label("Secret Message:");
        messageInput = new TextArea();
        messageInput.setPromptText("Enter the message you want to hide...");
        messageInput.setPrefRowCount(3);

        Button encodeButton = new Button("Encode Message");
        encodeButton.getStyleClass().add("primary-button");
        encodeButton.setOnAction(e -> encodeMessage());

        VBox section = new VBox(10);
        section.getChildren().addAll(encodeLabel, messageInput, encodeButton);
        return section;
    }

    private VBox createDecodingSection() {
        Button decodeButton = new Button("Decode Message");
        decodeButton.getStyleClass().add("primary-button");
        decodeButton.setOnAction(e -> decodeMessage());

        decodeOutput = new Label("Decoded message will appear here");
        decodeOutput.setWrapText(true);
        decodeOutput.setStyle("-fx-padding: 10; -fx-background-color: #f8f8f8; -fx-border-color: #ddd; -fx-border-radius: 5;");

        VBox section = new VBox(10);
        section.getChildren().addAll(decodeButton, decodeOutput);
        return section;
    }

    private VBox createSectionWithTitle(String title, Region content) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("section-title");

        VBox section = new VBox(10);
        section.getStyleClass().add("section-container");
        section.getChildren().addAll(titleLabel, content);
        return section;
    }

    private void setupDragAndDrop(VBox layout) {
        layout.setOnDragOver(e -> {
            if (e.getDragboard().hasFiles()) {
                e.acceptTransferModes(TransferMode.COPY);
            }
            e.consume();
        });

        layout.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasFiles() && isImageFile(db.getFiles().get(0))) {
                handleImageFile(db.getFiles().get(0));
                success = true;
            } else {
                showAlert(Alert.AlertType.WARNING, "Invalid File", "Please drag and drop only image files (PNG, JPG, JPEG).");
            }
            e.setDropCompleted(success);
            e.consume();
        });
    }

    private void selectFile(Stage primaryStage, Label fileLabel) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(primaryStage);

        if (file != null) {
            handleImageFile(file);
            fileLabel.setText("Selected File: " + file.getName());
        }
    }

    private boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg");
    }

    private void handleImageFile(File file) {
        selectedFile = file;
        loadImage(file);
        updateStatus("Image loaded: " + file.getName());
    }

    private void loadImage(File file) {
        Task<Image> loadTask = new Task<>() {
            @Override
            protected Image call() throws Exception {
                updateProgress(0, 100);
                return new Image(file.toURI().toString());
            }
        };

        loadTask.setOnSucceeded(e -> Platform.runLater(() -> {
            Image image = loadTask.getValue();
            originalImageView.setImage(image);
            originalImageView.setFitWidth(Math.min(image.getWidth(), 350));
            originalImageView.setFitHeight(Math.min(image.getHeight(), 350));
            progressBar.setVisible(false);
            updateStatus("Ready");
        }));

        loadTask.setOnFailed(e -> Platform.runLater(() -> {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load image: " + loadTask.getException().getMessage());
            progressBar.setVisible(false);
            updateStatus("Error loading image");
        }));

        progressBar.progressProperty().bind(loadTask.progressProperty());
        progressBar.setVisible(true);
        updateStatus("Loading image...");
        new Thread(loadTask).start();
    }

    private void encodeMessage() {
        if (!validateInput()) return;

        CompletableFuture<String> filenameFuture = new CompletableFuture<>();
        Platform.runLater(() -> {
            String filename = promptForFilename();
            filenameFuture.complete(filename);
        });

        Task<File> encodeTask = new Task<>() {
            @Override
            protected File call() throws Exception {
                updateProgress(0, 100);

                String filename = filenameFuture.get();
                if (filename == null) return null;

                updateProgress(25, 100);

                BufferedImage originalImage = ImageIO.read(selectedFile);
                SteganographyImage stegImage = new SteganographyImage(originalImage);

                updateProgress(50, 100);

                BufferedImage modifiedImage = Steganography.hideMessage(stegImage, messageInput.getText());

                updateProgress(75, 100);

                File outputFile = new File(filename + ".png");
                ImageIO.write(modifiedImage, "PNG", outputFile);

                return outputFile;
            }
        };

        encodeTask.setOnSucceeded(e -> Platform.runLater(() -> {
            File outputFile = encodeTask.getValue();
            if (outputFile != null) {
                Image encodedImage = new Image(outputFile.toURI().toString());
                encodedImageView.setImage(encodedImage);
                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Message encoded successfully!\nSaved as: " + outputFile.getName());
            }
            progressBar.setVisible(false);
            updateStatus("Encoding complete");
        }));

        encodeTask.setOnFailed(e -> Platform.runLater(() -> {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "An error occurred while encoding: " + encodeTask.getException().getMessage());
            progressBar.setVisible(false);
            updateStatus("Error encoding message");
        }));

        progressBar.progressProperty().bind(encodeTask.progressProperty());
        progressBar.setVisible(true);
        updateStatus("Encoding message...");
        new Thread(encodeTask).start();
    }

    private void decodeMessage() {
        if (selectedFile == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select an image first.");
            return;
        }

        Task<String> decodeTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                updateProgress(0, 100);

                BufferedImage encodedImage = ImageIO.read(selectedFile);
                updateProgress(33, 100);

                SteganographyImage stegImage = new SteganographyImage(encodedImage);
                updateProgress(66, 100);

                String extracted = Steganography.extractMessage(stegImage, stegImage.getTotalPixels());
                updateProgress(100, 100);

                return extracted.substring(0, extracted.indexOf("EOF"));
            }
        };

        decodeTask.setOnSucceeded(e -> Platform.runLater(() -> {
            decodeOutput.setText("Decoded Message: " + decodeTask.getValue());
            progressBar.setVisible(false);
            updateStatus("Message decoded successfully");
        }));

        decodeTask.setOnFailed(e -> Platform.runLater(() -> {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "An error occurred while decoding: " + decodeTask.getException().getMessage());
            progressBar.setVisible(false);
            updateStatus("Error decoding message");
        }));

        progressBar.progressProperty().bind(decodeTask.progressProperty());
        progressBar.setVisible(true);
        updateStatus("Decoding message...");
        new Thread(decodeTask).start();
    }

    private void updateStatus(String message) {
        Platform.runLater(() -> statusLabel.setText(message));
    }

    private boolean validateInput() {
        if (selectedFile == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select an image first.");
            return false;
        }
        if (messageInput.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please enter a message to encode.");
            return false;
        }
        return true;
    }

    private String promptForFilename() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Save Encoded Image");
        dialog.setHeaderText("Enter a name for the encoded image file");
        dialog.setContentText("Filename:");

        Optional<String> result = dialog.showAndWait();
        return result.map(String::trim)
                .filter(s -> !s.isEmpty())
                .orElse(null);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}