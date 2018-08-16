package com.ssii.demogeofences2.Objects;

/**
 * Created by Ague on 16/08/2018.
 */

public class ShownConcept {

    public String getAppearanceTime() {
        return appearanceTime;
    }

    public void setAppearanceTime(String appearanceTime) {
        this.appearanceTime = appearanceTime;
    }

    public String getShowTextTime() {
        return showTextTime;
    }

    public void setShowTextTime(String showTextTime) {
        this.showTextTime = showTextTime;
    }

    public int getWeigth() {
        return weigth;
    }

    public void setWeigth(int weigth) {
        this.weigth = weigth;
    }

    private String appearanceTime;
    private String showTextTime;
    private int weigth;

    public ShownConcept(String appearanceTime, String showTextTime, int weigth) {
        this.appearanceTime = appearanceTime;
        this.showTextTime = showTextTime;
        this.weigth = weigth;
    }
}
