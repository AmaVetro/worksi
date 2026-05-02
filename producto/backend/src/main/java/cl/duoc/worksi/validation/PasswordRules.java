package cl.duoc.worksi.validation;

import java.util.regex.Pattern;

public final class PasswordRules {
  private static final Pattern POLICY =
      Pattern.compile(
          "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{10,}$");

  private PasswordRules() {}

  public static boolean matches(String password) {
    return password != null && POLICY.matcher(password).matches();
  }
}
