class StateVO {
    var title: String = ""
    var selected: Boolean = false
    var spinnerPrompt: String = ""
    constructor()

    constructor(title: String, selected: Boolean , prompt: String) {
        this.title = title
        this.selected = selected
        this.spinnerPrompt = prompt
    }
}