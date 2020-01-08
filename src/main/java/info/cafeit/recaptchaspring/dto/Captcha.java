package info.cafeit.recaptchaspring.dto;

public class Captcha {
    private Boolean success;
    private Double score;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public boolean isHuman() {
        return this.score > 0.5;
    }
}
