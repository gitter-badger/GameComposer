package de.mirkosertic.gamecomposer.objectinspector.utils;

import de.mirkosertic.gameengine.type.Speed;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

public class SpeedPropertyEditor implements PropertyEditor<Speed> {

    private Speed value;
    private final HBox editor;
    private final TextField inputfield;
    private PropertySheet.Item item;
    private final ValidationSupport validationSupport;

    public SpeedPropertyEditor(PropertySheet.Item aItem) {

        item = aItem;

        inputfield = new TextField();
        inputfield.setMaxWidth(50);
        inputfield.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    if (!validationSupport.isInvalid()) {
                        value = new Speed(Long.parseLong(inputfield.getText()));
                        item.setValue(value);
                    }
                }
            }
        });

        validationSupport = new ValidationSupport();

        Validator theValidator = Validator.combine(
                Validator.createEmptyValidator("A value is required"),
                Validator.createPredicateValidator(new LongPredicate(), "Value is not a valid number")
        );

        validationSupport.registerValidator(inputfield, theValidator);

        editor = new HBox();
        editor.setAlignment(Pos.BASELINE_LEFT);
        editor.getChildren().add(inputfield);
    }

    @Override
    public Node getEditor() {
        return editor;
    }

    @Override
    public Speed getValue() {
        return value;
    }

    @Override
    public void setValue(Speed aValue) {
        value = aValue;
        inputfield.setText("" + value.speed);
        validationSupport.redecorate();
    }
}