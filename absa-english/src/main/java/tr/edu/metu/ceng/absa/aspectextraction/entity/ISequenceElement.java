package tr.edu.metu.ceng.absa.aspectextraction.entity;

public interface ISequenceElement extends Comparable<ISequenceElement> {

    char getElementType();

    int getExternalIndex();


    @Override
    default int compareTo(ISequenceElement sequenceElement){
        return getExternalIndex() > sequenceElement.getExternalIndex() ? 1 :
        (getExternalIndex() == sequenceElement.getExternalIndex() ? 0 : -1 );
    }
}
