package github.com.jbabe.service.exception;

import org.apache.tomcat.util.http.fileupload.FileUploadException;

public class FileUploadFailedException extends FileUploadException {

    private final String detailMessage;
    private final String request;

    public FileUploadFailedException(String detailMessage, String request) {
        this.detailMessage = detailMessage;
        this.request = request;
    }
}
