package com.easybraille.network

import com.google.gson.annotations.SerializedName

// ========== Admin Stats Response ==========
data class AdminStatsResponse(
    @SerializedName("users") val users: UserStats,
    @SerializedName("translations") val translations: TranslationStats,
    @SerializedName("ai") val ai: AiStats,
    @SerializedName("monthlyData") val monthlyData: List<MonthlyData>?
)

data class UserStats(
    @SerializedName("total") val total: Int,
    @SerializedName("activeThisMonth") val activeThisMonth: Int,
    @SerializedName("newThisWeek") val newThisWeek: Int
)

data class TranslationStats(
    @SerializedName("total") val total: Int,
    @SerializedName("todayCount") val todayCount: Int,
    @SerializedName("thisWeekCount") val thisWeekCount: Int,
    @SerializedName("thisMonthCount") val thisMonthCount: Int,
    @SerializedName("avgPerDay") val avgPerDay: Double
)

data class AiStats(
    @SerializedName("totalRequests") val totalRequests: Int,
    @SerializedName("successRate") val successRate: Double,
    @SerializedName("avgConfidence") val avgConfidence: Double
)

data class MonthlyData(
    @SerializedName("month") val month: String,
    @SerializedName("users") val users: Int,
    @SerializedName("translations") val translations: Int
)

// ========== Admin Users Response ==========
data class AdminUsersResponse(
    @SerializedName("total") val total: Int,
    @SerializedName("users") val users: List<AdminUser>
)

data class AdminUser(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("createdAt") val createdAt: String?
)

// ========== Admin Translations Response ==========
data class AdminTranslationsResponse(
    @SerializedName("total") val total: Int,
    @SerializedName("translations") val translations: List<AdminTranslation>
)

data class AdminTranslation(
    @SerializedName("_id") val id: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("originalText") val originalText: String,
    @SerializedName("brailleText") val brailleText: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("userName") val userName: String?
)

// ========== Admin Test Response ==========
data class AdminTestResponse(
    @SerializedName("status") val status: String,
    @SerializedName("database") val database: String,
    @SerializedName("collections") val collections: CollectionCounts,
    @SerializedName("timestamp") val timestamp: String
)

data class CollectionCounts(
    @SerializedName("users") val users: Int,
    @SerializedName("translations") val translations: Int
)
