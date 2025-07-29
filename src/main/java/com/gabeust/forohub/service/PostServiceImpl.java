package com.gabeust.forohub.service;

import com.gabeust.forohub.dto.PageDTO;
import com.gabeust.forohub.dto.PostDTO;
import com.gabeust.forohub.dto.UserStatsDTO;
import com.gabeust.forohub.entity.Category;
import com.gabeust.forohub.entity.Post;
import com.gabeust.forohub.entity.User;
import com.gabeust.forohub.enums.ReactionType;
import com.gabeust.forohub.mapper.PostMapper;
import com.gabeust.forohub.repository.*;
import com.gabeust.forohub.service.interf.IPostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
/**
 * Implementación del servicio para gestionar las publicaciones (posts) del foro.
 *
 * Proporciona métodos para buscar, crear, actualizar y eliminar publicaciones.
 * Además, permite obtener publicaciones filtradas por categoría o autor,
 * y obtener estadísticas del usuario relacionadas con sus publicaciones, reacciones y comentarios.
 *
 * Maneja las relaciones entre publicaciones, autores, categorías y reacciones.
 */
@Service
public class PostServiceImpl implements IPostService {

    private final IPostRepository postRepository;
    private final PostMapper postMapper;
    private final IUserRepository userRepository;
    private final ICategoryRepository categoryRepository;
    private final IReactionRepository reactionRepository;
    private final iCommentrepository commentRepository;

    public PostServiceImpl(IPostRepository postRepository, PostMapper postMapper, IUserRepository userRepository, ICategoryRepository categoryRepository, IReactionRepository reactionRepository, iCommentrepository commnetRepository) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.reactionRepository = reactionRepository;
        this.commentRepository = commnetRepository;
    }
    /**
     * Obtiene todas las publicaciones con sus reacciones agrupadas por tipo.
     *
     * @return lista de DTOs de publicaciones con estadísticas de reacciones
     */
    @Override
    public PageDTO<PostDTO> findAllPaged(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Post> postsPage = postRepository.findAll(pageable);

        List<PostDTO> dtos = postsPage.stream()
                .map(post -> {
                    PostDTO dto = postMapper.toDTO(post);

                    EnumMap<ReactionType, Long> reactions = new EnumMap<>(ReactionType.class);
                    for (ReactionType type : ReactionType.values()) {
                        long count = reactionRepository.countByPostIdAndReactionType(post.getId(), type);
                        reactions.put(type, count);
                    }

                    return new PostDTO(
                            dto.id(),
                            dto.title(),
                            dto.content(),
                            dto.authorNick(),
                            dto.authorId(),
                            dto.authorImage(),
                            dto.categoryName(),
                            dto.createdAt(),
                            reactions
                    );
                })
                .toList();

        return new PageDTO<>(
                dtos,
                postsPage.getNumber(),
                postsPage.getSize(),
                postsPage.getTotalElements(),
                postsPage.getTotalPages(),
                postsPage.isLast()
        );
    }


    public PageDTO<PostDTO> findByCategoryIdPaged(Long categoryId, Pageable pageable) {
        Page<Post> page = postRepository.findByCategoryId(categoryId, pageable);

        List<PostDTO> dtos = page.getContent().stream()
                .map(postMapper::toDTO)
                .toList();

        return new PageDTO<>(
                dtos,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }


    /**
     * Busca una publicación por su ID.
     *
     * @param id ID de la publicación
     * @return Optional con el DTO de la publicación si existe, o vacío si no
     */
    @Override
    public Optional<PostDTO> findById(Long id) {
        return postRepository.findById(id)
                .map(postMapper::toDTO);
    }
    /**
     * Crea una nueva publicación y la guarda en la base de datos.
     * Asocia la publicación con el autor y la categoría según los datos del DTO.
     *
     * @param postDTO DTO con los datos de la publicación a crear
     * @return DTO de la publicación creada y guardada
     * @throws RuntimeException si el autor o la categoría no existen
     */
    @Transactional
    @Override
    public PostDTO save(PostDTO postDTO) {
        Post post = postMapper.toEntity(postDTO);

        User author = userRepository.findByProfile_Nick(postDTO.authorNick())
                .orElseThrow(() -> new RuntimeException("User not found: " + postDTO.authorNick()));

        Category category = categoryRepository.findByName(postDTO.categoryName())
                .orElseThrow(() -> new RuntimeException("Category not found: " + postDTO.categoryName()));

        post.setAuthor(author);
        post.setCategory(category);

        Post saved = postRepository.save(post);

        return postMapper.toDTO(saved);
    }
    /**
     * Actualiza el título y contenido de una publicación existente.
     *
     * @param id ID de la publicación a actualizar
     * @param postDTO DTO con los datos actualizados de la publicación
     * @return DTO de la publicación actualizada, o null si no se encontró la publicación
     */
    @Override
    public PostDTO update(Long id, PostDTO postDTO) {
        Optional<Post> existingPostOpt = postRepository.findById(id);
        if (existingPostOpt.isEmpty()) {
            return null;
        }
        Post existingPost = existingPostOpt.get();
        existingPost.setTitle(postDTO.title());
        existingPost.setContent(postDTO.content());

        Post saved = postRepository.save(existingPost);
        return postMapper.toDTO(saved);
    }
    /**
     * Obtiene una lista de publicaciones que pertenecen a una categoría específica.
     *
     * @param categoryId ID de la categoría
     * @return lista de DTOs de publicaciones de la categoría
     */
    @Override
    public List<PostDTO> findByCategoryId(Long categoryId) {
        List<Post> posts = postRepository.findByCategoryId(categoryId);
        return posts.stream()
                .map(postMapper::toDTO)
                .toList();
    }

    /**
     * Obtiene una lista de publicaciones realizadas por un autor específico.
     *
     * @param authorId ID del autor
     * @return lista de DTOs de publicaciones del autor
     */
    @Override
    public List<PostDTO> findByAuthorId(Long authorId) {
        List<Post> posts = postRepository.findByAuthorId(authorId);
        return posts.stream()
                .map(postMapper::toDTO)
                .toList();
    }

    /**
     * Elimina una publicación según su ID.
     *
     * @param id ID de la publicación a eliminar
     */
    @Override
    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }

    /**
     * Obtiene estadísticas del usuario incluyendo total de publicaciones,
     * total de reacciones y total de comentarios realizados por el usuario.
     *
     * @param userId ID del usuario
     * @return DTO con las estadísticas agregadas del usuario
     */
    public UserStatsDTO getStatsByUserId(Long userId) {
        int totalPosts = postRepository.countByAuthorId(userId);
        int totalReactions = reactionRepository.countByUserId(userId);
        int totalComments = commentRepository.countByAuthorId(userId);

        return new UserStatsDTO(totalPosts, totalReactions, totalComments);
    }

}
