open module org.alver415.minecraft.server.wrapper {
	requires transitive javafx.controls;
	requires transitive javafx.fxml;
	requires transitive org.apache.commons.collections4;
	requires transitive org.apache.commons.lang3;
	requires transitive com.fasterxml.jackson.annotation;
	requires transitive org.apache.commons.io;
	requires transitive com.fasterxml.jackson.databind;
	requires transitive org.apache.logging.log4j;
	requires transitive org.alver415.javafx.custom.components;
	requires transitive org.controlsfx.controls;

	exports org.alver415.minecraft.server.wrapper;
	exports org.alver415.minecraft.server.wrapper.model;
	exports org.alver415.minecraft.server.wrapper.server;
	exports org.alver415.minecraft.server.wrapper.create;
}