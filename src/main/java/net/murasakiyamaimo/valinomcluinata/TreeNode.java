package net.murasakiyamaimo.valinomcluinata;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class TreeNode<T extends Data> {
    private T data;
    private TreeNode<T> parent;
    private List<TreeNode<T>> children;
    private Double[] coordinate = new Double[2];
    private static final AtomicInteger nextID = new AtomicInteger(0);
    private final int id;

    public TreeNode(T data) {
        this.data = data;
        this.children = new ArrayList<>();
        this.parent = null;
        this.id = nextID.getAndIncrement();
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    public int getID() {
        return id;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public void addChild(TreeNode<T> child) {
        children.add(child);
        child.parent = this;
    }

    public Double getCoordinateX() {
        return coordinate[0];
    }

    public Double getCoordinateY() {
        return coordinate[1];
    }

    public Double[] getCoordinate() {
        return coordinate;
    }

    public void setCoordinateX(Double X) {
        this.coordinate[0] = X;
    }

    public void setCoordinateY(Double Y) {
        this.coordinate[1] = Y;
    }

    public List<Integer> getIDPath() {
        List<Integer> path = new LinkedList<>();
        TreeNode<T> current = this;
        while (current != null) {
            path.addFirst(current.id);
            current = current.getParent();
        }
        return path;
    }

    public TreeNode<T> getRoot() {
        TreeNode<T> current = this;
        while (current.getParent() != null) {
            current = current.getParent();
        }
        return current;
    }

    public void drawPitch(GraphicsContext gc) {
        double nodeCoordinateX = this.getCoordinateX();
        double nodeCoordinateY = this.getCoordinateY();
        for (TreeNode<T> child : this.getChildren()) {
            System.out.println(child.getCoordinateY());
            if (child.getData().getDimension() == 2 || child.getData().getDimension() == 3) {
                DrawLine.draw(nodeCoordinateX, nodeCoordinateY, gc, child.getData().isUp(), child.getData().getDimension());
                gc.drawImage(DrawLine.pitch_line, child.getCoordinateX(), child.getCoordinateY() - 2.75);
            }else {
                if (child.getData().isUp()) {
                    gc.drawImage(DrawLine.pitch_line, child.getCoordinateX(), child.getCoordinateY());
                }else {
                    gc.drawImage(DrawLine.pitch_line, child.getCoordinateX(), child.getCoordinateY() - 6);
                }
                DrawLine.draw(nodeCoordinateX, nodeCoordinateY, gc, child.getData().isUp(), child.getData().getDimension());
            }
            child.drawPitch(gc);
        }
    }

    public void rootCoordinate(double howUp) {
        for (TreeNode<T> child : this.getChildren()) {
            child.setCoordinateY(child.getCoordinateY() + howUp);
            child.rootCoordinate(howUp);
        }
    }

    public TreeNode<T> getNodeByPath(List<Integer> path) {
        if (path.isEmpty()) {
            return null;
        } else if (path.size() == 1) {
            return this;
        }

        List<Integer> remainingPath = path.subList(1, path.size());
        TreeNode<T> currentNode = this;
        for (int id : remainingPath) {
            TreeNode<T> nextNode = null;
            for (TreeNode<T> child : currentNode.getChildren()) {
                if (child.getID() == id) {
                    nextNode = child;
                    break;
                }
            }
            if (nextNode == null) {
                return null; // パスに一致する子ノードが見つからなかった
            }
            currentNode = nextNode;
        }
        return currentNode;
    }

    public TreeNode<T> SearchCoordinate(double X, double Y) {
        if (X < coordinate[0] + 160 && X > coordinate[0] - 15 && Math.abs(coordinate[1] -Y) < 3) {
            return this;
        }else {
            for (TreeNode<T> child : children) {
                if (child.SearchCoordinate(X, Y) == child) {
                    return child;
                }
            }
        }
        return null;
    }
}

class Data {
    private int dimension;
    private boolean isUp;
    private boolean isMuted;

    public Data(int dimension, boolean isUp, boolean isMuted) {
        this.dimension = dimension;
        this.isUp = isUp;
        this.isMuted = isMuted;
    }

    public int getDimension() {
        return dimension;
    }

    public boolean isUp() {
        return isUp;
    }

    public boolean isMuted() {
        return isMuted;
    }

}

class DrawLine {
    public static final Image pitch_line = new Image(Objects.requireNonNull(EditorController.class.getResourceAsStream("images/pitch-line.png")));

    public static void draw(double rootX, double rootY, GraphicsContext gc, boolean isUp, int dimension) {
        if (dimension == 2) {
            gc.setFill(Color.web("#f27992"));
            if (isUp) {
                gc.fillRect(rootX, rootY - 160, 16, 160);
            } else {
                gc.fillRect(rootX, rootY, 16, 160);
            }
        } else if (dimension == 3) {
            gc.setFill(Color.web("#6cd985"));
            if (isUp) {
                gc.fillRect(rootX + 173 - 16, rootY - 90, 16, 90);
            } else {
                gc.fillRect(rootX + 173 - 16, rootY, 16, 90);
            }
        } else if (dimension == 4) {
            gc.setFill(Color.web("#b795e9"));
            double[] xPoints = {rootX, rootX + 177 - 16, rootX + 177, rootX + 16};
            double[] yPoints;
            if (isUp) {
                yPoints = new double[]{rootY - 2.75, rootY - 220 - 2.75, rootY - 220 - 2.75, rootY - 2.75};
            } else {
                xPoints = new double[]{rootX, rootX + 173 - 16, rootX + 173, rootX + 16};
                yPoints = new double[]{rootY + 220 - 2.75, rootY - 2.75, rootY - 2.75, rootY + 220 - 2.75};
            }
            gc.fillPolygon(xPoints, yPoints, 4);
        } else if (dimension == 5) {
            gc.setFill(Color.web("#ffc247"));
            double[] xPoints = {rootX + 173 - 16, rootX - 4, rootX - 4 + 16, rootX + 173};
            double[] yPoints;
            if (isUp) {
                yPoints = new double[]{rootY - 2.75, rootY - 392 - 2.75, rootY - 392 - 2.75, rootY - 2.75};
            } else {
                xPoints = new double[]{rootX + 173 - 16, rootX, rootX + 16, rootX + 173};
                yPoints = new double[]{rootY + 392 - 2.75, rootY - 2.75, rootY - 2.75, rootY + 392 - 2.75};
            }
            gc.fillPolygon(xPoints, yPoints, 4);
        }
    }
}