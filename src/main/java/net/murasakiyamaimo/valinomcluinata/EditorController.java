package net.murasakiyamaimo.valinomcluinata;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditorController {

    @FXML
    private Canvas score;
    @FXML
    private AnchorPane AnchorPane;

    private GraphicsContext gc;

    private double height;


    public void initialize() {
        if (AnchorPane != null) {
            if (score != null) {
                gc = score.getGraphicsContext2D();

                double width = score.getWidth();
                height = score.getHeight();

                gc.setFill(Color.web("#676681"));
                gc.fillRect(0, 0, width, height);

                gc.setStroke(Color.web("a9a9b4"));
                gc.strokeLine(0, height / 2, width, height / 2);

                gc.setStroke(Color.web("#8d8c9d"));
                for (double i = height / 2 % 160; i < height; i += 160) {
                    gc.strokeLine(0, i, width, i);
                }

                gc.setFill(Color.web("#f27992"));
                gc.fillRect(60, height / 2 - 160, 16, 160);

                gc.setFill(Color.web("#6cd985"));
                gc.fillRect(60 + 173 - 16, height /2 - 90, 16, 90);

                gc.drawImage(rootImage, 30, height / 2 - 13.5);
                gc.drawImage(pitch_line, 60, height / 2 - 2.75);
                gc.drawImage(pitch_line, 60, height / 2 - 2.75 - 90);
                gc.drawImage(pitch_line, 60, height / 2 - 2.75 - 160);
                gc.drawImage(pitch_line, 60, height / 2 - 2.75 - 220);
                gc.drawImage(pitch_line, 60, height / 2 - 2.75 - 392);

                gc.setFill(Color.web("#b795e9"));
                double[] xPoints = {60, 60 + 177 - 16, 60 + 177, 60 + 16};
                double[] yPoints = {height/2 - 2.75 , height/2 - 220 - 2.75, height/2 - 220 - 2.75, height/2 - 2.75};
                gc.fillPolygon(xPoints, yPoints, 4);

                xPoints = new double[]{60 + 173 - 16, 56, 56 + 16, 60 + 173};
                yPoints = new double[]{height / 2 - 2.75, height / 2 - 392 - 2.75, height / 2 - 392 - 2.75, height / 2 - 2.75};
                gc.setFill(Color.web("#ffc247"));
                gc.fillPolygon(xPoints, yPoints, 4);
            }
        }

        rootX.add(30.0);
        rootY.add(height / 2);
        pitchData.add(new TreeNode<>(new Data(0, true, false)));
        pitchData.getFirst().setCoordinateX(60.0);
        pitchData.getFirst().setCoordinateY(height/2);

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

        selectedPitchline = pitchData.getFirst().getIDPath();

        score.setOnMouseClicked(event -> {
            System.out.println("X:" + event.getX() + " Y:" + event.getY());
            for (TreeNode<Data> node : pitchData) {
                TreeNode<Data> searchedNode = node.SearchCoordinate(event.getX(), event.getY());
                if (searchedNode != null) {
                    System.out.println("found!");
                    selectedPitchline = searchedNode.getIDPath();
                    for (TreeNode<Data> rootNodes : pitchData) {
                        if (rootNodes.equals(searchedNode.getRoot())) {
                            sideIndex = pitchData.indexOf(rootNodes);
                        }
                    }
                    System.out.println(selectedPitchline);
                    System.out.println(sideIndex);
                }
            }
        });
    }

    private List<Integer> selectedPitchline;

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
    private boolean isPitch = true;
    private final Image pitch_line = new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/pitch-line.png")));
    private final Image rootImage = new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/root-symbol.png")));
    private ArrayList<Double> rootX = new ArrayList<>();
    private ArrayList<Double> rootY = new ArrayList<>();
    private int sideIndex = 0;
    private int heightIndex = 0;
    private ArrayList<TreeNode<Data>> pitchData = new ArrayList<>();

    private void pitch_setHandler(ImageView imageView) {
        imageView.setOnMouseClicked(this::pitch_handleMouseClicked);
        imageView.setOnMouseEntered(this::pitch_handleMouseEntered);
        imageView.setOnMouseExited(this::pitch_handleMouseExit);
    }

    private void pitch_handleMouseEntered(MouseEvent event) {
        ImageView enteredImageView = (ImageView) event.getSource();
        String imageName = null;

        if (enteredImageView == D2) {
            imageName = "2D_selected.png";
        } else if (enteredImageView == D3) {
            imageName = "3D_selected.png";
        } else if (enteredImageView == D4) {
            imageName = "4D_selected.png";
        } else if (enteredImageView == D5) {
            imageName = "5D_selected.png";
        } else if (enteredImageView == D2_Down) {
            imageName = "2D_Down_selected.png";
        } else if (enteredImageView == D3_Down) {
            imageName = "3D_Down_selected.png";
        } else if (enteredImageView == D4_Down) {
            imageName = "4D_Down_selected.png";
        } else if (enteredImageView == D5_Down) {
            imageName = "5D_Down_selected.png";
        }

        if (imageName != null) {
            enteredImageView.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/" + imageName))));
        }
    }

    private void pitch_handleMouseExit(MouseEvent event) {
        ImageView exitedImageView = (ImageView) event.getSource();
        String imageName = null;

        if (exitedImageView == D2) {
            imageName = "2D.png";
        } else if (exitedImageView == D3) {
            imageName = "3D.png";
        } else if (exitedImageView == D4) {
            imageName = "4D.png";
        } else if (exitedImageView == D5) {
            imageName = "5D.png";
        } else if (exitedImageView == D2_Down) {
            imageName = "2D_Down.png";
        } else if (exitedImageView == D3_Down) {
            imageName = "3D_Down.png";
        } else if (exitedImageView == D4_Down) {
            imageName = "4D_Down.png";
        } else if (exitedImageView == D5_Down) {
            imageName = "5D_Down.png";
        }

        if (imageName != null) {
            exitedImageView.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/" + imageName))));
        }
    }

    private void drawChordDiagram() {
        gc.setFill(Color.web("#676681"));
        gc.fillRect(rootX.get(sideIndex), 0, rootX.get(sideIndex) + 173 + 60, height);

        gc.setStroke(Color.web("a9a9b4"));
        gc.strokeLine(rootX.get(sideIndex), height / 2, rootX.get(sideIndex) + 173 + 60, height / 2);

        gc.setStroke(Color.web("#8d8c9d"));
        for (double i = height / 2 % 160; i < height; i += 160) {
            gc.strokeLine(rootX.get(sideIndex), i, rootX.get(sideIndex) + 173 + 60, i);
        }

        pitchData.get(sideIndex).setCoordinateX(rootX.get(sideIndex) + 30);
        pitchData.get(sideIndex).setCoordinateY(rootY.get(heightIndex));

        pitchData.get(sideIndex).drawPitch(gc);

        gc.drawImage(pitch_line, rootX.get(sideIndex) + 30, rootY.get(sideIndex) - 2.75);
        gc.drawImage(rootImage, rootX.get(sideIndex), rootY.get(sideIndex) - 13.5);
    }

    private void pitch_handleMouseClicked(MouseEvent event) {
        ImageView clickedImageView = (ImageView) event.getSource();
        TreeNode<Data> selectedNode = pitchData.get(sideIndex).getNodeByPath(selectedPitchline);
        double howUp = 0;
        Double[] Coordinate = selectedNode.getCoordinate();
        int dimension = 0;
        boolean isUp = true;
        boolean isMuted = false;
        TreeNode<Data> setNode = new TreeNode<>(new Data(0, true, false));
        System.out.println("setCoordinate:" + Coordinate[1]);
        if (clickedImageView == D2) {
            if (isPitch) {
                pitch[1] += 1;
                howUp -= 160;
            }else {
                Coordinate[1] -= 160;
                dimension = 2;
                setNode.setData(new Data(2, true, false));
            }
        } else if (clickedImageView == D3) {
            if (isPitch) {
                pitch[2] += 1;
                howUp -= 90;
            }else {
                Coordinate[1] -= 90;
                dimension = 3;
                setNode.setData(new Data(3, true, false));
            }
        } else if (clickedImageView == D4) {
            if (isPitch) {
                pitch[3] += 1;
                howUp -= 220 - 6;
            }else {
                Coordinate[1] -= 220 + 2.75;
                dimension = 4;
                setNode.setData(new Data(4, true, false));
            }
        } else if (clickedImageView == D5) {
            if (isPitch) {
                pitch[4] += 1;
                howUp -= 392 - 6;
            }else {
                Coordinate[1] -= 392 + 2.75;
                dimension = 5;
                setNode.setData(new Data(5, true, false));
            }
        } else if (clickedImageView == D2_Down) {
            if (isPitch) {
                pitch[1] -= 1;
                howUp += 160;
            }else {
                Coordinate[1] += 160;
                dimension = 2;
                isUp = false;
                setNode.setData(new Data(2, false, false));
            }
        } else if (clickedImageView == D3_Down) {
            if (isPitch) {
                pitch[2] -= 1;
                howUp += 90;
            }else {
                Coordinate[1] += 90;
                dimension = 3;
                isUp = false;
                setNode.setData(new Data(3, false, false));
            }
        } else if (clickedImageView == D4_Down) {
            if (isPitch) {
                pitch[3] -= 1;
                howUp += 220 - 6;
            }else {
                Coordinate[1] += 220 + 2.75;
                dimension = 4;
                isUp = false;
                setNode.setData(new Data(4, false, false));
            }
        } else if (clickedImageView == D5_Down) {
            if (isPitch) {
                pitch[4] -= 1;
                howUp += 392 - 6;
            }else {
                Coordinate[1] += 392 + 2.75;
                dimension = 5;
                isUp = false;
                setNode.setData(new Data(5, false, false));
            }
        }

        if (!isPitch) {
            setNode.setCoordinateX(Coordinate[0]);
            setNode.setCoordinateY(Coordinate[1]);

            pitchData.get(sideIndex).getNodeByPath(selectedPitchline).addChild(new TreeNode<>(new Data(dimension, isUp, isMuted)));
            pitchData.get(sideIndex).getNodeByPath(selectedPitchline).getChildren().getLast().setCoordinateX(Coordinate[0]);
            pitchData.get(sideIndex).getNodeByPath(selectedPitchline).getChildren().getLast().setCoordinateY(Coordinate[1]);
        }else {
            rootY.set(sideIndex, rootY.get(sideIndex) + howUp);
            pitchData.get(sideIndex).rootCoordinate(howUp);
        }

        System.out.println("childY:" + setNode.getCoordinateY());

        drawChordDiagram();
    }

    public void scoreIndex_handleKeyPressed(KeyEvent event) {
        if (event.isControlDown()) {
            switch (event.getCode()) {
                case RIGHT -> {
                    System.out.println("find RIGHT KEY");
                    sideIndex++;
                    heightIndex = 0;
                    if (sideIndex >= rootX.size()) {
                        rootX.add(rootX.get(sideIndex - 1) + 233);
                        rootY.add(height / 2);
                        gc.drawImage(rootImage, rootX.get(sideIndex), rootY.get(sideIndex) - 13.5);
                        pitchData.add(new TreeNode<>(new Data(0, true, false)));
                        pitchData.getLast().setCoordinateX(rootX.get(sideIndex) + 30);
                        pitchData.getLast().setCoordinateY(rootY.get(sideIndex));
                    }
                    System.out.println("Now side index is " + sideIndex);
                    selectedPitchline = pitchData.get(sideIndex).getIDPath();
                }
                case LEFT -> {
                    System.out.println("find LEFT KEY");
                    if (sideIndex > 0) {
                        sideIndex--;
                    }
                    heightIndex = 0;
                    System.out.println("Now side index is " + sideIndex);
                    selectedPitchline = pitchData.get(sideIndex).getIDPath();
                }
            }
        }else if (event.getCode() == KeyCode.SHIFT) {
            isPitch = !isPitch;
        }
    }

}