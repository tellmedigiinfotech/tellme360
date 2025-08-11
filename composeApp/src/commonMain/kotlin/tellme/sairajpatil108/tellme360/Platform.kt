package tellme.sairajpatil108.tellme360

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform