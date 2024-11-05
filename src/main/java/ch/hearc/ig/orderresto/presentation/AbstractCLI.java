package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Address;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractCLI {

    private static final Scanner scanner = new Scanner(System.in);
    private static final PrintStream printStream = System.out;

    protected void ln(String text) {
        printStream.println(text);
    }

    protected int readIntFromUser(int maxChoice) {
        return this.readIntFromUser(0, maxChoice);
    }

    protected int readIntFromUser(int minChoice, int maxChoice) {
        int choice = -1;
        while (choice < minChoice || choice > maxChoice) {
            try {
                choice = scanner.nextInt();
                if (choice < minChoice || choice > maxChoice) {
                    this.ln(String.format("Veuillez choisir un nombre entre %d et %d.", minChoice, maxChoice));
                }
            } catch (InputMismatchException e) {
                this.ln("Veuillez entrer un nombre entier.");
            } finally {
                scanner.nextLine();
            }
        }
        return choice;
    }

    protected String readStringFromUser(int minLength, int maxLength, String useDefault) {
        String input = null;
        while (input == null) {
            try {
                input = scanner.nextLine();
                if (input.isEmpty() && useDefault != null) {
                    return useDefault;
                } else if (input.length() < minLength) {
                    this.ln(String.format("La chaîne doit faire au moins %d charactères.", minLength));
                    input = null;
                }
                assert input != null;
                if (input.length() > maxLength) {
                    this.ln(String.format("La chaîne doit faire au plus %d charactères.", maxLength));
                    input = null;
                }
            } catch (InputMismatchException e) {
                this.ln("Veuillez entrer une chaîne.");
                input = null;
            }
        }
        return input;
    }

    protected String readStringFromUser() {
        return this.readStringFromUser(0, 255, null);
    }

    protected String readStringFromUserAllowEmpty() {
        return this.readStringFromUser(0, 255, null);
    }


    protected String readEmailFromUser() {
        String input = null;
        while (input == null) {
            input = this.readStringFromUser();
            Pattern pattern = Pattern.compile("^\\S+@\\S+$", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (!matcher.find()) {
                input = null;
                this.ln("Veuillez saisir une addresse email.");
            }
        }
        return input;
    }

    protected String readChoicesFromUser(String[] choices) {
        String input = null;
        while (input == null) {
            String inputString = this.readStringFromUser();
            input = Arrays.stream(choices).filter(choice -> choice.equals(inputString)).findFirst().orElse(null);
            if (input == null) {
                this.ln(String.format("Veuillez choisir une option (%s).", String.join(", ", choices)));
            }
        }
        return input;
    }

    protected Long readLongFromUser() {
        Long input = null;
        while (input == null) {
            try {
                input = scanner.nextLong();
            } catch (InputMismatchException e) {
                this.ln("Veuillez entrer un nombre entier.");
                input = null;
            } finally {
                scanner.nextLine();
            }
        }
        return input;
    }

    protected Address readAddressFromUser() {
        this.ln("Code du pays : ");
        String countryCode = this.readStringFromUser();
        this.ln("Code postal : ");
        String postalCode = this.readStringFromUser();
        this.ln("Localité : ");
        String locality = this.readStringFromUser();
        this.ln("Rue : ");
        String street = this.readStringFromUser();
        this.ln("Numéro de rue : ");
        String streetNumber = this.readStringFromUser();

        return new Address(countryCode, postalCode, locality, street, streetNumber);
    }

    protected boolean readBooleanFromUser() {
        String input = null;
        while (input == null) {
            input = this.readStringFromUser();
            if (!input.equalsIgnoreCase("oui") && !input.equalsIgnoreCase("non")) {
                this.ln("Veuillez répondre par 'oui' ou 'non'.");
                input = null;
            }
        }
        return input.equalsIgnoreCase("oui");

    }
}
