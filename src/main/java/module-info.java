module net.murasakiyamaimo.valinomcluinata {
    requires javafx.controls;
    requires javafx.fxml;

    requires javafx.web;
    requires java.desktop;

    opens net.murasakiyamaimo.valinomcluinata to javafx.fxml;
    exports net.murasakiyamaimo.valinomcluinata;
}