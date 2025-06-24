package tech.kjo.kjo_mind_care.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tech.kjo.kjo_mind_care.data.repository.IBlogRepository
import tech.kjo.kjo_mind_care.data.repository.ICategoryRepository
import tech.kjo.kjo_mind_care.data.repository.ICommentRepository
import tech.kjo.kjo_mind_care.data.repository.IReactionRepository
import tech.kjo.kjo_mind_care.data.repository.impl.BlogRepository
import tech.kjo.kjo_mind_care.data.repository.impl.CategoryRepository
import tech.kjo.kjo_mind_care.data.repository.impl.CommentRepository
import tech.kjo.kjo_mind_care.data.repository.impl.ReactionRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBlogRepository(
        blogRepositoryImpl: BlogRepository
    ): IBlogRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepository
    ): ICategoryRepository

    @Binds // Nuevo binding
    @Singleton
    abstract fun bindCommentRepository(
        commentRepositoryImpl: CommentRepository
    ): ICommentRepository

    @Binds
    @Singleton
    abstract fun bindReactionRepository(
        reactionRepositoryImpl: ReactionRepository
    ): IReactionRepository
}