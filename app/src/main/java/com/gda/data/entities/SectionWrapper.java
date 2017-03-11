package com.gda.data.entities;

public class SectionWrapper {
    private boolean isSection;
    private AssessmentType assessmentType;
    private Assessment assessment;
    private int sectionPosition;
    private int childPosition;

    public SectionWrapper(AssessmentType assessmentType, int sectionPosition) {
        this.isSection = true;
        this.assessmentType = assessmentType;
        this.assessment = null;
        this.sectionPosition = sectionPosition;
        this.childPosition = -1;
    }

    public SectionWrapper(Assessment assessment, int sectionPosition, int childPosition) {
        this.isSection = false;
        this.assessmentType = null;
        this.assessment = assessment;
        this.sectionPosition = sectionPosition;
        this.childPosition = childPosition;
    }

    public boolean isSection() {
        return isSection;
    }

    public AssessmentType getSection() {
        return assessmentType;
    }

    public Assessment getChild() {
        return assessment;
    }

    public int getSectionPosition() {
        return sectionPosition;
    }

    public int getChildPosition() {
        return childPosition;
    }
}
