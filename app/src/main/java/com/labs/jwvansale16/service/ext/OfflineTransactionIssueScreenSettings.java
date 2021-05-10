package com.labs.jwvansale16.service.ext;

import android.os.Parcel;
import com.sap.cloud.mobile.fiori.onboarding.ext.ScreenSettings;

import androidx.annotation.NonNull;

public class OfflineTransactionIssueScreenSettings extends ScreenSettings {
    private String title;
    private String label;
    private String instruction;
    private String instruction2;
    private String buttonText;

    private OfflineTransactionIssueScreenSettings(@NonNull Parcel parcel) {
        title = parcel.readString();
        label = parcel.readString();
        instruction = parcel.readString();
        instruction2 = parcel.readString();
        buttonText = parcel.readString();
    }

    private OfflineTransactionIssueScreenSettings(Builder builder) {
        title = builder.title;
        label = builder.label;
        instruction = builder.instruction;
        instruction2 = builder.instruction2;
        buttonText = builder.buttonText;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int flags) {
        parcel.writeString(title);
        parcel.writeString(label);
        parcel.writeString(instruction);
        parcel.writeString(instruction2);
        parcel.writeString(buttonText);
    }

    public String getTitle() {
        return title;
    }

    public String getLabel() {
        return label;
    }

    public String getInstruction() {
        return instruction;
    }

    public String getInstruction2() {
        return instruction2;
    }

    public String getButtonText() {
        return buttonText;
    }

    /**
     * The builder of <code>OfflineTransactionIssueScreenSettings</code>
     */
    public static class Builder {
        private String title;
        private String label;
        private String instruction;
        private String instruction2;
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

        public Builder setInstruction2(String instruction2){
            this.instruction2 = instruction2;
            return this;
        }

        public Builder setButtonText(String buttonText) {
            this.buttonText = buttonText;
            return this;
        }

        /**
         * Build a <code>OfflineTransactionIssueScreenSettings</code>
         *
         * @return the transaction issue screen settings.
         */
        public OfflineTransactionIssueScreenSettings build(){
            return new OfflineTransactionIssueScreenSettings(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * The parcel creator.
     */
    public static final Creator<OfflineTransactionIssueScreenSettings> CREATOR = new Creator<OfflineTransactionIssueScreenSettings>() {
        @NonNull
        @Override
        public OfflineTransactionIssueScreenSettings createFromParcel(Parcel in) {
            return new OfflineTransactionIssueScreenSettings ( in);
        }

        @NonNull
        @Override
        public OfflineTransactionIssueScreenSettings [] newArray (int size) {
            return new OfflineTransactionIssueScreenSettings [size];
        }
    };
}