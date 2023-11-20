package com.mycompany.app;


public class WordpressProps {
    private String mySQLImage;
    private String mySQLPassword;
    private Double mySQLStorage;
    private String wordpressImage;
    private Double wordpressStorage;

    private WordpressProps(String mySQLImage, String mySQLPassword, Double mySQLStorage, String wordpressImage, Double wordpressStorage) {
        this.mySQLImage = mySQLImage;
        this.mySQLPassword = mySQLPassword;
        this.mySQLStorage = mySQLStorage;
        this.wordpressImage = wordpressImage;
        this.wordpressStorage = wordpressStorage;
    }

    public static class Builder {


        private String mySQLImage;
        private String mySQLPassword;
        private Double mySQLStorage;
        private String wordpressImage;
        private Double wordpressStorage;

        public Builder mySQLImage(String mySQLImage) {
            this.mySQLImage = mySQLImage;
            return this;
        }

        public Builder mySQLPassword(String mySQLPassword) {
            this.mySQLPassword = mySQLPassword;
            return this;
        }

        public Builder mySQLStorage(Double mySQLStorage) {
            this.mySQLStorage = mySQLStorage;
            return this;
        }

        public Builder wordpressImage(String wordpressImage) {
            this.wordpressImage = wordpressImage;
            return this;
        }

        public Builder wordpressStorage(Double wordpressStorage) {
            this.wordpressStorage = wordpressStorage;
            return this;
        }

        public WordpressProps build() {
            return new WordpressProps(mySQLImage, mySQLPassword, mySQLStorage, wordpressImage, wordpressStorage);
        }
    }

    // getters and setters

    public String getMySQLImage() {
        return mySQLImage;
    }

    public void setMySQLImage(String mySQLImage) {
        this.mySQLImage = mySQLImage;
    }

    public String getMySQLPassword() {
        return mySQLPassword;
    }

    public void setMySQLPassword(String mySQLPassword) {
        this.mySQLPassword = mySQLPassword;
    }

    public Double getMySQLStorage() {
        return mySQLStorage;
    }

    public void setMySQLStorage(Double mySQLStorage) {
        this.mySQLStorage = mySQLStorage;
    }

    public String getWordpressImage() {
        return wordpressImage;
    }

    public void setWordpressImage(String wordpressImage) {
        this.wordpressImage = wordpressImage;
    }

    public Double getWordpressStorage() {
        return wordpressStorage;
    }

    public void setWordpressStorage(Double wordpressStorage) {
        this.wordpressStorage = wordpressStorage;
    }


}
