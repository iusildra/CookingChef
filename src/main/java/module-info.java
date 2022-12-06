module com.example.App {
	requires javafx.controls;
	requires javafx.fxml;

	requires org.controlsfx.controls;
	requires com.dlsc.formsfx;
	requires net.synedra.validatorfx;
	requires org.kordamp.ikonli.javafx;
	requires org.kordamp.bootstrapfx.core;

	opens com.cookingchef to javafx.fxml;
	exports com.cookingchef;
}