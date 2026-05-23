package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences

class UserSession(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("resumeai_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PLAN = "plan_type" // "free" or "pro"
        private const val KEY_ATS_CHECKS = "ats_checks_count"
        private const val KEY_REFERRAL_CODE = "referral_code"
        private const val KEY_REFERRALS_COUNT = "referrals_count"
    }

    var plan: String
        get() = prefs.getString(KEY_PLAN, "free") ?: "free"
        set(value) {
            prefs.edit().putString(KEY_PLAN, value).apply()
        }

    val isPro: Boolean
        get() = plan == "pro"

    var atsChecksCount: Int
        get() = prefs.getInt(KEY_ATS_CHECKS, 0)
        set(value) {
            prefs.edit().putInt(KEY_ATS_CHECKS, value).apply()
        }

    var referralCode: String
        get() {
            var code = prefs.getString(KEY_REFERRAL_CODE, "") ?: ""
            if (code.isEmpty()) {
                code = "RESUMEAI-${(1000..9999).random()}"
                prefs.edit().putString(KEY_REFERRAL_CODE, code).apply()
            }
            return code
        }
        set(value) {
            prefs.edit().putString(KEY_REFERRAL_CODE, value).apply()
        }

    var referralsCount: Int
        get() = prefs.getInt(KEY_REFERRALS_COUNT, 0)
        set(value) {
            prefs.edit().putInt(KEY_REFERRALS_COUNT, value).apply()
        }

    fun incrementAtsCheck() {
        atsChecksCount = atsChecksCount + 1
    }

    fun resetCounts() {
        atsChecksCount = 0
    }
}
