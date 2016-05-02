package org.gots.seed;

public class BotanicSpecie {
    private String specieName;
    private BotanicFamily family;
    private int specieId;
    private String filepath = null;

    public String getSpecieName() {
        return specieName;
    }

    public void setSpecieName(String specieName) {
        this.specieName = specieName;
    }

    public int getSpecieId() {
        return specieId;
    }

    public void setSpecieId(int specieId) {
        this.specieId = specieId;
    }

    public BotanicFamily getFamily() {
        return family;
    }

    public void setFamily(BotanicFamily family) {
        this.family = family;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    @Override
    public String toString() {
        return getSpecieName();
    }
}
