package com.project.cdh.models;

public class Disease
{
    String diseaseName;
    String diseaseType;
    String detectionDate;
    boolean inherited;

    public Disease(String diseaseName, String diseaseType, String detectionDate, boolean inherited)
    {
        this.diseaseName = diseaseName;
        this.diseaseType = diseaseType;
        this.detectionDate = detectionDate;
        this.inherited = inherited;
    }

    public Disease()
    {

    }


    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getDiseaseType() {
        return diseaseType;
    }

    public void setDiseaseType(String diseaseType) {
        this.diseaseType = diseaseType;
    }

    public String getDetectionDate() {
        return detectionDate;
    }

    public void setDetectionDate(String detectionDate) {
        this.detectionDate = detectionDate;
    }

    public boolean isInherited() {
        return inherited;
    }

    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }
}
