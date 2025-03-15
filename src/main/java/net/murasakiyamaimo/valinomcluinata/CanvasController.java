package net.murasakiyamaimo.valinomcluinata;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.Objects;

public class CanvasController {

    @FXML
    private Canvas score;
    @FXML
    private AnchorPane AnchorPane;
    @FXML
    private GridPane gridPane;

    public void initialize() {
        if (AnchorPane != null) {
            if (score != null) {
                draw();
            }
        }

        imageOne = new Image(Objects.requireNonNull(CanvasController.class.getResourceAsStream("images/Step-one.png")));
        imageLong = new Image(Objects.requireNonNull(CanvasController.class.getResourceAsStream("images/Step-long.png")));
        imageDefault = new Image(Objects.requireNonNull(CanvasController.class.getResourceAsStream("images/Step-null.png")));

        // ImageViewに初期画像を設定
        imageView1.setImage(imageDefault);
        imageView2.setImage(imageDefault);
        imageView3.setImage(imageDefault);
        imageView4.setImage(imageDefault);

        // イベントハンドラの設定
        setImageViewHandler(imageView1);
        setImageViewHandler(imageView2);
        setImageViewHandler(imageView3);
        setImageViewHandler(imageView4);


    }

    public void draw() {
        if(score != null){
            double width = score.getWidth();
            double height = score.getHeight();

            GraphicsContext gc = score.getGraphicsContext2D();
            gc.clearRect(0, 0, width, height);

            gc.setStroke(Color.RED);
            gc.strokeLine(0, 0, width, height);
            gc.strokeLine(0, height, width, 0);
        }
    }

    @FXML
    private ImageView imageView1;
    @FXML
    private ImageView imageView2;
    @FXML
    private ImageView imageView3;
    @FXML
    private ImageView imageView4;

    private Image imageOne;
    private Image imageLong;
    private Image imageDefault;
    private ImageView selectedImageView;
    private ImageView endImageView;
    private double startX;
    private double startY;
    private boolean isDragging;
    private static final double DRAG_THRESHOLD = 1.0;

    private void setImageViewHandler(ImageView imageView) {
        imageView.setOnMousePressed(this::handleMousePressed);
        imageView.setOnMouseDragged(this::handleMouseDragged);
        imageView.setOnMouseReleased(this::handleMouseReleased);
    }

    private void handleMousePressed(MouseEvent event) {
        if (event.isShiftDown()) {
            selectedImageView = (ImageView) event.getSource();
            startX = event.getX();
            startY = event.getY();
            isDragging = false;
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        if (event.isShiftDown() && selectedImageView != null) {
            double deltaX = Math.abs(event.getX() - startX);
            double deltaY = Math.abs(event.getY() - startY);
            if (deltaX > DRAG_THRESHOLD || deltaY > DRAG_THRESHOLD) {
                isDragging = true;
            }
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        if (event.isShiftDown() && selectedImageView != null) {
            if (isDragging) {
                endImageView = (ImageView) event.getSource();
                updateImagesBetween(selectedImageView, endImageView);
            } else if (!isDragging) {
                // クリック処理
                if (selectedImageView.getImage() == imageOne) {
                    selectedImageView.setImage(imageDefault);
                } else {
                    selectedImageView.setImage(imageOne);
                }
            }
        }
        selectedImageView = null;
        endImageView = null;
        isDragging = false;
    }

    private void updateImagesBetween(ImageView startImageView, ImageView endImageView) {
        int startColumn = GridPane.getColumnIndex(startImageView);
        int startRow = GridPane.getRowIndex(startImageView);
        int endColumn = GridPane.getColumnIndex(endImageView);
        int endRow = GridPane.getRowIndex(endImageView);

        if (endColumn > startColumn && startRow == endRow) {
            for (int column = startColumn + 1; column <= endColumn; column++) {
                for (Node node : gridPane.getChildren()) {
                    if (node instanceof ImageView) {
                        ImageView imageView = (ImageView) node;
                        if (GridPane.getColumnIndex(imageView) == column && GridPane.getRowIndex(imageView) == startRow) {
                            imageView.setImage(imageLong);
                        }
                    }
                }
            }
        }

    }
}