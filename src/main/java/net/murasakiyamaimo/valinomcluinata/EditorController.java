package net.murasakiyamaimo.valinomcluinata;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
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
    @FXML
    private ImageView PlayButtonImageView;
    @FXML
    private Button PlayButton;
    @FXML
    private TextField timeField;
    @FXML
    private ChoiceBox synType;

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

                gc.drawImage(rootImage, 30, height / 2 - 13.5);
                gc.drawImage(pitch_line, 60, height / 2 - 2.75);
            }
        }

        rootX.add(30.0);
        rootY.add(height / 2);
        pitchData.add(new TreeNode<>(new Data(0, true, false, rootFrequency)));
        pitchData.getFirst().setCoordinateX(60.0);
        pitchData.getFirst().setCoordinateY(height / 2);
        pitch.add(new int[5]);

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

        PlayButton.setOnMouseClicked(this::playButton_handleMouseClicked);

        selectedPitchline = pitchData.getFirst().getIDPath();

        score.setOnMouseClicked(event -> {
            System.out.println("X:" + event.getX() + " Y:" + event.getY());
            for (TreeNode<Data> node : pitchData) {
                TreeNode<Data> searchedNode = node.SearchCoordinate(event.getX(), event.getY());
                if (searchedNode != null) {
                    selectedPitchline = searchedNode.getIDPath();
                    for (TreeNode<Data> rootNodes : pitchData) {
                        if (rootNodes.equals(searchedNode.getRoot())) {
                            sideIndex = pitchData.indexOf(rootNodes);
                        }
                    }
                    System.out.println(selectedPitchline);
                    drawChordDiagram();
                }
            }
        });

        PlayButtonImageView.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/play.png"))));
        synType.getItems().addAll("Sine", "Sawtooth", "Square", "Triangle");
        synType.setValue("Sine");
        timeField.setPromptText("和音図再生時間（秒）");
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

    private final ArrayList<int[]> pitch = new ArrayList<>();
    private boolean isPitch = true;
    private final Image pitch_line = new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/pitch-line.png")));
    private final Image rootImage = new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/root-symbol.png")));
    private final ArrayList<Double> rootX = new ArrayList<>();
    private final ArrayList<Double> rootY = new ArrayList<>();
    private int sideIndex = 0;
    private final ArrayList<TreeNode<Data>> pitchData = new ArrayList<>();
    public static double rootFrequency = Math.pow(2, 0.25) * 220;
    private boolean isPlaying = false;
    private SoundPlayer soundPlayer;

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
        for (int i = 0; i < pitchData.size(); i++) {
            gc.setFill(Color.web("#676681"));
            gc.fillRect(rootX.get(i), 0, 173 + 60, height);

            gc.setStroke(Color.web("a9a9b4"));
            gc.strokeLine(rootX.get(i), height / 2, rootX.get(i) + 173 + 60, height / 2);

            gc.setStroke(Color.web("#8d8c9d"));
            for (double j = height / 2 % 160; j < height; j += 160) {
                gc.strokeLine(rootX.get(i), j, rootX.get(i) + 173 + 60, j);
            }

            pitchData.get(i).drawPitch(gc);

            if (!pitchData.get(i).getData().isMuted()) {
                gc.drawImage(DrawLine.pitch_line, rootX.get(i) + 30, rootY.get(i) - 2.75);
            } else {
                gc.drawImage(DrawLine.mutedPitch_line, rootX.get(i) + 30, rootY.get(i) - 2.75);
            }
            gc.drawImage(rootImage, rootX.get(i), rootY.get(i) - 13.5);
        }

        gc.setFill(Color.color(0.5, 0.76, 1, 0.3));
        gc.fillRoundRect(pitchData.get(sideIndex).getNodeByPath(selectedPitchline).getCoordinateX() - 4, pitchData.get(sideIndex).getNodeByPath(selectedPitchline).getCoordinateY() - 5, 181, 10, 5, 5);
    }

    private void pitch_handleMouseClicked(MouseEvent event) {
        ImageView clickedImageView = (ImageView) event.getSource();
        TreeNode<Data> selectedNode = pitchData.get(sideIndex).getNodeByPath(selectedPitchline);
        double howUp = 0;
        double[] Coordinate = new double[2];
        double frequency = pitchData.get(sideIndex).getNodeByPath(selectedPitchline).getData().getFrequency();
        Coordinate[0] = selectedNode.getCoordinateX();
        Coordinate[1] = selectedNode.getCoordinateY();
        int dimension = 0;
        boolean isUp = true;
        boolean isMuted = false;
        if (clickedImageView == D2) {
            if (isPitch) {
                pitch.get(sideIndex)[1] += 1;
                howUp -= 160;
            } else {
                Coordinate[1] -= 160;
                dimension = 2;
                frequency *= 3.0000 / 2;
            }
        } else if (clickedImageView == D3) {
            if (isPitch) {
                pitch.get(sideIndex)[2] += 1;
                howUp -= 90;
            } else {
                Coordinate[1] -= 90;
                dimension = 3;
                frequency *= 5.0000 / 4;
            }
        } else if (clickedImageView == D4) {
            if (isPitch) {
                pitch.get(sideIndex)[3] += 1;
                howUp -= 220 - 6;
            } else {
                Coordinate[1] -= 220;
                dimension = 4;
                frequency *= 7.0000 / 4;
            }
        } else if (clickedImageView == D5) {
            if (isPitch) {
                pitch.get(sideIndex)[4] += 1;
                howUp -= 392 - 6;
            } else {
                Coordinate[1] -= 392;
                dimension = 5;
                frequency *= 11.0000 / 8;
            }
        } else if (clickedImageView == D2_Down) {
            if (isPitch) {
                pitch.get(sideIndex)[1] -= 1;
                howUp += 160;
            } else {
                Coordinate[1] += 160;
                dimension = 2;
                isUp = false;
                frequency *= 2.0000 / 3;
            }
        } else if (clickedImageView == D3_Down) {
            if (isPitch) {
                pitch.get(sideIndex)[2] -= 1;
                howUp += 90;
            } else {
                Coordinate[1] += 90;
                dimension = 3;
                isUp = false;
                frequency *= 4.0000 / 5;
            }
        } else if (clickedImageView == D4_Down) {
            if (isPitch) {
                pitch.get(sideIndex)[3] -= 1;
                howUp += 220 - 6;
            } else {
                Coordinate[1] += 220 ;
                dimension = 4;
                isUp = false;
                frequency *= 4.0000 / 7;
            }
        } else if (clickedImageView == D5_Down) {
            if (isPitch) {
                pitch.get(sideIndex)[4] -= 1;
                howUp += 392 - 6;
            } else {
                Coordinate[1] += 392 ;
                dimension = 5;
                isUp = false;
                frequency *= 8.0000 / 11;
            }
        }

        if (!isPitch) {
            pitchData.get(sideIndex).getNodeByPath(selectedPitchline).addChild(new TreeNode<>(new Data(dimension, isUp, isMuted, frequency)));
            pitchData.get(sideIndex).getNodeByPath(selectedPitchline).getChildren().getLast().setCoordinateX(Coordinate[0]);
            pitchData.get(sideIndex).getNodeByPath(selectedPitchline).getChildren().getLast().setCoordinateY(Coordinate[1]);
        } else {
            rootY.set(sideIndex, rootY.get(sideIndex) + howUp);
            pitchData.get(sideIndex).rootCoordinate(howUp);
        }

        drawChordDiagram();
    }

    public void scoreIndex_handleKeyPressed(KeyEvent event) {
        if (event.isControlDown()) {
            switch (event.getCode()) {
                case RIGHT -> {
                    System.out.println("find RIGHT KEY");
                    rootX.add(rootX.getLast() + 233);
                    rootY.add(height / 2);
                    gc.drawImage(rootImage, rootX.getLast(), rootY.getLast() - 13.5);
                    pitchData.add(new TreeNode<>(new Data(0, true, false, rootFrequency)));
                    pitchData.getLast().setCoordinateX(rootX.getLast() + 30);
                    pitchData.getLast().setCoordinateY(rootY.getLast());
                    pitch.add(new int[5]);
                    drawChordDiagram();
                }
                case SPACE -> {
                    pitchData.get(sideIndex).getNodeByPath(selectedPitchline).getData().setMuted(!pitchData.get(sideIndex).getNodeByPath(selectedPitchline).getData().isMuted());
                    drawChordDiagram();
                }
            }
        } else if (event.getCode() == KeyCode.SHIFT) {
            isPitch = !isPitch;
        } else if (event.getCode() == KeyCode.DELETE) {
            boolean deleted = pitchData.get(sideIndex).removeNode(pitchData.get(sideIndex).getNodeByPath(selectedPitchline));
            if (!deleted) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("エラー");
                alert.setContentText("ルート音は削除できません");
                alert.show();
            }
            selectedPitchline = pitchData.get(sideIndex).getIDPath();
            drawChordDiagram();
        }
    }

    public void playButton_handleMouseClicked(MouseEvent event) {
        if (!isPlaying) {
            playSound();
            isPlaying = true;
            PlayButtonImageView.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/pause.png"))));
        }else {
            isPlaying = false;
            soundPlayer.stopSound();
            PlayButtonImageView.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/play.png"))));
        }
    }

    public void playSound() {
        ArrayList<ArrayList<Double>> frequencies = new ArrayList<>();
        for (TreeNode<Data> rootNode : pitchData) {
            frequencies.add(rootNode.returnFrequencies());
            int rootIndex = pitchData.indexOf(rootNode);
            for (int i = 0; i < frequencies.getLast().size(); i++) {
                System.out.println(pitch.get(rootIndex)[1]);
                if (pitch.get(rootIndex)[1] > 0) {
                    frequencies.getLast().set(i, frequencies.getLast().get(i) * Math.pow(3.0000 / 2, pitch.get(rootIndex)[1]));
                } else if (pitch.get(rootIndex)[1] < 0) {
                    frequencies.getLast().set(i, frequencies.getLast().get(i) * Math.pow(2.0000 / 3, Math.abs(pitch.get(rootIndex)[1])));
                } else if (pitch.get(rootIndex)[2] > 0) {
                    frequencies.getLast().set(i, frequencies.getLast().get(i) * Math.pow(5.0000 / 4, pitch.get(rootIndex)[2]));
                } else if (pitch.get(rootIndex)[2] < 0) {
                    frequencies.getLast().set(i, frequencies.getLast().get(i) * Math.pow(4.0000 / 5, Math.abs(pitch.get(rootIndex)[2])));
                } else if (pitch.get(rootIndex)[3] > 0) {
                    frequencies.getLast().set(i, frequencies.getLast().get(i) * Math.pow(7.0000 / 4, pitch.get(rootIndex)[3]));
                } else if (pitch.get(rootIndex)[3] < 0) {
                    frequencies.getLast().set(i, frequencies.getLast().get(i) * Math.pow(4.0000 / 7, Math.abs(pitch.get(rootIndex)[3])));
                } else if (pitch.get(rootIndex)[4] > 0) {
                    frequencies.getLast().set(i, frequencies.getLast().get(i) * Math.pow(11.0000 / 8, pitch.get(rootIndex)[4]));
                } else if (pitch.get(rootIndex)[4] < 0) {
                    frequencies.getLast().set(i, frequencies.getLast().get(i) * Math.pow(8.0000 / 11, Math.abs(pitch.get(rootIndex)[4])));
                }
            }
        }
        soundPlayer = new SoundPlayer(
                synType.getSelectionModel().getSelectedIndex(),
                Integer.parseInt(timeField.getText()),
                frequencies
        );
        soundPlayer.setOnSucceeded(event -> {
            isPlaying = false;
            PlayButtonImageView.setImage(new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/play.png"))));
        });
        Thread synThread = new Thread(soundPlayer);
        synThread.setDaemon(true);
        synThread.start();


    }
}