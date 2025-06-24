package tech.kjo.kjo_mind_care.data.model

import android.content.Context
import com.google.firebase.Timestamp
import tech.kjo.kjo_mind_care.data.enums.BlogStatus
import tech.kjo.kjo_mind_care.data.enums.MediaType
import tech.kjo.kjo_mind_care.data.model.Blog
import java.time.LocalDateTime

object StaticBlogData {
    val currentUser = User(
        uid = "user_current",
//        username = "kjo_dev",
        email = "kjo.dev@example.com",
        fullName = "Kjo Developer",
        profileImage = null
    )

    var applicationContext: Context? = null
        private set

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    fun getSampleBlogPosts(): List<Blog> {
        return listOf(
            Blog(
                id = "blog_1",
                title = "Cómo manejar la ansiedad en exámenes",
                content = "La ansiedad ante los exámenes es una respuesta común que muchos estudiantes experimentan. Aquí hay algunas estrategias que pueden ayudarte a manejarla...",
                author = User(
                    uid = "user_1",
//                    username = "carlos_m",
                    fullName = "Carlos M.",
                    profileImage = "https://randomuser.me/api/portraits/men/1.jpg"
                ),
                createdAt = Timestamp.now(),
                mediaUrl = "https://santiagocidpsicologia.com/wp-content/uploads/ansiedad-ante-los-examanes.jpg",
                mediaType = MediaType.IMAGE,
                reaction = 24,
                comments = 8,
                isLiked = false,
                categoryId = "cat_anxiety",
                status = BlogStatus.PUBLISHED
            ),
            Blog(
                id = "blog_2",
                title = "Técnicas de respiración para momentos de estrés",
                content = "La respiración consciente es una herramienta poderosa para calmar la mente y reducir el estrés. Estas técnicas simples pueden practicarse en cualquier lugar...",
                author = User(
                    uid = "user_2",
//                    username = "ana_p",
                    fullName = "Ana P.",
                    profileImage = "https://randomuser.me/api/portraits/women/2.jpg"
                ),
                createdAt = Timestamp.now(),
                mediaUrl = null,
                mediaType = null,
                reaction = 42,
                comments = 15,
                isLiked = true,
                categoryId = "cat_stress",
                status = BlogStatus.PUBLISHED
            ),
            Blog(
                id = "blog_3",
                title = "Mi experiencia con la meditación diaria",
                content = "Hace seis meses comencé a meditar todos los días durante 10 minutos. Al principio fue difícil mantener la constancia, pero los beneficios que he experimentado son...",
                author = User(
                    uid = "user_3",
//                    username = "miguel_l",
                    fullName = "Miguel L.",
                    profileImage = "https://randomuser.me/api/portraits/men/3.jpg"
                ),
                createdAt = Timestamp.now(),
                mediaUrl = "https://videos.pexels.com/video-files/3712701/3712701-uhd_2160_4096_25fps.mp4",
                mediaType = MediaType.VIDEO,
                reaction = 36,
                comments = 12,
                isLiked = false,
                categoryId = "cat_meditation",
                status = BlogStatus.PUBLISHED
            ),
            Blog(
                id = "blog_4",
                title = "Beneficios de una dieta saludable para la mente",
                content = "La alimentación juega un papel crucial en nuestra salud mental. Descubre cómo ciertos alimentos pueden mejorar tu estado de ánimo y concentración...",
                author = User(
                    uid = "user_4",
//                    username = "laura_g",
                    fullName = "Laura G.",
                    profileImage = "https://randomuser.me/api/portraits/women/4.jpg"
                ),
                createdAt = Timestamp.now(),
                mediaUrl = "https://mejorconsalud.as.com/wp-content/uploads/2017/05/alimentos-salud-cerebro.jpg",
                mediaType = MediaType.IMAGE,
                reaction = 50,
                comments = 20,
                isLiked = false,
                categoryId = "cat_nutrition",
                status = BlogStatus.PUBLISHED
            ),
            Blog(
                id = "blog_5",
                title = "Superando el insomnio: una guía práctica",
                content = "El sueño es fundamental para el bienestar. Si luchas contra el insomnio, estas estrategias y consejos pueden ayudarte a mejorar la calidad de tu descanso...",
                author = currentUser,
                createdAt = Timestamp.now(),
                mediaUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ5mVtqidHO1-jrCEr0YlO54WbvQarvu4QAhA&s",
                mediaType = MediaType.IMAGE,
                reaction = 60,
                comments = 25,
                isLiked = true,
                categoryId = "cat_sleep",
                status = BlogStatus.PUBLISHED
            ),
            Blog(
                id = "blog_6",
                title = "Diario de gratitud: transformando tu perspectiva",
                content = "Llevar un diario de gratitud puede ser una práctica simple pero poderosa para mejorar tu bienestar emocional y cambiar tu forma de ver el mundo.",
                author = User(
                    uid = "user_1",
//                    username = "carlos_m",
                    fullName = "Carlos M.",
                    profileImage = "https://randomuser.me/api/portraits/men/1.jpg"
                ),
                createdAt = Timestamp.now(),
                mediaUrl = null,
                mediaType = null,
                reaction = 18,
                comments = 5,
                isLiked = false,
                categoryId = "cat_meditation",
                status = BlogStatus.PUBLISHED
            ),
            Blog(
                id = "blog_7",
                title = "Ejercicios de mindfulness para la vida diaria",
                content = "Incorporar el mindfulness en tu rutina diaria no requiere mucho tiempo. Pequeños ejercicios pueden hacer una gran diferencia en tu atención y calma.",
                author = User(
                    uid = "user_2",
//                    username = "ana_p",
                    fullName = "Ana P.",
                    profileImage = "https://randomuser.me/api/portraits/women/2.jpg"
                ),
                createdAt = Timestamp.now(),
                mediaUrl = "https://img.youtube.com/vi/UjQjE0C825o/0.jpg",
                mediaType = MediaType.VIDEO,
                reaction = 75,
                comments = 30,
                isLiked = true,
                categoryId = "cat_stress",
                status = BlogStatus.PUBLISHED
            ),
            Blog(
                id = "blog_8",
                title = "Manejando el burnout: señales y estrategias",
                content = "El burnout es un estado de agotamiento físico y emocional. Reconocer las señales tempranas es clave para prevenirlo y recuperarse.",
                author = currentUser,
                createdAt = Timestamp.now(),
                mediaUrl = null,
                mediaType = null,
                reaction = 90,
                comments = 40,
                isLiked = false,
                categoryId = "cat_other",
                status = BlogStatus.PUBLISHED
            )
        )
    }

    fun getSampleCommentsForBlog(blogId: String): List<Comment> {
        return when (blogId) {
            "blog_1" -> listOf(
                Comment(
                    id = "c1",
                    author = User(
                        uid = "u1",
//                        username = "commenter1",
                        fullName = "User A",
                        profileImage = "https://randomuser.me/api/portraits/men/5.jpg"
                    ),
                    content = "¡Gran artículo! La ansiedad en los exámenes es un problema real.",
                    createdAt = Timestamp.now(),
                    isMine = false,
                    replies = listOf(
                        Comment(
                            id = "r1",
                            author = User(
                                uid = "u2",
//                                username = "commenter2",
                                fullName = "User B",
                                profileImage = "https://randomuser.me/api/portraits/women/6.jpg"
                            ),
                            content = "Totalmente de acuerdo. Las técnicas de respiración me han ayudado mucho.",
                            createdAt = Timestamp.now(),
                            isMine = false
                        )
                    )
                ),
                Comment(
                    id = "c2",
                    author = currentUser,
                    content = "Me alegra que encuentres útil la información. ¡Gracias por leer!",
                    createdAt = Timestamp.now(),
                    isMine = true
                )
            )

            "blog_2" -> listOf(
                Comment(
                    id = "c3",
                    author = User(
                        uid = "u3",
//                        username = "commenter3",
                        fullName = "User C",
                        profileImage = "https://randomuser.me/api/portraits/men/7.jpg"
                    ),
                    content = "Necesito probar estas técnicas. Últimamente estoy muy estresado.",
                    createdAt = Timestamp.now(),
                    isMine = false
                )
            )

            "blog_3" -> listOf(
                Comment(
                    id = "c4",
                    author = User(
                        uid = "u4",
//                        username = "commenter4",
                        fullName = "User D",
                        profileImage = "https://randomuser.me/api/portraits/women/8.jpg"
                    ),
                    content = "La meditación es un cambio de vida. Llevo un año practicando y me siento mucho mejor.",
                    createdAt = Timestamp.now(),
                    isMine = false
                ),
                Comment(
                    id = "c5",
                    author = currentUser,
                    content = "¡Qué bueno escuchar eso! Es un viaje increíble.",
                    createdAt = Timestamp.now(),
                    isMine = true
                )
            )

            "blog_5" -> listOf(
                Comment(
                    id = "c6",
                    author = User(
                        uid = "u5",
//                        username = "commenter5",
                        fullName = "User E",
                        profileImage = "https://randomuser.me/api/portraits/men/9.jpg"
                    ),
                    content = "Gracias por estos consejos. Llevo meses con problemas de sueño.",
                    createdAt = Timestamp.now(),
                    isMine = false
                )
            )

            "blog_8" -> listOf(
                Comment(
                    id = "c7",
                    author = User(
                        uid = "u6",
//                        username = "commenter6",
                        fullName = "User F",
                        profileImage = "https://randomuser.me/api/portraits/women/10.jpg"
                    ),
                    content = "El burnout es más común de lo que pensamos. Buen post.",
                    createdAt = Timestamp.now(),
                    isMine = false
                )
            )

            else -> emptyList()
        }
    }

    fun getSampleUsers(): List<User> {
        return listOf(
            currentUser,
            User(
                uid = "user_1",
                fullName = "Carlos M.",
                email = "carlos.m@example.com",
                profileImage = "https://randomuser.me/api/portraits/men/1.jpg",
                phone = "+51987654321",
                age = 30
            ),
            User(
                uid = "user_2",
                fullName = "Ana P.",
                email = "ana.p@example.com",
                profileImage = "https://randomuser.me/api/portraits/women/2.jpg",
                phone = "+51912345678",
                age = 25
            ),
            User(
                uid = "user_3",
                fullName = "Miguel L.",
                email = "miguel.l@example.com",
                profileImage = "https://randomuser.me/api/portraits/men/3.jpg",
                phone = "+51998765432",
                age = 35
            ),
            User(
                uid = "user_4",
                fullName = "Laura G.",
                email = "laura.g@example.com",
                profileImage = "https://randomuser.me/api/portraits/women/4.jpg",
                phone = "+51901234567",
                age = 28
            ),
            User(
                uid = "user_a",
                fullName = "User A",
                email = "user.a@example.com",
                profileImage = "https://randomuser.me/api/portraits/men/5.jpg"
            ),
            User(
                uid = "user_b",
                fullName = "User B",
                email = "user.b@example.com",
                profileImage = "https://randomuser.me/api/portraits/women/6.jpg"
            ),
            User(
                uid = "user_c",
                fullName = "User C",
                email = "user.c@example.com",
                profileImage = "https://randomuser.me/api/portraits/men/7.jpg"
            )
        )
    }
    fun getSampleDailyActivities(): List<DailyActivity> {
        return listOf(
            DailyActivity(
                id = "activity_1",
                name = "Meditación guiada",
                description = "Sesión de 10 minutos de meditación para empezar el día con calma.",
                category = "Mindfulness",
                durationMinutes = 10,
                difficulty = "Fácil",
                isCompleted = false
            ),
            DailyActivity(
                id = "activity_2",
                name = "Ejercicio de respiración profunda",
                description = "Cinco minutos para practicar la respiración diafragmática y reducir el estrés.",
                category = "Respiración",
                durationMinutes = 5,
                difficulty = "Fácil",
                isCompleted = false
            ),
            DailyActivity(
                id = "activity_3",
                name = "Escribir en el diario de gratitud",
                description = "Dedica unos minutos a escribir tres cosas por las que estés agradecido.",
                category = "Gratitud",
                durationMinutes = 7,
                difficulty = "Fácil",
                isCompleted = true
            ),
            DailyActivity(
                id = "activity_4",
                name = "Paseo consciente",
                description = "Un paseo de 15 minutos prestando atención a tus sentidos y al entorno.",
                category = "Actividad Física",
                durationMinutes = 15,
                difficulty = "Fácil",
                isCompleted = false
            ),
            DailyActivity(
                id = "activity_5",
                name = "Lectura reflexiva",
                description = "Lee un pasaje inspirador y reflexiona sobre su significado para ti.",
                category = "Lectura",
                durationMinutes = 10,
                difficulty = "Moderado",
                isCompleted = false
            )
        )
    }
}