package com.esh.smartsight.util;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import com.esh.smartsight.model.Recognition;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.List;
import java.util.Vector;

import static com.esh.smartsight.util.Config.IMAGE_MEAN;
import static com.esh.smartsight.util.Config.IMAGE_STD;
import static com.esh.smartsight.util.Config.INPUT_NAME;
import static com.esh.smartsight.util.Config.INPUT_SIZE;
import static com.esh.smartsight.util.Config.MODEL_FILE;
import static com.esh.smartsight.util.Config.OUTPUT_NAME;

public class TensorFlowImageRecognizer {
    private int outputSize;
    private Vector<String> labels;
    private TensorFlowInferenceInterface inferenceInterface;
    private TensorFlowImageRecognizer() {
    }
    public static TensorFlowImageRecognizer create(AssetManager assetManager) {
        TensorFlowImageRecognizer recognizer = new TensorFlowImageRecognizer();
        recognizer.labels = ClassAttrProvider.newInstance(assetManager).getLabels();
        recognizer.inferenceInterface = new TensorFlowInferenceInterface(assetManager,
                "file:///android_asset/" + MODEL_FILE);
        recognizer.outputSize = YOLOClassifier.getInstance()
                .getOutputSizeByShape(recognizer.inferenceInterface.graphOperation(OUTPUT_NAME));
        return recognizer;
    }

    public List<Recognition> recognizeImage(final Bitmap bitmap) {
        return YOLOClassifier.getInstance().classifyImage(runTensorFlow(bitmap), labels);
    }

    public String getStatString() {
        return inferenceInterface.getStatString();
    }

    public void close() {
        inferenceInterface.close();
    }

    private float[] runTensorFlow(final Bitmap bitmap) {
        final float[] tfOutput = new float[outputSize];
        inferenceInterface.feed(INPUT_NAME, processBitmap(bitmap), 1, INPUT_SIZE, INPUT_SIZE, 3);

        inferenceInterface.run(new String[]{OUTPUT_NAME});
        inferenceInterface.fetch(OUTPUT_NAME, tfOutput);
        return tfOutput;
    }

    private float[] processBitmap(final Bitmap bitmap) {
        int[] intValues = new int[INPUT_SIZE * INPUT_SIZE];
        float[] floatValues = new float[INPUT_SIZE * INPUT_SIZE * 3];

        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int i = 0; i < intValues.length; ++i) {
            final int val = intValues[i];
            floatValues[i * 3] = (((val >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD;
            floatValues[i * 3 + 1] = (((val >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD;
            floatValues[i * 3 + 2] = ((val & 0xFF) - IMAGE_MEAN) / IMAGE_STD;
        }
        return floatValues;
    }
}
