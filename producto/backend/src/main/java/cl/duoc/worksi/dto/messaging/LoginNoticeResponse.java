package cl.duoc.worksi.dto.messaging;

public class LoginNoticeResponse {
  private final LoginNoticeItemResponse item;

  public LoginNoticeResponse(LoginNoticeItemResponse item) {
    this.item = item;
  }

  public LoginNoticeItemResponse getItem() {
    return item;
  }
}
