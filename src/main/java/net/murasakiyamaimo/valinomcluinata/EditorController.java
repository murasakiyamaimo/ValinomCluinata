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

public class EditorController {

    @FXML
    private Canvas score;
    @FXML
    private AnchorPane AnchorPane;
    @FXML
    private GridPane gridPane;

    private GraphicsContext gc = score.getGraphicsContext2D();

    public void initialize() {
        if (AnchorPane != null) {
            if (score != null) {
                double width = score.getWidth();
                double height = score.getHeight();

                gc.setFill(Color.web("#676681"));
                gc.fillRect(0, 0, width, height);

                gc.setStroke(Color.web("a9a9b4"));
                gc.strokeLine(0, height/2, width, height/2);

                gc.setStroke(Color.web("#8d8c9d"));
                for (double i = height/2 % 169; i < height; i += 169) {
                    gc.strokeLine(0, i, width, i);
                }

                gc.setFill(Color.web("#f27992"));
                gc.fillRect(10, height/2,16, 169);
            }
        }

        imageOne = new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/Step-one.png")));
        imageLong = new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/Step-long.png")));
        imageDefault = new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/Step-null.png")));

        // ImageViewに初期画像を設定
        dragImageView1.setImage(imageDefault);
        dragImageView2.setImage(imageDefault);
        dragImageView3.setImage(imageDefault);
        dragImageView4.setImage(imageDefault);

        // イベントハンドラの設定
        rhythm_setHandler(dragImageView1);
        rhythm_setHandler(dragImageView2);
        rhythm_setHandler(dragImageView3);
        rhythm_setHandler(dragImageView4);

        image2D = new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/2D.png")));
        image3D = new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/3D.png")));
        image4D = new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/4D.png")));
        image5D = new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/5D.png")));

        D2.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/2D.png"))));
        D3.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/3D.png"))));
        D4.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/4D.png"))));
        D5.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/5D.png"))));

        D2_Down.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/2D_Down.png"))));
        D3_Down.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/3D_Down.png"))));
        D4_Down.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/4D_Down.png"))));
        D5_Down.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/5D_Down.png"))));

        pitch_setHandler(D2);
        pitch_setHandler(D3);
        pitch_setHandler(D4);
        pitch_setHandler(D5);

        pitch_setHandler(D2_Down);
        pitch_setHandler(D3_Down);
        pitch_setHandler(D4_Down);
        pitch_setHandler(D5_Down);
    }

    @FXML
    private ImageView dragImageView1;
    @FXML
    private ImageView dragImageView2;
    @FXML
    private ImageView dragImageView3;
    @FXML
    private ImageView dragImageView4;

    private Image imageOne;
    private Image imageLong;
    private Image imageDefault;
    private ImageView selectedImageView;
    private ImageView endImageView;
    private double startX;
    private double startY;
    private boolean isDragging;
    private static final double DRAG_THRESHOLD = 1.0;

    private void rhythm_setHandler(ImageView imageView) {
        imageView.setOnMousePressed(this::rhythm_handleMousePressed);
        imageView.setOnMouseDragged(this::rhythm_handleMouseDragged);
        imageView.setOnMouseReleased(this::rhythm_handleMouseReleased);
    }

    private void rhythm_handleMousePressed(MouseEvent event) {
        if (event.isShiftDown()) {
            selectedImageView = (ImageView) event.getSource();
            startX = event.getX();
            startY = event.getY();
            isDragging = false;
        }
    }

    private void rhythm_handleMouseDragged(MouseEvent event) {
        if (event.isShiftDown() && selectedImageView != null) {
            double deltaX = Math.abs(event.getX() - startX);
            double deltaY = Math.abs(event.getY() - startY);
            if (deltaX > DRAG_THRESHOLD || deltaY > DRAG_THRESHOLD) {
                isDragging = true;
            }
        }
    }

    private void rhythm_handleMouseReleased(MouseEvent event) {
        if (event.isShiftDown() && selectedImageView != null) {
            if (isDragging) {
                endImageView = (ImageView) event.getSource();
                rhythm_UpdateImagesBetween(selectedImageView, endImageView);
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

    private void rhythm_UpdateImagesBetween(ImageView startImageView, ImageView endImageView) {
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


    @FXML
    private ImageView D2;
    @FXML
    private ImageView D3;
    @FXML
    private ImageView D4;
    @FXML
    private ImageView D5;
    @FXML
    private ImageView D2_Down;
    @FXML
    private ImageView D3_Down;
    @FXML
    private ImageView D4_Down;
    @FXML
    private ImageView D5_Down;

    private final int[] pitch = new int[5];

    private Image image2D;
    private Image image3D;
    private Image image4D;
    private Image image5D;

    private ImageView clickedImageView;
    private ImageView enteredImageView;
    private ImageView exitedImageView;

    private void pitch_setHandler(ImageView imageView) {
        imageView.setOnMouseClicked(this::pitch_handleMouseClicked);
        imageView.setOnMouseEntered(this::pitch_handleMouseEntered);
        imageView.setOnMouseExited(this::pitch_handleMouseExit);
    }

    private void pitch_handleMouseEntered(MouseEvent event) {
        enteredImageView = (ImageView) event.getSource();
        if (enteredImageView == D2) {
            D2.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/2D_selected.png"))));
        } else if (enteredImageView == D3) {
            D3.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/3D_selected.png"))));
        } else if (enteredImageView == D4) {
            D4.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/4D_selected.png"))));
        } else if (enteredImageView == D5) {
            D5.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/5D_selected.png"))));
        }
        if (enteredImageView == D2_Down) {
            D2_Down.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/2D_Down_selected.png"))));
        } else if (enteredImageView == D3_Down) {
            D3_Down.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/3D_Down_selected.png"))));
        } else if (enteredImageView == D4_Down) {
            D4_Down.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/4D_Down_selected.png"))));
        } else if (enteredImageView == D5_Down) {
            D5_Down.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/5D_Down_selected.png"))));
        }
    }

    private void pitch_handleMouseExit(MouseEvent event) {
        exitedImageView = (ImageView) event.getSource();
        if (exitedImageView == D2) {
            D2.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/2D.png"))));
        } else if (exitedImageView == D3) {
            D3.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/3D.png"))));
        } else if (exitedImageView == D4) {
            D4.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/4D.png"))));
        } else if (exitedImageView == D5) {
            D5.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/5D.png"))));
        }
        if (exitedImageView == D2_Down) {
            D2_Down.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/2D_Down.png"))));
        } else if (exitedImageView == D3_Down) {
            D3_Down.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/3D_Down.png"))));
        } else if (exitedImageView == D4_Down) {
            D4_Down.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/4D_Down.png"))));
        } else if (exitedImageView == D5_Down) {
            D5_Down.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/5D_Down.png"))));
        }
    }

    private void pitch_handleMouseClicked(MouseEvent event) {
        clickedImageView = (ImageView) event.getSource();
        if (clickedImageView == D2) {
            pitch[1] += 1;
        } else if (clickedImageView == D3) {
            pitch[2] += 1;
        } else if (clickedImageView == D4) {
            pitch[3] += 1;
        } else if (clickedImageView == D5) {
            pitch[4] += 1;
        }
        if (clickedImageView == D2_Down) {
            pitch[1] -= 1;
        } else if (clickedImageView == D3_Down) {
            pitch[2] -= 1;
        } else if (clickedImageView == D4_Down) {
            pitch[3] -= 1;
        } else if (clickedImageView == D5_Down) {
            pitch[4] -= 1;
        }
    }

}