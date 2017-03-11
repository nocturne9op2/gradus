package com.gda.data.utilities;

import com.gda.data.entities.Assessment;
import com.gda.data.entities.AssessmentType;
import com.gda.data.entities.Course;

import java.util.ArrayList;

public final class MathUtils {
    public static double units(ArrayList<Course> courses) {
        double total = 0;

        for (Course course : courses) {
            total += course.getUnits();
        }

        return total;
    }

    public static double GPA(ArrayList<Course> courses) {
        double a = 0;
        double b = 0;

        for (Course course : courses) {
            a += course.getGrade() * course.getUnits();
            b += course.getUnits();
        }

        if (b == 0) {
            return 0;
        } else {
            return a / b;
        }
    }

    public static double unweightedCoursePercentage(ArrayList<AssessmentType> assessmentTypes) {
        double a = 0;
        double b = 0;

        for (AssessmentType assessmentType : assessmentTypes) {
            for (Assessment assessment : assessmentType.getAssessments()) {
                a += assessment.getScore();
                b += assessment.getTotal();
            }
        }

        if (b == 0) {
            return 0;
        } else {
            return (a / b) * 100;
        }
    }

    public static double weightedCoursePercentage(ArrayList<AssessmentType> assessmentTypes) {
        double total = 0;
        double totalGradePortion = totalGradePortion(assessmentTypes);

        for (AssessmentType assessmentType : assessmentTypes) {
            total += weightedAssessmentPercentage(assessmentType.getGradePortion(), totalGradePortion, assessmentType, assessmentType.getAssessments());
        }

        return total;
    }

    private static double weightedAssessmentPercentage(int gradePortion, double combinedGradePortion, AssessmentType assessmentType, ArrayList<Assessment> assessments) {
        double a = 0;
        double b = 0;
        double percent = 0;

        for (Assessment assessment : assessments) {
            a += assessment.getScore();
            b += assessment.getTotal();
        }

        if (b == 0 || combinedGradePortion == 0) {
            assessmentType.updatePercent(percent);
            return percent;
        } else {
            percent = (a / b) * ((gradePortion / combinedGradePortion) * 100);
            assessmentType.updatePercent(percent);
            return percent;
        }
    }

    private static double totalGradePortion(ArrayList<AssessmentType> assessmentTypes) {
        double total = 0;

        for (AssessmentType assessmentType : assessmentTypes) {
            total += assessmentType.getGradePortion();
        }

        return total;
    }

    private MathUtils() {
    }
}
