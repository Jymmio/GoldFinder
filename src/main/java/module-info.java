module com.example.goldfinder {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;


    opens com.example.goldfinder to javafx.fxml;
    exports com.example.goldfinder;
}