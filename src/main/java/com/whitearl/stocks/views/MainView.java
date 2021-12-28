package com.whitearl.stocks.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

/**
 * Main screen for the application.
 * 
 * @author WhitearL
 *
 */
@Route("")
public class MainView extends VerticalLayout {

    private static final long serialVersionUID = -7477807136719162992L;

    // Form Elements ---
    private H2 h2Title;
    private TextField txtFirstName;
    private TextField txtSurname;
    private TextField txtAge;
    private TextField txtBMI;
    private TextField txtNumberOfChildren;
    private Checkbox chkSmokerStatus;
    private Button btnPredict;
    private ComboBox<String> cmbSex;
    private ComboBox<String> cmbRegion;

    private Span spnErrorMsg;
    private FormLayout formLayout;
    // Form Elements ---

    /**
     * Public constructor, allow instantiation Constructs UI
     */
    public MainView() {

	configureFormElements();
	configureFormLayout();

	add(formLayout);

    }

    /**
     * Configure text boxes, buttons and other elements.
     */
    private void configureFormElements() {
	h2Title = new H2("Enter health details");

	txtFirstName = new TextField("First name");
	txtSurname = new TextField("Surname");
	txtAge = new TextField("Age");

	cmbSex = new ComboBox<>("Sex", "Male", "Female");
	cmbSex.setAllowCustomValue(true);

	cmbRegion = new ComboBox<>("Region", "North-East", "North-West", "South-East", "South-West");
	cmbRegion.setAllowCustomValue(true);

	txtBMI = new TextField("BMI");
	txtNumberOfChildren = new TextField("BMI");

	chkSmokerStatus = new Checkbox("Smoker?");
	chkSmokerStatus.getStyle().set("padding-top", "10px");

	spnErrorMsg = new Span();
	spnErrorMsg.getStyle().set("color", "var(--lumo-error-text-color)");
	spnErrorMsg.getStyle().set("padding", "15px 0");

	btnPredict = new Button("Predict Insurance Premium");
	btnPredict.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    }

    /**
     * Lay out the elements into two columns, with the submit button at the bottom.
     */
    private void configureFormLayout() {
	formLayout = new FormLayout(h2Title, txtFirstName, txtSurname, txtAge, txtBMI, cmbSex, cmbRegion,
		chkSmokerStatus, txtNumberOfChildren, spnErrorMsg, btnPredict);

	formLayout.setMaxWidth("500px");
	formLayout.getStyle().set("margin", "0 auto");

	formLayout.setResponsiveSteps(
		new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
		new FormLayout.ResponsiveStep("490px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));
	formLayout.setColspan(h2Title, 2);
	formLayout.setColspan(spnErrorMsg, 2);
	formLayout.setColspan(btnPredict, 2);
    }
}
