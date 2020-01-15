package ca.shehryar.mobileapprestfulws.ui.model.response;

public enum ErrorMessages {
    MISSING_REQUIRED_FIELD("Missing field required"),
    RECORD_ALREADY_EXISTS("Record already exists"),
    INTERNAL_SERVER_ERROR("Internal server error"),
    NO_RECORD_FOUND("No record found with provided ID"),
    AUTHENTICATION_FAILED("Failed to authenticate"),
    COULD_NOT_UPDATE_RECORD("Could not update record"),
    COULD_NOT_DELETE_RECORD("Could not delete record"),
    EMAIL_NOT_VERIFIED("Email address not verified");

    private String errorMessage;

    ErrorMessages(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
