package com.labs.jwvansale16.service.ext;

import android.os.Parcel;
import com.sap.cloud.mobile.fiori.onboarding.ext.ScreenSettings;

import androidx.annotation.NonNull;

public class OfflineNetworkErrorScreenSettings extends ScreenSettings {
    private String title;
    private String label;
    private String instruction;
    private String buttonText;


    private OfflineNetworkErrorScreenSettings(@NonNull Parcel parcel){
        title = parcel.readString();
        label = parcel.readString();
        instruction = parcel.readString();
        buttonText = parcel.readString();
    }

    private OfflineNetworkErrorScreenSettings(Builder builder) {
        title = builder.title;
        label = builder.label;
        instruction = builder.instruction;
        buttonText = builder.buttonText;
    }

    public String getTitle() {
        return title;
    }

    public String getInstruction() {
        return instruction;
    }

    public String getLabel() {
        return label;
    }

    public String getButtonText() {
        return buttonText;
    }

    /**
     * The builder of <code>OfflineNetworkErrorScreenSettings</code>
     */
    public static class Builder {
        private String title;
        private String label;
        private String instruction;
        private String buttonText;


        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setLabel(String label) {
            this.label = label;
            return this;
        }

        public Builder setInstruction(String instruction) {
            this.instruction = instruction;
            return this;
        }

        public Builder setButtonText(String buttonText) {
            this.buttonText = buttonText;
            return this;
        }

        /**
         * Build a <code>OfflineNetworkErrorScreenSettings</code>
         *
         * @return the network error screen settings.
         */
        public OfflineNetworkErrorScreenSettings build() {
            return new OfflineNetworkErrorScreenSettings(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(label);
        dest.writeString(instruction);
        dest.writeString(buttonText);
    }

    /**
     * The parcel creator.
     */
    public static final Creator<OfflineNetworkErrorScreenSettings> CREATOR = new Creator<OfflineNetworkErrorScreenSettings>() {
        @NonNull
        @Override
        public OfflineNetworkErrorScreenSettings createFromParcel(Parcel in) {
            return new OfflineNetworkErrorScreenSettings(in);
        }

        @NonNull
        @Override
        public OfflineNetworkErrorScreenSettings[] newArray(int size) {
            return new OfflineNetworkErrorScreenSettings[size];
        }
    };

}