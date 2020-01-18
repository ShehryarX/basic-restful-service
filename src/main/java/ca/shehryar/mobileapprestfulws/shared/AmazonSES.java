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
}
