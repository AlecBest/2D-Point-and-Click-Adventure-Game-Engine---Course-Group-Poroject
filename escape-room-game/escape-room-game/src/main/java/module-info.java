module ca.uwo.cs2212.group21 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires org.json;

    opens ca.uwo.cs2212.group21 to javafx.fxml;

    exports ca.uwo.cs2212.group21;
}
