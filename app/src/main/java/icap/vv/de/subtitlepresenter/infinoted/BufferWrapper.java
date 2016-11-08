package icap.vv.de.subtitlepresenter.infinoted;

import java.io.Serializable;

import de.lits.adopted.exception.BufferException;
import de.lits.adopted.model.Buffer;
import de.lits.adopted.model.Segment;
import de.lits.adopted.text.model.segmented.BufferSimple;

/**
 * Created by Marko Nalis on 23.10.2015.
 */
public class BufferWrapper extends BufferSimple {

    private final static String TAG = "BufferWrapper";
    private StringBuilder buffer = new StringBuilder();

    public BufferWrapper() {
        super();
        buffer.append("");
    }

    @Override
    public void appendSegment(Segment seg) {
        super.appendSegment(seg);
        buffer.append(seg.getText());
    }

    @Override
    public void splice(int position, int length, Buffer insert) throws BufferException {
        super.splice(position, length, insert);
        final String tex = insert != null ? insert.getText() : "";
        buffer.replace(position,length,tex);
    }

    @Override
    public int length() {
        return buffer.toString().length();
    }

    @Override
    public String getText() {
        return buffer.toString();
    }
}
