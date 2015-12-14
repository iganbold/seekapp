package net.bluemix.seek.model;

public class Uid {

    private String uid;
    private double confidence;
    private String prediction;
    private PhotoModel model;

    public PhotoModel getModel() {
        return model;
    }

    public void setModel(PhotoModel model) {
        this.model = model;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getPrediction() {
        return prediction;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }

    @Override
    public String toString() {
        return "Uid{" +
                "uid='" + uid + '\'' +
                ", confidence=" + confidence +
                ", prediction='" + prediction + '\'' +
                ", model=" + model +
                '}';
    }
}
