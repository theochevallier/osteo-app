module org.example.ostheo_projet {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    requires org.hibernate.orm.core;

    requires jakarta.persistence;

    requires java.sql;

    requires org.postgresql.jdbc;

    opens org.example.ostheo_projet.model to
            org.hibernate.orm.core,
            javafx.base;

    opens org.example.ostheo_projet.ui.controller to javafx.fxml;

    exports org.example.ostheo_projet;

}