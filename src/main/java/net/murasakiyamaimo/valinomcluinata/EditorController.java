package net.murasakiyamaimo.valinomcluinata;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Objects;

public class EditorController {

    @FXML
    private Canvas score;
    @FXML
    private AnchorPane AnchorPane;
    @FXML
    private GridPane gridPane;

    private GraphicsContext gc;

    private double width;
    private double height;


    public void initialize() {
        if (AnchorPane != null) {
            if (score != null) {
                gc = score.getGraphicsContext2D();

                width = score.getWidth();
                height = score.getHeight();

                gc.setFill(Color.web("#676681"));
                gc.fillRect(0, 0, width, height);

                gc.setStroke(Color.web("a9a9b4"));
                gc.strokeLine(0, height/2, width, height/2);

                gc.setStroke(Color.web("#8d8c9d"));
                for (double i = height/2 % 160; i < height; i += 160) {
                    gc.strokeLine(0, i, width, i);
                }

                gc.setFill(Color.web("#f27992"));
                gc.fillRect(10, height/2,16, 160);

                gc.drawImage(rootImage, 30, height/2 - 13.5);
                gc.drawImage(pitch_line, 60, height/2 - 2.75);
            }
        }

        rootX.add(30.0);
        rootY.add(height/2);

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
            } else {
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

    private final Image pitch_line = new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/pitch-line.png")));
    private final Image rootImage = new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/root-symbol.png")));
    private ArrayList<Double> rootX = new ArrayList<Double>();
    private ArrayList<Double> rootY = new ArrayList<Double>();
    private int index = 0;

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
        } else if (enteredImageView == D2_Down) {
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
        } else if (exitedImageView == D2_Down) {
            D2_Down.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/2D_Down.png"))));
        } else if (exitedImageView == D3_Down) {
            D3_Down.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/3D_Down.png"))));
        } else if (exitedImageView == D4_Down) {
            D4_Down.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/4D_Down.png"))));
        } else if (exitedImageView == D5_Down) {
            D5_Down.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/5D_Down.png"))));
        }

    }

    private void drawRoot(boolean isUp, double px) {
        gc.drawImage(rootImage, rootX.get(index), rootY.get(index) - 13.5);
            if (isUp) {
                gc.setFill(Color.web("#676681"));
                gc.fillRect(rootX.get(index), rootY.get(index) + px - 13.5, 24, 27);
                if ((rootY.get(index) + px - height/2) % 160 == 0) {
                        if (rootY.get(index) + px == height/2) {
                            gc.setStroke(Color.web("#a9a9b4"));
                            gc.strokeLine(rootX.get(index), rootY.get(index) + px, rootX.get(index) + 24, rootY.get(index) + px);
                        }else {
                            gc.setStroke(Color.web("#8d8c9d"));
                            gc.strokeLine(rootX.get(index), rootY.get(index) + px, rootX.get(index) + 24, rootY.get(index) + px);
                        }
                }
            }else {
                gc.setFill(Color.web("#676681"));
                gc.fillRect(rootX.get(index), rootY.get(index) - px - 13.5, 24, 27);
                if ((rootY.get(index) - px - height / 2) % 160 == 0) {
                    if (rootY.get(index) - px == height / 2) {
                        gc.setStroke(Color.web("#a9a9b4"));
                        gc.strokeLine(rootX.get(index), rootY.get(index) - px, rootX.get(index) + 24, rootY.get(index) - px);
                    } else {
                        gc.setStroke(Color.web("#8d8c9d"));
                        gc.strokeLine(rootX.get(index), rootY.get(index) - px, rootX.get(index) + 24, rootY.get(index) - px);
                    }

                }
            }
    }

    private void pitch_handleMouseClicked(MouseEvent event) {
        clickedImageView = (ImageView) event.getSource();
        if (clickedImageView == D2) {
            pitch[1] += 1;
            rootY.set(index, rootY.get(index) - 160);
            drawRoot(true, 160);
            System.out.println("Drew!! " + rootY.get(index) + " And " + index + " And " + rootX.get(index));
        } else if (clickedImageView == D3) {
            pitch[2] += 1;
            rootY.set(index, rootY.get(index) - 90);
            drawRoot(true, 90);
        } else if (clickedImageView == D4) {
            pitch[3] += 1;
            rootY.set(index, rootY.get(index) - 220);
            drawRoot(true, 220);
        } else if (clickedImageView == D5) {
            pitch[4] += 1;
            rootY.set(index, rootY.get(index) - 392);
            drawRoot(true, 392);
        } else if (clickedImageView == D2_Down) {
            pitch[1] -= 1;
            rootY.set(index, rootY.get(index) + 160);
            drawRoot(false, 160);
            System.out.println("Drew!! " + rootY.get(index) + " And " + index + " And " + rootX.get(index));
        } else if (clickedImageView == D3_Down) {
            pitch[2] -= 1;
            rootY.set(index, rootY.get(index) + 90);
            drawRoot(false, 90);
        } else if (clickedImageView == D4_Down) {
            pitch[3] -= 1;
            rootY.set(index, rootY.get(index) + 220);
            drawRoot(false, 220);
        } else if (clickedImageView == D5_Down) {
            pitch[4] -= 1;
            rootY.set(index, rootY.get(index) + 392);
            drawRoot(false, 392);
        }
    }

    public void scoreIndex_handleKeyPressed(KeyEvent event) {
        if (event.isControlDown()) {
            switch (event.getCode()) {
                case RIGHT -> {
                    System.out.println("find RIGHT KEY");
                    index++;
                    if (index >= rootX.size()) {
                        rootX.add(rootX.get(index - 1) + 233);
                        rootY.add(height/2);
                        gc.drawImage(rootImage, rootX.get(index), rootY.get(index) - 13.5);
                    }
                    System.out.println("Now index is " + index);
                }
                case LEFT -> {
                    System.out.println("find LEFT KEY");
                    index--;
                    System.out.println("Now index is " + index);
                }

            }
        }
    }

}