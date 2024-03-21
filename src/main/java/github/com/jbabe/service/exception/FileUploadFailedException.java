package github.com.jbabe.service.exception;

import lombok.Getter;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
@Getter
public class FileUploadFailedException extends RuntimeException {

    private final String detailMessage;
    private final String request;

    public FileUploadFailedException(String detailMessage, String request) {
        this.detailMessage = detailMessage;
        this.request = request;
    }
}
