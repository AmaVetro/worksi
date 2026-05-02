package cl.duoc.worksi.validation;

public final class RutRules {
  private RutRules() {}

  public static String normalize(String raw) {
    if (raw == null) {
      return "";
    }
    return raw.trim().replace(".", "");
  }

  public static boolean isValidChileRut(String raw) {
    String n = normalize(raw);
    if (n.isEmpty()) {
      return false;
    }
    int dash = n.lastIndexOf('-');
    if (dash < 1 || dash != n.length() - 2) {
      return false;
    }
    String body = n.substring(0, dash);
    String dv = n.substring(dash + 1);
    if (!body.matches("[0-9]{7,8}") || !dv.matches("[0-9kK]")) {
      return false;
    }
    int factor = 2;
    int sum = 0;
    for (int i = body.length() - 1; i >= 0; i--) {
      sum += Character.digit(body.charAt(i), 10) * factor;
      factor = factor == 7 ? 2 : factor + 1;
    }
    int rest = 11 - (sum % 11);
    char expected;
    if (rest == 11) {
      expected = '0';
    } else if (rest == 10) {
      expected = 'K';
    } else {
      expected = (char) ('0' + rest);
    }
    char got = Character.toUpperCase(dv.charAt(0));
    return expected == got;
  }
}
