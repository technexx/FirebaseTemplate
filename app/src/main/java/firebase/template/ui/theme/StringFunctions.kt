package firebase.template.ui.theme

fun showPlaceHolderTextIfFieldIsEmpty(field: String, placeHolder: String): String {
    return if (field.isBlank()) placeHolder else ""
}