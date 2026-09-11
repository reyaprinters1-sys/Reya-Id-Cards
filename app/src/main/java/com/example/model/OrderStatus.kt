package com.example.model

enum class OrderStatus(
    val code: String,
    val titleTa: String,
    val titleEn: String,
    val descriptionTa: String,
    val stepIndex: Int
) {
    RECEIVED(
        code = "RECEIVED",
        titleTa = "பெறப்பட்டது",
        titleEn = "Received",
        descriptionTa = "ஆர்டர் பெறப்பட்டு சரிபார்க்கப்படுகிறது",
        stepIndex = 0
    ),
    PRINTING(
        code = "PRINTING",
        titleTa = "பிரிண்டிங் ஆகிறது",
        titleEn = "Printing",
        descriptionTa = "ஆர்டர் ஏற்றுக்கொள்ளப்பட்டு அச்சிடப்படும் நிலை",
        stepIndex = 1
    ),
    DISPATCHED(
        code = "DISPATCHED",
        titleTa = "டெலிவரிக்கு அனுப்பப்பட்டது",
        titleEn = "Dispatched",
        descriptionTa = "கொரியர்/வேன் மூலம் அனுப்பப்பட்ட நிலை",
        stepIndex = 2
    ),
    DELIVERED(
        code = "DELIVERED",
        titleTa = "டெலிவரி முடிந்தது",
        titleEn = "Delivered",
        descriptionTa = "கார்டு கடைக்கு வந்து சேர்ந்ததற்கான உறுதிப்படுத்தல்",
        stepIndex = 3
    )
}
