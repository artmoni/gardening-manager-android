package org.gots.justvisual;

import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

/**
 * Created by sfleury on 03/08/15.
 */
public interface OnRecognitionFinished {
    void onRecognitionSucceed();

    void onRecognitionFailed(String message);

    void onRecognitionConfirmed(Document plantDoc);
}
