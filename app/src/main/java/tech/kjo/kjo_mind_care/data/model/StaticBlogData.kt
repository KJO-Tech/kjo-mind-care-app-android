package tech.kjo.kjo_mind_care.data.model

import java.time.LocalDateTime

object StaticBlogData {
    val currentUser = User(id = "user_current", username = "kjo_dev", fullName = "Kjo Developer", profileImageUrl = null)

    fun getSampleBlogPosts(): List<BlogPost> {
        return listOf(
            BlogPost(
                id = "blog_1",
                title = "Cómo manejar la ansiedad en exámenes",
                content = "La ansiedad ante los exámenes es una respuesta común que muchos estudiantes experimentan. Aquí hay algunas estrategias que pueden ayudarte a manejarla...",
                author = User(id = "user_1", username = "carlos_m", fullName = "Carlos M.", profileImageUrl = "https://randomuser.me/api/portraits/men/1.jpg"),
                createdAt = LocalDateTime.now().minusHours(2),
                mediaUrl = "https://santiagocidpsicologia.com/wp-content/uploads/ansiedad-ante-los-examanes.jpg",
                mediaType = MediaType.IMAGE,
                likes = 24,
                comments = 8,
                isLiked = false,
                categoryId = "cat_anxiety"
            ),
            BlogPost(
                id = "blog_2",
                title = "Técnicas de respiración para momentos de estrés",
                content = "La respiración consciente es una herramienta poderosa para calmar la mente y reducir el estrés. Estas técnicas simples pueden practicarse en cualquier lugar...",
                author = User(id = "user_2", username = "ana_p", fullName = "Ana P.", profileImageUrl = "https://randomuser.me/api/portraits/women/2.jpg"),
                createdAt = LocalDateTime.now().minusHours(5),
                mediaUrl = null,
                mediaType = null,
                likes = 42,
                comments = 15,
                isLiked = true,
                categoryId = "cat_stress"
            ),
            BlogPost(
                id = "blog_3",
                title = "Mi experiencia con la meditación diaria",
                content = "Hace seis meses comencé a meditar todos los días durante 10 minutos. Al principio fue difícil mantener la constancia, pero los beneficios que he experimentado son...",
                author = User(id = "user_3", username = "miguel_l", fullName = "Miguel L.", profileImageUrl = "https://randomuser.me/api/portraits/men/3.jpg"),
                createdAt = LocalDateTime.now().minusDays(1),
                mediaUrl = "https://videos.pexels.com/video-files/3712701/3712701-uhd_2160_4096_25fps.mp4",
                mediaType = MediaType.VIDEO,
                likes = 36,
                comments = 12,
                isLiked = false,
                categoryId = "cat_meditation"
            ),
            BlogPost(
                id = "blog_4",
                title = "Beneficios de una dieta saludable para la mente",
                content = "La alimentación juega un papel crucial en nuestra salud mental. Descubre cómo ciertos alimentos pueden mejorar tu estado de ánimo y concentración...",
                author = User(id = "user_4", username = "laura_g", fullName = "Laura G.", profileImageUrl = "https://randomuser.me/api/portraits/women/4.jpg"),
                createdAt = LocalDateTime.now().minusDays(3),
                mediaUrl = "https://mejorconsalud.as.com/wp-content/uploads/2017/05/alimentos-salud-cerebro.jpg",
                mediaType = MediaType.IMAGE,
                likes = 50,
                comments = 20,
                isLiked = false,
                categoryId = "cat_nutrition"
            ),
            BlogPost(
                id = "blog_5",
                title = "Superando el insomnio: una guía práctica",
                content = "El sueño es fundamental para el bienestar. Si luchas contra el insomnio, estas estrategias y consejos pueden ayudarte a mejorar la calidad de tu descanso...",
                author = currentUser,
                createdAt = LocalDateTime.now().minusWeeks(1),
                mediaUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ5mVtqidHO1-jrCEr0YlO54WbvQarvu4QAhA&s",
                mediaType = MediaType.IMAGE,
                likes = 60,
                comments = 25,
                isLiked = true,
                categoryId = "cat_sleep"
            ),
            BlogPost(
                id = "blog_6",
                title = "Diario de gratitud: transformando tu perspectiva",
                content = "Llevar un diario de gratitud puede ser una práctica simple pero poderosa para mejorar tu bienestar emocional y cambiar tu forma de ver el mundo.",
                author = User(id = "user_1", username = "carlos_m", fullName = "Carlos M.", profileImageUrl = "https://randomuser.me/api/portraits/men/1.jpg"),
                createdAt = LocalDateTime.now().minusDays(2),
                mediaUrl = null,
                mediaType = null,
                likes = 18,
                comments = 5,
                isLiked = false,
                categoryId = "cat_meditation"
            ),
            BlogPost(
                id = "blog_7",
                title = "Ejercicios de mindfulness para la vida diaria",
                content = "Incorporar el mindfulness en tu rutina diaria no requiere mucho tiempo. Pequeños ejercicios pueden hacer una gran diferencia en tu atención y calma.",
                author = User(id = "user_2", username = "ana_p", fullName = "Ana P.", profileImageUrl = "https://randomuser.me/api/portraits/women/2.jpg"),
                createdAt = LocalDateTime.now().minusDays(4),
                mediaUrl = "https://img.youtube.com/vi/UjQjE0C825o/0.jpg",
                mediaType = MediaType.VIDEO,
                likes = 75,
                comments = 30,
                isLiked = true,
                categoryId = "cat_stress"
            ),
            BlogPost(
                id = "blog_8",
                title = "Manejando el burnout: señales y estrategias",
                content = "El burnout es un estado de agotamiento físico y emocional. Reconocer las señales tempranas es clave para prevenirlo y recuperarse.",
                author = currentUser,
                createdAt = LocalDateTime.now().minusMonths(1),
                mediaUrl = null,
                mediaType = null,
                likes = 90,
                comments = 40,
                isLiked = false,
                categoryId = "cat_other"
            )
        )
    }

    fun getSampleCommentsForBlog(blogId: String): List<Comment> {
        return when (blogId) {
            "blog_1" -> listOf(
                Comment(
                    id = "c1",
                    author = User(id = "u1", username = "commenter1", fullName = "User A", profileImageUrl = "https://randomuser.me/api/portraits/men/5.jpg"),
                    content = "¡Gran artículo! La ansiedad en los exámenes es un problema real.",
                    createdAt = LocalDateTime.now().minusMinutes(30),
                    isMine = false,
                    replies = listOf(
                        Comment(
                            id = "r1",
                            author = User(id = "u2", username = "commenter2", fullName = "User B", profileImageUrl = "https://randomuser.me/api/portraits/women/6.jpg"),
                            content = "Totalmente de acuerdo. Las técnicas de respiración me han ayudado mucho.",
                            createdAt = LocalDateTime.now().minusMinutes(20),
                            isMine = false
                        )
                    )
                ),
                Comment(
                    id = "c2",
                    author = currentUser,
                    content = "Me alegra que encuentres útil la información. ¡Gracias por leer!",
                    createdAt = LocalDateTime.now().minusMinutes(10),
                    isMine = true
                )
            )
            "blog_2" -> listOf(
                Comment(
                    id = "c3",
                    author = User(id = "u3", username = "commenter3", fullName = "User C", profileImageUrl = "https://randomuser.me/api/portraits/men/7.jpg"),
                    content = "Necesito probar estas técnicas. Últimamente estoy muy estresado.",
                    createdAt = LocalDateTime.now().minusHours(1),
                    isMine = false
                )
            )
            "blog_3" -> listOf(
                Comment(
                    id = "c4",
                    author = User(id = "u4", username = "commenter4", fullName = "User D", profileImageUrl = "https://randomuser.me/api/portraits/women/8.jpg"),
                    content = "La meditación es un cambio de vida. Llevo un año practicando y me siento mucho mejor.",
                    createdAt = LocalDateTime.now().minusDays(1),
                    isMine = false
                ),
                Comment(
                    id = "c5",
                    author = currentUser,
                    content = "¡Qué bueno escuchar eso! Es un viaje increíble.",
                    createdAt = LocalDateTime.now().minusHours(12),
                    isMine = true
                )
            )
            "blog_5" -> listOf(
                Comment(
                    id = "c6",
                    author = User(id = "u5", username = "commenter5", fullName = "User E", profileImageUrl = "https://randomuser.me/api/portraits/men/9.jpg"),
                    content = "Gracias por estos consejos. Llevo meses con problemas de sueño.",
                    createdAt = LocalDateTime.now().minusHours(5),
                    isMine = false
                )
            )
            "blog_8" -> listOf(
                Comment(
                    id = "c7",
                    author = User(id = "u6", username = "commenter6", fullName = "User F", profileImageUrl = "https://randomuser.me/api/portraits/women/10.jpg"),
                    content = "El burnout es más común de lo que pensamos. Buen post.",
                    createdAt = LocalDateTime.now().minusDays(3),
                    isMine = false
                )
            )
            else -> emptyList()
        }
    }
}