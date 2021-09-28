package be.vlaio.dosis.connector.pusher.dosis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DosisDossierUploadStatusTO {


    private String status;
    private String uploadId;

    public DosisDossierUploadStatusTO(@JsonProperty("Status") String status, @JsonProperty("UploadId") String uploadId) {
        this.status = status;
        this.uploadId = uploadId;
    }

    public String getStatus() {
        return status;
    }

    public String getUploadId() {
        return uploadId;
    }
}
