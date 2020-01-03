package com.quasiris.qsc.reader;


import java.io.Serializable;


public class FeedingQueue implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String documentId;

    private String batchId;

    private String operation;

    private String payload;

    private String referenceType;

    private String reference;

    private String status;

    private String checkSum;

    private Long size = 0L;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "FeedingQueue{" +
                "id=" + id +
                ", documentId='" + documentId + '\'' +
                ", batchId='" + batchId + '\'' +
                ", operation='" + operation + '\'' +
                ", payload='" + payload + '\'' +
                ", referenceType='" + referenceType + '\'' +
                ", reference='" + reference + '\'' +
                ", status='" + status + '\'' +
                ", checkSum='" + checkSum + '\'' +
                ", size=" + size +
                '}';
    }
}
