/**
 * A concrete implementation of the Output Service for a Command Line Interface (CLI).
 * <p>
 * <b>Architectural Role:</b>
 * This class serves as the **Output Boundary**. It isolates the rest of the application
 * from the specific mechanics of printing to a console.
 * </p>
 * <p>
 * <b>Design Principles:</b>
 * <ul>
 * <li><b>Single Responsibility Principle (SRP):</b> Its only job is to display text.
 * It removes "printing logic" from Services, keeping them focused on business rules.</li>
 * <li><b>Dependency Inversion:</b> High-level modules (like {@link ApplicationService})
 * depend on the {@link IOutputService} interface, not this concrete class directly.</li>
 * </ul>
 * </p>
 */
// Single Responsibility - handles only console output
public class ConsoleOutputService implements IOutputService {
    @Override
    /**
     * Displays a standard informational message to the user.
     * <p>
     * Uses standard output stream ({@code System.out}).
     * </p>
     *
     * @param message The text string to display.
     */
    public void displayMessage(String message) {
        System.out.println(message);
    }
    /**
     * Displays an error message to the user.
     * <p>
     * <b>Visual Distinction:</b>
     * Uses the standard error stream ({@code System.err}), which often appears in red
     * in modern IDE consoles (like Eclipse or IntelliJ). This provides immediate
     * visual feedback that something went wrong.
     * </p>
     * <p>
     * It also prefixes the message with "ERROR: " to ensure clarity in log files
     * where colors might not be available.
     * </p>
     *
     * @param error The error description to display.
     */
    @Override
    public void displayError(String error) {
        System.err.println("ERROR: " + error);
    }
}