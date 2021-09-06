package ru.insoft.rgali.model.html


data class Select (
    var options: MutableList<Option> = mutableListOf()
)
{
    fun setSelectedField(field: String?) {
        for (option in options) {
            if (option.value == field) {
                option.selected = true
                return
            }
        }
    }
}