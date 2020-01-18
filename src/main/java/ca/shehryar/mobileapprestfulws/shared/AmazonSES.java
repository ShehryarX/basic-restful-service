package ca.shehryar.mobileapprestfulws.shared;

import ca.shehryar.mobileapprestfulws.shared.dto.UserDto;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;

public class AmazonSES {
    final String FROM = "xshehryar@gmail.com";
    final String SUBJECT = "One last step to complete your registration";
    final String HTML_BODY = "<h1>Please verify your email address by clicking this link.</h1>"
            + "<a><href = 'http://localhost:8000/users/verification-service?token=$tokenValue'>Here</a><br/>"
            + "<p>Thank you!</p>";


    final String PASSWORD_RESET_SUBJECT = "Reset your password";
    final String PASSWORD_RESET_HTML_BODY = "<h1>Click this link to reset your password.</h1>"
            + "<a><href = 'http://localhost:8000/users//password-reset-request?token=$tokenValue'>Here</a><br/>"
            + "<p>Thank you!</p>";

    public void verifyEmail(UserDto userDto) {
        AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder
                .standard()
                .withRegion(Regions.US_WEST_1)
                .build();

        String htmlWithToken = HTML_BODY.replace("$tokenValue", userDto.getEmailVerificationToken());

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(userDto.getEmail()))
                .withMessage(new Message().withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(htmlWithToken)))
                        .withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
                .withSource(FROM);

        client.sendEmail(request);
    }

    public boolean sendPasswordResetRequest(String firstName, String email, String token) {
        boolean returnVal = false;

        AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder
                .standard()
                .withRegion(Regions.US_WEST_1)
                .build();

        String htmlBodyWithToken = PASSWORD_RESET_HTML_BODY.replace("$tokenValue", token);
        htmlBodyWithToken = htmlBodyWithToken.replace("$firstName", firstName);

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(email))
                .withMessage(new Message().withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken)))
                        .withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
                .withSource(FROM);

        SendEmailResult result = client.sendEmail(request);

        if (result != null && result.getMessageId() != null && !result.getMessageId().isEmpty()) {
            returnVal = true;
        }

        return returnVal;
    }
}
