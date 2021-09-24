package be.vlaio.dosis.connector.pusher.dosis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DosisDossierUploadStatusTO {

    @JsonProperty("Status")
    private String status;
    @JsonProperty("UploadId")
    private String uploadId;

    public DosisDossierUploadStatusTO(String status, String uploadId) {
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
