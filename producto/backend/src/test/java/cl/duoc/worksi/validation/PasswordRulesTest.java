package cl.duoc.worksi.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PasswordRulesTest {

  @Test
  void acceptsPasswordMeetingPolicy() {
    assertTrue(PasswordRules.matches("Aa!1234567890"));
  }

  @Test
  void rejectsTooShortPassword() {
    assertFalse(PasswordRules.matches("Aa!1"));
  }

  @Test
  void rejectsWithoutUppercase() {
    assertFalse(PasswordRules.matches("aa!1234567890"));
  }

  @Test
  void rejectsWithoutLowercase() {
    assertFalse(PasswordRules.matches("AA!1234567890"));
  }

  @Test
  void rejectsWithoutDigit() {
    assertFalse(PasswordRules.matches("Aa!abcdefghi"));
  }

  @Test
  void rejectsWithoutSymbol() {
    assertFalse(PasswordRules.matches("Aa1234567890"));
  }

  @Test
  void rejectsNullPassword() {
    assertFalse(PasswordRules.matches(null));
  }
}
