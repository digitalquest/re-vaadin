package hello;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class CustomerEditor extends VerticalLayout implements KeyNotifier {
    private final CustomerRepository repository;
    private Customer customer;
    TextField firstName = new TextField("First name");
    TextField lastName = new TextField("Last name");
    Button save = new Button("Save", VaadinIcon.CHECK.create());
    Button cancel = new Button("Cancel");
    Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

    Binder<Customer> binder = new Binder<>(Customer.class);

    private ChangeHandler changeHandler;

    public CustomerEditor(CustomerRepository repository) {
        this.repository = repository;
        add(firstName, lastName, actions);
        binder.bindInstanceFields(this);
        setSpacing(true);
        save.getElement().getThemeList().add("primary");
        delete.getElement().getThemeList().add("error");
        addKeyPressListener(Key.ENTER, e -> save());
        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> editCustomer(customer));
        setVisible(false);
    }


    public interface ChangeHandler {
        void onChange();
    }

    public void setChangeHandler(ChangeHandler changeHandler) {
        this.changeHandler = changeHandler;
    }

    void editCustomer(Customer c) {
        final boolean persisted = c.getId() != null;
        if (persisted) {
            // Find fresh entity for editing
            customer = repository.findById(c.getId()).get();
        }
        else {
            customer = c;
        }
        cancel.setVisible(persisted);
        binder.setBean(customer);
        setVisible(true);
        firstName.focus();
    }

    private void delete() {
        repository.delete(customer);
        changeHandler.onChange();
    }

    private void save() {
        repository.save(customer);
        changeHandler.onChange();
    }
}
