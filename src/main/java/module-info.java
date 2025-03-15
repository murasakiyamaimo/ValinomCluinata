module net.murasakiyamaimo.valinomcluinata {
    requires javafx.controls;
    requires javafx.fxml;

    requires javafx.web;

    opens net.murasakiyamaimo.valinomcluinata to javafx.fxml;
    exports net.murasakiyamaimo.valinomcluinata;
}