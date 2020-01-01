package com.george.firebasetest

data class UserSystem(

    val email: String = "",

    val name: String = "",

    val id: String? = ""
)

data class PostFunction(

    val article_id: String = "",
    val article_title: String = "",
    val article_content: String = "",
    val article_tag: ArticleTags,
    val author: String? = "",
    val created_time: String = ""
)

enum class ArticleTags {
    Beauty,
    Gossiping,
    Joke,
    SchoolLife

}