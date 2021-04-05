package com.example.healthza;

public class Disease {

    String diseaseName;
    String diseaseType;
    String diagnosisDate;
    boolean inherited;

    public Disease(String diseaseName, String diseaseType, String diagnosisDate, boolean inherited) {
        this.diseaseName = diseaseName;
        this.diseaseType = diseaseType;
        this.diagnosisDate = diagnosisDate;
        this.inherited = inherited;
    }

    public Disease() {

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

    public String getDiagnosisDate() {
        return diagnosisDate;
    }

    public void setDiagnosisDate(String diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
    }

    public boolean isInherited() {
        return inherited;
    }

    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }
}
