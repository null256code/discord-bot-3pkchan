package spkchan.adapter.listeners

enum class BotButton {
    CREATE_TOKEN_INPUT_MODAL_BUTTON,
}

enum class BotModal {
    TOKEN_INPUT_MODAL,
    ;

    // 2つ以上のinput必要な時に対応できないから、そのときはBotModalをsealed classにしてネストして定義させた方が良さそう
    val textInputId: String get() = "${name}_TEXT_INPUT"
}
