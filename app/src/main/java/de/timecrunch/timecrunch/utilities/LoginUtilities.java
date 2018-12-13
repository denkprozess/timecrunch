package de.timecrunch.timecrunch.utilities;

public class LoginUtilities {
    public static boolean isEmailValid(String email) {
        return email.contains("@");
    }

    public static boolean isPasswordValid(String password) {
        if (password == null) {
            return false;
        }
        return password.length() > 4;
    }

    public static boolean doPasswordsMatch(String password1, String password2) {
        return password1.equals(password2);
    }
}
