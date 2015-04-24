package gr.ntua.ece.sevle.sentimentit.sentimentit.entities;

/**
 * Created by Sevle on 4/1/2015.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Map;
import java.util.HashMap;

    public class UserVotedData {

        @Expose
        private double sos;

        @Expose
        @SerializedName("uservoteddataPK")
        private UserVotedDataPK uservoteddataPK;

        @Expose
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        /**
         *
         * @return
         * The sos
         */
        public double getSos() {
            return sos;
        }

        /**
         *
         * @param sos
         * The sos
         */
        public void setSos(double sos) {
            this.sos = sos;
        }

        /**
         *
         * @return
         * The uservoteddataPK
         */
        public UserVotedDataPK getUservoteddataPK() {
            return uservoteddataPK;
        }

        /**
         *
         * @param uservoteddataPK
         * The uservoteddataPK
         */
        public void setUservoteddataPK(UserVotedDataPK uservoteddataPK) {
            this.uservoteddataPK = uservoteddataPK;
        }

        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

    }


