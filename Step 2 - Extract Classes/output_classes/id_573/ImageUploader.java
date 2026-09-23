package com.openisle.service;

/**
 * Abstract service for uploading images.
 */
public abstract class ImageUploader {
    /**
     * Upload an image and return its accessible URL.
     * @param data image binary data
     * @param filename name of the file
     * @return accessible URL of the uploaded file
     */
    public abstract String upload(byte[] data, String filename);
}
