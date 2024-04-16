class StateVO {
    var title: String = ""
    var selected: Boolean = false
    constructor()

    constructor(title: String, selected: Boolean) {
        this.title = title
        this.selected = selected
    }
}