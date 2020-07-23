package scot.gov.publications.rest;


import scot.gov.publications.repo.Publication;

public class UploadResponse {

    // was the upload accepted?
    private boolean accepted;

    // message - 'acepted' if it was accetped and an reason string if it was not
    private String message;

    private Publication publication;

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    public static UploadResponse error(String message) {
        UploadResponse response = new UploadResponse();
        response.setAccepted(false);
        response.setMessage(message);
        return response;
    }

    public static UploadResponse accepted(Publication publication) {
        UploadResponse response = new UploadResponse();
        response.setAccepted(true);
        response.setMessage("accepted");
        response.setPublication(publication);
        return response;
    }
}