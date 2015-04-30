package com.shark.demo.shark_demo;

import android.widget.TextView;

import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.KnowledgeBaseListener;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SNSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.system.L;

/**
 * Created by simon on 22.03.15.
 *
 * Helps writing output to the text view. This class keeps two strings, on for the knowledge
 * base output, one for the log output. It offers methods to append to or set either of these
 * strings and switch between showing one of those strings in the textview.
 */
public class KbTextViewWriter implements KnowledgeBaseListener {

    protected static KbTextViewWriter instance;
    protected TextView outputTextView;
    protected String kbText, logText;

    private KbTextViewWriter() {
        kbText = "";
        logText = "";
    }

    public static KbTextViewWriter getInstance() {
        if (KbTextViewWriter.instance == null) {
            KbTextViewWriter.instance = new KbTextViewWriter();
        }
        return KbTextViewWriter.instance;
    }

    /**
     * Append to the kb text string. this is not automatically shown.
     * @param append
     */
    public void appendToKbText(String append) {
        kbText += append + System.lineSeparator() + System.lineSeparator();
        showKbText();
    }

    /**
     * Set the kb text string. this is not automatically shown.
     * @param textToSet
     */
    public void setKbText(String textToSet) {
        kbText = textToSet + System.lineSeparator();
        showKbText();
    }

    /**
     * Append to the log text string. this is not automatically shown.
     * @param append
     */
    public void appendToLogText(String append) {
        logText += append + System.lineSeparator() + System.lineSeparator();
        showLogText();
    }

    /**
     * Set the log text string. this is not automatically shown.
     * @param textToSet
     */
    public void setLogText(String textToSet) {
        logText = textToSet + System.lineSeparator();
        showLogText();
    }

    /**
     * Show what is in the knowledge base string in the text view.
     */
    public void showKbText() {
        outputTextView.setText(kbText);
    }

    /**
     * Show what is in the log string in the text view.
     */
    public void showLogText() {
        outputTextView.setText(logText);
    }

    /**
     * Write the kb to the text view. This automatically switches the text to the kb text.
     * I remember there was a weird bug when this L.kb2String method could somehow throw an
     * unchecked exception, so better wrap in in try-catch.
     */
    public void writeKbToTextView(SharkKB kb) {
        try {
            setKbText(L.kb2String(kb));
        } catch (Exception e) {
            L.d("Internal", "Exception while writing kb to text view: " + e.toString());
        }
        showKbText();
    }

    public void setOutputTextView(TextView outputTextView) {
        this.outputTextView = outputTextView;
        this.setLogText("Text view changed");
    }

    @Override
    public void topicAdded(SemanticTag semanticTag) {
        try {
            appendToLogText("Semantic Tag added: " + L.semanticTag2String(semanticTag));
        } catch (SharkKBException e) {
            L.d("Internal", "Error while writing Tag to text view: " + e.toString());
        }    }

    @Override
    public void peerAdded(PeerSemanticTag peerSemanticTag) {
        try {
            appendToLogText("Peer Semantic Tag added: " + L.semanticTag2String(peerSemanticTag));
        } catch (SharkKBException e) {
            L.d("Internal", "Error while writing Tag to text view: " + e.toString());
        }    }

    @Override
    public void locationAdded(SpatialSemanticTag spatialSemanticTag) {
        try {
            appendToLogText("Spatial Semantic Tag added: " + L.semanticTag2String(spatialSemanticTag));
        } catch (SharkKBException e) {
            L.d("Internal", "Error while writing Tag to text view: " + e.toString());
        }    }

    @Override
    public void timespanAdded(TimeSemanticTag timeSemanticTag) {
        try {
            appendToLogText("Time Semantic Tag added: " + L.semanticTag2String(timeSemanticTag));
        } catch (SharkKBException e) {
            L.d("Internal", "Error while writing Tag to text view: " + e.toString());
        }    }

    @Override
    public void topicRemoved(SemanticTag semanticTag) {
        try {
            appendToLogText("Semantic Tag removed: " + L.semanticTag2String(semanticTag));
        } catch (SharkKBException e) {
            L.d("Internal", "Error while writing Tag to text view: " + e.toString());
        }    }

    @Override
    public void peerRemoved(PeerSemanticTag peerSemanticTag) {
        try {
            appendToLogText("Peer Semantic Tag removed: " + L.semanticTag2String(peerSemanticTag));
        } catch (SharkKBException e) {
            L.d("Internal", "Error while writing Tag to text view: " + e.toString());
        }
    }

    @Override
    public void locationRemoved(SpatialSemanticTag spatialSemanticTag) {
        try {
            appendToLogText("Spatial Semantic Tag removed: " + L.semanticTag2String(spatialSemanticTag));
        } catch (SharkKBException e) {
            L.d("Internal", "Error while writing Tag to text view: " + e.toString());
        }
    }

    @Override
    public void timespanRemoved(TimeSemanticTag timeSemanticTag) {
        try {
            appendToLogText("Time Semantic Tag removed: " + L.semanticTag2String(timeSemanticTag));
        } catch (SharkKBException e) {
            L.d("Internal", "Error while writing Tag to text view: " + e.toString());
        }
    }

    @Override
    public void predicateCreated(SNSemanticTag snSemanticTag, String s, SNSemanticTag snSemanticTag2) {

    }

    @Override
    public void predicateRemoved(SNSemanticTag snSemanticTag, String s, SNSemanticTag snSemanticTag2) {

    }

    @Override
    public void contextPointAdded(ContextPoint contextPoint) {
        appendToLogText("Context Point added: " + L.cp2String(contextPoint));
    }

    @Override
    public void cpChanged(ContextPoint contextPoint) {
        appendToLogText("Context Point changed: " + L.cp2String(contextPoint));
    }

    @Override
    public void contextPointRemoved(ContextPoint contextPoint) {
        appendToLogText("Context Point removed: " + L.cp2String(contextPoint));
    }
}
