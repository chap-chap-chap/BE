package org.chapchap.be.domain.route.util;

import org.chapchap.be.domain.user.entity.UserProfile;

public final class CalorieUtil {
    private CalorieUtil() {}

    /* -------------------- 사람: 하루 권장 칼로리 (활동계수 없음; BMR만) -------------------- */
    public static int humanDailyKcal(UserProfile p) {
        if (p == null || p.getHumanWeightKg() == null) return 2200; // 값 없으면 기본치

        double w = p.getHumanWeightKg();
        double height = (p.getHumanHeightCm() != null) ? p.getHumanHeightCm() : 170.0;
        int years = (p.getHumanAge() != null) ? p.getHumanAge() : 30;
        UserProfile.Sex sx = (p.getHumanSex() != null) ? p.getHumanSex() : UserProfile.Sex.MALE;

        // Mifflin–St Jeor (BMR)
        double bmr = (sx == UserProfile.Sex.MALE)
                ? 10 * w + 6.25 * height - 5 * years + 5
                : 10 * w + 6.25 * height - 5 * years - 161;
        return (int)Math.round(bmr);
    }

    /* -------------------- 사람: 이번 산책 칼로리 -------------------- */
    public static int humanWalkKcal(double distanceMeters, long durationSeconds, Double weightKg) {
        if (weightKg == null || weightKg <= 0 || distanceMeters <= 0 || durationSeconds <= 0) return 0;
        double hours = durationSeconds / 3600.0;
        // 보행 MET 고정 (활동량 미사용)
        double MET = 3.5;
        double kcal = MET * weightKg * hours;
        return (int)Math.round(kcal);
    }

    /* -------------------- 강아지: 하루 권장 칼로리 (단순화) -------------------- */
    // 퍼피(12개월 미만): 130 * w^0.75, 성견: 110 * w^0.75, 노견(>7y): ×0.9
    public static int dogDailyKcal(Double weightKg, Integer ageMonths) {
        if (weightKg == null || weightKg <= 0) return 0;

        if (ageMonths != null && ageMonths < 12) {
            double E = 130.0 * Math.pow(weightKg, 0.75);
            return (int)Math.round(E);
        } else {
            int years = (ageMonths != null) ? ageMonths / 12 : 3;
            double E = 110.0 * Math.pow(weightKg, 0.75);
            if (years > 7) E *= 0.9;
            return (int)Math.round(E);
        }
    }

    /* -------------------- 강아지: 이번 산책 칼로리 (간이 v1) -------------------- */
    public static int dogWalkKcal(double distanceMeters, Double weightKg) {
        if (weightKg == null || weightKg <= 0 || distanceMeters <= 0) return 0;
        double km = distanceMeters / 1000.0;
        double kcal = 1.0 * weightKg * km;
        return (int)Math.round(kcal);
    }
}