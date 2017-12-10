package tr.edu.metu.ceng.absa.aspectextraction.entity;

public class Aspect {
    private final String word;

    public Aspect(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    @Override
    public String toString() {
        return "ASPECT: " + word;
    }
}
