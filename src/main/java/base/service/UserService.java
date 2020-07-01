package base.service;

import javax.inject.Inject;

public class UserService {

  @Inject
  public UserService() {
    ////
  }

  public void newJWTTokenForEmail(String url, String email) {
    // Add sending mail to queue (make this a queued service)
    // --queued--
    // Ensure db user record or create one
    // Make JWT
    // Create link based on request data: need base url + ?jwt=xxx
    // Send the email with link
  }


}
