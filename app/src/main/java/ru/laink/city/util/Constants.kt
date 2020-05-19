package ru.laink.city.util

class Constants {
    companion object {
        const val CAMERA_PERMISSION_CODE = 101
        const val CAMERA_REQUEST_CODE = 102
        const val GALLERY_REQUEST_CODE = 105
        const val GOOGLE_SIGN_IN = 1337

        const val COLLECTION_CATEGORIES = "categories"
        const val COLLECTION_DOCUMENT = "titles"
        const val COLLECTION_REQUEST = "requests"

        const val WORK_CATEGORY = "ru.laink.city.work.RefreshCategoryWorker"
        const val WORK_REQUEST = "ru.laink.city.work.RefreshRequestWorker"

        const val IMAGE_EXPANSION = "png"

        const val REQUEST_LOCATION_PERMISSION = 1

        const val SMOLENSK_LONGITUDE = 32.050169
        const val SMOLENSK_LATITUDE = 54.790495
        const val ZOOM_LEVEL = 10f

        const val TYPE_DONE = 1
        const val TYPE_REJECTED = -1
        const val TYPE_IN_DEVELOPING = 0

        const val MARKER_DONE = "РЕШЕНО"
        const val MARKER_REJECTED = "ОТКЛОНЕНО"
        const val MARKER_IN_DEVELOPING = "В РАЗРАБОТКЕ"
    }
}