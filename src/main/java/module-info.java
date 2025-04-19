module net.murasakiyamaimo.valinomcluinata {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.desktop;
    requires jdk.compiler;

    opens net.murasakiyamaimo.valinomcluinata to javafx.fxml;
    exports net.murasakiyamaimo.valinomcluinata;
}