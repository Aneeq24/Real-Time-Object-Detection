package com.esh.smartsight.util;

public interface Config {
    int INPUT_SIZE = 416;
    int IMAGE_MEAN = 128;
    float IMAGE_STD = 128.0f;
    String MODEL_FILE = "tiny-yolo-voc-graph.pb";
    String LABEL_FILE = "tiny-yolo-voc-labels.txt";
    String INPUT_NAME = "input";
    String OUTPUT_NAME = "output";

    String LOGGING_TAG = "log";
}
