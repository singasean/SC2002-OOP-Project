// Single Responsibility - handles only console output
public class ConsoleOutputService implements IOutputService {
    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void displayError(String error) {
        System.err.println("ERROR: " + error);
    }
}