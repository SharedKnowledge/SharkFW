package net.sharkfw.knowledgeBase.persistent.fileDump;

import net.sharkfw.knowledgeBase.*;

import java.util.*;

/**
 * Created by j4rvis on 2/28/17.
 */
public class FileDumpTimeSTSet extends FileDumpSTSet implements TimeSTSet {

    private final TimeSTSet timeSTSet;

    public FileDumpTimeSTSet(FileDumpSharkKB kb, TimeSTSet set) {
        super(kb, set);
        timeSTSet = set;
    }

    @Override
    public TimeSTSet fragment(TimeSemanticTag anchor) throws SharkKBException {
        TimeSTSet fragment = this.timeSTSet.fragment(anchor);
        this.kb.persist();
        return new FileDumpTimeSTSet(this.kb, fragment);
    }

    @Override
    public TimeSTSet contextualize(TimeSTSet context, FragmentationParameter fp) throws SharkKBException {
        TimeSTSet contextualize = this.timeSTSet.contextualize(context, fp);
        this.kb.persist();
        return new FileDumpTimeSTSet(this.kb, contextualize);

    }

    @Override
    public TimeSemanticTag createTimeSemanticTag(long from, long duration) throws SharkKBException {
        TimeSemanticTag timeSemanticTag = this.timeSTSet.createTimeSemanticTag(from, duration);
        this.kb.persist();
        return new FileDumpTimeSemanticTag(this.kb, timeSemanticTag);
    }

    @Override
    public Enumeration<TimeSemanticTag> timeTags() throws SharkKBException {
        Enumeration<TimeSemanticTag> timeSemanticTagEnumeration = this.timeSTSet.timeTags();
        List<TimeSemanticTag> list = new ArrayList<>();
        while (timeSemanticTagEnumeration.hasMoreElements()){
            list.add(new FileDumpTimeSemanticTag(this.kb, timeSemanticTagEnumeration.nextElement()));
        }
        return Collections.enumeration(list);
    }

    @Override
    public Iterator<TimeSemanticTag> tstTags() throws SharkKBException {
        Iterator<TimeSemanticTag> timeSemanticTagIterator = this.timeSTSet.tstTags();
        ArrayList<TimeSemanticTag> list = new ArrayList<>();
        while (timeSemanticTagIterator.hasNext()){
            list.add(new FileDumpTimeSemanticTag(this.kb, timeSemanticTagIterator.next()));
        }
        return list.iterator();
    }
}
