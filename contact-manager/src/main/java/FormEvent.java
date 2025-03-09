/**
 * Form event to pass data between the form and main frame
 */
public class FormEvent {
    private String actionCommand;
    private Contact contact;

    public FormEvent(String actionCommand, Contact contact) {
        this.actionCommand = actionCommand;
        this.contact = contact;
    }

    public String getActionCommand() {
        return actionCommand;
    }

    public Contact getContact() {
        return contact;
    }
}