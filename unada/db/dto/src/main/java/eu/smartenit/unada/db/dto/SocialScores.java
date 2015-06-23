/**
 * Copyright (C) 2014 The SmartenIT consortium (http://www.smartenit.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.smartenit.unada.db.dto;

import java.io.Serializable;

/**
 * Created by geopet on 4/20/15.
 */
public class SocialScores implements Serializable{


    public SocialScores() {
    }

    public SocialScores(long contentID, double alpha, double delta,
                        double eta, double phi, double gamma) {
        this.contentID = contentID;
        this.alpha = alpha;
        this.delta = delta;
        this.eta = eta;
        this.phi = phi;
        this.gamma = gamma;
    }

    private long contentID;

    private double alpha;

    private double delta;

    private double eta;

    private double phi;

    private double gamma;


    public long getContentID() {
        return contentID;
    }

    public void setContentID(long contentID) {
        this.contentID = contentID;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getDelta() {
        return delta;
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }

    public double getEta() {
        return eta;
    }

    public void setEta(double eta) {
        this.eta = eta;
    }

    public double getPhi() {
        return phi;
    }

    public void setPhi(double phi) {
        this.phi = phi;
    }

    public double getGamma() {
        return gamma;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }


    @Override
    public String toString() {
        return "SocialScores{" +
                "contentID=" + contentID +
                ", alpha=" + alpha +
                ", delta=" + delta +
                ", eta=" + eta +
                ", phi=" + phi +
                ", gamma=" + gamma +
                '}';
    }
}
